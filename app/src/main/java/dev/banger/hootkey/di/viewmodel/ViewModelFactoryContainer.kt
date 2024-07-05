package dev.banger.hootkey.di.viewmodel

import dev.banger.hootkey.presentation.viewmodel.EditTemplateFieldViewModel
import dev.banger.hootkey.presentation.viewmodel.EditVaultViewModel
import dev.banger.hootkey.presentation.viewmodel.VaultDetailsViewModel
import dev.banger.hootkey.presentation.viewmodel.VaultsListViewModel
import javax.inject.Inject

class ViewModelFactoryContainer @Inject constructor(
    val editTemplateFieldViewModelFactory: EditTemplateFieldViewModel.Factory,
    val editVaultViewModelFactory: EditVaultViewModel.Factory,
    val vaultDetailsViewModelFactory: VaultDetailsViewModel.Factory,
    val vaultsListViewModelFactory: VaultsListViewModel.Factory
)