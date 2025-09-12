package org.ttpss930141011.bj.presentation.components.displays

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.presentation.design.AppConstants
import org.ttpss930141011.bj.presentation.layout.ScreenWidth

/**
 * Overlapping cards display component for space-efficient hand visualization
 * 
 * Features:
 * - 50% default overlap ratio for realistic card hand appearance
 * - Responsive overlap ratios based on screen size
 * - Z-order management (last card on top)
 * - Space savings: 40%+ reduction for multi-card hands
 * 
 * Business Logic:
 * - First card: fully visible (establishes baseline)
 * - Subsequent cards: overlap by configured ratio
 * - Each card offset by (cardWidth * (1 - overlapRatio))
 */
@Composable
fun OverlappingCardsDisplay(
    cards: List<Card>,
    modifier: Modifier = Modifier,
    cardSize: Tokens.CardDimensions = Tokens.Card.medium,
    screenWidth: ScreenWidth = ScreenWidth.MEDIUM
) {
    // Handle empty state gracefully
    if (cards.isEmpty()) return
    
    // Business logic: adaptive overlap ratio based on screen size
    val overlapRatio = when (screenWidth) {
        ScreenWidth.COMPACT -> AppConstants.Card.COMPACT_OVERLAP_RATIO    // 0.6f - more overlap for mobile
        ScreenWidth.MEDIUM -> AppConstants.Card.DEFAULT_OVERLAP_RATIO     // 0.5f - standard overlap
        ScreenWidth.EXPANDED -> AppConstants.Card.EXPANDED_OVERLAP_RATIO  // 0.4f - less overlap for desktop
    }
    
    // Calculate card offsets - no magic numbers
    val cardOffsets = calculateCardOffsets(
        cardCount = cards.size,
        cardWidth = cardSize.width,
        overlapRatio = overlapRatio
    )
    
    // Calculate total width needed for the hand
    val totalWidth = if (cards.size == 1) {
        cardSize.width
    } else {
        cardOffsets.last() + cardSize.width
    }
    
    Box(
        modifier = modifier.width(totalWidth)
    ) {
        cards.forEachIndexed { index, card ->
            CardImageDisplay(
                card = card,
                size = cardSize,
                modifier = Modifier.offset(x = cardOffsets[index])
            )
        }
    }
}

/**
 * Overlapping hole cards display for dealer's hidden cards
 * Maintains same overlap logic as regular cards for visual consistency
 */
@Composable
fun OverlappingHoleCardsDisplay(
    cardCount: Int,
    modifier: Modifier = Modifier,
    cardSize: Tokens.CardDimensions = Tokens.Card.medium,
    screenWidth: ScreenWidth = ScreenWidth.MEDIUM
) {
    if (cardCount <= 0) return
    
    // Use same overlap ratio as regular cards
    val overlapRatio = when (screenWidth) {
        ScreenWidth.COMPACT -> AppConstants.Card.COMPACT_OVERLAP_RATIO
        ScreenWidth.MEDIUM -> AppConstants.Card.DEFAULT_OVERLAP_RATIO
        ScreenWidth.EXPANDED -> AppConstants.Card.EXPANDED_OVERLAP_RATIO
    }
    
    val cardOffsets = calculateCardOffsets(
        cardCount = cardCount,
        cardWidth = cardSize.width,
        overlapRatio = overlapRatio
    )
    
    val totalWidth = if (cardCount == 1) {
        cardSize.width
    } else {
        cardOffsets.last() + cardSize.width
    }
    
    Box(
        modifier = modifier.width(totalWidth)
    ) {
        repeat(cardCount) { index ->
            HoleCardDisplay(
                size = cardSize,
                modifier = Modifier.offset(x = cardOffsets[index])
            )
        }
    }
}

/**
 * Pure function to calculate card offset positions
 * 
 * Business logic:
 * - First card at position 0 (fully visible)
 * - Each subsequent card offset by visible portion width
 * - Visible portion = cardWidth * (1 - overlapRatio)
 * 
 * Example with 70dp cards and 0.5 overlap:
 * - Card 1: offset 0dp
 * - Card 2: offset 35dp (70 * 0.5)
 * - Card 3: offset 70dp (35 * 2)
 * - Card 4: offset 105dp (35 * 3)
 */
private fun calculateCardOffsets(
    cardCount: Int,
    cardWidth: Dp,
    overlapRatio: Float
): List<Dp> {
    // Business constant: visible portion width
    val visibleWidth = cardWidth * (1f - overlapRatio)
    
    return (0 until cardCount).map { index ->
        visibleWidth * index
    }
}