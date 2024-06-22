package dev.banger.hootkey.presentation.entity

/**
 * A universal model used for any type of vault field. Depending on type,
 * you should decide which fields should be used.
 */
data class UiEditableTemplateFieldShort(
    val name: String = "",
    val type: UiFieldType = UiFieldType.LOGIN,
    val value: String = "",
    val valueMillis: Long? = null,
    val isFocused: Boolean = false,
    val isHidden: Boolean = true,
)
