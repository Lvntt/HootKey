package dev.banger.hootkey.di

import android.util.Patterns
import dev.banger.hootkey.data.repository.AuthRepositoryImpl
import dev.banger.hootkey.domain.repository.AuthRepository
import dev.banger.hootkey.domain.usecase.AuthUseCase
import dev.banger.hootkey.domain.usecase.RegisterUseCase
import dev.banger.hootkey.domain.usecase.LoginUseCase
import dev.banger.hootkey.domain.usecase.CheckUserLoggedInUseCase
import dev.banger.hootkey.domain.usecase.ValidateEmailUseCase
import dev.banger.hootkey.domain.usecase.ValidatePasswordUseCase
import dev.banger.hootkey.Constants
import dev.banger.hootkey.data.repository.CategoryRepositoryImpl
import dev.banger.hootkey.data.repository.TemplateRepositoryImpl
import dev.banger.hootkey.data.repository.VaultRepositoryImpl
import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.domain.repository.TemplateRepository
import dev.banger.hootkey.domain.repository.VaultRepository
import dev.banger.hootkey.data.repository.PasswordRepositoryImpl
import dev.banger.hootkey.data.repository.SettingsRepositoryImpl
import dev.banger.hootkey.domain.repository.PasswordRepository
import dev.banger.hootkey.domain.repository.SettingsRepository
import dev.banger.hootkey.domain.usecase.CheckPasswordUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun provideDomainModule() = module {
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::TemplateRepositoryImpl) bind TemplateRepository::class
    singleOf(::CategoryRepositoryImpl) bind CategoryRepository::class
    singleOf(::VaultRepositoryImpl) bind VaultRepository::class
    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class

    factoryOf(::RegisterUseCase) {
        named(Constants.REGISTER)
        bind<AuthUseCase>()
    }

    factoryOf(::LoginUseCase) {
        named(Constants.LOGIN)
        bind<AuthUseCase>()
    }

    factoryOf(::CheckUserLoggedInUseCase)

    factoryOf(::ValidatePasswordUseCase)

    factory {
        ValidateEmailUseCase {
            Patterns.EMAIL_ADDRESS.matcher(
                it
            ).matches()
        }
    }

    factoryOf(::CheckPasswordUseCase)

    singleOf(::PasswordRepositoryImpl) bind PasswordRepository::class

}