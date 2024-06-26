package dev.banger.hootkey.di

import dev.banger.hootkey.service.HootKeyWorkerFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun provideAppModule() = module {

    singleOf(::HootKeyWorkerFactory)

}