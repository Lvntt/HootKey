package dev.banger.hootkey.presentation.entity

data class UiField(
    val name: String = "",
    val type: UiFieldType = UiFieldType.LOGIN,
    val value: String = "",
    val valueMillis: Long? = null,
    val isHidden: Boolean = true,
)
