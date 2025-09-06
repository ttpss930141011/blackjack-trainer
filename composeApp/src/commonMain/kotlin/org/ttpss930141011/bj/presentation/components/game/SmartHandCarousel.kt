package org.ttpss930141011.bj.presentation.components.game

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.ttpss930141011.bj.domain.entities.Game
import org.ttpss930141011.bj.domain.enums.GamePhase
import org.ttpss930141011.bj.domain.enums.HandStatus
import org.ttpss930141011.bj.domain.services.ChipCompositionService
import org.ttpss930141011.bj.domain.valueobjects.ChipInSpot
import org.ttpss930141011.bj.domain.valueobjects.PlayerHand
import org.ttpss930141011.bj.presentation.components.displays.CardImageDisplay
import org.ttpss930141011.bj.presentation.components.displays.ChipImageDisplay
import org.ttpss930141011.bj.presentation.components.displays.StatusOverlay
import org.ttpss930141011.bj.presentation.design.AppConstants
import org.ttpss930141011.bj.presentation.design.CasinoSemanticColors
import org.ttpss930141011.bj.presentation.design.CasinoTheme
import org.ttpss930141011.bj.presentation.design.CasinoThemeConstants
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.presentation.layout.ScreenWidth

/**
 * Smart Hand Carousel Component
 * 
 * Provides intelligent layout and centering for split blackjack hands:
 * - Single hand: Centered display
 * - Multiple hands: Auto-centering carousel with active hand emphasis
 * - Smooth animations and visual hierarchy
 * - Maintains DDD principles with pure UI logic
 */
@Composable
fun SmartHandCarousel(
    playerHands: List<PlayerHand>,
    currentHandIndex: Int,
    phase: GamePhase,
    chipCompositionService: ChipCompositionService,
    screenWidth: ScreenWidth,
    modifier: Modifier = Modifier
) {
    if (playerHands.isEmpty()) return

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        when {
            playerHands.size == 1 -> {
                // Single hand: Simple centered display
                SingleHandDisplay(
                    hand = playerHands[0],
                    isActive = currentHandIndex == 0,
                    phase = phase,
                    chipCompositionService = chipCompositionService,
                    screenWidth = screenWidth
                )
            }
            else -> {
                // Multiple hands: Smart carousel with centering
                MultiHandCarousel(
                    playerHands = playerHands,
                    currentHandIndex = currentHandIndex,
                    phase = phase,
                    chipCompositionService = chipCompositionService,
                    screenWidth = screenWidth
                )
            }
        }
    }
}

@Composable
private fun SingleHandDisplay(
    hand: PlayerHand,
    isActive: Boolean,
    phase: GamePhase,
    chipCompositionService: ChipCompositionService,
    screenWidth: ScreenWidth
) {
    SmartHandCard(
        hand = hand,
        isActive = isActive,
        phase = phase,
        chipCompositionService = chipCompositionService,
        screenWidth = screenWidth
    )
}

@Composable
private fun MultiHandCarousel(
    playerHands: List<PlayerHand>,
    currentHandIndex: Int,
    phase: GamePhase,
    chipCompositionService: ChipCompositionService,
    screenWidth: ScreenWidth
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-center active hand with smooth animation
    LaunchedEffect(currentHandIndex) {
        if (currentHandIndex in playerHands.indices) {
            coroutineScope.launch {
                listState.animateScrollToItem(
                    index = currentHandIndex,
                    scrollOffset = getScrollOffsetForCentering(screenWidth)
                )
            }
        }
    }

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(Tokens.Space.l),
        contentPadding = PaddingValues(horizontal = Tokens.Space.xl),
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(playerHands) { index, hand ->
            val isActive = currentHandIndex == index
            
            // Animated scaling for visual hierarchy
            val scale by animateFloatAsState(
                targetValue = if (isActive) CarouselConstants.ACTIVE_SCALE else CarouselConstants.INACTIVE_SCALE,
                animationSpec = tween(
                    durationMillis = CasinoThemeConstants.HAND_TRANSITION_DURATION,
                    easing = EaseInOutCubic
                ),
                label = "handScale"
            )

            Box(
                modifier = Modifier.scale(scale)
            ) {
                SmartHandCard(
                    hand = hand,
                    isActive = isActive,
                    phase = phase,
                    chipCompositionService = chipCompositionService,
                    screenWidth = screenWidth
                )
            }
        }
    }
}

@Composable
private fun SmartHandCard(
    hand: PlayerHand,
    isActive: Boolean,
    phase: GamePhase,
    chipCompositionService: ChipCompositionService,
    screenWidth: ScreenWidth
) {
    val showActiveIndicators = isActive && phase == GamePhase.PLAYER_TURN
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Tokens.Space.xs)
    ) {
        // Main hand card with enhanced visual indicators
        Box {
            Card(
                colors = if (showActiveIndicators) {
                    CasinoSemanticColors.activeHandColors()
                } else {
                    CasinoSemanticColors.inactiveHandColors()
                },
                border = if (showActiveIndicators) {
                    BorderStroke(CasinoThemeConstants.ACTIVE_BORDER_WIDTH.dp, CasinoTheme.CasinoAccentPrimary)
                } else {
                    BorderStroke(CasinoThemeConstants.INACTIVE_BORDER_WIDTH.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (showActiveIndicators) {
                        CasinoThemeConstants.ACTIVE_HAND_ELEVATION.dp
                    } else {
                        CasinoThemeConstants.INACTIVE_HAND_ELEVATION.dp
                    }
                ),
                shape = RoundedCornerShape(Tokens.cornerRadius(screenWidth))
            ) {
                Column(
                    modifier = Modifier.padding(Tokens.Space.m),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Tokens.Space.s)
                ) {
                    // Cards display
                    Row(horizontalArrangement = Arrangement.spacedBy(Tokens.Space.xs)) {
                        hand.cards.forEach { card ->
                            CardImageDisplay(
                                card = card, 
                                size = Tokens.Card.medium
                            )
                        }
                    }
                    
                    // Hand value with soft indicator
                    HandValueDisplay(
                        hand = hand,
                        isActive = showActiveIndicators
                    )
                }
            }

            // Status overlay for completed hands
            StatusOverlay(
                status = hand.status,
                isBusted = hand.isBusted,
                showStatus = hand.isBusted || phase == GamePhase.SETTLEMENT,
                modifier = Modifier.matchParentSize()
            )
        }
        
        // Chip display below card
        if (hand.bet > 0) {
            ChipStackDisplay(
                chipComposition = chipCompositionService.calculateOptimalComposition(hand.bet),
                isActive = showActiveIndicators
            )
        }
    }
}

@Composable
private fun HandValueDisplay(
    hand: PlayerHand,
    isActive: Boolean
) {
    val textColor = if (isActive) {
        Color.White
    } else {
        Color.White.copy(alpha = 0.8f)
    }
    
    Text(
        text = "${hand.bestValue}${if (hand.isSoft) " (soft)" else ""}",
        color = textColor,
        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
        fontSize = if (isActive) 16.sp else 14.sp
    )
}

@Composable
private fun ChipStackDisplay(
    chipComposition: List<ChipInSpot>,
    isActive: Boolean
) {
    Box(
        modifier = Modifier.size(AppConstants.Dimensions.CHIP_SIZE_COMPACT.dp),
        contentAlignment = Alignment.Center
    ) {
        chipComposition.forEachIndexed { index, chipInSpot ->
            val offsetX = (index * AppConstants.ChipStack.HORIZONTAL_OFFSET).dp
            val offsetY = (index * AppConstants.ChipStack.VERTICAL_OFFSET).dp
            
            Box(modifier = Modifier.offset(x = offsetX, y = -offsetY)) {
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

/**
 * Calculates optimal scroll offset for centering active hand
 */
private fun getScrollOffsetForCentering(screenWidth: ScreenWidth): Int {
    return when (screenWidth) {
        ScreenWidth.COMPACT -> CarouselConstants.COMPACT_CENTER_OFFSET
        ScreenWidth.MEDIUM -> CarouselConstants.MEDIUM_CENTER_OFFSET
        ScreenWidth.EXPANDED -> CarouselConstants.EXPANDED_CENTER_OFFSET
    }
}

/**
 * Constants for carousel behavior - avoiding magic numbers
 */
private object CarouselConstants {
    const val ACTIVE_SCALE = 1.1f
    const val INACTIVE_SCALE = 1.0f
    
    // Centering offsets for different screen sizes
    const val COMPACT_CENTER_OFFSET = -100
    const val MEDIUM_CENTER_OFFSET = -150
    const val EXPANDED_CENTER_OFFSET = -200
}