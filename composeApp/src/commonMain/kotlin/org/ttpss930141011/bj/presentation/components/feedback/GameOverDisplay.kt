package org.ttpss930141011.bj.presentation.components.feedback

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.valueobjects.SessionStats
import org.ttpss930141011.bj.presentation.design.Tokens

@Composable
fun GameOverDisplay(
    totalChips: Int,
    sessionStats: SessionStats = SessionStats(),
    onNewGame: () -> Unit = {},
    onViewHistory: () -> Unit = {},
    onViewSummary: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(Tokens.Space.l),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(Tokens.Space.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Game Over Header
            Text(
                text = "🎲 Game Over!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Text(
                text = "Insufficient chips to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = Tokens.Space.l)
            )
            
            // Final Result
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(Tokens.Space.l),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Final Chips",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$totalChips",
                        style = MaterialTheme.typography.headlineLarge,
                        color = if (totalChips > 0) MaterialTheme.colorScheme.primary 
                               else MaterialTheme.colorScheme.error
                    )
                }
            }
            
            // Learning Summary (if available)
            if (sessionStats.totalRounds > 0) {
                Spacer(modifier = Modifier.height(Tokens.Space.l))
                
                Text(
                    text = "📊 Session Learning",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = Tokens.Space.s)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickStat(
                        label = "Rounds",
                        value = "${sessionStats.totalRounds}"
                    )
                    QuickStat(
                        label = "Accuracy",
                        value = "${(sessionStats.overallDecisionRate * 100).toInt()}%"
                    )
                    QuickStat(
                        label = "Perfect",
                        value = "${sessionStats.perfectRounds}"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Tokens.Space.xl))
            
            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onNewGame,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🎯 Start New Game")
                }
                
                Spacer(modifier = Modifier.height(Tokens.Space.s))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Tokens.Space.s)
                ) {
                    if (sessionStats.totalRounds > 0) {
                        OutlinedButton(
                            onClick = onViewSummary,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("📈 Details")
                        }
                    }
                    
                    OutlinedButton(
                        onClick = onViewHistory,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("📚 History")
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickStat(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}