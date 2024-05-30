package dev.banger.hootkey.di

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import org.koin.dsl.module

private fun provideFirebaseAuth() = Firebase.auth

fun provideDataModule() = module {
    single { provideFirebaseAuth() }
}