package dev.banger.hootkey.domain.repository

import dev.banger.hootkey.domain.entity.settings.UserSettings

interface SettingsRepository {

    suspend fun getUserSettings(): UserSettings

    suspend fun isOffline(): Boolean

    suspend fun isUsingBiometry(): Boolean

    suspend fun isCompromisedPasswordDetectionEnabled(): Boolean

    suspend fun goOffline()

    suspend fun goOnline()

    suspend fun enableBiometry()

    suspend fun disableBiometry()

    suspend fun enableCompromisedPasswordsDetection()

    suspend fun disableCompromisedPasswordsDetection()

}