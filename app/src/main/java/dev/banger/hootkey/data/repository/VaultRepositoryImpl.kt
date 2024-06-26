package dev.banger.hootkey.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.toObject
import dev.banger.hootkey.data.Constants.EMPTY_STRING
import dev.banger.hootkey.data.Constants.VAULT_COUNT
import dev.banger.hootkey.data.crypto.CryptoManager
import dev.banger.hootkey.data.model.VaultFieldModel
import dev.banger.hootkey.data.model.VaultModel
import dev.banger.hootkey.data.network.NetworkManager
import dev.banger.hootkey.domain.entity.auth.exception.UnauthorizedException
import dev.banger.hootkey.domain.entity.category.Category
import dev.banger.hootkey.domain.entity.category.CategoryDoesNotExistException
import dev.banger.hootkey.domain.entity.template.FieldType
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
import java.util.UUID

class VaultRepositoryImpl(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val crypto: CryptoManager,
    private val categoryRepository: CategoryRepository,
    private val network: NetworkManager
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
        .get(network).await()

    private fun QuerySnapshot.toVaultModels(
        query: String?, filter: FilterType
    ) = this.map { vaultSnapshot -> vaultSnapshot.id to vaultSnapshot.toObject<VaultModel>() }
        .filter { (_, vault) ->
            if (!query.isNullOrBlank() && filter == FilterType.FAVOURITE) vault.name
                .decryptWhen(crypto) { it.isNotEmpty() }
                .contains(query, ignoreCase = true) && vault.isFavourite
            else if (filter == FilterType.FAVOURITE) vault.isFavourite
            else if (!query.isNullOrBlank())
                vault.name.decryptWhen(crypto) { it.isNotEmpty() }
                    .contains(query, ignoreCase = true)
            else true
        }

    private fun mergeVaultFields(fieldValues: Map<Int, String>, category: Category) =
        category.template.fields.associateBy { it.index }.mapValues { (_, field) ->
            val value = fieldValues[field.index] ?: EMPTY_STRING
            FieldValue(
                name = field.name, type = field.type, value = value
            )
        }

    override suspend fun getAllNames(): List<VaultNameWithId> {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        return fireStore.vaultCollection(userId).get(network).await().map { vaultSnapshot ->
            VaultNameWithId(
                vaultSnapshot.toObject<VaultModel>().name.decryptWhen(crypto) { it.isNotEmpty() },
                vaultSnapshot.id
            )
        }
    }

    override suspend fun getShortByIds(ids: List<String>): List<VaultShort> {
        if (ids.isEmpty()) return emptyList()

        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        val vaults =
            fireStore.vaultCollection(userId).whereIn(FieldPath.documentId(), ids.take(30))
                .get(network).await()
        val vaultModels =
            vaults.map { vaultSnapshot -> vaultSnapshot.id to vaultSnapshot.toObject<VaultModel>() }

        val convertedVaults = vaultModels.map { (id, vault) ->
            val login =
                vault.login.decryptWhen(crypto) { it.isNotEmpty() }.takeIf { it.isNotBlank() }
            val link = vault.link.decryptWhen(crypto) { it.isNotEmpty() }.takeIf { it.isNotBlank() }
            val password =
                vault.password.decryptWhen(crypto) { it.isNotEmpty() }.takeIf { it.isNotBlank() }

            VaultShort(
                id = id,
                name = vault.name.decryptWhen(crypto) { it.isNotEmpty() },
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
            if (pageKey != null) fireStore.vaultCollection(userId).document(pageKey).get(network)
                .await()
            else null
        val vaults = queryVaults(userId, filter, query, pageStartVault)

        val endReached = vaults.size() < determinePageSize(filter, query)
        val nextPageKey = if (endReached) null else vaults.documents.last().id

        val vaultModels = vaults.toVaultModels(query, filter)

        val convertedVaults = vaultModels.map { (id, vault) ->
            val login =
                vault.login.decryptWhen(crypto) { it.isNotEmpty() }.takeIf { it.isNotBlank() }
            val link = vault.link.decryptWhen(crypto) { it.isNotEmpty() }.takeIf { it.isNotBlank() }
            val password =
                vault.password.decryptWhen(crypto) { it.isNotEmpty() }.takeIf { it.isNotBlank() }

            VaultShort(
                id = id,
                name = vault.name.decryptWhen(crypto) { it.isNotEmpty() },
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
            if (pageKey != null) fireStore.vaultCollection(userId).document(pageKey).get(network)
                .await()
            else null
        val vaults = queryVaults(userId, filter, query, pageStartVault) {
            whereEqualTo("categoryId", categoryId)
        }

        val endReached = vaults.size() < determinePageSize(filter, query)
        val nextPageKey = if (endReached) null else vaults.documents.last().id

        val vaultModels = vaults.toVaultModels(query, filter)

        val convertedVaults = vaultModels.map { (id, vault) ->
            val login =
                vault.login.decryptWhen(crypto) { it.isNotEmpty() }.takeIf { it.isNotBlank() }
            val link = vault.link.decryptWhen(crypto) { it.isNotEmpty() }.takeIf { it.isNotBlank() }
            val password =
                vault.password.decryptWhen(crypto) { it.isNotEmpty() }.takeIf { it.isNotBlank() }

            VaultShort(
                id = id,
                name = vault.name.decryptWhen(crypto) { it.isNotEmpty() },
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

    override suspend fun getShortById(id: String): VaultShort {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val vault =
            fireStore.vaultCollection(userId).document(id).get(network).await()
                .toObject<VaultModel>()
                ?: throw VaultNotFoundException("Vault with id $id does not exist")

        val login = vault.login.decryptWhen(crypto) { it.isNotEmpty() }.takeIf { it.isNotBlank() }
        val link = vault.link.decryptWhen(crypto) { it.isNotEmpty() }.takeIf { it.isNotBlank() }
        val password =
            vault.password.decryptWhen(crypto) { it.isNotEmpty() }.takeIf { it.isNotBlank() }

        return VaultShort(
            id = id,
            name = vault.name.decryptWhen(crypto) { it.isNotEmpty() },
            isFavourite = vault.isFavourite,
            login = login,
            link = link,
            password = password,
            categoryId = vault.categoryId
        )
    }

    override suspend fun getAllFull() {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        var pageStartVault: DocumentSnapshot? = null
        var endReached = false

        while (!endReached) {
            val vaultSnapshots = fireStore
                .vaultCollection(userId)
                .orderBy(
                    "name",
                    Query.Direction.ASCENDING
                )
                .startAfterIfNotNull(pageStartVault)
                .limit(PAGE_SIZE).get(Source.SERVER).await()

            if (vaultSnapshots.isEmpty) return
            pageStartVault = vaultSnapshots.documents.last()

            val vaults =
                vaultSnapshots.map { vaultSnapshot -> vaultSnapshot.id to vaultSnapshot.toObject<VaultModel>() }
            if (vaults.isEmpty()) return

            vaults.forEach {
                fireStore.fieldCollection(userId, it.first).get(Source.SERVER).await()
            }
            if (vaults.size < PAGE_SIZE) endReached = true
        }
    }

    override suspend fun getById(id: String): Vault {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        val vault =
            fireStore.vaultCollection(userId).document(id).get(network).await()
                .toObject<VaultModel>()
                ?: throw VaultNotFoundException("Vault with id $id does not exist")
        val category = categoryRepository.getById(vault.categoryId)
            ?: throw CategoryDoesNotExistException("Category with id ${vault.categoryId} does not exist")
        val vaultFields =
            fireStore.fieldCollection(userId, id).get(network).await().associate { fieldSnapshot ->
                fieldSnapshot.id.toInt() to fieldSnapshot.toObject<VaultFieldModel>().value.decryptWhen(
                    crypto
                ) { it.isNotEmpty() }
            }

        return Vault(
            id = id,
            name = vault.name.decryptWhen(crypto) { it.isNotEmpty() },
            category = category,
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

        val vaultId = UUID.randomUUID().toString()
        return runCatching {
            val loginIndex = category.template.fields.indexOfFirst { it.type == FieldType.LOGIN }
            val linkIndex = category.template.fields.indexOfFirst { it.type == FieldType.LINK }
            val passwordIndex =
                category.template.fields.indexOfFirst { it.type == FieldType.PASSWORD }
            val login =
                if (loginIndex >= 0) vault.fieldValues[loginIndex] ?: EMPTY_STRING else EMPTY_STRING
            val link =
                if (linkIndex >= 0) vault.fieldValues[linkIndex] ?: EMPTY_STRING else EMPTY_STRING
            val password = if (passwordIndex >= 0) vault.fieldValues[passwordIndex]
                ?: EMPTY_STRING else EMPTY_STRING

            val vaultModel = VaultModel(
                name =
                if (vault.name.isNotEmpty()) crypto.encryptBase64(vault.name)
                else vault.name,
                categoryId = vault.categoryId,
                isFavourite = false,
                lastEditTime = Timestamp.now(),
                lastViewedTime = Timestamp.now(),
                login = if (login.isNotEmpty()) crypto.encryptBase64(login) else login,
                link = if (link.isNotEmpty()) crypto.encryptBase64(link) else link,
                password = if (password.isNotEmpty()) crypto.encryptBase64(password) else password
            )

            fireStore.vaultCollection(userId).document(vaultId).set(vaultModel)
                .awaitWhenNetworkAvailable(network)

            vault.fieldValues.forEach { (index, value) ->
                fireStore.fieldCollection(userId, vaultId).document("$index").set(
                    VaultFieldModel(
                        value = if (value.isNotEmpty()) crypto.encryptBase64(value) else value
                    )
                )
            }

            if (category.isCustom) {
                fireStore.categoryCollection(userId).document(vault.categoryId).update(
                    mapOf(VAULT_COUNT to com.google.firebase.firestore.FieldValue.increment(1))
                ).awaitWhenNetworkAvailable(network)
            } else {
                if (fireStore.commonTemplateVaultCountDocument(userId, vault.categoryId)
                        .get(network).await().exists()
                ) {
                    fireStore.commonTemplateVaultCountDocument(userId, vault.categoryId).update(
                        mapOf(VAULT_COUNT to com.google.firebase.firestore.FieldValue.increment(1))
                    ).awaitWhenNetworkAvailable(network)
                } else {
                    fireStore.commonTemplateVaultCountDocument(userId, vault.categoryId).set(
                        mapOf(VAULT_COUNT to 1)
                    ).awaitWhenNetworkAvailable(network)
                }
            }

            Vault(
                id = vaultId,
                name = vault.name,
                category = category,
                isFavourite = false,
                lastEditTimeMillis = System.currentTimeMillis(),
                lastViewedTimeMillis = System.currentTimeMillis(),
                fieldValues = mergeVaultFields(vault.fieldValues, category)
            )
        }.onFailure { throwable ->
            fireStore.vaultCollection(userId).document(vaultId).delete()
                .awaitWhenNetworkAvailable(network)
            throw VaultCreationException("Failed to create vault: ${throwable.stackTraceToString()}")
        }.getOrThrow()
    }

    override suspend fun edit(vault: EditVaultRequest): Vault {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val currentVaultSnapshot =
            fireStore.vaultCollection(userId).document(vault.vaultId).get(network).await()
                .toObject<VaultModel>()
                ?: throw VaultNotFoundException("Vault with id ${vault.vaultId} does not exist")

        if (currentVaultSnapshot.categoryId != vault.categoryId) {
            val oldCategory = categoryRepository.getById(currentVaultSnapshot.categoryId)
                ?: throw CategoryDoesNotExistException("Category with id ${currentVaultSnapshot.categoryId} does not exist")
            oldCategory.template.fields.forEach {
                fireStore.fieldCollection(userId, vault.vaultId).document("${it.index}").delete()
                    .awaitWhenNetworkAvailable(network)
            }
        }

        val category = categoryRepository.getById(vault.categoryId)
            ?: throw CategoryDoesNotExistException("Category with id ${vault.categoryId} does not exist")
        if (category.template.fields.size != vault.fieldValues.size) throw VaultCreationException("Invalid number of fields")

        val loginIndex = category.template.fields.indexOfFirst { it.type == FieldType.LOGIN }
        val linkIndex = category.template.fields.indexOfFirst { it.type == FieldType.LINK }
        val passwordIndex = category.template.fields.indexOfFirst { it.type == FieldType.PASSWORD }
        val login =
            if (loginIndex >= 0) vault.fieldValues[loginIndex] ?: EMPTY_STRING else EMPTY_STRING
        val link =
            if (linkIndex >= 0) vault.fieldValues[linkIndex] ?: EMPTY_STRING else EMPTY_STRING
        val password = if (passwordIndex >= 0) vault.fieldValues[passwordIndex]
            ?: EMPTY_STRING else EMPTY_STRING

        fireStore.vaultCollection(userId).document(vault.vaultId).update(
            mapOf(
                "name" to if (vault.name.isNotEmpty()) crypto.encryptBase64(vault.name) else vault.name,
                "categoryId" to vault.categoryId,
                "lastEditTime" to Timestamp.now(),
                "login" to if (login.isNotEmpty()) crypto.encryptBase64(login) else login,
                "link" to if (link.isNotEmpty()) crypto.encryptBase64(link) else link,
                "password" to if (password.isNotEmpty()) crypto.encryptBase64(password) else password
            )
        ).awaitWhenNetworkAvailable(network)

        vault.fieldValues.forEach { (index, value) ->
            fireStore.fieldCollection(userId, vault.vaultId).document("$index").set(
                VaultFieldModel(
                    value = if (value.isNotEmpty()) crypto.encryptBase64(value) else value
                )
            ).awaitWhenNetworkAvailable(network)
        }

        return Vault(
            id = vault.vaultId,
            name = vault.name,
            category = category,
            isFavourite = false,
            lastEditTimeMillis = System.currentTimeMillis(),
            lastViewedTimeMillis = System.currentTimeMillis(),
            fieldValues = mergeVaultFields(vault.fieldValues, category)
        )
    }

    override suspend fun delete(id: String) {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val vault = fireStore.vaultCollection(userId).document(id)
        val vaultSnapshot = vault.get(network).await().toObject<VaultModel>()
            ?: throw VaultNotFoundException("Vault with id $id does not exist")
        val isCustomCategory = categoryRepository.getShortById(vaultSnapshot.categoryId)?.isCustom
            ?: throw CategoryDoesNotExistException("Category with id ${vaultSnapshot.categoryId} does not exist")

        if (isCustomCategory) {
            fireStore.categoryCollection(userId).document(vaultSnapshot.categoryId).update(
                mapOf(VAULT_COUNT to com.google.firebase.firestore.FieldValue.increment(-1))
            ).awaitWhenNetworkAvailable(network)
        } else {
            if (fireStore.commonTemplateVaultCountDocument(userId, vaultSnapshot.categoryId)
                    .get(network).await().exists()
            ) {
                fireStore.commonTemplateVaultCountDocument(userId, vaultSnapshot.categoryId).update(
                    mapOf(VAULT_COUNT to com.google.firebase.firestore.FieldValue.increment(-1))
                ).awaitWhenNetworkAvailable(network)
            }
        }

        vault.delete().awaitWhenNetworkAvailable(network)
        getFieldRefs(vault, networkManager = network).forEach { fieldRef ->
            fieldRef.delete().awaitWhenNetworkAvailable(network)
        }
    }

    override suspend fun notifyViewed(id: String) {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        fireStore.vaultCollection(userId).document(id).update(
            mapOf("lastViewedTime" to Timestamp.now())
        ).awaitWhenNetworkAvailable(network)
    }

    override suspend fun addToFavourites(id: String) {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        fireStore.vaultCollection(userId).document(id).update(
            mapOf("isFavourite" to true)
        ).awaitWhenNetworkAvailable(network)
    }

    override suspend fun removeFromFavourites(id: String) {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        fireStore.vaultCollection(userId).document(id).update(
            mapOf("isFavourite" to false)
        ).awaitWhenNetworkAvailable(network)
    }
}