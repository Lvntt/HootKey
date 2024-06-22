package dev.banger.hootkey.presentation.entity

import dev.banger.hootkey.domain.entity.vault.Index
import dev.banger.hootkey.domain.entity.vault.Value

data class UiEditableTemplate(
    val id: String,
    val name: String,
    val isCustom: Boolean,
    val fields: List<UiEditableTemplateFieldShort>,
    val fieldValues: Map<Index, Value>,
)