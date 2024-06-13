package dev.banger.hootkey.data.model

import dev.banger.hootkey.data.Constants.EMPTY_STRING

data class FieldModel(
    val name: String = EMPTY_STRING,
    val type: Int = 0
)