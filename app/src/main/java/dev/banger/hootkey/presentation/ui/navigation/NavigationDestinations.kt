package dev.banger.hootkey.presentation.ui.navigation

import kotlinx.serialization.Serializable

sealed interface NavigationDestinations {

    @Serializable
    data object AccountLogin : NavigationDestinations

    @Serializable
    data object AccountRegistration : NavigationDestinations

    @Serializable
    data object Dashboard : NavigationDestinations

    @Serializable
    data object Login : NavigationDestinations

}