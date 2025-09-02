package org.ttpss930141011.bj.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.*
import org.ttpss930141011.bj.presentation.CardImageDisplay
import org.ttpss930141011.bj.presentation.CardImageMapper
import org.ttpss930141011.bj.presentation.CardSize

@Composable
fun GameTableDisplay(
    game: Game,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            val phaseTitle = when (game.phase) {
                GamePhase.WAITING_FOR_BETS -> "Place Your Bet"
                GamePhase.PLAYER_ACTIONS -> "Player Actions"
                GamePhase.DEALER_TURN -> "Dealer's Turn"
                GamePhase.SETTLEMENT -> "Round Results"
                else -> "Game Table"
            }
            
            Text(
                text = phaseTitle,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                DealerDisplay(
                    dealerHand = game.dealer.hand,
                    dealerUpCard = game.dealer.upCard,
                    phase = game.phase
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (game.hasPlayer && game.playerHands.isNotEmpty()) {
                Text(
                    text = "Your Hands",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    PlayerHandsDisplay(
                        playerHands = game.playerHands,
                        currentHandIndex = game.currentHandIndex,
                        phase = game.phase
                    )
                }
            } else if (game.hasPlayer) {
                Text(
                    text = "${game.player!!.id} - Ready to play",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DealerDisplay(
    dealerHand: Hand?,
    dealerUpCard: Card?,
    phase: GamePhase
) {
    Card {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Dealer",
                style = MaterialTheme.typography.titleMedium
            )
            
            when (phase) {
                GamePhase.WAITING_FOR_BETS -> {
                    Text(
                        text = "Waiting for bets...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                GamePhase.PLAYER_ACTIONS -> {
                    dealerUpCard?.let { upCard ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            CardImageDisplay(card = upCard, size = CardSize.MEDIUM)
                            HoleCardDisplay(size = CardSize.MEDIUM)
                        }
                        Text("Up Card: ${upCard.rank}")
                    }
                }
                GamePhase.DEALER_TURN-> {
                    dealerHand?.let { hand ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            hand.cards.forEach { card ->
                                CardImageDisplay(card = card, size = CardSize.MEDIUM)
                            }
                        }
                        Text("Value: ${hand.bestValue}${if (hand.isSoft) " (soft)" else ""}")
                        if (hand.isBusted) {
                            Text("Busted!", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                 GamePhase.SETTLEMENT -> {
                    dealerHand?.let { hand ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            hand.cards.forEach { card ->
                                CardImageDisplay(card = card, size = CardSize.MEDIUM)
                            }
                        }
                        Text("Value: ${hand.bestValue}${if (hand.isSoft) " (soft)" else ""}")
                        if (hand.isBusted) {
                            Text("Busted!", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun HoleCardDisplay(
    modifier: Modifier = Modifier,
    size: CardSize = CardSize.MEDIUM
) {
    Image(
        painter = CardImageMapper.getCardBackPainter(),
        contentDescription = "Hidden card",
        modifier = modifier.size(size.width, size.height),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun PlayerHandsDisplay(
    playerHands: List<PlayerHand>,
    currentHandIndex: Int,
    phase: GamePhase
) {
    if (playerHands.size == 1) {
        PlayerHandCard(
            hand = playerHands[0],
            handIndex = 0,
            isActive = currentHandIndex == 0,
            phase = phase
        )
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(playerHands) { index, hand ->
                PlayerHandCard(
                    hand = hand,
                    handIndex = index,
                    isActive = currentHandIndex == index,
                    phase = phase,
                    modifier = Modifier.width(200.dp)
                )
            }
        }
    }
}

@Composable
private fun PlayerHandCard(
    hand: PlayerHand,
    handIndex: Int,
    isActive: Boolean,
    phase: GamePhase,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isActive && phase == GamePhase.PLAYER_ACTIONS) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (handIndex > 0) {
                Text(
                    text = "Hand ${handIndex + 1}",
                    style = MaterialTheme.typography.labelMedium
                )
            }
            
            if (isActive && phase == GamePhase.PLAYER_ACTIONS) {
                Text(
                    text = "Your Turn",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(hand.cards) { card ->
                    CardImageDisplay(card = card, size = CardSize.MEDIUM)
                }
            }
            
            Text("Value: ${hand.bestValue}${if (hand.isSoft) " (soft)" else ""}")
            Text("Bet: $${hand.bet}")
            
            if (phase == GamePhase.SETTLEMENT) {
                val statusColor = when (hand.status) {
                    HandStatus.WIN -> MaterialTheme.colorScheme.primary
                    HandStatus.LOSS -> MaterialTheme.colorScheme.error
                    HandStatus.PUSH -> MaterialTheme.colorScheme.tertiary
                    HandStatus.BUSTED -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
                Text(
                    text = hand.status.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor
                )
            }
        }
    }
}