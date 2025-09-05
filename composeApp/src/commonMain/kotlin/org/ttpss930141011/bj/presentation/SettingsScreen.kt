package org.ttpss930141011.bj.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.valueobjects.GameRules
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.presentation.design.GameStatusColors
import org.ttpss930141011.bj.presentation.layout.Layout

@Composable
fun SettingsScreen(
    currentRules: GameRules,
    onRulesChanged: (GameRules) -> Unit,
    onBack: () -> Unit
) {
    Layout { screenWidth ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = GameStatusColors.casinoBackgroundGradient
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Tokens.padding(screenWidth))
            ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Game Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    color = GameStatusColors.casinoGold
                )
                
                TextButton(onClick = onBack) {
                    Text(
                        text = "Back",
                        color = GameStatusColors.casinoGold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Tokens.Space.xl))
            
                SettingsContent(
                    currentRules = currentRules,
                    onRulesChanged = onRulesChanged,
                    screenWidth = screenWidth
                )
            }
        }
    }
}

@Composable
fun SettingsSheetContent(
    currentRules: GameRules,
    onRulesChanged: (GameRules) -> Unit,
    onClose: () -> Unit,
    // Feedback notification settings
    feedbackNotificationEnabled: Boolean = true,
    feedbackDurationSeconds: Float = 2.5f,
    onFeedbackSettingsChanged: (enabled: Boolean, duration: Float) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Tokens.Space.xl)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Game Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = GameStatusColors.casinoGold
            )
            
            TextButton(onClick = onClose) {
                Text(
                    text = "Close",
                    color = GameStatusColors.casinoGold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Tokens.Space.l))
        
        SettingsContent(
            currentRules = currentRules,
            onRulesChanged = onRulesChanged,
            screenWidth = org.ttpss930141011.bj.presentation.layout.ScreenWidth.MEDIUM,
            feedbackNotificationEnabled = feedbackNotificationEnabled,
            feedbackDurationSeconds = feedbackDurationSeconds,
            onFeedbackSettingsChanged = onFeedbackSettingsChanged
        )
    }
}

@Composable
private fun SettingsContent(
    currentRules: GameRules,
    onRulesChanged: (GameRules) -> Unit,
    screenWidth: org.ttpss930141011.bj.presentation.layout.ScreenWidth,
    // Feedback notification settings
    feedbackNotificationEnabled: Boolean = true,
    feedbackDurationSeconds: Float = 2.5f,
    onFeedbackSettingsChanged: (enabled: Boolean, duration: Float) -> Unit = { _, _ -> }
) {
    var rules by remember { mutableStateOf(currentRules) }
    var notificationEnabled by remember { mutableStateOf(feedbackNotificationEnabled) }
    var notificationDuration by remember { mutableStateOf(feedbackDurationSeconds) }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = GameStatusColors.casinoGreen
        ),
        shape = RoundedCornerShape(Tokens.cornerRadius(screenWidth)),
        elevation = CardDefaults.cardElevation(defaultElevation = Tokens.Space.m)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Tokens.cornerRadius(screenWidth)))
                .background(
                    brush = Brush.radialGradient(
                        colors = GameStatusColors.casinoTableGradient,
                        radius = 800f
                    )
                )
                .padding(Tokens.Space.xl),
            verticalArrangement = Arrangement.spacedBy(Tokens.Space.l)
        ) {
            // Feedback Settings Section
            Text(
                text = "Feedback Settings",
                style = MaterialTheme.typography.titleMedium,
                color = GameStatusColors.casinoGold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Show feedback notifications",
                    color = GameStatusColors.casinoGold
                )
                Switch(
                    checked = notificationEnabled,
                    onCheckedChange = { 
                        notificationEnabled = it
                        onFeedbackSettingsChanged(it, notificationDuration)
                    }
                )
            }
            
            if (notificationEnabled) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Notification duration",
                            color = GameStatusColors.casinoGold
                        )
                        Text(
                            text = "${notificationDuration.toInt()}s",
                            color = GameStatusColors.casinoGold.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Slider(
                        value = notificationDuration,
                        onValueChange = { 
                            notificationDuration = it
                            onFeedbackSettingsChanged(notificationEnabled, it)
                        },
                        valueRange = 1f..5f,
                        steps = 7,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
            
            HorizontalDivider(
                color = GameStatusColors.casinoGold.copy(alpha = 0.3f),
                modifier = Modifier.padding(vertical = Tokens.Space.m)
            )
            
            Text(
                text = "Dealer Rules",
                style = MaterialTheme.typography.titleMedium,
                color = GameStatusColors.casinoGold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dealer hits on soft 17",
                    color = GameStatusColors.casinoGold
                )
                Switch(
                    checked = rules.dealerHitsOnSoft17,
                    onCheckedChange = { 
                        rules = rules.copy(dealerHitsOnSoft17 = it)
                    }
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Surrender allowed",
                    color = GameStatusColors.casinoGold
                )
                Switch(
                    checked = rules.surrenderAllowed,
                    onCheckedChange = { 
                        rules = rules.copy(surrenderAllowed = it)
                    }
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Double after split",
                    color = GameStatusColors.casinoGold
                )
                Switch(
                    checked = rules.doubleAfterSplit,
                    onCheckedChange = { 
                        rules = rules.copy(doubleAfterSplit = it)
                    }
                )
            }
            
            Text(
                text = "Advanced Rules",
                style = MaterialTheme.typography.titleMedium,
                color = GameStatusColors.casinoGold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Blackjack Payout",
                        color = GameStatusColors.casinoGold
                    )
                    Text(
                        text = if (rules.blackjackPayout == 1.5) "3:2" else "6:5",
                        style = MaterialTheme.typography.bodySmall,
                        color = GameStatusColors.casinoGold.copy(alpha = 0.7f)
                    )
                }
                Switch(
                    checked = rules.blackjackPayout == 1.5,
                    onCheckedChange = { 
                        rules = rules.copy(blackjackPayout = if (it) 1.5 else 1.2)
                    }
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Resplit Aces",
                    color = GameStatusColors.casinoGold
                )
                Switch(
                    checked = rules.resplitAces,
                    onCheckedChange = { 
                        rules = rules.copy(resplitAces = it)
                    }
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hit Split Aces",
                    color = GameStatusColors.casinoGold
                )
                Switch(
                    checked = rules.hitSplitAces,
                    onCheckedChange = { 
                        rules = rules.copy(hitSplitAces = it)
                    }
                )
            }
        }
    }
    
    Spacer(modifier = Modifier.height(Tokens.Space.xl))
    
    Button(
        onClick = { onRulesChanged(rules) },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = GameStatusColors.casinoGold
        )
    ) {
        Text(
            text = "Save Settings",
            color = GameStatusColors.casinoGreen
        )
    }
}