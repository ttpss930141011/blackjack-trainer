package org.ttpss930141011.bj.presentation.components.displays

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import org.ttpss930141011.bj.presentation.mappers.ChipImageMapper
import org.ttpss930141011.bj.presentation.shared.ChipSize

/**
 * Display component for showing chip images with animation
 * Handles click interactions and visual feedback
 */

@Composable
fun ChipImageDisplay(
    value: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ChipSize = ChipSize.MEDIUM
) {
    val scale by animateFloatAsState(
        targetValue = 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val interactionSource = remember { MutableInteractionSource() }
    
    Image(
        painter = ChipImageMapper.getChipPainter(value),
        contentDescription = "$$value chip",
        modifier = modifier
            .size(size.diameter)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentScale = ContentScale.Fit
    )
}