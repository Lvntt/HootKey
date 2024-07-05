package dev.banger.hootkey.di.viewmodel

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(
        factory: HootKeyViewModelFactory
    ): ViewModelProvider.Factory
}