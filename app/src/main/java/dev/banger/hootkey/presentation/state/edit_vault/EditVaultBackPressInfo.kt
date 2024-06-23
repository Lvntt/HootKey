package dev.banger.hootkey.presentation.state.edit_vault

data class EditVaultSuccessInfo(
    val vaultId: String,
    val oldCategoryId: String?,
    val newCategoryId: String?
)
