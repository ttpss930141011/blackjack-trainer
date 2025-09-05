package org.ttpss930141011.bj.presentation.components.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.*
import org.ttpss930141011.bj.presentation.components.displays.CardImageDisplay
import org.ttpss930141011.bj.presentation.components.displays.StatusOverlay
import org.ttpss930141011.bj.presentation.components.displays.ChipImageDisplay
import org.ttpss930141011.bj.presentation.design.GameStatusColors
import org.ttpss930141011.bj.presentation.layout.BreakpointLayout
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import org.ttpss930141011.bj.domain.valueobjects.ChipInSpot
import org.ttpss930141011.bj.presentation.design.AppConstants

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
    val chipCompositionService = remember { ChipCompositionService() }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (game.phase) {
            GamePhase.WAITING_FOR_BETS -> {
                BettingCircle(
                    currentBet = viewModel.currentBetAmount,
                    chipComposition = viewModel.chipComposition,
                    onClearBet = { viewModel.clearBet() }
                )
            }
            else -> {
                PlayerHandsDisplay(
                    playerHands = game.playerHands,
                    currentHandIndex = game.currentHandIndex,
                    phase = game.phase,
                    chipCompositionService = chipCompositionService
                )
            }
        }
    }
}

@Composable
private fun PlayerHandsDisplay(
    playerHands: List<PlayerHand>,
    currentHandIndex: Int,
    phase: GamePhase,
    chipCompositionService: ChipCompositionService
) {
    if (playerHands.isEmpty()) return
    
    if (playerHands.size == 1) {
        PlayerHandCard(
            hand = playerHands[0],
            isActive = currentHandIndex == 0,
            phase = phase,
            chipCompositionService = chipCompositionService
        )
    } else {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(Tokens.Space.m)) {
            itemsIndexed(playerHands) { index, hand ->
                PlayerHandCard(
                    hand = hand,
                    isActive = currentHandIndex == index,
                    phase = phase,
                    chipCompositionService = chipCompositionService
                )
            }
        }
    }
}


@Composable
private fun PlayerHandCard(
    hand: PlayerHand,
    isActive: Boolean,
    phase: GamePhase,
    chipCompositionService: ChipCompositionService,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Tokens.Space.xs)
    ) {
        // Wrap the entire card in a Box so we can layer the overlay above it
        Box {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isActive && phase == GamePhase.PLAYER_TURN) {
                        GameStatusColors.activeColor.copy(alpha = 0.8f)
                    } else {
                        GameStatusColors.casinoGreen.copy(alpha = 0.6f)
                    }
                ),
                shape = RoundedCornerShape(Tokens.Space.m),
                elevation = CardDefaults.cardElevation(defaultElevation = Tokens.Space.xs)
            ) {
                Column(
                    modifier = Modifier.padding(Tokens.Space.m),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Tokens.Space.s)
                ) {
                    // Cards only - no text labels
                    Row(horizontalArrangement = Arrangement.spacedBy(Tokens.Space.xs)) {
                        hand.cards.forEach { card ->
                            CardImageDisplay(card = card, size = Tokens.Card.medium)
                        }
                    }
                    
                    // Hand value (keep for strategy learning)
                    Text(
                        text = "${hand.bestValue}${if (hand.isSoft) " (soft)" else ""}",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }

            // This will now fill the exact bounds of the Card and appear on top
            StatusOverlay(
                status = hand.status,
                isBusted = hand.isBusted,
                showStatus = hand.isBusted || phase == GamePhase.SETTLEMENT,
                modifier = Modifier.matchParentSize()
            )
        }
        
        // Chip stack display below card (extracted from card)
        if (hand.bet > 0) {
            ChipDisplay(
                chipComposition = chipCompositionService.calculateOptimalComposition(hand.bet),
                modifier = Modifier.size(AppConstants.Dimensions.CHIP_SIZE_COMPACT.dp)
            )
        }
    }
}

@Composable
private fun ChipDisplay(
    chipComposition: List<ChipInSpot>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(AppConstants.Dimensions.CHIP_SIZE_COMPACT.dp),
        contentAlignment = Alignment.Center
    ) {
        chipComposition.forEachIndexed { index, chipInSpot ->
            val offsetX = (index * AppConstants.ChipStack.HORIZONTAL_OFFSET).dp
            val offsetY = (index * AppConstants.ChipStack.VERTICAL_OFFSET).dp
            
            Box(
                modifier = Modifier.offset(x = offsetX, y = -offsetY)
            ) {
                repeat(chipInSpot.count) { stackIndex ->
                    ChipImageDisplay(
                        value = chipInSpot.value.value,
                        onClick = { },
                        size = Tokens.Size.chipDiameter,
                        modifier = Modifier.offset(
                            x = (stackIndex * AppConstants.ChipStack.STACK_HORIZONTAL_OFFSET).dp,
                            y = (stackIndex * AppConstants.ChipStack.STACK_VERTICAL_OFFSET).dp
                        )
                    )
                }
            }
        }
    }
}