package dev.banger.hootkey.data.datasource

import android.content.Context
import android.view.autofill.AutofillManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dev.banger.hootkey.Constants.IS_AUTOFILL_ON_KEY
import dev.banger.hootkey.Constants.IS_BIOMETRY_ON_KEY
import dev.banger.hootkey.Constants.IS_COMPROMISE_DETECTION_ON_KEY
import dev.banger.hootkey.Constants.IS_OFFLINE_KEY
import dev.banger.hootkey.Constants.SETTINGS_PREFERENCES_KEY

class SettingsManager(private val context: Context) {

    private val masterKeyAlias = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        SETTINGS_PREFERENCES_KEY,
        masterKeyAlias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun isOffline() = prefs.getBoolean(IS_OFFLINE_KEY, false)

    fun isAutofillOn(): Boolean {
        val autofillManager = context.getSystemService(AutofillManager::class.java)
        return autofillManager != null && autofillManager.hasEnabledAutofillServices()
    }

    fun isBiometryOn() = prefs.getBoolean(IS_BIOMETRY_ON_KEY, true)

    fun isCompromiseDetectionOn() = prefs.getBoolean(IS_COMPROMISE_DETECTION_ON_KEY, false)

    fun setOffline(isOffline: Boolean) {
        prefs.edit()
            .putBoolean(IS_OFFLINE_KEY, isOffline)
            .apply()
    }

    fun setBiometry(isOn: Boolean) {
        prefs.edit()
            .putBoolean(IS_BIOMETRY_ON_KEY, isOn)
            .apply()
    }

    fun setCompromiseDetection(isOn: Boolean) {
        prefs.edit()
            .putBoolean(IS_COMPROMISE_DETECTION_ON_KEY, isOn)
            .apply()
    }

}