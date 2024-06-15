package dev.banger.hootkey.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dev.banger.hootkey.data.Constants.CATEGORIES
import dev.banger.hootkey.data.Constants.COMMON
import dev.banger.hootkey.data.Constants.VAULTS
import dev.banger.hootkey.data.crypto.CryptoManager
import dev.banger.hootkey.data.model.CategoryModel
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

class CategoryRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val templateRepository: TemplateRepository,
    private val crypto: CryptoManager
) : CategoryRepository {

    private fun categoryCollection(userId: String) =
        firestore.collection(userId).document(CATEGORIES).collection(CATEGORIES)

    private fun commonCategoryCollection() =
        firestore.collection(COMMON).document(CATEGORIES).collection(CATEGORIES)

    private suspend fun getVaultCountInCategory(categoryId: String): Int {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        return firestore.collection(userId).document(VAULTS).collection(VAULTS)
            .whereEqualTo("categoryId", categoryId).count().get(
                AggregateSource.SERVER
            ).await().count.toInt()
    }

    private suspend inline fun DocumentSnapshot.toCategory(isCustom: Boolean): Category {
        val categoryModel = toObject<CategoryModel>()
            ?: throw CategoryDoesNotExistException("Category with id $id does not exist")
        return Category(
            id = id,
            name = categoryModel.name.decryptIfCustom(isCustom),
            icon = CategoryIcon.entries[categoryModel.icon],
            template = templateRepository.getById(categoryModel.templateId)!!,
            vaultsAmount = getVaultCountInCategory(id),
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
        val id = category.id
        val categoryModel = category.toObject<CategoryModel>()
        CategoryShort(
            id = id,
            name = categoryModel.name.decryptIfCustom(customCollection),
            icon = CategoryIcon.entries[categoryModel.icon],
            templateId = categoryModel.templateId,
            vaultsAmount = getVaultCountInCategory(id),
            isCustom = customCollection
        )
    }

    override suspend fun getAllFull(): List<Category> {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        return categoryCollection(userId).getCategories(customCollection = true) + commonCategoryCollection().getCategories(
            customCollection = false
        )
    }

    override suspend fun getAllShort(): List<CategoryShort> {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        return categoryCollection(userId).getCategoriesShort(customCollection = true) + commonCategoryCollection().getCategoriesShort(
            customCollection = false
        )
    }

    override suspend fun getById(id: String): Category? {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val customCategory = categoryCollection(userId).document(id).get().await()
        if (customCategory.exists()) return customCategory.toCategory(isCustom = true)
        val commonCategory = commonCategoryCollection().document(id).get().await()
        if (commonCategory.exists()) return commonCategory.toCategory(isCustom = false)
        return null
    }

    override suspend fun create(category: CreateCategoryRequest): Category {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        if (!templateRepository.templateExists(category.templateId)) throw TemplateDoesNotExistException(
            "Template with id ${category.templateId} does not exist"
        )

        val categoryModel = CategoryModel(
            name = crypto.encryptBase64(category.name),
            icon = category.icon.ordinal,
            templateId = category.templateId
        )
        val categoryId = categoryCollection(userId).add(categoryModel).await().id

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
        val document = categoryCollection(userId).document(category.id).get().await()
        if (!document.exists()) throw CategoryDoesNotExistException("Category with id ${category.id} does not exist")
        val templateId = document.toObject<CategoryModel>()?.templateId
            ?: throw CategoryDoesNotExistException("Category with id ${category.id} does not exist")

        categoryCollection(userId).document(category.id).update(
            mapOf(
                "name" to crypto.encryptBase64(category.name), "icon" to category.icon.ordinal
            )
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

        val vaultRefs = getVaultRefs(firestore, id, userId)
        val fieldRefs = getFieldRefs(vaultRefs)

        //TODO add specific exception when internet is unavailable
        firestore.runTransaction { transaction ->
            transaction.delete(categoryCollection(userId).document(id))
            vaultRefs.forEach { vaultRef ->
                transaction.delete(vaultRef)
            }
            fieldRefs.forEach { fieldRef ->
                transaction.delete(fieldRef)
            }
        }.await()
    }

    private fun String.decryptIfCustom(isCustom: Boolean) =
        if (isCustom) crypto.decryptBase64(this) else this

}