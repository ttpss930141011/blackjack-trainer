package org.ttpss930141011.bj.presentation.components.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.domain.*
import org.ttpss930141011.bj.presentation.components.displays.CardImageDisplay
import org.ttpss930141011.bj.presentation.shared.CardSize
import org.ttpss930141011.bj.presentation.shared.GameStatusColors

/**
 * Player area component that handles player hand display
 * Shows betting circle during betting phase, player hands during game
 */

@Composable
fun PlayerArea(
    game: Game,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (game.phase) {
            GamePhase.WAITING_FOR_BETS -> {
                BettingCircle(
                    bettingTableState = viewModel.bettingTableState,
                    onClearBet = { viewModel.clearBet() }
                )
            }
            else -> {
                PlayerHandsDisplay(
                    playerHands = game.playerHands,
                    currentHandIndex = game.currentHandIndex,
                    phase = game.phase
                )
            }
        }
    }
}

@Composable
private fun PlayerHandsDisplay(
    playerHands: List<PlayerHand>,
    currentHandIndex: Int,
    phase: GamePhase
) {
    if (playerHands.isEmpty()) return
    
    if (playerHands.size == 1) {
        PlayerHandCard(
            hand = playerHands[0],
            handIndex = 0,
            isActive = currentHandIndex == 0,
            phase = phase
        )
    } else {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(playerHands) { index, hand ->
                PlayerHandCard(
                    hand = hand,
                    handIndex = index,
                    isActive = currentHandIndex == index,
                    phase = phase,
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
                GameStatusColors.activeColor.copy(alpha = 0.8f)
            } else {
                GameStatusColors.casinoGreen.copy(alpha = 0.6f)
            }
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Hand title
            val title = when {
                handIndex > 0 && isActive && phase == GamePhase.PLAYER_ACTIONS -> "Hand ${handIndex + 1} (Your Turn)"
                handIndex > 0 -> "Hand ${handIndex + 1}"
                isActive && phase == GamePhase.PLAYER_ACTIONS -> "Your Turn"
                else -> "Your Hand"
            }
            
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = if (isActive && phase == GamePhase.PLAYER_ACTIONS) {
                    GameStatusColors.casinoGold
                } else {
                    Color.White
                },
                fontWeight = FontWeight.Bold
            )
            
            // Cards
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                hand.cards.forEach { card ->
                    CardImageDisplay(card = card, size = CardSize.MEDIUM)
                }
            }
            
            // Hand value
            Text(
                text = "Value: ${hand.bestValue}${if (hand.isSoft) " (soft)" else ""}",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            
            // Bet amount
            Text(
                text = "Bet: $${hand.bet}",
                color = GameStatusColors.betColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            
            // Status area - fixed height to prevent jumping
            Box(
                modifier = Modifier.height(20.dp),
                contentAlignment = Alignment.Center
            ) {
                val statusText = when {
                    hand.isBusted -> "Busted!"
                    phase == GamePhase.SETTLEMENT -> hand.status.name
                    else -> ""
                }
                
                val statusColor = when {
                    hand.isBusted -> GameStatusColors.bustColor
                    phase == GamePhase.SETTLEMENT -> GameStatusColors.getHandStatusColor(hand.status)
                    else -> Color.Transparent
                }
                
                if (statusText.isNotEmpty()) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}