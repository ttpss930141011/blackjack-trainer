package org.ttpss930141011.bj

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.ttpss930141011.bj.presentation.CasinoGameScreen
import org.ttpss930141011.bj.presentation.design.CasinoColorScheme

/**
 * Main application composable
 * 
 * Provides the single entry point to the casino game application,
 * applying the unified casino theme throughout the application.
 * All navigation is now handled internally by CasinoGameScreen.
 */
@Composable
@Preview
fun App() {
    MaterialTheme(colorScheme = CasinoColorScheme) {
        CasinoGameScreen()
    }
}