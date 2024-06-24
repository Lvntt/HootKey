package dev.banger.hootkey.domain.entity.settings

data class UserSettings(
    val isBiometricsOn: Boolean,
    val isAutofillOn: Boolean,
    val isSyncOn: Boolean
)
