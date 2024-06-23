package dev.banger.hootkey.presentation.state.edit_vault

sealed interface EditVaultEffect {
    data object GoToCategories : EditVaultEffect
    data object ShowVaultLoadingError : EditVaultEffect
    data object ShowCategoryLoadingError : EditVaultEffect
    data object ShowVaultEditError : EditVaultEffect
    data object HandleSuccess : EditVaultEffect
}