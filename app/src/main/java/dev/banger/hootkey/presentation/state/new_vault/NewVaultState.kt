package dev.banger.hootkey.presentation.state.new_vault

import dev.banger.hootkey.presentation.entity.UiCategory

data class NewVaultState(
    val category: UiCategory? = null,
    val name: String = "",
    val isCategoryLoading: Boolean = false,
    val isCreationLoading: Boolean = false
) {
    val isCreationAllowed = name.isNotBlank() && !isCategoryLoading && !isCreationLoading
}
