package dev.banger.hootkey.presentation.state.auth

sealed interface BiometricError {
    data object BiometricNotAvailable : BiometricError

    data object BiometricNotEnrolled : BiometricError
}