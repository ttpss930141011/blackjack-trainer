package org.ttpss930141011.bj.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.valueobjects.GameRules

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    rules: GameRules,
    onRulesChange: (GameRules) -> Unit
) {
    var currentRules by remember(rules) { mutableStateOf(rules) }
    
    LaunchedEffect(currentRules) {
        onRulesChange(currentRules)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Game Settings",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Basic Rules",
                    style = MaterialTheme.typography.titleMedium
                )
                
                // Dealer hits on soft 17
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dealer hits on soft 17")
                    Switch(
                        checked = currentRules.dealerHitsOnSoft17,
                        onCheckedChange = { 
                            currentRules = currentRules.copy(dealerHitsOnSoft17 = it)
                        }
                    )
                }
                
                // Double after split
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Double after split allowed")
                    Switch(
                        checked = currentRules.doubleAfterSplitAllowed,
                        onCheckedChange = { 
                            currentRules = currentRules.copy(doubleAfterSplitAllowed = it)
                        }
                    )
                }
                
                // Surrender allowed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Surrender allowed")
                    Switch(
                        checked = currentRules.surrenderAllowed,
                        onCheckedChange = { 
                            currentRules = currentRules.copy(surrenderAllowed = it)
                        }
                    )
                }
                
                // Blackjack payout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Blackjack payout")
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentRules.blackjackPayout == 1.5,
                                onClick = { 
                                    currentRules = currentRules.copy(blackjackPayout = 1.5)
                                }
                            )
                            Text("3:2 (1.5x)")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentRules.blackjackPayout == 1.2,
                                onClick = { 
                                    currentRules = currentRules.copy(blackjackPayout = 1.2)
                                }
                            )
                            Text("6:5 (1.2x)")
                        }
                    }
                }
            }
        }
        
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Advanced Rules",
                    style = MaterialTheme.typography.titleMedium
                )
                
                // Max splits
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Maximum splits")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..4).forEach { splits ->
                            FilterChip(
                                onClick = { 
                                    currentRules = currentRules.copy(maxSplits = splits - 1)
                                },
                                label = { Text(splits.toString()) },
                                selected = currentRules.maxSplits == splits - 1
                            )
                        }
                    }
                }
                
                // Deck count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Number of decks")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(1, 2, 4, 6, 8).forEach { decks ->
                            FilterChip(
                                onClick = { 
                                    currentRules = currentRules.copy(deckCount = decks)
                                },
                                label = { Text(decks.toString()) },
                                selected = currentRules.deckCount == decks
                            )
                        }
                    }
                }
                
                // Resplit aces
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Resplit aces allowed")
                    Switch(
                        checked = currentRules.resplitAces,
                        onCheckedChange = { 
                            currentRules = currentRules.copy(resplitAces = it)
                        }
                    )
                }
                
                // Hit split aces
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hit split aces allowed")
                    Switch(
                        checked = currentRules.hitSplitAces,
                        onCheckedChange = { 
                            currentRules = currentRules.copy(hitSplitAces = it)
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { 
                currentRules = GameRules() // Reset to defaults
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset to Default")
        }
    }
}

@Composable
fun SettingsSheetContent(
    currentRules: GameRules,
    onRulesChanged: (GameRules) -> Unit,
    onClose: () -> Unit,
    feedbackNotificationEnabled: Boolean,
    feedbackDurationSeconds: Float,
    onFeedbackSettingsChanged: (Boolean, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with close button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Game Settings",
                style = MaterialTheme.typography.headlineSmall
            )
            TextButton(onClick = onClose) {
                Text("Close")
            }
        }
        
        // Settings content using the existing SettingsScreen logic
        SettingsScreen(
            rules = currentRules,
            onRulesChange = onRulesChanged
        )
    }
}