package dev.banger.hootkey.data.model

import dev.banger.hootkey.data.Constants.EMPTY_STRING

data class CategoryModel(
    val name: String = EMPTY_STRING,
    val icon: Int = 0,
    val templateId: String = EMPTY_STRING,
    val loginIndex: Int = -1,
    val linkIndex: Int = -1,
    val passwordIndex: Int = -1
)