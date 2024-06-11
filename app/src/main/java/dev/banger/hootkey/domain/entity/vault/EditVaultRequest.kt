package dev.banger.hootkey.domain.entity.vault

data class EditVaultRequest(
    val vaultId: String,
    val categoryId: String,
    val name: String,
    val fieldValues: Map<Index, Value>,
)
