package org.ttpss930141011.bj.presentation.components.displays

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import org.ttpss930141011.bj.domain.Card
import org.ttpss930141011.bj.presentation.mappers.CardImageMapper
import org.ttpss930141011.bj.presentation.shared.CardSize

/**
 * Display component for showing card images
 * Handles both regular cards and hole cards
 */

@Composable
fun CardImageDisplay(
    card: Card,
    modifier: Modifier = Modifier,
    size: CardSize = CardSize.MEDIUM
) {
    Image(
        painter = CardImageMapper.getCardPainter(card),
        contentDescription = "${card.rank} of ${card.suit}",
        modifier = modifier.size(size.width, size.height),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun HoleCardDisplay(
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