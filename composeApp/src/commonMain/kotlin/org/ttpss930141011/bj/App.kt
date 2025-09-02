package org.ttpss930141011.bj

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.ttpss930141011.bj.domain.GameRules
import org.ttpss930141011.bj.presentation.*

enum class AppScreen {
    MAIN_MENU,
    GAME,
    SETTINGS
}

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf(AppScreen.MAIN_MENU) }
    var gameRules by remember { mutableStateOf(GameRules()) }
    
    MaterialTheme {
        when (currentScreen) {
            AppScreen.MAIN_MENU -> {
                MainMenuScreen(
                    onStartGame = { currentScreen = AppScreen.GAME },
                    onViewSettings = { currentScreen = AppScreen.SETTINGS }
                )
            }
            
            AppScreen.GAME -> {
                BlackjackGameScreen(
                    gameRules = gameRules,
                    onBackToMenu = { currentScreen = AppScreen.MAIN_MENU }
                )
            }
            
            AppScreen.SETTINGS -> {
                SettingsScreen(
                    currentRules = gameRules,
                    onRulesChanged = { newRules ->
                        gameRules = newRules
                        currentScreen = AppScreen.MAIN_MENU
                    },
                    onBack = { currentScreen = AppScreen.MAIN_MENU }
                )
            }
        }
    }
}