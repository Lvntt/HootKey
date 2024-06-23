package dev.banger.hootkey.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import dev.banger.hootkey.data.Constants.EMPTY_STRING
import dev.banger.hootkey.data.crypto.CryptoManager
import dev.banger.hootkey.data.model.CategoryModel
import dev.banger.hootkey.data.model.VaultFieldModel
import dev.banger.hootkey.data.model.VaultModel
import dev.banger.hootkey.domain.entity.auth.exception.UnauthorizedException
import dev.banger.hootkey.domain.entity.category.Category
import dev.banger.hootkey.domain.entity.category.CategoryDoesNotExistException
import dev.banger.hootkey.domain.entity.category.VaultCategoryInfo
import dev.banger.hootkey.domain.entity.vault.CreateVaultRequest
import dev.banger.hootkey.domain.entity.vault.EditVaultRequest
import dev.banger.hootkey.domain.entity.vault.FieldValue
import dev.banger.hootkey.domain.entity.vault.FilterType
import dev.banger.hootkey.domain.entity.vault.Vault
import dev.banger.hootkey.domain.entity.vault.VaultCreationException
import dev.banger.hootkey.domain.entity.vault.VaultNameWithId
import dev.banger.hootkey.domain.entity.vault.VaultNotFoundException
import dev.banger.hootkey.domain.entity.vault.VaultShort
import dev.banger.hootkey.domain.entity.vault.VaultsPage
import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.domain.repository.VaultRepository
import kotlinx.coroutines.tasks.await

class VaultRepositoryImpl(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val crypto: CryptoManager,
    private val categoryRepository: CategoryRepository
) : VaultRepository {

    private companion object {
        const val PAGE_SIZE = 25L
        const val PAGE_SIZE_WITH_ONE_NARROW = 50L
        const val PAGE_SIZE_WITH_TWO_NARROWS = 75L
    }

    private fun determinePageSize(filter: FilterType, query: String?) = when {
        query.isNullOrBlank() && filter != FilterType.FAVOURITE -> PAGE_SIZE
        filter == FilterType.FAVOURITE && query.isNullOrBlank() || filter != FilterType.FAVOURITE && !query.isNullOrBlank() -> PAGE_SIZE_WITH_ONE_NARROW

        else -> PAGE_SIZE_WITH_TWO_NARROWS
    }

    private suspend inline fun queryVaults(
        userId: String,
        filter: FilterType,
        query: String?,
        pageStartVault: DocumentSnapshot?,
        additionalFilter: (Query.() -> Query) = { this }
    ): QuerySnapshot = fireStore.vaultCollection(userId).orderBy(
        if (filter == FilterType.RECENT) "lastViewedTime" else if (filter == FilterType.LAST_EDIT) "lastEditTime" else "name",
        if (filter == FilterType.RECENT || filter == FilterType.LAST_EDIT) Query.Direction.DESCENDING else Query.Direction.ASCENDING
    ).additionalFilter().startAfterIfNotNull(pageStartVault).limit(determinePageSize(filter, query))
        .get().await()

    private fun QuerySnapshot.toVaultModels(
        query: String?, filter: FilterType
    ) = this.map { vaultSnapshot -> vaultSnapshot.id to vaultSnapshot.toObject<VaultModel>() }
        .filter { (_, vault) ->
            if (!query.isNullOrBlank() && filter == FilterType.FAVOURITE) vault.name.contains(
                query, ignoreCase = true
            ) && vault.isFavourite
            else if (filter == FilterType.FAVOURITE) vault.isFavourite
            else if (!query.isNullOrBlank()) vault.name.contains(query, ignoreCase = true)
            else true
        }

    private suspend inline fun fieldValue(
        userId: String,
        id: String,
        valueIndex: Int,
        decryptCondition: (String) -> Boolean = { false }
    ) = if (valueIndex < 0) null else fireStore.fieldCollection(userId, id).document("$valueIndex")
        .get().await().toObject<VaultFieldModel>()?.value?.decryptWhen(crypto, decryptCondition)

    private fun mergeVaultFields(fieldValues: Map<Int, String>, category: Category) =
        category.template.fields.associateBy { it.index }.mapValues { (_, field) ->
            val value = fieldValues[field.index] ?: EMPTY_STRING
            FieldValue(
                name = field.name, type = field.type, value = value
            )
        }

    override suspend fun getAllNames(): List<VaultNameWithId> {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        return fireStore.vaultCollection(userId).get().await().map { vaultSnapshot ->
            VaultNameWithId(vaultSnapshot.toObject<VaultModel>().name, vaultSnapshot.id)
        }
    }

    override suspend fun getShortByIds(ids: List<String>): List<VaultShort> {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        val vaults = fireStore.vaultCollection(userId).whereIn(FieldPath.documentId(), ids).get().await()
        val vaultModels = vaults.map { vaultSnapshot -> vaultSnapshot.id to vaultSnapshot.toObject<VaultModel>() }

        val categories = vaultModels.map { (_, vault) -> vault.categoryId }.distinct()
            .associateWith { categoryId ->
                val customCategory =
                    fireStore.categoryCollection(userId).document(categoryId).get().await()
                if (customCategory.exists()) customCategory.toObject<CategoryModel>()
                else fireStore.commonCategoryCollection().document(categoryId).get().await()
                    .toObject<CategoryModel>()
            }

        val convertedVaults = vaultModels.map { (id, vault) ->
            val category = categories[vault.categoryId]
            val linkIndex = category?.linkIndex ?: -1
            val loginIndex = category?.loginIndex ?: -1
            val passwordIndex = category?.passwordIndex ?: -1
            val login = fieldValue(userId, id, loginIndex) { it.isNotEmpty() }
            val link = fieldValue(userId, id, linkIndex) { it.isNotEmpty() }
            val password = fieldValue(userId, id, passwordIndex) { it.isNotEmpty() }

            VaultShort(
                id = id,
                name = vault.name,
                isFavourite = vault.isFavourite,
                login = login,
                link = link,
                password = password,
                categoryId = vault.categoryId
            )
        }

        return convertedVaults
    }

    override suspend fun getAll(filter: FilterType, query: String?, pageKey: String?): VaultsPage {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val pageStartVault =
            if (pageKey != null) fireStore.vaultCollection(userId).document(pageKey).get().await()
            else null
        val vaults = queryVaults(userId, filter, query, pageStartVault)

        val endReached = vaults.size() < PAGE_SIZE
        val nextPageKey = if (endReached) null else vaults.documents.last().id

        val vaultModels = vaults.toVaultModels(query, filter)
        val categories = vaultModels.map { (_, vault) -> vault.categoryId }.distinct()
            .associateWith { categoryId ->
                val customCategory =
                    fireStore.categoryCollection(userId).document(categoryId).get().await()
                if (customCategory.exists()) customCategory.toObject<CategoryModel>()
                else fireStore.commonCategoryCollection().document(categoryId).get().await()
                    .toObject<CategoryModel>()
            }

        val convertedVaults = vaultModels.map { (id, vault) ->
            val category = categories[vault.categoryId]
            val linkIndex = category?.linkIndex ?: -1
            val loginIndex = category?.loginIndex ?: -1
            val passwordIndex = category?.passwordIndex ?: -1
            val login = fieldValue(userId, id, loginIndex) { it.isNotEmpty() }
            val link = fieldValue(userId, id, linkIndex) { it.isNotEmpty() }
            val password = fieldValue(userId, id, passwordIndex) { it.isNotEmpty() }

            VaultShort(
                id = id,
                name = vault.name,
                isFavourite = vault.isFavourite,
                login = login,
                link = link,
                password = password,
                categoryId = vault.categoryId
            )
        }

        return VaultsPage(
            vaults = convertedVaults,
            nextPageKey = nextPageKey ?: EMPTY_STRING,
            endReached = endReached
        )
    }

    override suspend fun getAllByCategory(
        categoryId: String, filter: FilterType, query: String?, pageKey: String?
    ): VaultsPage {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val pageStartVault =
            if (pageKey != null) fireStore.vaultCollection(userId).document(pageKey).get().await()
            else null
        val vaults = queryVaults(userId, filter, query, pageStartVault) {
            whereEqualTo("categoryId", categoryId)
        }

        val endReached = vaults.size() < PAGE_SIZE
        val nextPageKey = if (endReached) null else vaults.documents.last().id

        val vaultModels = vaults.toVaultModels(query, filter)
        val customCategory = fireStore.categoryCollection(userId).document(categoryId).get().await()
        val category = if (customCategory.exists()) customCategory.toObject<CategoryModel>()
        else fireStore.commonCategoryCollection().document(categoryId).get().await()
            .toObject<CategoryModel>()
        val linkIndex = category?.linkIndex ?: -1
        val loginIndex = category?.loginIndex ?: -1
        val passwordIndex = category?.passwordIndex ?: -1

        val convertedVaults = vaultModels.map { (id, vault) ->
            val login = fieldValue(userId, id, loginIndex) { it.isNotEmpty() }
            val link = fieldValue(userId, id, linkIndex) { it.isNotEmpty() }
            val password = fieldValue(userId, id, passwordIndex) { it.isNotEmpty() }

            VaultShort(
                id = id,
                name = vault.name,
                isFavourite = vault.isFavourite,
                login = login,
                link = link,
                password = password,
                categoryId = categoryId
            )
        }

        return VaultsPage(
            vaults = convertedVaults,
            nextPageKey = nextPageKey ?: EMPTY_STRING,
            endReached = endReached
        )
    }

    override suspend fun getById(id: String): Vault {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        val vault =
            fireStore.vaultCollection(userId).document(id).get().await().toObject<VaultModel>()
                ?: throw VaultNotFoundException("Vault with id $id does not exist")
        val category = categoryRepository.getById(vault.categoryId)
            ?: throw CategoryDoesNotExistException("Category with id ${vault.categoryId} does not exist")
        val vaultFields =
            fireStore.fieldCollection(userId, id).get().await().associate { fieldSnapshot ->
                fieldSnapshot.id.toInt() to fieldSnapshot.toObject<VaultFieldModel>().value
            }

        return Vault(
            id = id,
            name = vault.name,
            category = VaultCategoryInfo(
                id = category.id, name = category.name, icon = category.icon
            ),
            isFavourite = vault.isFavourite,
            lastEditTimeMillis = vault.lastEditTime.toInstant().toEpochMilli(),
            lastViewedTimeMillis = vault.lastViewedTime.toInstant().toEpochMilli(),
            fieldValues = mergeVaultFields(vaultFields, category)
        )
    }

    override suspend fun create(vault: CreateVaultRequest): Vault {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        val category = categoryRepository.getById(vault.categoryId)
            ?: throw CategoryDoesNotExistException("Category with id ${vault.categoryId} does not exist")
        if (category.template.fields.size != vault.fieldValues.size) throw VaultCreationException("Invalid number of fields")

        var vaultId = EMPTY_STRING
        return runCatching {
            val createdVault = fireStore.vaultCollection(userId).add(
                mapOf(
                    "name" to vault.name,
                    "categoryId" to vault.categoryId,
                    "isFavourite" to false,
                    "lastEditTime" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                    "lastViewedTime" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )
            ).await()
            vaultId = createdVault.id

            vault.fieldValues.forEach { (index, value) ->
                fireStore.fieldCollection(userId, vaultId).document("$index").set(
                    VaultFieldModel(
                        value = if (value.isNotEmpty()) crypto.encryptBase64(value) else value
                    )
                )
            }

            Vault(
                id = vaultId,
                name = vault.name,
                category = VaultCategoryInfo(
                    id = category.id, name = category.name, icon = category.icon
                ),
                isFavourite = false,
                lastEditTimeMillis = System.currentTimeMillis(),
                lastViewedTimeMillis = System.currentTimeMillis(),
                fieldValues = mergeVaultFields(vault.fieldValues, category)
            )
        }.onFailure { throwable ->
            if (vaultId.isNotBlank()) fireStore.vaultCollection(userId).document(vaultId).delete()
                .await()
            throw VaultCreationException("Failed to create vault: ${throwable.stackTraceToString()}")
        }.getOrThrow()
    }

    override suspend fun edit(vault: EditVaultRequest): Vault {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        val category = categoryRepository.getById(vault.categoryId)
            ?: throw CategoryDoesNotExistException("Category with id ${vault.categoryId} does not exist")
        if (category.template.fields.size != vault.fieldValues.size) throw VaultCreationException("Invalid number of fields")

        if (!fireStore.vaultCollection(userId).document(vault.vaultId).get().await()
                .exists()
        ) throw VaultNotFoundException("Vault with id ${vault.vaultId} does not exist")

        fireStore.vaultCollection(userId).document(vault.vaultId).update(
            mapOf("name" to vault.name, "categoryId" to vault.categoryId)
        ).await()

        vault.fieldValues.forEach { (index, value) ->
            fireStore.fieldCollection(userId, vault.vaultId).document("$index").set(
                VaultFieldModel(
                    value = if (value.isNotEmpty()) crypto.encryptBase64(value) else value
                )
            )
        }

        return Vault(
            id = vault.vaultId,
            name = vault.name,
            category = VaultCategoryInfo(
                id = category.id, name = category.name, icon = category.icon
            ),
            isFavourite = false,
            lastEditTimeMillis = System.currentTimeMillis(),
            lastViewedTimeMillis = System.currentTimeMillis(),
            fieldValues = mergeVaultFields(vault.fieldValues, category)
        )
    }

    override suspend fun delete(id: String) {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        if (!fireStore.vaultCollection(userId).document(id).get().await()
                .exists()
        ) throw VaultNotFoundException("Vault with id $id does not exist")

        val vault = fireStore.vaultCollection(userId).document(id)
        vault.delete().await()
        getFieldRefs(vault).forEach { fieldRef ->
            fieldRef.delete().await()
        }
    }

    override suspend fun notifyViewed(id: String) {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        fireStore.vaultCollection(userId).document(id).update(
            mapOf("lastViewedTime" to com.google.firebase.firestore.FieldValue.serverTimestamp())
        ).await()
    }

    override suspend fun addToFavourites(id: String) {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        fireStore.vaultCollection(userId).document(id).update(
            mapOf("isFavourite" to true)
        ).await()
    }

    override suspend fun removeFromFavourites(id: String) {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        fireStore.vaultCollection(userId).document(id).update(
            mapOf("isFavourite" to false)
        ).await()
    }
}