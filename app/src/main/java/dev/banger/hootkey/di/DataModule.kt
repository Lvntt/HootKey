package dev.banger.hootkey.di

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dev.banger.hootkey.data.crypto.CryptoManager
import dev.banger.hootkey.data.crypto.PasswordValidator
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private fun provideFirebaseAuth() = Firebase.auth

private fun provideFirebaseFireStore() = Firebase.firestore

fun provideDataModule() = module {
    single { provideFirebaseAuth() }
    single { provideFirebaseFireStore() }
    singleOf(::CryptoManager)
    single { PasswordValidator(androidApplication()) }
}