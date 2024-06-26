package dev.banger.hootkey.di

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dev.banger.hootkey.data.crypto.CryptoManager
import dev.banger.hootkey.data.crypto.PasswordValidator
import dev.banger.hootkey.data.crypto.SharedPrefsManager
import dev.banger.hootkey.data.datasource.SettingsManager
import dev.banger.hootkey.data.network.NetworkManager
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private fun provideFirebaseAuth() = Firebase.auth

private fun provideFirebaseFireStore() = Firebase.firestore

fun provideDataModule() = module {
    single { provideFirebaseAuth() }
    single { provideFirebaseFireStore() }
    singleOf(::CryptoManager)
    singleOf(::PasswordValidator)
    single { SharedPrefsManager(androidApplication()) }
    single { SettingsManager(androidApplication().applicationContext) }
    single { NetworkManager(androidApplication(), get()) }
}