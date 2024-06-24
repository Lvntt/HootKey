package dev.banger.hootkey.di

import dev.banger.hootkey.Constants
import dev.banger.hootkey.presentation.viewmodel.AccountAuthViewModel
import dev.banger.hootkey.presentation.viewmodel.AuthViewModel
import dev.banger.hootkey.presentation.viewmodel.CategoriesViewModel
import dev.banger.hootkey.presentation.viewmodel.DashboardViewmodel
import dev.banger.hootkey.presentation.viewmodel.EditTemplateFieldViewModel
import dev.banger.hootkey.presentation.viewmodel.EditVaultViewModel
import dev.banger.hootkey.presentation.viewmodel.LaunchViewModel
import dev.banger.hootkey.presentation.viewmodel.NewCategoryViewModel
import dev.banger.hootkey.presentation.viewmodel.NewTemplateFieldViewModel
import dev.banger.hootkey.presentation.viewmodel.NewTemplateViewModel
import dev.banger.hootkey.presentation.viewmodel.NewVaultViewModel
import dev.banger.hootkey.presentation.viewmodel.PasswordGeneratorViewModel
import dev.banger.hootkey.presentation.viewmodel.SettingsViewModel
import dev.banger.hootkey.presentation.viewmodel.TemplatesViewModel
import dev.banger.hootkey.presentation.viewmodel.VaultsListViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun providePresentationModule() = module {
    viewModel(named(Constants.REGISTER)) {
        AccountAuthViewModel(get(named(Constants.REGISTER)), get(), get())
    }

    viewModel(named(Constants.LOGIN)) {
        AccountAuthViewModel(get(named(Constants.LOGIN)), get(), get())
    }

    viewModelOf(::AuthViewModel)

    viewModelOf(::LaunchViewModel)

    viewModelOf(::PasswordGeneratorViewModel)

    viewModel {
        NewTemplateViewModel(get(), Dispatchers.IO)
    }

    viewModelOf(::NewTemplateFieldViewModel)

    viewModel { parameters ->
        EditTemplateFieldViewModel(field = parameters.get())
    }

    viewModel {
        NewCategoryViewModel(get(), Dispatchers.IO)
    }

    viewModel {
        TemplatesViewModel(get(), Dispatchers.IO)
    }

    viewModel {
        CategoriesViewModel(get(), Dispatchers.IO)
    }

    viewModel {
        NewVaultViewModel(get(), get(), Dispatchers.IO)
    }

    viewModel {
        DashboardViewmodel(get(), get(), Dispatchers.IO)
    }

    viewModel { parameters ->
        EditVaultViewModel(
            vaultId = parameters.get(),
            get(), get(), Dispatchers.IO
        )
    }

    viewModel { params ->
        VaultsListViewModel(get(), Dispatchers.IO, params.get())
    }

    viewModel {
        SettingsViewModel(get(), get(), Dispatchers.IO)
    }
}