package dev.banger.hootkey.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dev.banger.hootkey.presentation.ui.navigation.AppNavigation
import dev.banger.hootkey.presentation.ui.theme.HootKeyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            HootKeyTheme {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()) {
                    AppNavigation(navController)
                }
            }
        }
    }
}