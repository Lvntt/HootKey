package dev.banger.hootkey.presentation.intent

import android.app.Activity

sealed interface SettingsIntent {
    data class BiometryChanged(val isOn: Boolean) : SettingsIntent
    data class AutofillChanged(val isOn: Boolean, val activityContext: Activity) : SettingsIntent
    data class AutofillServiceChosen(val activityContext: Activity) : SettingsIntent
    data class SyncChanged(val isOn: Boolean) : SettingsIntent
    data object ShowLogoutDialog : SettingsIntent
    data object DismissLogoutDialog : SettingsIntent
    data object DismissDataCachingDialog : SettingsIntent
    data object Logout : SettingsIntent
}