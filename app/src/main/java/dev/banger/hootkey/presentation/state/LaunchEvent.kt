package dev.banger.hootkey.presentation.state

sealed interface LaunchEvent {

    data object Unauthorized : LaunchEvent

    data object Authorized : LaunchEvent

}