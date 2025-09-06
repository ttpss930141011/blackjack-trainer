package org.ttpss930141011.bj.presentation.components.feedback

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.valueobjects.SessionStats
import org.ttpss930141011.bj.presentation.design.Tokens

/**
 * Streamlined Game Over overlay - shows as centered overlay on the game table
 * with simplified controls (only restart needed)
 */
@Composable
fun GameOverOverlay(
    onNewGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(320.dp)
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
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
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = Tokens.Space.l)
            )
            
            // Single action button - streamlined UX
            Button(
                onClick = onNewGame,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🎯 Start Over")
            }
        }
    }
}

/**
 * Legacy GameOverDisplay - kept for backward compatibility if needed
 * But recommend using GameOverOverlay instead
 */
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
            
            // Learning Summary (if available) - removed Final Chips card
            if (sessionStats.totalRounds > 0) {
                Text(
                    text = "📊 Session Summary",
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
                
                Spacer(modifier = Modifier.height(Tokens.Space.xl))
            }
            
            // Single essential action - removed History button
            Button(
                onClick = onNewGame,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🎯 Start New Game")
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