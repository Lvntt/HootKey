package dev.banger.hootkey.di.modules

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.banger.hootkey.data.repository.AuthRepositoryImpl
import dev.banger.hootkey.data.repository.CategoryRepositoryImpl
import dev.banger.hootkey.data.repository.PasswordRepositoryImpl
import dev.banger.hootkey.data.repository.SettingsRepositoryImpl
import dev.banger.hootkey.data.repository.TemplateRepositoryImpl
import dev.banger.hootkey.data.repository.VaultRepositoryImpl
import dev.banger.hootkey.domain.repository.AuthRepository
import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.domain.repository.PasswordRepository
import dev.banger.hootkey.domain.repository.SettingsRepository
import dev.banger.hootkey.domain.repository.TemplateRepository
import dev.banger.hootkey.domain.repository.VaultRepository
import javax.inject.Singleton

@Module
abstract class DataModule {

    companion object {

        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

        @Provides
        @Singleton
        fun provideFireStore(): FirebaseFirestore = Firebase.firestore
    }

    @Binds
    abstract fun bindSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    abstract fun bindTemplateRepository(templateRepositoryImpl: TemplateRepositoryImpl): TemplateRepository

    @Binds
    abstract fun bindCategoryRepository(categoryRepositoryImpl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    abstract fun bindVaultRepository(vaultRepositoryImpl: VaultRepositoryImpl): VaultRepository

    @Binds
    abstract fun bindPasswordRepository(passwordRepositoryImpl: PasswordRepositoryImpl): PasswordRepository

    @Binds
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository
}