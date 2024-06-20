package dev.banger.hootkey.presentation.entity

import dev.banger.hootkey.domain.entity.vault.Index
import dev.banger.hootkey.domain.entity.vault.Value

data class UiTemplate(
    val id: String,
    val name: String,
    val isCustom: Boolean,
    val fields: List<UiTemplateFieldShort>,
    val fieldValues: Map<Index, Value>
)