package dev.banger.hootkey.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dev.banger.hootkey.data.Constants.VAULT_COUNT
import dev.banger.hootkey.data.crypto.CryptoManager
import dev.banger.hootkey.data.datasource.SettingsManager
import dev.banger.hootkey.data.model.CategoryModel
import dev.banger.hootkey.data.network.NetworkManager
import dev.banger.hootkey.domain.entity.OfflineException
import dev.banger.hootkey.domain.entity.auth.exception.UnauthorizedException
import dev.banger.hootkey.domain.entity.category.Category
import dev.banger.hootkey.domain.entity.category.CategoryDoesNotExistException
import dev.banger.hootkey.domain.entity.category.CategoryIcon
import dev.banger.hootkey.domain.entity.category.CategoryShort
import dev.banger.hootkey.domain.entity.category.CreateCategoryRequest
import dev.banger.hootkey.domain.entity.category.EditCategoryRequest
import dev.banger.hootkey.domain.entity.template.TemplateDoesNotExistException
import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.domain.repository.TemplateRepository
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val templateRepository: TemplateRepository,
    private val crypto: CryptoManager,
    private val settingsManager: SettingsManager,
    private val network: NetworkManager
) : CategoryRepository {

    private suspend inline fun DocumentSnapshot.toCategory(
        isCustom: Boolean,
        userId: String
    ): Category? {
        val categoryModel = toObject<CategoryModel>()
            ?: throw CategoryDoesNotExistException("Category with id $id does not exist")
        return Category(
            id = id,
            name = categoryModel.name.decryptWhen(crypto) { isCustom },
            icon = CategoryIcon.entries[categoryModel.icon],
            template = templateRepository.getById(categoryModel.templateId) ?: return null,
            vaultsAmount =
            if (isCustom)
                categoryModel.vaultCount
            else
                fireStore.commonTemplateVaultCountDocument(userId, id).get(network).await()
                    .getLong(VAULT_COUNT)?.toInt() ?: 0,
            isCustom = isCustom
        )
    }

    private suspend inline fun CollectionReference.getCategories(
        customCollection: Boolean,
        userId: String
    ): List<Category> = this.get(network).await().mapNotNull { category ->
        category.toCategory(isCustom = customCollection, userId)
    }

    private suspend inline fun CollectionReference.getCategoriesShort(
        customCollection: Boolean,
        userId: String
    ): List<CategoryShort> = this.get(network).await().map { category ->
        category.toCategoryShort(customCollection, userId)
    }

    private suspend fun DocumentSnapshot.toCategoryShort(
        customCollection: Boolean,
        userId: String
    ): CategoryShort {
        val categoryModel = toObject<CategoryModel>() ?: throw CategoryDoesNotExistException(
            "Category with id $id does not exist"
        )
        return CategoryShort(
            id = id,
            name = categoryModel.name.decryptWhen(crypto) { customCollection },
            icon = CategoryIcon.entries[categoryModel.icon],
            templateId = categoryModel.templateId,
            vaultsAmount =
            if (customCollection)
                categoryModel.vaultCount
            else
                fireStore.commonTemplateVaultCountDocument(userId, id).get(network).await()
                    .getLong(VAULT_COUNT)?.toInt() ?: 0,
            isCustom = customCollection
        )
    }

    override suspend fun getAllFull(): List<Category> {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        return (fireStore.categoryCollection(userId)
            .getCategories(customCollection = true, userId) + fireStore.commonCategoryCollection()
            .getCategories(
                customCollection = false, userId
            )).sortedByDescending { category -> category.vaultsAmount }
    }

    override suspend fun getAllShort(): List<CategoryShort> {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        return (fireStore.categoryCollection(userId)
            .getCategoriesShort(
                customCollection = true,
                userId
            ) + fireStore.commonCategoryCollection()
            .getCategoriesShort(
                customCollection = false, userId
            )).sortedByDescending { category -> category.vaultsAmount }
    }

    override suspend fun getShortById(id: String): CategoryShort? {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val customCategory = fireStore.categoryCollection(userId).document(id).get(network).await()
        if (customCategory.exists()) return customCategory.toCategoryShort(
            customCollection = true,
            userId
        )
        val commonCategory = fireStore.commonCategoryCollection().document(id).get(network).await()
        if (commonCategory.exists()) return commonCategory.toCategoryShort(
            customCollection = false,
            userId
        )
        return null
    }

    override suspend fun getById(id: String): Category? {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val customCategory = fireStore.categoryCollection(userId).document(id).get(network).await()
        if (customCategory.exists()) return customCategory.toCategory(isCustom = true, userId)
        val commonCategory = fireStore.commonCategoryCollection().document(id).get(network).await()
        if (commonCategory.exists()) return commonCategory.toCategory(isCustom = false, userId)
        return null
    }

    override suspend fun getAutoSaveCategoryId(): String? {
        return settingsManager.getAutoSaveCategoryId()
    }

    override suspend fun create(category: CreateCategoryRequest): Category {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        if (!templateRepository.templateExists(category.templateId))
            throw TemplateDoesNotExistException("Template with id ${category.templateId} does not exist")

        val categoryModel = CategoryModel(
            name = crypto.encryptBase64(category.name),
            icon = category.icon.ordinal,
            templateId = category.templateId,
        )

        val categoryId = UUID.randomUUID().toString()
        fireStore.categoryCollection(userId).document(categoryId).set(categoryModel)
            .awaitWhenNetworkAvailable(network)

        return Category(
            id = categoryId,
            name = category.name,
            icon = category.icon,
            template = templateRepository.getById(category.templateId)!!,
            vaultsAmount = 0,
            isCustom = true
        )
    }

    override suspend fun edit(category: EditCategoryRequest): Category {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        val document =
            fireStore.categoryCollection(userId).document(category.id).get(network).await()
        val categorySnapshot = document.toObject<CategoryModel>()
            ?: throw CategoryDoesNotExistException("Category with id ${category.id} does not exist")
        val templateId = categorySnapshot.templateId

        fireStore.categoryCollection(userId).document(category.id).update(
            mapOf("name" to crypto.encryptBase64(category.name), "icon" to category.icon.ordinal)
        ).awaitWhenNetworkAvailable(network)

        return Category(
            id = category.id,
            name = category.name,
            icon = category.icon,
            template = templateRepository.getById(templateId)!!,
            vaultsAmount = categorySnapshot.vaultCount,
            isCustom = true
        )
    }

    override suspend fun delete(id: String) {
        if (!network.isNetworkAvailable)
            throw OfflineException("Deleting a category is impossible without network as it requires a transaction")
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val vaultRefs = getVaultRefs(fireStore, id, userId)
        val fieldRefs = getFieldRefs(vaultRefs)

        fireStore.runTransaction { transaction ->
            transaction.delete(fireStore.categoryCollection(userId).document(id))
            vaultRefs.forEach { vaultRef ->
                transaction.delete(vaultRef)
            }
            fieldRefs.forEach { fieldRef ->
                transaction.delete(fieldRef)
            }
        }.await()
    }

}