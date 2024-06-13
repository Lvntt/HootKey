package dev.banger.hootkey.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.banger.hootkey.data.Constants.EMPTY_STRING
import dev.banger.hootkey.data.crypto.CryptoManager
import dev.banger.hootkey.data.model.VaultFieldModel
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
import dev.banger.hootkey.domain.entity.vault.VaultNotFoundException
import dev.banger.hootkey.domain.entity.vault.VaultShort
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
        const val VAULTS = "vaults"
        const val FIELDS = "fields"
    }

    private fun vaultCollection(userId: String) =
        fireStore.collection(userId).document(VAULTS).collection(
            VAULTS
        )

    private fun fieldCollection(userId: String, vaultId: String) =
        vaultCollection(userId).document(vaultId).collection(FIELDS)

    override suspend fun getAll(filter: FilterType, query: String?): List<VaultShort> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllByCategory(
        categoryId: String, filter: FilterType, query: String?
    ): List<VaultShort> {
        TODO("Not yet implemented")
    }

    override suspend fun getCountInCategory(categoryId: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: String): Vault {
        TODO("Not yet implemented")
    }

    private fun mergeVaultFields(fieldValues: Map<Int, String>, category: Category) =
        category.template.fields.associateBy { it.index }.mapValues { (_, field) ->
            val value = fieldValues[field.index] ?: EMPTY_STRING
            FieldValue(
                name = field.name, type = field.type, value = value
            )
        }

    override suspend fun create(vault: CreateVaultRequest): Vault {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        val category = categoryRepository.getById(vault.categoryId)
            ?: throw CategoryDoesNotExistException("Category with id ${vault.categoryId} does not exist")
        if (category.template.fields.size != vault.fieldValues.size) throw VaultCreationException("Invalid number of fields")

        var vaultId = EMPTY_STRING
        return runCatching {
            val createdVault = vaultCollection(userId).add(
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
                fieldCollection(userId, vaultId).document("$index").set(
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
            if (vaultId.isNotBlank()) vaultCollection(userId).document(vaultId).delete().await()
            throw VaultCreationException("Failed to create vault: ${throwable.stackTraceToString()}")
        }.getOrThrow()
    }

    override suspend fun edit(vault: EditVaultRequest): Vault {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        val category = categoryRepository.getById(vault.categoryId)
            ?: throw CategoryDoesNotExistException("Category with id ${vault.categoryId} does not exist")
        if (category.template.fields.size != vault.fieldValues.size) throw VaultCreationException("Invalid number of fields")

        if (!vaultCollection(userId).document(vault.vaultId).get().await()
                .exists()
        ) throw VaultNotFoundException("Vault with id ${vault.vaultId} does not exist")

        vaultCollection(userId).document(vault.vaultId).update(
            mapOf(
                "name" to vault.name, "categoryId" to vault.categoryId
            )
        ).await()

        vault.fieldValues.forEach { (index, value) ->
            fieldCollection(userId, vault.vaultId).document("$index").set(
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
        if (!vaultCollection(userId).document(id).get().await()
                .exists()
        ) throw VaultNotFoundException("Vault with id $id does not exist")

        vaultCollection(userId).document(id).delete().await()
    }

    override suspend fun notifyViewed(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun addToFavourites(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromFavourites(id: String) {
        TODO("Not yet implemented")
    }
}