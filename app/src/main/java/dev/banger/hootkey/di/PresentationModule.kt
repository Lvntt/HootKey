package dev.banger.hootkey.di

import dev.banger.hootkey.Constants
import org.koin.dsl.module
import dev.banger.hootkey.presentation.viewmodel.AccountAuthViewModel
import dev.banger.hootkey.presentation.viewmodel.AuthViewModel
import dev.banger.hootkey.presentation.viewmodel.EditTemplateFieldViewModel
import dev.banger.hootkey.presentation.viewmodel.LaunchViewModel
import dev.banger.hootkey.presentation.viewmodel.NewTemplateFieldViewModel
import dev.banger.hootkey.presentation.viewmodel.NewTemplateViewModel
import dev.banger.hootkey.presentation.viewmodel.PasswordGeneratorViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.scopedOf
import org.koin.core.qualifier.named

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
}