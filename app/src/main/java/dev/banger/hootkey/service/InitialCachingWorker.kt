package dev.banger.hootkey.service

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.domain.repository.SettingsRepository
import dev.banger.hootkey.domain.repository.TemplateRepository
import dev.banger.hootkey.domain.repository.VaultRepository

class InitialCachingWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val templateRepository: TemplateRepository,
    private val categoryRepository: CategoryRepository,
    private val vaultRepository: VaultRepository,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, workerParams) {

    private companion object {
        const val TAG = "InitialCachingWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            templateRepository.getAllFull()
            categoryRepository.getAllFull()
            // TODO vaultRepository.getAllFull()
            settingsRepository.setDataCached()
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            Result.retry()
        }
    }

}