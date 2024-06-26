package dev.banger.hootkey.domain.repository

import dev.banger.hootkey.domain.entity.settings.UserSettings

interface SettingsRepository {

    fun getUserSettings(): UserSettings

    fun clearUserSettings()

    fun isOffline(): Boolean

    fun isAutofillOn(): Boolean

    fun isBiometryOn(): Boolean

    fun isCompromiseDetectionOn(): Boolean

    suspend fun goOffline()

    suspend fun goOnline()

    fun enableBiometry()

    fun disableBiometry()

    fun enableCompromiseDetection()

    fun disableCompromiseDetection()

    fun isDataCached(): Boolean

    fun setDataCached()

}