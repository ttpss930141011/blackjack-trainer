package org.ttpss930141011.bj.presentation.components.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.*
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.presentation.components.displays.CardImageDisplay
import org.ttpss930141011.bj.presentation.components.displays.HoleCardDisplay
import org.ttpss930141011.bj.presentation.design.GameStatusColors

/**
 * Dealer area component that handles dealer display logic
 * Shows waiting state, up card, or full hand based on game phase
 */

@Composable
fun DealerArea(
    game: Game,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (game.phase) {
            GamePhase.WAITING_FOR_BETS -> {
                DealerWaitingDisplay()
            }
            else -> {
                DealerHandDisplay(
                    dealerHand = game.dealer.hand,
                    dealerUpCard = game.dealer.upCard,
                    phase = game.phase
                )
            }
        }
    }
}

@Composable
private fun DealerWaitingDisplay() {
    Card {
        Column(
            modifier = Modifier.padding(Tokens.Space.l),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Tokens.Space.s)
        ) {
            Text(
                text = "Dealer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Show placeholder cards
            Row(horizontalArrangement = Arrangement.spacedBy(Tokens.Space.xs)) {
                repeat(2) {
                    PlaceholderCard()
                }
            }
            
            Text(
                text = "Waiting for bets...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DealerHandDisplay(
    dealerHand: Hand?,
    dealerUpCard: Card?,
    phase: GamePhase
) {
    Card {
        Column(
            modifier = Modifier.padding(Tokens.Space.l),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Tokens.Space.s)
        ) {
            Text(
                text = "Dealer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            when (phase) {
                GamePhase.PLAYER_ACTIONS -> {
                    dealerUpCard?.let { upCard ->
                        Row(horizontalArrangement = Arrangement.spacedBy(Tokens.Space.xs)) {
                            CardImageDisplay(card = upCard, size = Tokens.Card.medium)
                            HoleCardDisplay(size = Tokens.Card.medium)
                        }
                        Text("Up Card: ${upCard.rank}")
                    }
                }
                else -> {
                    dealerHand?.let { hand ->
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(Tokens.Space.xs)) {
                            items(hand.cards) { card ->
                                CardImageDisplay(card = card, size = Tokens.Card.medium)
                            }
                        }
                        Text(
                            text = "Value: ${hand.bestValue}${if (hand.isSoft) " (soft)" else ""}",
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                        if (hand.isBusted) {
                            Text(
                                text = "Busted!",
                                color = GameStatusColors.bustColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderCard() {
    Box(
        modifier = Modifier
            .size(Tokens.Card.medium.width, Tokens.Card.medium.height)
            .background(
                Color.Gray.copy(alpha = 0.3f),
                RoundedCornerShape(Tokens.Space.xs)
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.3f),
                RoundedCornerShape(Tokens.Space.xs)
            )
    )
}