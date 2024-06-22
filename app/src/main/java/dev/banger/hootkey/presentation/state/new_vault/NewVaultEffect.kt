package dev.banger.hootkey.presentation.state.new_vault

sealed interface NewVaultEffect {
    data object GoToCategories : NewVaultEffect
    data object ShowCategoryLoadingError : NewVaultEffect
    data object ShowVaultCreationError : NewVaultEffect
    data object HandleSuccess : NewVaultEffect
}