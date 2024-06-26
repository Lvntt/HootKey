package dev.banger.hootkey.domain.entity.settings

data class UserSettings(
    val isBiometryOn: Boolean,
    val isAutofillOn: Boolean,
    val isCompromiseDetectionOn: Boolean,
    val isSyncOn: Boolean
)
