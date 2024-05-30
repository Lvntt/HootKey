package dev.banger.hootkey.presentation.ui.navigation

import kotlinx.serialization.Serializable

sealed interface NavigationDestinations {

    @Serializable
    data object Login : NavigationDestinations

    @Serializable
    data object Registration : NavigationDestinations

}