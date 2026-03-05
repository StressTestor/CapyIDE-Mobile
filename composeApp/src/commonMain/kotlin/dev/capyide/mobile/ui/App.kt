package dev.capyide.mobile.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import dev.capyide.mobile.ui.navigation.CapyNavHost
import dev.capyide.mobile.ui.theme.CapyIDEMobileTheme

@Composable
fun App(onSubscribeClick: () -> Unit = {}) {
    CapyIDEMobileTheme {
        val navController = rememberNavController()
        CapyNavHost(navController = navController, onSubscribeClick = onSubscribeClick)
    }
}
