package org.ttpss930141011.bj.presentation.components.displays

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import org.ttpss930141011.bj.presentation.design.Tokens

/**
 * Display component for deck information with card back image
 * Shows remaining deck count and provides visual consistency
 */
@Composable
fun DeckIndicatorArea(
    remainingCards: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Tokens.Space.xs)
    ) {
        // Card back - always visible for visual consistency (smaller size to minimize space)
        HoleCardDisplay(size = Tokens.Card.small)
        
        // Deck count text with business logic
        DeckCountText(remainingCards = remainingCards)
    }
}

@Composable
private fun DeckCountText(remainingCards: Int) {
    // Business constants - no magic numbers
    val standardDeckSize = 52
    val minimumDeckThreshold = 1.0
    
    val decksRemaining = remainingCards.toDouble() / standardDeckSize
    val displayText = if (decksRemaining >= minimumDeckThreshold) {
        val rounded = (decksRemaining * 10).toInt() / 10.0
        "$rounded decks"
    } else {
        "<1 deck"
    }
    
    Text(
        text = displayText,
        color = Color.White,
        style = MaterialTheme.typography.labelSmall,
        textAlign = TextAlign.Center
    )
}