package org.ttpss930141011.bj.presentation.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.SessionStats

@Composable
fun GameSummaryDialog(
    stats: SessionStats,
    onDismiss: () -> Unit,
    onBackToMenu: () -> Unit
) {
    if (stats.totalRounds == 0) {
        onDismiss()
        return
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Game Summary",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Rounds: ${stats.totalRounds}")
                    Text("Perfect Rounds: ${stats.perfectRounds}")
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Overall Accuracy: ${(stats.overallDecisionRate * 100).toInt()}%")
                    Text("Perfect Rate: ${(stats.perfectRoundRate * 100).toInt()}%")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Round History",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyColumn(
                modifier = Modifier.height(200.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Round", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                        Text("Decisions", modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelMedium)
                        Text("Result", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                        Text("Correct", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                items(stats.roundHistory) { record ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("${record.roundNumber}", modifier = Modifier.weight(1f))
                        Text(
                            text = record.decisions.joinToString(",") { it.action.name.take(1) },
                            modifier = Modifier.weight(2f)
                        )
                        Text(record.outcome, modifier = Modifier.weight(1f))
                        Text(
                            text = "${record.decisions.count { it.isCorrect }}/${record.decisions.size}",
                            modifier = Modifier.weight(1f),
                            color = if (record.decisions.all { it.isCorrect }) 
                                MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Continue Game")
                }
                Button(onClick = onBackToMenu) {
                    Text("Back to Menu")
                }
            }
        }
    }
}