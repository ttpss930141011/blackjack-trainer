package org.ttpss930141011.bj.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.*
import org.ttpss930141011.bj.presentation.CardImageDisplay
import org.ttpss930141011.bj.presentation.CardImageMapper
import org.ttpss930141011.bj.presentation.CardSize
import org.ttpss930141011.bj.presentation.shared.CardDisplayUtils
import org.ttpss930141011.bj.presentation.shared.GameStatusColors

@Composable
fun GameTableDisplay(
    game: Game,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = GameStatusColors.casinoGreen.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            if (game.phase != GamePhase.WAITING_FOR_BETS) {
                val phaseTitle = when (game.phase) {
                    GamePhase.PLAYER_ACTIONS -> "Player Actions"
                    GamePhase.DEALER_TURN -> "Dealer's Turn"
                    GamePhase.SETTLEMENT -> "Round Results"
                    else -> "Game Table"
                }
                
                Text(
                    text = phaseTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
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
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
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
                                HoleCardDisplay(size = CardSize.MEDIUM)
                            }
                        }
                        CardDisplayUtils.HandValueDisplay(
                            value = hand.bestValue,
                            isSoft = hand.isSoft,
                            isBusted = hand.isBusted
                        )
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
                        CardDisplayUtils.HandValueDisplay(
                            value = hand.bestValue,
                            isSoft = hand.isSoft,
                            isBusted = hand.isBusted
                        )
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
                // Dynamic width calculation based on card count
                val cardCount = hand.cards.size
                val cardWidth = CardSize.MEDIUM.width.value
                val cardSpacing = 4f
                val contentPadding = 24f // Total horizontal padding
                val textSpace = 60f // Space for value/bet text
                
                val neededWidth = (cardCount * cardWidth) + 
                                ((cardCount - 1) * cardSpacing) + 
                                contentPadding + textSpace
                
                val dynamicWidth = maxOf(200f, neededWidth).dp
                
                PlayerHandCard(
                    hand = hand,
                    handIndex = index,
                    isActive = currentHandIndex == index,
                    phase = phase,
                    modifier = Modifier.width(dynamicWidth)
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
                GameStatusColors.activeColor.copy(alpha = 0.8f) // Active green
            } else {
                GameStatusColors.casinoGreen.copy(alpha = 0.6f) // Inactive darker green
            }
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (handIndex > 0) {
                Text(
                    text = "Hand ${handIndex + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (isActive && phase == GamePhase.PLAYER_ACTIONS) {
                Text(
                    text = "Your Turn",
                    style = MaterialTheme.typography.labelSmall,
                    color = GameStatusColors.casinoGold,
                    fontWeight = FontWeight.Bold
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
            
            CardDisplayUtils.HandValueDisplay(
                value = hand.bestValue,
                isSoft = hand.isSoft,
                isBusted = hand.isBusted
            )
            CardDisplayUtils.BetDisplay(amount = hand.bet)
            
            if (phase == GamePhase.SETTLEMENT) {
                val statusColor = GameStatusColors.getHandStatusColor(hand.status)
                Text(
                    text = hand.status.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}