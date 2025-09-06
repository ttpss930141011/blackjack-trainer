package org.ttpss930141011.bj.presentation.components.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.valueobjects.DecisionRecord
import org.ttpss930141011.bj.domain.valueobjects.Suit
import org.ttpss930141011.bj.domain.valueobjects.Rank
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.presentation.layout.ScreenWidth

@Composable
fun HistorySection(
    decisionHistory: List<DecisionRecord>,
    onClearHistory: () -> Unit,
    screenWidth: ScreenWidth,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(Tokens.spacing(screenWidth)),
        verticalArrangement = Arrangement.spacedBy(Tokens.spacing(screenWidth))
    ) {
        // Header with summary and clear button
        HistoryHeader(
            decisionHistory = decisionHistory,
            onClearHistory = onClearHistory,
            screenWidth = screenWidth
        )
        
        if (decisionHistory.isEmpty()) {
            EmptyHistoryState(screenWidth = screenWidth)
        } else {
            decisionHistory.reversed().forEach { record -> // Show most recent first
                DecisionHistoryItem(
                    decision = record,
                    screenWidth = screenWidth
                )
            }
        }
    }
}

@Composable
fun HistoryHeader(
    decisionHistory: List<DecisionRecord>,
    onClearHistory: () -> Unit,
    screenWidth: ScreenWidth
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(Tokens.spacing(screenWidth))
        ) {
            // Title and clear button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Decision History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (decisionHistory.isNotEmpty()) {
                    TextButton(
                        onClick = onClearHistory
                    ) {
                        Text("Clear All")
                    }
                }
            }
            
            // Summary stats
            if (decisionHistory.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Tokens.spacing(screenWidth)))
                
                val correctCount = decisionHistory.count { it.isCorrect }
                val accuracy = (correctCount * 100) / decisionHistory.size
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HistoryStat(
                        label = "Total",
                        value = decisionHistory.size.toString(),
                        screenWidth = screenWidth
                    )
                    HistoryStat(
                        label = "Correct",
                        value = correctCount.toString(),
                        screenWidth = screenWidth
                    )
                    HistoryStat(
                        label = "Accuracy",
                        value = "$accuracy%",
                        screenWidth = screenWidth
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryStat(
    label: String,
    value: String,
    screenWidth: ScreenWidth
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EmptyHistoryState(screenWidth: ScreenWidth) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Tokens.padding(screenWidth)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No decisions recorded yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Tokens.spacing(screenWidth)))
            Text(
                text = "Start playing to build your learning history",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DecisionHistoryItem(
    decision: DecisionRecord,
    screenWidth: ScreenWidth
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (decision.isCorrect) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            }
        ),
        shape = RoundedCornerShape(Tokens.cornerRadius(screenWidth))
    ) {
        Column(
            modifier = Modifier.padding(Tokens.spacing(screenWidth))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Result indicator and scenario
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Tokens.spacing(screenWidth))
                ) {
                    Text(
                        text = if (decision.isCorrect) "✅" else "❌",
                        fontSize = 18.sp
                    )
                    
                    Text(
                        text = decision.scenarioKey,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Timestamp (simplified)
                Text(
                    text = formatDecisionTime(decision.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(Tokens.spacing(screenWidth)))
            
            // Cards display
            Row(
                horizontalArrangement = Arrangement.spacedBy(Tokens.spacing(screenWidth)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player cards
                Text(
                    text = "You:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    decision.handCards.forEach { card ->
                        CardChip(card = card, screenWidth = screenWidth)
                    }
                }
                
                Text(
                    text = "vs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Dealer card
                CardChip(card = decision.dealerUpCard, screenWidth = screenWidth)
            }
            
            Spacer(modifier = Modifier.height(Tokens.spacing(screenWidth)))
            
            // Action summary
            Text(
                text = "You chose: ${decision.playerAction.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (decision.isCorrect) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CardChip(
    card: Card,
    screenWidth: ScreenWidth
) {
    val cardColor = when (card.suit) {
        Suit.HEARTS, Suit.DIAMONDS -> MaterialTheme.colorScheme.error
        Suit.CLUBS, Suit.SPADES -> MaterialTheme.colorScheme.onSurface
    }
    
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.padding(1.dp)
    ) {
        Text(
            text = "${getCardRankSymbol(card.rank)}${getCardSuitSymbol(card.suit)}",
            color = cardColor,
            fontSize = when (screenWidth) {
                ScreenWidth.COMPACT -> 12.sp
                ScreenWidth.MEDIUM -> 14.sp
                ScreenWidth.EXPANDED -> 16.sp
            },
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

private fun getCardRankSymbol(rank: Rank): String {
    return when (rank) {
        Rank.ACE -> "A"
        Rank.TWO -> "2"
        Rank.THREE -> "3"
        Rank.FOUR -> "4"
        Rank.FIVE -> "5"
        Rank.SIX -> "6"
        Rank.SEVEN -> "7"
        Rank.EIGHT -> "8"
        Rank.NINE -> "9"
        Rank.TEN -> "10"
        Rank.JACK -> "J"
        Rank.QUEEN -> "Q"
        Rank.KING -> "K"
    }
}

private fun getCardSuitSymbol(suit: Suit): String {
    return when (suit) {
        Suit.HEARTS -> "♥️"
        Suit.DIAMONDS -> "♦️"
        Suit.CLUBS -> "♣️"
        Suit.SPADES -> "♠️"
    }
}

private fun formatDecisionTime(timestamp: Long): String {
    // Simplified timestamp formatting to avoid platform dependencies
    val id = (timestamp % 100000).toInt()
    return "#$id"
}