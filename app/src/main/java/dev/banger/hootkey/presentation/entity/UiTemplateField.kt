package dev.banger.hootkey.presentation.entity

import java.util.UUID

data class UiTemplateField(
    val uuid: UUID,
    val name: String,
    val type: UiFieldType,
)
