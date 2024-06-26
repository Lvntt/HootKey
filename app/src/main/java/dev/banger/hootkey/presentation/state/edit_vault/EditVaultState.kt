package dev.banger.hootkey.presentation.state.edit_vault

import dev.banger.hootkey.presentation.entity.UiCategory

data class EditVaultState(
    val vaultId: String,
    val category: UiCategory? = null,
    val name: String = "",
    val isVaultLoading: Boolean = false,
    val isCategoryLoading: Boolean = false,
    val isEditLoading: Boolean = false,
    val generatingPasswordForIndex: Int? = null,
    val pickingDateForIndex: Int? = null,
    val oldCategoryId: String = "",
) {
    val isEditAllowed = name.isNotBlank() && !isVaultLoading && !isCategoryLoading && !isEditLoading
}