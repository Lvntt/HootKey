package dev.banger.hootkey.domain.repository

import dev.banger.hootkey.domain.entity.category.Category
import dev.banger.hootkey.domain.entity.category.CreateCategoryRequest
import dev.banger.hootkey.domain.entity.category.EditCategoryRequest

interface CategoryRepository {

    fun getAll(): List<Category>

    fun create(category: CreateCategoryRequest): Category

    fun edit(category: EditCategoryRequest): Category

    fun delete(id: String)

}