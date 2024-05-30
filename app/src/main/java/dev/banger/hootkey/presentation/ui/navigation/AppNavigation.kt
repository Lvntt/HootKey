package dev.banger.hootkey.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.banger.hootkey.presentation.ui.screen.auth.LoginScreen
import dev.banger.hootkey.presentation.ui.screen.auth.RegistrationScreen

@Composable
fun AppNavigation(navHostController: NavHostController) {
    NavHost(navHostController, NavigationDestinations.Login) {
        composable<NavigationDestinations.Login> {
            LoginScreen {
                navHostController.navigate(NavigationDestinations.Registration)
            }
        }
        composable<NavigationDestinations.Registration> {
            RegistrationScreen {
                navHostController.navigate(NavigationDestinations.Login)
            }
        }
    }
}