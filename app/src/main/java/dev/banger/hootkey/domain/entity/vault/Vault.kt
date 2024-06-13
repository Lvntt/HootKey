package dev.banger.hootkey.domain.entity.vault

import dev.banger.hootkey.domain.entity.category.VaultCategoryInfo

data class Vault(
    val id: String,
    val name: String,
    val category: VaultCategoryInfo,
    val isFavourite: Boolean,
    val lastEditTimeMillis: Long,
    val lastViewedTimeMillis: Long,
    val fieldValues: Map<Index, FieldValue>,
)