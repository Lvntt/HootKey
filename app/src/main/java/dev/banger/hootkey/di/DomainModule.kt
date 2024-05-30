package dev.banger.hootkey.di

import android.util.Patterns
import dev.banger.hootkey.data.repository.AuthRepositoryImpl
import dev.banger.hootkey.domain.repository.AuthRepository
import dev.banger.hootkey.domain.usecase.RegisterUseCase
import dev.banger.hootkey.domain.usecase.LoginUseCase
import dev.banger.hootkey.domain.usecase.CheckUserLoggedInUseCase
import dev.banger.hootkey.domain.usecase.ValidateEmailUseCase
import dev.banger.hootkey.domain.usecase.ValidatePasswordUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun provideDomainModule() = module {
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class

    factoryOf(::RegisterUseCase)

    factoryOf(::LoginUseCase)

    factoryOf(::CheckUserLoggedInUseCase)


    factoryOf(::ValidatePasswordUseCase)

    factory {
        ValidateEmailUseCase {
            Patterns.EMAIL_ADDRESS.matcher(
                it
            ).matches()
        }
    }

}