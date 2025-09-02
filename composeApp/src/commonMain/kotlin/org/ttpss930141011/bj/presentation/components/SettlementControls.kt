package org.ttpss930141011.bj.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.*

@Composable
fun SettlementControls(
    game: Game,
    feedback: DecisionFeedback?,
    roundDecisions: List<PlayerDecision>,
    onSettle: () -> Unit,
    onNextRound: () -> Unit
) {
    val isSettled = game.playerHands.any { hand ->
        hand.status == HandStatus.WIN || 
        hand.status == HandStatus.LOSS || 
        hand.status == HandStatus.PUSH 
    }
    
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isSettled) {
                Button(
                    onClick = onSettle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Calculate Results",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            } else {
                feedback?.let { fb ->
                    Card {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = fb.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (fb.isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                if (roundDecisions.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("This Round Decisions:")
                            roundDecisions.forEachIndexed { index, decision ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${index + 1}. ${decision.action.name}")
                                    Text(
                                        text = if (decision.isCorrect) "✓" else "✗",
                                        color = if (decision.isCorrect) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Button(
                    onClick = onNextRound,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Next Round",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}