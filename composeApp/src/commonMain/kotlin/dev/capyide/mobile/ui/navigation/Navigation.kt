package dev.capyide.mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.capyide.mobile.core.config.SettingsRepository
import dev.capyide.mobile.core.update.UpdateChecker
import dev.capyide.mobile.ui.editor.EditorScreen
import dev.capyide.mobile.ui.settings.SettingsScreen
import dev.capyide.mobile.ui.settings.SettingsViewModel
import org.koin.compose.koinInject

sealed class Screen(val route: String) {
    data object Editor : Screen("editor")
    data object Settings : Screen("settings")
}

@Composable
fun CapyNavHost(
    navController: NavHostController,
    onSubscribeClick: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Editor.route
    ) {
        composable(Screen.Editor.route) {
            EditorScreen(
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onSubscribeClick = onSubscribeClick
            )
        }
        composable(Screen.Settings.route) {
            val settingsRepository: SettingsRepository = koinInject()
            val updateChecker: UpdateChecker = koinInject()
            val viewModel = SettingsViewModel(settingsRepository, updateChecker)
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}
