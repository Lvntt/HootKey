package dev.banger.hootkey

import android.app.Application
import dev.banger.hootkey.di.provideDataModule
import dev.banger.hootkey.di.provideDomainModule
import dev.banger.hootkey.di.providePresentationModule
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HootKeyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@HootKeyApp)
            modules(provideDomainModule(), provideDataModule(), providePresentationModule())
        }
    }
}