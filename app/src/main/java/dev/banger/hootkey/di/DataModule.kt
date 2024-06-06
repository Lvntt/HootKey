package dev.banger.hootkey.di

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dev.banger.hootkey.data.crypto.CryptoManager
import dev.banger.hootkey.data.crypto.PasswordValidator
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private fun provideFirebaseAuth() = Firebase.auth

fun provideDataModule() = module {
    single { provideFirebaseAuth() }
    singleOf(::CryptoManager)
    single { PasswordValidator(androidApplication()) }
}