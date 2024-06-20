package dev.banger.hootkey.presentation.entity

/**
 * A universal model used for any type of vault field. Depending on type,
 * you should decide whether isHidden should be used or not.
 */
data class UiEditableTemplateFieldShort(
    val name: String = "",
    val type: UiFieldType = UiFieldType.LOGIN,
    val value: String = "",
    val isFocused: Boolean = false,
    val isHidden: Boolean = true,
)
