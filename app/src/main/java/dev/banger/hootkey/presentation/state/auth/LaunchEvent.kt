package dev.banger.hootkey.presentation.state.auth

sealed interface LaunchEvent {

    data object Unauthorized : LaunchEvent

    data object Authorized : LaunchEvent

}