package org.ttpss930141011.bj.presentation.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.presentation.design.CasinoTheme
import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.valueobjects.DecisionRecord
import org.ttpss930141011.bj.domain.valueobjects.PlayerHand
import org.ttpss930141011.bj.domain.valueobjects.RoundHistory
import org.ttpss930141011.bj.domain.valueobjects.Suit
import org.ttpss930141011.bj.domain.valueobjects.Rank
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.presentation.layout.ScreenWidth

/**
 * HistoryPage - Simplified decision flow visualization
 * 
 * BREAKING CHANGE: Completely redesigned for user learning focus
 * - Removed: Clear history button (not needed by users)
 * - Removed: Meaningless round IDs ("回合 round_24")
 * - Removed: Statistics display (accuracy, timing, decision count)
 * - Added: Decision flow visualization with correct/wrong icons
 * - Language: Changed to English for clarity
 * 
 * Core purpose: Show decision-making flow for learning improvement
 */
@Composable
fun HistoryPage(
    roundHistory: List<RoundHistory>,
    screenWidth: ScreenWidth,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        if (roundHistory.isEmpty()) {
            item {
                EmptyHistoryCard()
            }
        } else {
            items(roundHistory) { round ->
                RoundDecisionFlowCard(round = round)
            }
        }
    }
}

@Composable
private fun EmptyHistoryCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No game history yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start playing to see your decision patterns",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


/**
 * Decision Flow Card - Shows round as a sequence of decisions with outcomes
 */
@Composable
private fun RoundDecisionFlowCard(
    round: RoundHistory,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Simple round header
            RoundBasicInfo(round = round)
            
            // Decision flow - the core feature
            DecisionFlowVisualization(decisions = round.decisions)
            
            // Final outcome
            RoundOutcome(round = round)
        }
    }
}

/**
 * Basic round information without meaningless IDs
 */
@Composable
private fun RoundBasicInfo(round: RoundHistory) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Bet: $${round.betAmount}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Decision flow visualization - core learning feature
 */
@Composable
private fun DecisionFlowVisualization(decisions: List<DecisionRecord>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Decision Flow",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )
        
        decisions.forEachIndexed { index, decision ->
            DecisionStep(
                decision = decision,
                stepNumber = index + 1,
                cumulativeCards = getCumulativeCards(decisions, index)
            )
        }
    }
}

/**
 * Individual decision step with correct/wrong indicator
 */
@Composable
private fun DecisionStep(
    decision: DecisionRecord,
    stepNumber: Int,
    cumulativeCards: List<Card>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Step number
        Text(
            text = "$stepNumber.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Cumulative hand cards with action-specific grouping
        CumulativeHandDisplay(
            cumulativeCards = cumulativeCards,
            action = decision.playerAction
        )
        
        Text("vs", style = MaterialTheme.typography.bodySmall)
        
        // Dealer up card
        CardChip(card = decision.dealerUpCard)
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Correct/Wrong indicator - key learning feature
        Text(
            text = if (decision.isCorrect) "✅" else "❌",
            fontSize = 18.sp
        )
    }
}

/**
 * Simple round outcome
 */
@Composable
private fun RoundOutcome(round: RoundHistory) {
    val (outcomeText, outcomeColor) = when {
        round.isWinningRound -> "WIN (+$${round.netChipChange})" to MaterialTheme.colorScheme.primary
        round.isPushRound -> "PUSH" to MaterialTheme.colorScheme.onSurfaceVariant
        else -> "LOSE ($${round.netChipChange})" to MaterialTheme.colorScheme.error
    }
    
    Text(
        text = outcomeText,
        style = MaterialTheme.typography.bodyMedium,
        color = outcomeColor,
        fontWeight = FontWeight.Medium
    )
}

/**
 * Get cumulative cards up to a specific decision point
 * Each DecisionRecord contains all cards at that point in time
 */
private fun getCumulativeCards(decisions: List<DecisionRecord>, currentIndex: Int): List<Card> {
    // Return the hand cards from the current decision - they represent cumulative state
    return decisions.getOrNull(currentIndex)?.handCards ?: emptyList()
}

/**
 * Cumulative hand display that shows progression of cards across decisions
 */
@Composable
private fun CumulativeHandDisplay(
    cumulativeCards: List<Card>,
    action: Action
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (action) {
            Action.HIT -> {
                if (cumulativeCards.size <= 2) {
                    // Initial HIT decision: show initial cards + ActionIndicator + new card
                    InitialHandGroup(cards = cumulativeCards.dropLast(1))
                    ActionIndicator(action = action)
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        CardChip(card = cumulativeCards.last())
                    }
                } else {
                    // Subsequent HIT: show all previous cards + ActionIndicator + new card
                    InitialHandGroup(cards = cumulativeCards.dropLast(1))
                    ActionIndicator(action = action)
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        CardChip(card = cumulativeCards.last())
                    }
                }
            }
            else -> {
                // STAND, DOUBLE, SPLIT, SURRENDER: show all cards + ActionIndicator
                InitialHandGroup(cards = cumulativeCards)
                ActionIndicator(action = action)
            }
        }
    }
}

/**
 * Action indicator with colored background and symbol instead of text
 */
@Composable
private fun ActionIndicator(action: Action) {
    val (backgroundColor, symbol) = when (action) {
        Action.HIT -> CasinoTheme.HitButtonBackground to "+"
        Action.STAND -> CasinoTheme.ButtonDanger to "−"
        Action.DOUBLE -> CasinoTheme.CasinoAccentSecondary to "×2"
        Action.SPLIT -> CasinoTheme.CasinoAccentPrimary to "⁝⁝"
        Action.SURRENDER -> CasinoTheme.ButtonSecondary to "↓"
    }
    
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.size(20.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = symbol,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Grouped hand display showing initial cards (in white border) and additional cards with + separator
 */
@Composable
private fun GroupedHandDisplay(handCards: List<Card>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Initial cards (first 2) in white border group
        if (handCards.size >= 2) {
            InitialHandGroup(cards = handCards.take(2))
        }
        
        // Additional cards with + separator
        if (handCards.size > 2) {
            Text(
                text = "+",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                handCards.drop(2).forEach { card ->
                    CardChip(card = card)
                }
            }
        }
    }
}

/**
 * Initial hand group with white border to distinguish from additional cards
 */
@Composable
private fun InitialHandGroup(cards: List<Card>) {
    Surface(
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(2.dp, Color.White),
        color = Color.Transparent,
        modifier = Modifier.padding(2.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            cards.forEach { card ->
                CardChip(card = card)
            }
        }
    }
}

/**
 * Compact card display for individual cards
 */
@Composable
private fun CardChip(card: Card) {
    val cardColor = when (card.suit) {
        Suit.HEARTS, Suit.DIAMONDS -> MaterialTheme.colorScheme.error
        Suit.CLUBS, Suit.SPADES -> MaterialTheme.colorScheme.onSurface
    }
    
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.extraSmall,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.padding(1.dp)
    ) {
        Text(
            text = "${getCardRankSymbol(card.rank)}${getCardSuitSymbol(card.suit)}",
            color = cardColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
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



