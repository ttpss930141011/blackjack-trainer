package org.ttpss930141011.bj.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.domain.*
import org.ttpss930141011.bj.presentation.CardImageDisplay
import org.ttpss930141011.bj.presentation.HoleCardDisplay
import org.ttpss930141011.bj.presentation.CardSize
import org.ttpss930141011.bj.presentation.responsive.ResponsiveLayout
import org.ttpss930141011.bj.presentation.responsive.WindowInfo
import org.ttpss930141011.bj.presentation.responsive.getResponsivePadding
import org.ttpss930141011.bj.presentation.responsive.getResponsiveSpacing
import org.ttpss930141011.bj.presentation.responsive.getCardCornerRadius

@Composable
fun ResponsiveGameTable(
    game: Game,
    modifier: Modifier = Modifier
) {
    ResponsiveLayout { windowInfo ->
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1B5E20).copy(alpha = 0.7f)
            ),
            shape = RoundedCornerShape(windowInfo.getCardCornerRadius()),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (windowInfo.isCompact) 4.dp else 8.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(windowInfo.getResponsivePadding())
            ) {
                when {
                    windowInfo.isCompact -> CompactGameTable(game, windowInfo)
                    windowInfo.isMedium -> TabletGameTable(game, windowInfo)
                    else -> DesktopGameTable(game, windowInfo)
                }
            }
        }
    }
}

@Composable
private fun CompactGameTable(game: Game, windowInfo: WindowInfo) {
    Column(
        verticalArrangement = Arrangement.spacedBy(windowInfo.getResponsiveSpacing())
    ) {
        // Phase indicator (smaller on mobile)
        if (game.phase != GamePhase.WAITING_FOR_BETS) {
            Text(
                text = getCompactPhaseTitle(game.phase),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Dealer section (compact)
        CompactDealerDisplay(
            dealerHand = game.dealer.hand,
            dealerUpCard = game.dealer.upCard,
            phase = game.phase,
            windowInfo = windowInfo
        )
        
        // Player hands (vertical stack on mobile)
        if (game.hasPlayer && game.playerHands.isNotEmpty()) {
            Text(
                text = "Your Hands",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            CompactPlayerHands(
                playerHands = game.playerHands,
                currentHandIndex = game.currentHandIndex,
                phase = game.phase,
                windowInfo = windowInfo
            )
        }
    }
}

@Composable
private fun TabletGameTable(game: Game, windowInfo: WindowInfo) {
    Column(
        verticalArrangement = Arrangement.spacedBy(windowInfo.getResponsiveSpacing())
    ) {
        if (game.phase != GamePhase.WAITING_FOR_BETS) {
            Text(
                text = getPhaseTitle(game.phase),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TabletDealerDisplay(
                dealerHand = game.dealer.hand,
                dealerUpCard = game.dealer.upCard,
                phase = game.phase,
                windowInfo = windowInfo
            )
        }
        
        if (game.hasPlayer && game.playerHands.isNotEmpty()) {
            Text(
                text = "Your Hands",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            TabletPlayerHands(
                playerHands = game.playerHands,
                currentHandIndex = game.currentHandIndex,
                phase = game.phase,
                windowInfo = windowInfo
            )
        }
    }
}

@Composable
private fun DesktopGameTable(game: Game, windowInfo: WindowInfo) {
    // Use original desktop layout with responsive spacing
    Column(
        verticalArrangement = Arrangement.spacedBy(windowInfo.getResponsiveSpacing())
    ) {
        if (game.phase != GamePhase.WAITING_FOR_BETS) {
            Text(
                text = getPhaseTitle(game.phase),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            DesktopDealerDisplay(
                dealerHand = game.dealer.hand,
                dealerUpCard = game.dealer.upCard,
                phase = game.phase,
                windowInfo = windowInfo
            )
        }
        
        if (game.hasPlayer && game.playerHands.isNotEmpty()) {
            Text(
                text = "Your Hands",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                DesktopPlayerHands(
                    playerHands = game.playerHands,
                    currentHandIndex = game.currentHandIndex,
                    phase = game.phase,
                    windowInfo = windowInfo
                )
            }
        }
    }
}

@Composable
private fun CompactPlayerHands(
    playerHands: List<PlayerHand>,
    currentHandIndex: Int,
    phase: GamePhase,
    windowInfo: WindowInfo
) {
    if (playerHands.size == 1) {
        CompactPlayerHandCard(
            hand = playerHands[0],
            handIndex = 0,
            isActive = currentHandIndex == 0,
            phase = phase,
            windowInfo = windowInfo
        )
    } else {
        // Mobile: Stack hands vertically for better visibility
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(300.dp) // Limit height to prevent scrolling issues
        ) {
            itemsIndexed(playerHands) { index, hand ->
                CompactPlayerHandCard(
                    hand = hand,
                    handIndex = index,
                    isActive = currentHandIndex == index,
                    phase = phase,
                    windowInfo = windowInfo,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun CompactPlayerHandCard(
    hand: PlayerHand,
    handIndex: Int,
    isActive: Boolean,
    phase: GamePhase,
    windowInfo: WindowInfo,
    modifier: Modifier = Modifier
) {
    // Use the reusable component for player hands too
    val title = when {
        handIndex > 0 && isActive && phase == GamePhase.PLAYER_ACTIONS -> "Hand ${handIndex + 1} (Your Turn)"
        handIndex > 0 -> "Hand ${handIndex + 1}"
        isActive && phase == GamePhase.PLAYER_ACTIONS -> "Your Turn"
        else -> "Your Hand"
    }
    
    ResponsiveHandCard(
        title = title,
        cards = hand.cards,
        handValue = hand.bestValue,
        isSoft = hand.isSoft,
        isBusted = hand.isBusted,
        isActive = isActive && phase == GamePhase.PLAYER_ACTIONS,
        bet = hand.bet,
        handStatus = hand.status,
        phase = phase,
        windowInfo = windowInfo,
        modifier = modifier
    )
}

// Delegate to existing components for tablet and desktop with responsive sizing
@Composable
private fun TabletPlayerHands(
    playerHands: List<PlayerHand>,
    currentHandIndex: Int,
    phase: GamePhase,
    windowInfo: WindowInfo
) {
    // Similar to desktop but with medium card size
    if (playerHands.size == 1) {
        ResponsivePlayerHandCard(
            hand = playerHands[0],
            handIndex = 0,
            isActive = currentHandIndex == 0,
            phase = phase,
            cardSize = CardSize.MEDIUM,
            windowInfo = windowInfo
        )
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(playerHands) { index, hand ->
                val cardCount = hand.cards.size
                val cardWidth = CardSize.MEDIUM.width.value
                val cardSpacing = 4f
                val contentPadding = 24f
                val textSpace = 60f
                
                val neededWidth = (cardCount * cardWidth) + 
                                ((cardCount - 1) * cardSpacing) + 
                                contentPadding + textSpace
                
                val dynamicWidth = maxOf(180f, neededWidth).dp
                
                ResponsivePlayerHandCard(
                    hand = hand,
                    handIndex = index,
                    isActive = currentHandIndex == index,
                    phase = phase,
                    cardSize = CardSize.MEDIUM,
                    windowInfo = windowInfo,
                    modifier = Modifier.width(dynamicWidth)
                )
            }
        }
    }
}

@Composable
private fun DesktopPlayerHands(
    playerHands: List<PlayerHand>,
    currentHandIndex: Int,
    phase: GamePhase,
    windowInfo: WindowInfo
) {
    // Use existing dynamic width logic
    if (playerHands.size == 1) {
        ResponsivePlayerHandCard(
            hand = playerHands[0],
            handIndex = 0,
            isActive = currentHandIndex == 0,
            phase = phase,
            cardSize = CardSize.MEDIUM,
            windowInfo = windowInfo
        )
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(windowInfo.getResponsiveSpacing())
        ) {
            itemsIndexed(playerHands) { index, hand ->
                val cardCount = hand.cards.size
                val cardWidth = CardSize.MEDIUM.width.value
                val cardSpacing = 4f
                val contentPadding = 24f
                val textSpace = 60f
                
                val neededWidth = (cardCount * cardWidth) + 
                                ((cardCount - 1) * cardSpacing) + 
                                contentPadding + textSpace
                
                val dynamicWidth = maxOf(200f, neededWidth).dp
                
                ResponsivePlayerHandCard(
                    hand = hand,
                    handIndex = index,
                    isActive = currentHandIndex == index,
                    phase = phase,
                    cardSize = CardSize.MEDIUM,
                    windowInfo = windowInfo,
                    modifier = Modifier.width(dynamicWidth)
                )
            }
        }
    }
}

@Composable
private fun ResponsivePlayerHandCard(
    hand: PlayerHand,
    handIndex: Int,
    isActive: Boolean,
    phase: GamePhase,
    cardSize: CardSize,
    windowInfo: WindowInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isActive && phase == GamePhase.PLAYER_ACTIONS) {
                Color(0xFF4CAF50).copy(alpha = 0.8f)
            } else {
                Color(0xFF2E7D32).copy(alpha = 0.6f)
            }
        ),
        shape = RoundedCornerShape(windowInfo.getCardCornerRadius()),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(windowInfo.getResponsivePadding()),
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
                    color = Color(0xFFFFC107),
                    fontWeight = FontWeight.Bold
                )
            }
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(vertical = windowInfo.getResponsiveSpacing())
            ) {
                items(hand.cards) { card ->
                    CardImageDisplay(card = card, size = cardSize)
                }
            }
            
            Text(
                text = "Value: ${hand.bestValue}${if (hand.isSoft) " (soft)" else ""}",
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Bet: $${hand.bet}",
                color = Color(0xFFFFC107),
                fontWeight = FontWeight.Bold
            )
            
            if (phase == GamePhase.SETTLEMENT) {
                val statusColor = when (hand.status) {
                    HandStatus.WIN -> Color(0xFF4CAF50)
                    HandStatus.LOSS -> Color(0xFFF44336)
                    HandStatus.PUSH -> Color(0xFFFFC107)
                    HandStatus.BUSTED -> Color(0xFFF44336)
                    else -> Color.White
                }
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

// Dealer display components (simplified for brevity - similar pattern)
@Composable
private fun CompactDealerDisplay(
    dealerHand: Hand?,
    dealerUpCard: Card?,
    phase: GamePhase,
    windowInfo: WindowInfo
) {
    // Convert dealer hand to use the same reusable component
    ResponsiveHandCard(
        title = "Dealer",
        cards = when (phase) {
            GamePhase.PLAYER_ACTIONS -> listOfNotNull(dealerUpCard)
            else -> dealerHand?.cards ?: emptyList()
        },
        showHoleCard = phase == GamePhase.PLAYER_ACTIONS,
        handValue = if (phase == GamePhase.DEALER_TURN || phase == GamePhase.SETTLEMENT) {
            dealerHand?.bestValue
        } else null,
        isSoft = dealerHand?.isSoft ?: false,
        isBusted = dealerHand?.isBusted ?: false,
        isDealer = true,
        upCardInfo = if (phase == GamePhase.PLAYER_ACTIONS) "Up Card: ${dealerUpCard?.rank}" else null,
        windowInfo = windowInfo
    )
}

// Reusable hand card component for both dealer and player
@Composable
private fun ResponsiveHandCard(
    title: String,
    cards: List<Card>,
    showHoleCard: Boolean = false,
    handValue: Int? = null,
    isSoft: Boolean = false,
    isBusted: Boolean = false,
    isDealer: Boolean = false,
    isActive: Boolean = false,
    upCardInfo: String? = null,
    bet: Int? = null,
    handStatus: HandStatus? = null,
    phase: GamePhase? = null,
    windowInfo: WindowInfo,
    modifier: Modifier = Modifier
) {
    // Calculate optimal width based on content
    val totalCards = cards.size + if (showHoleCard) 1 else 0
    val cardWidth = CardSize.MEDIUM.width // Always use consistent card width
    val cardSpacing = 6.dp
    val contentPadding = windowInfo.getResponsivePadding() * 2
    
    // Use same minimum width for both dealer and player for consistent sizing
    val minWidth = 140.dp
    
    val optimalWidth = maxOf(
        minWidth,
        (cardWidth * totalCards) + (cardSpacing * maxOf(0, totalCards - 1)) + contentPadding
    )
    
    val containerColor = when {
        isDealer -> Color.White.copy(alpha = 0.9f)
        isActive -> Color(0xFF4CAF50).copy(alpha = 0.8f)
        else -> Color(0xFF2E7D32).copy(alpha = 0.6f)
    }
    
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.width(optimalWidth),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            shape = RoundedCornerShape(windowInfo.getCardCornerRadius()),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(windowInfo.getResponsivePadding()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (windowInfo.isCompact) 12.sp else 14.sp,
                    color = if (isDealer) Color.Black else Color.White
                )
                
                // Cards display
                if (cards.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(cardSpacing),
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        // Define consistent card size for all cards in this hand
                        val consistentCardSize = CardSize.MEDIUM // Always use MEDIUM for consistency with Player cards
                        
                        cards.forEach { card ->
                            CardImageDisplay(
                                card = card, 
                                size = consistentCardSize
                            )
                        }
                        
                        // Show hole card if needed
                        if (showHoleCard) {
                            HoleCardDisplay(
                                size = consistentCardSize
                            )
                        }
                    }
                }
                
                // Up card info (dealer specific)
                upCardInfo?.let { info ->
                    Text(
                        text = info,
                        fontSize = 12.sp,
                        color = if (isDealer) Color.Black else Color.White
                    )
                }
                
                // Hand value and bet info for player
                if (!isDealer && (handValue != null || bet != null)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        handValue?.let { value ->
                            Text(
                                text = "Value: $value${if (isSoft) " (soft)" else ""}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                        bet?.let { betAmount ->
                            Text(
                                text = "$$betAmount",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFC107)
                            )
                        }
                    }
                } else if (isDealer) {
                    // Dealer value display
                    handValue?.let { value ->
                        Text(
                            text = "Value: $value${if (isSoft) " (soft)" else ""}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
                
                // Settlement status for player
                if (phase == GamePhase.SETTLEMENT && handStatus != null && !isDealer) {
                    val statusColor = when (handStatus) {
                        HandStatus.WIN -> Color(0xFF4CAF50)
                        HandStatus.LOSS -> Color(0xFFF44336)
                        HandStatus.PUSH -> Color(0xFFFFC107)
                        HandStatus.BUSTED -> Color(0xFFF44336)
                        else -> Color.White
                    }
                    Text(
                        text = handStatus.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
                
                // Busted indicator
                if (isBusted) {
                    Text(
                        text = "Busted!",
                        color = Color(0xFFF44336),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// Similar implementations for TabletDealerDisplay and DesktopDealerDisplay
@Composable
private fun TabletDealerDisplay(
    dealerHand: Hand?,
    dealerUpCard: Card?,
    phase: GamePhase,
    windowInfo: WindowInfo
) {
    // Implementation similar to compact but with medium card size
    CompactDealerDisplay(dealerHand, dealerUpCard, phase, windowInfo)
}

@Composable
private fun DesktopDealerDisplay(
    dealerHand: Hand?,
    dealerUpCard: Card?,
    phase: GamePhase,
    windowInfo: WindowInfo
) {
    // Implementation similar to existing but with responsive spacing
    CompactDealerDisplay(dealerHand, dealerUpCard, phase, windowInfo)
}

private fun getCompactPhaseTitle(phase: GamePhase): String = when (phase) {
    GamePhase.PLAYER_ACTIONS -> "Your Turn"
    GamePhase.DEALER_TURN -> "Dealer's Turn"
    GamePhase.SETTLEMENT -> "Results"
    else -> "Game"
}

private fun getPhaseTitle(phase: GamePhase): String = when (phase) {
    GamePhase.PLAYER_ACTIONS -> "Player Actions"
    GamePhase.DEALER_TURN -> "Dealer's Turn"
    GamePhase.SETTLEMENT -> "Round Results"
    else -> "Game Table"
}