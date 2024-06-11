package dev.banger.hootkey.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.banger.hootkey.presentation.ui.screen.auth.AccountAuthScreen
import dev.banger.hootkey.presentation.ui.screen.auth.AuthScreen
import dev.banger.hootkey.presentation.ui.screen.launch.LaunchScreen

@Composable
fun AppNavigation(navHostController: NavHostController) {
    NavHost(navHostController, NavigationDestinations.Launch) {
        composable<NavigationDestinations.Launch> {
            LaunchScreen(onNavigateToAccountLogin = {
                navHostController.navigate(NavigationDestinations.AccountLogin)
            }, onNavigateToLogin = {
                navHostController.navigate(NavigationDestinations.Login)
            })
        }
        composable<NavigationDestinations.AccountLogin> {
            AccountAuthScreen(isLogin = true, onNavigateFromBottomHint = {
                navHostController.navigate(NavigationDestinations.AccountRegistration)
            }, onSuccess = {
                navHostController.navigate(NavigationDestinations.Dashboard)
            })
        }
        composable<NavigationDestinations.AccountRegistration> {
            AccountAuthScreen(isLogin = false, onNavigateFromBottomHint = {
                navHostController.navigate(NavigationDestinations.AccountLogin)
            }, onSuccess = {
                navHostController.navigate(NavigationDestinations.Dashboard)
            })
        }
        composable<NavigationDestinations.Login> {
            AuthScreen(onSuccess = {
                navHostController.navigate(NavigationDestinations.Dashboard)
            })
        }
        composable<NavigationDestinations.Dashboard> {}
    }
}