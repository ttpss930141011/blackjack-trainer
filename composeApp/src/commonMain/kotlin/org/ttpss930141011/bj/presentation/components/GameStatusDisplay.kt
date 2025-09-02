package org.ttpss930141011.bj.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.Player
import org.ttpss930141011.bj.domain.SessionStats

@Composable
fun GameStatusDisplay(player: Player, stats: SessionStats) {
    if (stats.hasSignificantData) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rounds counter
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${stats.totalRounds}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Rounds",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Accuracy with color-coded styling
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val accuracyPercentage = (stats.overallDecisionRate * 100).toInt()
                    val accuracyColor = when {
                        stats.overallDecisionRate >= 0.8 -> Color(0xFF4CAF50) // Green
                        stats.overallDecisionRate >= 0.6 -> Color(0xFFFF9800) // Orange
                        else -> Color(0xFFF44336) // Red
                    }
                    
                    Text(
                        text = "$accuracyPercentage%",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = accuracyColor
                    )
                    Text(
                        text = "Accuracy",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}