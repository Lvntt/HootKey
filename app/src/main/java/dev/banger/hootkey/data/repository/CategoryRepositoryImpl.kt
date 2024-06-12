package dev.banger.hootkey.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dev.banger.hootkey.data.Constants.COMMON
import dev.banger.hootkey.data.model.CategoryModel
import dev.banger.hootkey.domain.entity.auth.exception.UnauthorizedException
import dev.banger.hootkey.domain.entity.category.Category
import dev.banger.hootkey.domain.entity.category.CategoryDoesNotExistException
import dev.banger.hootkey.domain.entity.category.CategoryIcon
import dev.banger.hootkey.domain.entity.category.CreateCategoryRequest
import dev.banger.hootkey.domain.entity.category.EditCategoryRequest
import dev.banger.hootkey.domain.entity.template.TemplateDoesNotExistException
import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.domain.repository.TemplateRepository
import dev.banger.hootkey.domain.repository.VaultRepository
import kotlinx.coroutines.tasks.await

class CategoryRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val templateRepository: TemplateRepository,
    private val vaultRepository: VaultRepository
) : CategoryRepository {

    private companion object {
        const val CATEGORIES = "categories"
    }

    private fun categoryCollection(userId: String) =
        firestore.collection(userId).document(CATEGORIES).collection(CATEGORIES)

    private fun commonCategoryCollection() =
        firestore.collection(COMMON).document(CATEGORIES).collection(CATEGORIES)

    private suspend inline fun CollectionReference.getCategories(
        customCollection: Boolean
    ): List<Category> = this.get().await().map { category ->
        val id = category.id
        val categoryModel = category.toObject<CategoryModel>()
        Category(
            id = id,
            name = categoryModel.name,
            icon = CategoryIcon.entries[categoryModel.icon],
            template = templateRepository.getById(categoryModel.templateId)!!,
            vaultsAmount = vaultRepository.getCountInCategory(id),
            isCustom = customCollection
        )
    }

    override suspend fun getAll(): List<Category> {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        return categoryCollection(userId).getCategories(customCollection = true) +
                commonCategoryCollection().getCategories(customCollection = false)
    }

    override suspend fun create(category: CreateCategoryRequest): Category {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        if (!templateRepository.templateExists(category.templateId)) throw TemplateDoesNotExistException(
            "Template with id ${category.templateId} does not exist"
        )

        val categoryModel = CategoryModel(
            name = category.name, icon = category.icon.ordinal, templateId = category.templateId
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
                "name" to category.name, "icon" to category.icon.ordinal
            )
        ).await()

        return Category(
            id = category.id,
            name = category.name,
            icon = category.icon,
            template = templateRepository.getById(templateId)!!,
            vaultsAmount = vaultRepository.getCountInCategory(category.id),
            isCustom = true
        )
    }

    override suspend fun delete(id: String) {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        categoryCollection(userId).document(id).delete().await()
    }
}