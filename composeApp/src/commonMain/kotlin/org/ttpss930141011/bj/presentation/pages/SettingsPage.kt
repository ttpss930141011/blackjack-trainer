package org.ttpss930141011.bj.presentation.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.valueobjects.GameRules
import org.ttpss930141011.bj.domain.valueobjects.DisplaySettings
import org.ttpss930141011.bj.domain.valueobjects.UserPreferences

@Composable
fun SettingsPage(
    userPreferences: UserPreferences,
    onPreferencesChanged: (UserPreferences) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Game Rules Settings
        item {
            GameRulesSettingsCard(
                gameRules = userPreferences.preferredRules,
                onRulesChanged = { newRules ->
                    onPreferencesChanged(userPreferences.updatePreferredRules(newRules))
                }
            )
        }
        
        // Betting Preferences
        item {
            BettingSettingsCard(
                lastBetAmount = userPreferences.lastBetAmount,
                minimumBet = userPreferences.preferredRules.minimumBet,
                onBetAmountChanged = { newAmount ->
                    onPreferencesChanged(userPreferences.rememberLastBet(newAmount))
                }
            )
        }
        
        // Audio Settings
        item {
            AudioSettingsCard(
                displaySettings = userPreferences.displaySettings,
                onDisplaySettingsChanged = { newSettings ->
                    onPreferencesChanged(userPreferences.updateDisplaySettings(newSettings))
                }
            )
        }
    }
}

@Composable
private fun GameRulesSettingsCard(
    gameRules: GameRules,
    onRulesChanged: (GameRules) -> Unit
) {
    SettingsCard(title = "Game Rules") {
        SettingsSwitch(
            text = "Dealer hits on soft 17",
            checked = gameRules.dealerHitsOnSoft17,
            onCheckedChange = { onRulesChanged(gameRules.copy(dealerHitsOnSoft17 = it)) }
        )
        
        SettingsSwitch(
            text = "Double after split allowed",
            checked = gameRules.doubleAfterSplitAllowed,
            onCheckedChange = { onRulesChanged(gameRules.copy(doubleAfterSplitAllowed = it)) }
        )
        
        SettingsSwitch(
            text = "Surrender allowed",
            checked = gameRules.surrenderAllowed,
            onCheckedChange = { onRulesChanged(gameRules.copy(surrenderAllowed = it)) }
        )
        
        SettingsSwitch(
            text = "Resplit Aces allowed",
            checked = gameRules.resplitAces,
            onCheckedChange = { onRulesChanged(gameRules.copy(resplitAces = it)) }
        )
        
        SettingsSwitch(
            text = "Hit split Aces allowed",
            checked = gameRules.hitSplitAces,
            onCheckedChange = { onRulesChanged(gameRules.copy(hitSplitAces = it)) }
        )
        
        SettingsSlider(
            text = "Number of decks",
            value = gameRules.deckCount,
            valueRange = 1..8,
            onValueChanged = { onRulesChanged(gameRules.copy(deckCount = it)) }
        )
        
        SettingsSlider(
            text = "Maximum splits",
            value = gameRules.maxSplits,
            valueRange = 0..3,
            onValueChanged = { onRulesChanged(gameRules.copy(maxSplits = it)) }
        )
        
        SettingsSlider(
            text = "Deck penetration",
            value = (gameRules.penetrationPercentage * 100).toInt(),
            valueRange = 50..90,
            step = 5,
            onValueChanged = { percentage ->
                val newPenetration = percentage / 100.0
                onRulesChanged(gameRules.copy(penetrationPercentage = newPenetration))
            },
            formatValue = { "${it}%" }
        )
    }
}



@Composable
private fun BettingSettingsCard(
    lastBetAmount: Int,
    minimumBet: Int,
    onBetAmountChanged: (Int) -> Unit
) {
    SettingsCard(title = "Betting Preferences") {
        SettingsSlider(
            text = "Default bet amount",
            value = if (lastBetAmount > 0) lastBetAmount else minimumBet,
            valueRange = minimumBet..500,
            step = 5,
            onValueChanged = onBetAmountChanged,
            formatValue = { "$$it" }
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

@Composable
private fun SettingsSwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color.Green.copy(alpha = 0.7f),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun SettingsSlider(
    text: String,
    value: Int,
    valueRange: IntRange,
    step: Int = 1,
    onValueChanged: (Int) -> Unit,
    formatValue: (Int) -> String = { it.toString() }
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Text(
                text = formatValue(value),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChanged(it.toInt()) },
            valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
            steps = (valueRange.last - valueRange.first) / step - 1,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.Green.copy(alpha = 0.7f),
                inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun AudioSettingsCard(
    displaySettings: DisplaySettings,
    onDisplaySettingsChanged: (DisplaySettings) -> Unit
) {
    SettingsCard(title = "Audio") {
        SettingsSwitch(
            text = "Sound Effects",
            checked = displaySettings.soundEnabled,
            onCheckedChange = { enabled ->
                onDisplaySettingsChanged(displaySettings.withSoundEnabled(enabled))
            }
        )
        
        if (displaySettings.soundEnabled) {
            SettingsSlider(
                text = "Volume",
                value = (displaySettings.soundVolume * 100).toInt(),
                valueRange = 0..100,
                step = 10,
                onValueChanged = { volumePercent ->
                    val volume = volumePercent / 100.0f
                    onDisplaySettingsChanged(displaySettings.withSoundVolume(volume))
                },
                formatValue = { "$it%" }
            )
        }
    }
}

