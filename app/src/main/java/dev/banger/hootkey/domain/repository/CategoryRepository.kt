package dev.banger.hootkey.domain.repository

import dev.banger.hootkey.domain.entity.category.Category
import dev.banger.hootkey.domain.entity.category.CreateCategoryRequest
import dev.banger.hootkey.domain.entity.category.EditCategoryRequest

interface CategoryRepository {

    suspend fun getAll(): List<Category>

    suspend fun create(category: CreateCategoryRequest): Category

    suspend fun edit(category: EditCategoryRequest): Category

    suspend fun delete(id: String)

}