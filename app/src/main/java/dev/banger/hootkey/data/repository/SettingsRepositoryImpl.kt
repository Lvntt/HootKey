package dev.banger.hootkey.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import dev.banger.hootkey.data.datasource.SettingsManager
import dev.banger.hootkey.domain.entity.settings.UserSettings
import dev.banger.hootkey.domain.repository.SettingsRepository
import kotlinx.coroutines.tasks.await

class SettingsRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val settingsManager: SettingsManager
) : SettingsRepository {

    override fun getUserSettings(): UserSettings {
        val isBiometryOn = isBiometryOn()
        val isAutofillOn = isAutofillOn()
        val isCompromiseDetectionOn = isCompromiseDetectionOn()
        val isSyncOn = !isOffline()

        return UserSettings(
            isBiometryOn = isBiometryOn,
            isAutofillOn = isAutofillOn,
            isCompromiseDetectionOn = isCompromiseDetectionOn,
            isSyncOn = isSyncOn
        )
    }

    override fun isOffline(): Boolean = settingsManager.isOffline()

    override fun isAutofillOn(): Boolean = settingsManager.isAutofillOn()

    override fun isBiometryOn(): Boolean = settingsManager.isBiometryOn()

    override fun isCompromiseDetectionOn(): Boolean = settingsManager.isCompromiseDetectionOn()

    override suspend fun goOffline() {
        firestore.disableNetwork().await()
        settingsManager.setOffline(true)
    }

    override suspend fun goOnline() {
        firestore.enableNetwork().await()
        settingsManager.setOffline(false)
    }

    override fun enableBiometry() {
        settingsManager.setBiometry(true)
    }

    override fun disableBiometry() {
        settingsManager.setBiometry(false)
    }

    override fun enableCompromiseDetection() {
        settingsManager.setCompromiseDetection(true)
    }

    override fun disableCompromiseDetection() {
        settingsManager.setCompromiseDetection(false)
    }

}