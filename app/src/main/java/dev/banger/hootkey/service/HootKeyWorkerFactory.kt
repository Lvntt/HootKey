package dev.banger.hootkey.service

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.domain.repository.SettingsRepository
import dev.banger.hootkey.domain.repository.TemplateRepository
import dev.banger.hootkey.domain.repository.VaultRepository
import javax.inject.Inject

class HootKeyWorkerFactory @Inject constructor(
    private val templateRepository: TemplateRepository,
    private val categoryRepository: CategoryRepository,
    private val vaultRepository: VaultRepository,
    private val settingsRepository: SettingsRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            InitialCachingWorker::class.java.name -> {
                InitialCachingWorker(
                    appContext,
                    workerParameters,
                    templateRepository,
                    categoryRepository,
                    vaultRepository,
                    settingsRepository
                )
            }
            else -> null
        }
    }
}