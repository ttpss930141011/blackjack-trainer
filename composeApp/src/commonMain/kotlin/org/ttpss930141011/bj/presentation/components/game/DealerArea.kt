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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.*
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.presentation.design.AppConstants
import org.ttpss930141011.bj.presentation.components.displays.CardImageDisplay
import org.ttpss930141011.bj.presentation.components.displays.HoleCardDisplay
import org.ttpss930141011.bj.presentation.components.displays.StatusOverlay
import org.ttpss930141011.bj.presentation.components.displays.DeckIndicatorArea
import org.ttpss930141011.bj.presentation.components.displays.OverlappingCardsDisplay
import org.ttpss930141011.bj.presentation.layout.ScreenWidth
import org.ttpss930141011.bj.presentation.design.CasinoTheme
import org.ttpss930141011.bj.presentation.mappers.DealerStatus

/**
 * Dealer area with true centering approach
 * Center: Dealer hand truly centered in available space
 * Left overlay: Deck indicator as non-interfering overlay
 */
@Composable
fun DealerArea(
    game: Game,
    screenWidth: ScreenWidth,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Main content: Dealer hand centered in full width
        DealerHandArea(
            game = game,
            screenWidth = screenWidth,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Overlay: Deck indicator positioned on the left
        DeckIndicatorArea(
            remainingCards = game.deck.remainingCards,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = Tokens.Space.s)
        )
    }
}

@Composable
private fun DealerWaitingDisplay() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = CasinoTheme.CasinoPrimary.copy(alpha = 0.6f)
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

/**
 * Isolated dealer hand area that handles phase-specific display
 * Separated from layout concerns for cleaner separation
 */
@Composable
private fun DealerHandArea(
    game: Game,
    screenWidth: ScreenWidth,
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
                    phase = game.phase,
                    screenWidth = screenWidth
                )
            }
        }
    }
}

@Composable
private fun DealerHandCard(
    dealerHand: Hand?,
    dealerUpCard: Card?,
    phase: GamePhase,
    screenWidth: ScreenWidth
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Tokens.Space.xs)
    ) {
        Box { // Wrap in Box to overlay status
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = CasinoTheme.CasinoPrimary.copy(alpha = 0.6f)
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
                        GamePhase.PLAYER_TURN -> {
                            dealerUpCard?.let { upCard ->
                                // Use overlapping display: hole card first (bottom), then up card (top, overlapping)
                                DealerUpCardWithHoleCard(
                                    upCard = upCard,
                                    screenWidth = screenWidth
                                )
                            }
                        }
                        else -> {
                            // Show full dealer hand with overlapping display for space efficiency
                            dealerHand?.let { hand ->
                                OverlappingCardsDisplay(
                                    cards = hand.cards,
                                    cardSize = Tokens.Card.medium,
                                    screenWidth = screenWidth
                                )
                            }
                        }
                    }
                    
                    // Hand value display (matches PlayerHandCard structure)
                    Text(
                        text = when (phase) {
                            GamePhase.PLAYER_TURN -> dealerUpCard?.let { 
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
            
            // StatusOverlay for dealer states
            val dealerStatus = getDealerStatus(dealerHand, phase)
            val showDealerStatus = dealerStatus != null
            
            StatusOverlay(
                dealerStatus = dealerStatus,
                showDealerStatus = showDealerStatus,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}

// Helper function to determine dealer status
private fun getDealerStatus(dealerHand: Hand?, phase: GamePhase): DealerStatus? {
    return when {
        dealerHand == null -> null
        phase == GamePhase.PLAYER_TURN -> null // Don't show status during player turn
        dealerHand.isBusted -> DealerStatus.BUSTED
        phase == GamePhase.DEALER_TURN -> DealerStatus.HITTING
        phase == GamePhase.SETTLEMENT -> DealerStatus.STANDING
        else -> null
    }
}

/**
 * Displays dealer up card overlapping hole card during player turn
 * Hole card at bottom, up card overlapping on top following same ratio as other cards
 */
@Composable
private fun DealerUpCardWithHoleCard(
    upCard: Card,
    screenWidth: ScreenWidth,
    cardSize: Tokens.CardDimensions = Tokens.Card.medium
) {
    // Use same overlap ratio as other card displays for consistency
    val overlapRatio = when (screenWidth) {
        ScreenWidth.COMPACT -> AppConstants.Card.COMPACT_OVERLAP_RATIO    // 0.6f
        ScreenWidth.MEDIUM -> AppConstants.Card.DEFAULT_OVERLAP_RATIO     // 0.5f  
        ScreenWidth.EXPANDED -> AppConstants.Card.EXPANDED_OVERLAP_RATIO  // 0.4f
    }
    
    // Calculate offset for up card (same logic as OverlappingCardsDisplay)
    val visibleWidth: Dp = cardSize.width * (1f - overlapRatio)
    val totalWidth: Dp = visibleWidth + cardSize.width
    
    Box(
        modifier = Modifier.width(totalWidth)
    ) {
        // Hole card at bottom (position 0)
        HoleCardDisplay(
            size = cardSize,
            modifier = Modifier.offset(x = 0.dp)
        )
        
        // Up card overlapping on top (position with offset)
        CardImageDisplay(
            card = upCard,
            size = cardSize,
            modifier = Modifier.offset(x = visibleWidth)
        )
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