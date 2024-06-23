package dev.banger.hootkey.domain.repository

import dev.banger.hootkey.domain.entity.category.Category
import dev.banger.hootkey.domain.entity.category.CategoryShort
import dev.banger.hootkey.domain.entity.category.CreateCategoryRequest
import dev.banger.hootkey.domain.entity.category.EditCategoryRequest

interface CategoryRepository {

    /**
     * Returns all categories including templates, may be slow
     * Use getAllShort and/or getById for specific category where possible instead
     */
    suspend fun getAllFull(): List<Category>

    suspend fun getAllShort(): List<CategoryShort>

    suspend fun getShortById(id: String): CategoryShort?

    suspend fun getById(id: String): Category?

    suspend fun create(category: CreateCategoryRequest): Category

    suspend fun edit(category: EditCategoryRequest): Category

    suspend fun delete(id: String)

}