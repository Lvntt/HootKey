package dev.banger.hootkey.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.banger.hootkey.presentation.ui.screen.auth.AuthScreen

@Composable
fun AppNavigation(navHostController: NavHostController) {
    NavHost(navHostController, NavigationDestinations.Login) {
        composable<NavigationDestinations.Login> {
            AuthScreen(isLogin = true, onNavigateFromBottomHint = {
                navHostController.navigate(NavigationDestinations.Registration)
            }, onSuccess = {
                navHostController.navigate(NavigationDestinations.Dashboard)
            })
        }
        composable<NavigationDestinations.Registration> {
            AuthScreen(isLogin = false, onNavigateFromBottomHint = {
                navHostController.navigate(NavigationDestinations.Login)
            }, onSuccess = {
                navHostController.navigate(NavigationDestinations.Dashboard)
            })
        }
        composable<NavigationDestinations.Dashboard> {}
    }
}