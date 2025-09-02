package org.ttpss930141011.bj.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

enum class ChipSize(val diameter: Dp) {
    SMALL(50.dp),
    MEDIUM(70.dp), 
    LARGE(90.dp)
}