package org.ttpss930141011.bj.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.Player
import org.ttpss930141011.bj.domain.SessionStats

@Composable
fun GameStatusDisplay(player: Player, stats: SessionStats) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Chips: ${player.chips}",
                style = MaterialTheme.typography.headlineSmall
            )
            
            if (stats.hasSignificantData) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Rounds: ${stats.totalRounds}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Accuracy: ${(stats.overallDecisionRate * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = when {
                            stats.overallDecisionRate >= 0.8 -> MaterialTheme.colorScheme.primary
                            stats.overallDecisionRate >= 0.6 -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        }
    }
}