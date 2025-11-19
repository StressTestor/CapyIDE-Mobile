package dev.capyide.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import dev.capyide.mobile.ui.navigation.CapyNavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dev.capyide.mobile.ui.theme.CapyIDEMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            CapyIDEMobileTheme {
                val navController = rememberNavController()
                CapyNavHost(navController = navController)
            }
        }
    }
}