package dev.banger.hootkey.presentation.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.banger.hootkey.presentation.ui.navigation.AppNavigation
import dev.banger.hootkey.presentation.ui.navigation.NavigationDestinations
import dev.banger.hootkey.presentation.ui.theme.HootKeyTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            HootKeyTheme {
                Box(modifier = Modifier
                    .fillMaxSize()) {
                    AppNavigation(navController)
                }
            }

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination?.route
            when (currentDestination) {
                NavigationDestinations.LOGIN,
                NavigationDestinations.ACCOUNT_LOGIN,
                NavigationDestinations.ACCOUNT_REGISTRATION -> {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                }
                else -> {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                }
            }
        }
    }
}