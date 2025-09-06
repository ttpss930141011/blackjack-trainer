package org.ttpss930141011.bj

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.ttpss930141011.bj.domain.valueobjects.GameRules
import org.ttpss930141011.bj.presentation.*
import org.ttpss930141011.bj.presentation.design.CasinoColorScheme

enum class AppScreen {
    CASINO,
    SETTINGS
}

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf(AppScreen.CASINO) }
    var gameRules by remember { mutableStateOf(GameRules()) }
    
    MaterialTheme(colorScheme = CasinoColorScheme) { // Apply unified casino theme
        when (currentScreen) {
            AppScreen.CASINO -> {
                CasinoGameScreen(
                    gameRules = gameRules,
                )
            }
            
            AppScreen.SETTINGS -> {
                SettingsScreen(
                    rules = gameRules,
                    onRulesChange = { newRules ->
                        gameRules = newRules
                        currentScreen = AppScreen.CASINO
                    }
                )
            }
        }
    }
}