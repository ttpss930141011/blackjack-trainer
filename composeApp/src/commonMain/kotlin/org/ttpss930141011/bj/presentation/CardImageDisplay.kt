package org.ttpss930141011.bj.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.Card

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

enum class CardSize(val width: Dp, val height: Dp) {
    SMALL(40.dp, 56.dp),
    MEDIUM(60.dp, 84.dp), 
    LARGE(80.dp, 112.dp)
}