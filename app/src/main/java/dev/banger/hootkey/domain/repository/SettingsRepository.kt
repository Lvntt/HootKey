package dev.banger.hootkey.domain.repository

interface SettingsRepository {

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