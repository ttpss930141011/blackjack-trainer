package org.ttpss930141011.bj.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.domain.valueobjects.GameRules
import org.ttpss930141011.bj.domain.valueobjects.UserPreferences
import org.ttpss930141011.bj.presentation.layout.ScreenWidth
import org.ttpss930141011.bj.presentation.pages.SettingsPage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    rules: GameRules,
    onRulesChange: (GameRules) -> Unit
) {
    // Create a ViewModel for this screen to handle persistence
    val viewModel = remember { GameViewModel() }
    val userPreferences = viewModel.userPreferences
    
    // Update the rules when they change
    LaunchedEffect(userPreferences.preferredRules) {
        if (userPreferences.preferredRules != rules) {
            onRulesChange(userPreferences.preferredRules)
        }
    }
    
    // Use the new SettingsPage implementation
    SettingsPage(
        userPreferences = userPreferences,
        onPreferencesChanged = { newPreferences ->
            viewModel.updateUserPreferences(newPreferences)
        }
    )
}
