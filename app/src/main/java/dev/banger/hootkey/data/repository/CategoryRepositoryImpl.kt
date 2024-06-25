package dev.banger.hootkey.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dev.banger.hootkey.data.crypto.CryptoManager
import dev.banger.hootkey.data.datasource.SettingsManager
import dev.banger.hootkey.data.model.CategoryModel
import dev.banger.hootkey.domain.entity.auth.exception.UnauthorizedException
import dev.banger.hootkey.domain.entity.category.Category
import dev.banger.hootkey.domain.entity.category.CategoryDoesNotExistException
import dev.banger.hootkey.domain.entity.category.CategoryIcon
import dev.banger.hootkey.domain.entity.category.CategoryShort
import dev.banger.hootkey.domain.entity.category.CreateCategoryRequest
import dev.banger.hootkey.domain.entity.category.EditCategoryRequest
import dev.banger.hootkey.domain.entity.template.FieldType
import dev.banger.hootkey.domain.entity.template.TemplateDoesNotExistException
import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.domain.repository.TemplateRepository
import kotlinx.coroutines.tasks.await

class CategoryRepositoryImpl(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val templateRepository: TemplateRepository,
    private val crypto: CryptoManager,
    private val settingsManager: SettingsManager
) : CategoryRepository {

    private suspend fun getVaultCountInCategory(categoryId: String): Int {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        return fireStore.vaultCollection(userId)
            .whereEqualTo("categoryId", categoryId).count().get(
                AggregateSource.SERVER
            ).await().count.toInt()
    }

    private suspend inline fun DocumentSnapshot.toCategory(isCustom: Boolean, includeVaultCount: Boolean = true): Category {
        val categoryModel = toObject<CategoryModel>()
            ?: throw CategoryDoesNotExistException("Category with id $id does not exist")
        return Category(
            id = id,
            name = categoryModel.name.decryptWhen(crypto) { isCustom },
            icon = CategoryIcon.entries[categoryModel.icon],
            template = templateRepository.getById(categoryModel.templateId)!!,
            vaultsAmount = if (includeVaultCount) getVaultCountInCategory(id) else 0,
            isCustom = isCustom
        )
    }

    private suspend inline fun CollectionReference.getCategories(
        customCollection: Boolean
    ): List<Category> = this.get().await().map { category ->
        category.toCategory(isCustom = customCollection)
    }

    private suspend inline fun CollectionReference.getCategoriesShort(
        customCollection: Boolean
    ): List<CategoryShort> = this.get().await().map { category ->
        category.toCategoryShort(customCollection)
    }

    private suspend fun DocumentSnapshot.toCategoryShort(
        customCollection: Boolean
    ): CategoryShort {
        val categoryModel = toObject<CategoryModel>() ?: throw CategoryDoesNotExistException(
            "Category with id $id does not exist"
        )
        return CategoryShort(
            id = id,
            name = categoryModel.name.decryptWhen(crypto) { customCollection },
            icon = CategoryIcon.entries[categoryModel.icon],
            templateId = categoryModel.templateId,
            vaultsAmount = getVaultCountInCategory(id),
            isCustom = customCollection
        )
    }

    override suspend fun getAllFull(): List<Category> {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        return (fireStore.categoryCollection(userId)
            .getCategories(customCollection = true) + fireStore.commonCategoryCollection()
            .getCategories(
                customCollection = false
            )).sortedByDescending { category -> category.vaultsAmount }
    }

    override suspend fun getAllShort(): List<CategoryShort> {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        return (fireStore.categoryCollection(userId)
            .getCategoriesShort(customCollection = true) + fireStore.commonCategoryCollection()
            .getCategoriesShort(
                customCollection = false
            )).sortedByDescending { category -> category.vaultsAmount }
    }

    override suspend fun getShortById(id: String): CategoryShort? {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val customCategory = fireStore.categoryCollection(userId).document(id).get().await()
        if (customCategory.exists()) return customCategory.toCategoryShort(customCollection = true)
        val commonCategory = fireStore.commonCategoryCollection().document(id).get().await()
        if (commonCategory.exists()) return commonCategory.toCategoryShort(customCollection = false)
        return null
    }

    override suspend fun getById(id: String, includeVaultCount: Boolean): Category? {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val customCategory = fireStore.categoryCollection(userId).document(id).get().await()
        if (customCategory.exists()) return customCategory.toCategory(isCustom = true, includeVaultCount = includeVaultCount)
        val commonCategory = fireStore.commonCategoryCollection().document(id).get().await()
        if (commonCategory.exists()) return commonCategory.toCategory(isCustom = false, includeVaultCount = includeVaultCount)
        return null
    }

    override suspend fun getAutoSaveCategoryId(): String? {
        return settingsManager.getAutoSaveCategoryId()
    }

    override suspend fun create(category: CreateCategoryRequest): Category {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        if (!templateRepository.templateExists(category.templateId))
            throw TemplateDoesNotExistException("Template with id ${category.templateId} does not exist")
        val template = templateRepository.getById(category.templateId)!!
        val loginIndex =
            template.fields.firstOrNull { field -> field.type == FieldType.LOGIN }?.index ?: -1
        val linkIndex =
            template.fields.firstOrNull { field -> field.type == FieldType.LINK }?.index ?: -1
        val passwordIndex =
            template.fields.firstOrNull { field -> field.type == FieldType.PASSWORD }?.index ?: -1

        val categoryModel = CategoryModel(
            name = crypto.encryptBase64(category.name),
            icon = category.icon.ordinal,
            templateId = category.templateId,
            loginIndex = loginIndex,
            linkIndex = linkIndex,
            passwordIndex = passwordIndex
        )
        val categoryId = fireStore.categoryCollection(userId).add(categoryModel).await().id

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
        val document = fireStore.categoryCollection(userId).document(category.id).get().await()
        if (!document.exists()) throw CategoryDoesNotExistException("Category with id ${category.id} does not exist")
        val templateId = document.toObject<CategoryModel>()?.templateId
            ?: throw CategoryDoesNotExistException("Category with id ${category.id} does not exist")

        fireStore.categoryCollection(userId).document(category.id).update(
            mapOf("name" to crypto.encryptBase64(category.name), "icon" to category.icon.ordinal)
        ).await()

        return Category(
            id = category.id,
            name = category.name,
            icon = category.icon,
            template = templateRepository.getById(templateId)!!,
            vaultsAmount = getVaultCountInCategory(category.id),
            isCustom = true
        )
    }

    override suspend fun delete(id: String) {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val vaultRefs = getVaultRefs(fireStore, id, userId)
        val fieldRefs = getFieldRefs(vaultRefs)

        //TODO add specific exception when internet is unavailable
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