package org.ttpss930141011.bj.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.Card
import org.ttpss930141011.bj.domain.Hand
import org.ttpss930141011.bj.domain.PlayerHand
import org.ttpss930141011.bj.presentation.CardImageDisplay
import org.ttpss930141011.bj.presentation.CardSize

/**
 * Shared utilities for card display components.
 * Reduces code duplication across different UI components.
 */
object CardDisplayUtils {
    
    @Composable
    fun CardRow(
        cards: List<Card>,
        size: CardSize = CardSize.MEDIUM,
        modifier: Modifier = Modifier
    ) {
        if (cards.size <= 5) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = modifier
            ) {
                cards.forEach { card ->
                    CardImageDisplay(card = card, size = size)
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = modifier
            ) {
                items(cards) { card ->
                    CardImageDisplay(card = card, size = size)
                }
            }
        }
    }
    
    @Composable
    fun HandValueDisplay(
        value: Int,
        isSoft: Boolean,
        isBusted: Boolean,
        showSoft: Boolean = true,
        textColor: Color = Color.White,
        modifier: Modifier = Modifier
    ) {
        Text(
            text = buildString {
                append("Value: $value")
                if (showSoft && isSoft) {
                    append(" (soft)")
                }
            },
            color = textColor,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier
        )
        
        if (isBusted) {
            Text(
                text = "Busted!",
                color = GameStatusColors.bustColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
    
    @Composable
    fun BetDisplay(
        amount: Int,
        textColor: Color = GameStatusColors.betColor,
        modifier: Modifier = Modifier
    ) {
        Text(
            text = "Bet: $$amount",
            color = textColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier
        )
    }
}