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
import androidx.compose.ui.unit.sp
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
                DealerHandCard(
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
    Card(
        colors = CardDefaults.cardColors(
            containerColor = GameStatusColors.casinoGreen.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(Tokens.Space.m),
        elevation = CardDefaults.cardElevation(defaultElevation = Tokens.Space.xs)
    ) {
        Column(
            modifier = Modifier.padding(Tokens.Space.l),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Tokens.Space.s)
        ) {
            // Clean card-only display
            Row(horizontalArrangement = Arrangement.spacedBy(Tokens.Space.xs)) {
                repeat(2) {
                    PlaceholderCard()
                }
            }
        }
    }
}

@Composable
private fun DealerHandCard(
    dealerHand: Hand?,
    dealerUpCard: Card?,
    phase: GamePhase
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Tokens.Space.xs)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = GameStatusColors.casinoGreen.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(Tokens.Space.m),
            elevation = CardDefaults.cardElevation(defaultElevation = Tokens.Space.xs)
        ) {
            Column(
                modifier = Modifier.padding(Tokens.Space.m),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Tokens.Space.s)
            ) {
                when (phase) {
                    GamePhase.PLAYER_ACTIONS -> {
                        dealerUpCard?.let { upCard ->
                            Row(horizontalArrangement = Arrangement.spacedBy(Tokens.Space.xs)) {
                                CardImageDisplay(card = upCard, size = Tokens.Card.medium)
                                HoleCardDisplay(size = Tokens.Card.medium)
                            }
                        }
                    }
                    else -> {
                        dealerHand?.let { hand ->
                            Row(horizontalArrangement = Arrangement.spacedBy(Tokens.Space.xs)) {
                                hand.cards.forEach { card ->
                                    CardImageDisplay(card = card, size = Tokens.Card.medium)
                                }
                            }
                        }
                    }
                }
                
                // Hand value display (matches PlayerHandCard structure)
                Text(
                    text = when (phase) {
                        GamePhase.PLAYER_ACTIONS -> dealerUpCard?.let { 
                            val upCardValue = if (it.rank == Rank.ACE) "A/11" else "${it.blackjackValue}"
                            upCardValue
                        } ?: ""
                        else -> dealerHand?.let { "${it.bestValue}${if (it.isSoft) " (soft)" else ""}" } ?: ""
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
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