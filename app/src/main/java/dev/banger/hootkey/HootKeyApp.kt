package dev.banger.hootkey

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import dev.banger.hootkey.di.components.AppComponent
import dev.banger.hootkey.di.components.DaggerAppComponent
import dev.banger.hootkey.service.HootKeyWorkerFactory
import javax.inject.Inject

class HootKeyApp : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this, this)
    }

    @Inject
    lateinit var hootKeyWorkerFactory: HootKeyWorkerFactory

    override fun onCreate() {
        super.onCreate()

        appComponent.workerComponent().create().inject(this)

        initWorkManager()
    }

    private fun initWorkManager() {
        val workManagerConfiguration = Configuration.Builder()
            .setWorkerFactory(hootKeyWorkerFactory)
            .build()
        WorkManager.initialize(this, workManagerConfiguration)
    }
}