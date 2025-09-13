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
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.presentation.layout.ScreenWidth

/**
 * HistoryPage - Clean decision flow visualization with split fix
 * 
 * Core principle: Show exactly what happened, no inference, no guessing.
 * Split decisions show the 4-4 pair that was split, not the 4-5 result.
 * 
 * Design:
 * - One decision = one display row
 * - Before state + Action + After state
 * - No special cases for different actions
 * - Clean, readable decision flow
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
                RoundCard(round = round)
            }
        }
    }
}

@Composable
private fun EmptyHistoryCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
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
 * Round display showing decision sequence
 */
@Composable
private fun RoundCard(round: RoundHistory, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Round header
            RoundHeader(round = round)
            
            // Decision sequence - the core feature
            Text(
                text = "Decision Flow",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            // Each decision is one row - simple and clear
            round.decisions.forEachIndexed { index, decision ->
                DecisionRow(
                    decision = decision,
                    stepNumber = index + 1
                )
            }
            
            // Final outcome
            RoundOutcome(round = round)
        }
    }
}

@Composable
private fun RoundHeader(round: RoundHistory) {
    Text(
        text = "Bet: $${round.initialBet}",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium
    )
}

/**
 * Correct decision display logic:
 * beforeAction.cards in white frame → ActionIndicator → (optional new card for Hit/Double)
 */
@Composable
private fun DecisionRow(
    decision: DecisionRecord,
    stepNumber: Int
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
        
        // Before: Show complete hand at decision time in white frame
        InitialCardsFrame(decision.beforeAction.cards)
        
        // Action: What they did
        ActionIndicator(decision.action)
        
        // After: Only show new card for Hit/Double actions
        when (val result = decision.afterAction) {
            is ActionResult.Hit -> {
                CardChip(result.newCard)
            }
            is ActionResult.Double -> {
                CardChip(result.newCard)
            }
            is ActionResult.Split -> {
                // No additional cards shown for split - keep UI clean
            }
            is ActionResult.Stand, is ActionResult.Surrender -> {
                // No additional cards shown
            }
        }
        
        Text("vs", style = MaterialTheme.typography.bodySmall)
        CardChip(decision.beforeAction.dealerUpCard)
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Correctness indicator
        Text(
            text = if (decision.isCorrect) "✅" else "❌",
            fontSize = 18.sp
        )
    }
}

/**
 * Display a hand of cards with visual grouping
 * Initial cards (first 2) in white frame, subsequent cards after "+" indicator
 */
@Composable
private fun GroupedHandDisplay(cards: List<Card>) {
    if (cards.size <= 2) {
        // Only initial cards, show in white frame
        InitialCardsFrame(cards = cards)
    } else {
        // Initial cards + subsequent cards
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Initial cards in white frame
            InitialCardsFrame(cards = cards.take(2))
            
            // Plus indicator using consistent styling
            Surface(
                color = CasinoTheme.HitButtonBackground,
                shape = RoundedCornerShape(3.dp),
                modifier = Modifier.size(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "+",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Subsequent cards
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                cards.drop(2).forEach { card ->
                    CardChip(card)
                }
            }
        }
    }
}

/**
 * Initial cards with white border frame
 */
@Composable
private fun InitialCardsFrame(cards: List<Card>) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.5.dp, Color.White),
        color = Color.Transparent,
        modifier = Modifier.padding(1.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            cards.forEach { card ->
                CardChip(card)
            }
        }
    }
}

/**
 * Display a hand of cards - clean and simple (legacy function for other uses)
 */
@Composable
private fun HandDisplay(cards: List<Card>) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        cards.forEach { card ->
            CardChip(card)
        }
    }
}

/**
 * Action indicator - clean symbols, no text bloat
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
 * Compact card display
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