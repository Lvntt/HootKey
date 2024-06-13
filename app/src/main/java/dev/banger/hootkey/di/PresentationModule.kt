package dev.banger.hootkey.di

import dev.banger.hootkey.Constants
import org.koin.dsl.module
import dev.banger.hootkey.presentation.viewmodel.AccountAuthViewModel
import dev.banger.hootkey.presentation.viewmodel.AuthViewModel
import dev.banger.hootkey.presentation.viewmodel.LaunchViewModel
import dev.banger.hootkey.presentation.viewmodel.PasswordGeneratorViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
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
}