package org.ttpss930141011.bj

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.ttpss930141011.bj.domain.valueobjects.GameRules
import org.ttpss930141011.bj.presentation.*
import org.ttpss930141011.bj.presentation.design.CasinoColorScheme

/**
 * Enumeration of main application screens
 */
enum class AppScreen {
    /** Main blackjack game screen */
    CASINO,
    /** Game settings and rules configuration */
    SETTINGS
}

/**
 * Main application composable that manages screen navigation
 * 
 * Provides the root level navigation between the casino game and settings screens,
 * applying the unified casino theme throughout the application.
 */
@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf(AppScreen.CASINO) }
    MaterialTheme(colorScheme = CasinoColorScheme) { // Apply unified casino theme
        when (currentScreen) {
            AppScreen.CASINO -> {
                CasinoGameScreen()
            }

            AppScreen.SETTINGS -> {
                SettingsScreen(
                    rules = GameRules(), // Default rules for display only
                    onRulesChange = { newRules ->
                        // Rules are now managed by SettingsScreen internally via userPreferences
                        currentScreen = AppScreen.CASINO
                    }
                )
            }
        }
    }
}