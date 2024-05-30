package dev.banger.hootkey.di

import dev.banger.hootkey.Constants
import org.koin.dsl.module
import dev.banger.hootkey.presentation.viewmodel.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named

fun providePresentationModule() = module {
    viewModel(named(Constants.REGISTER)) {
        AuthViewModel(get(named(Constants.REGISTER)), get(), get())
    }

    viewModel(named(Constants.LOGIN)) {
        AuthViewModel(get(named(Constants.LOGIN)), get(), get())
    }
}