package dev.banger.hootkey.domain.entity.vault

import dev.banger.hootkey.domain.entity.template.FieldType

data class FieldValue(
    val name: String,
    val type: FieldType,
    val value: String
)
