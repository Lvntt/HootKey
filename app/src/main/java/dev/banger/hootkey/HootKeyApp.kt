package dev.banger.hootkey

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import dev.banger.hootkey.di.provideAppModule
import dev.banger.hootkey.di.provideDataModule
import dev.banger.hootkey.di.provideDomainModule
import dev.banger.hootkey.di.providePresentationModule
import dev.banger.hootkey.service.HootKeyWorkerFactory
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HootKeyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@HootKeyApp)
            modules(
                provideAppModule(),
                provideDomainModule(),
                provideDataModule(),
                providePresentationModule()
            )
        }

        initWorkManager()
    }

    private fun initWorkManager() {
        val hootKeyWorkerFactory by inject<HootKeyWorkerFactory>()
        val workManagerConfiguration = Configuration.Builder()
            .setWorkerFactory(hootKeyWorkerFactory)
            .build()
        WorkManager.initialize(this, workManagerConfiguration)
    }
}