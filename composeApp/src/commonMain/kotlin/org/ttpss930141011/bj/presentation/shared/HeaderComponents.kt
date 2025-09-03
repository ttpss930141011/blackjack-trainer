package org.ttpss930141011.bj.presentation.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Shared header components used across different responsive layouts
 * Provides consistent styling and behavior for header elements
 */

@Composable
fun HeaderTitle(
    title: String,
    subtitle: String,
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = if (isCompact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = if (isCompact) 18.sp else 24.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFA5D6A7),
            fontSize = if (isCompact) 12.sp else 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun BalanceBadge(
    balance: Int,
    style: BalanceBadgeStyle,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.let {
            if (style == BalanceBadgeStyle.FULL_WIDTH) it.fillMaxWidth() else it
        },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107)),
        shape = RoundedCornerShape(when (style) {
            BalanceBadgeStyle.FULL_WIDTH -> 12.dp
            BalanceBadgeStyle.CARD -> 14.dp
            BalanceBadgeStyle.ELEVATED -> 16.dp
        }),
        elevation = CardDefaults.cardElevation(
            defaultElevation = when (style) {
                BalanceBadgeStyle.FULL_WIDTH -> 4.dp
                BalanceBadgeStyle.CARD -> 6.dp  
                BalanceBadgeStyle.ELEVATED -> 8.dp
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = when (style) {
                    BalanceBadgeStyle.FULL_WIDTH -> 16.dp
                    BalanceBadgeStyle.CARD -> 16.dp
                    BalanceBadgeStyle.ELEVATED -> 20.dp
                },
                vertical = when (style) {
                    BalanceBadgeStyle.FULL_WIDTH -> 8.dp
                    BalanceBadgeStyle.CARD -> 10.dp
                    BalanceBadgeStyle.ELEVATED -> 12.dp
                }
            ),
            horizontalArrangement = if (style == BalanceBadgeStyle.FULL_WIDTH) {
                Arrangement.Center
            } else {
                Arrangement.spacedBy(6.dp)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Balance:",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$$balance",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun HeaderActions(
    hasStats: Boolean,
    onShowSummary: () -> Unit,
    onShowSettings: () -> Unit,
    size: HeaderActionSize,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(when (size) {
            HeaderActionSize.COMPACT -> 8.dp
            HeaderActionSize.MEDIUM -> 10.dp
            HeaderActionSize.EXPANDED -> 12.dp
        }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasStats) {
            HeaderIconButton(
                icon = "📊",
                onClick = onShowSummary,
                color = Color(0xFF4CAF50),
                size = size
            )
        }
        HeaderIconButton(
            icon = "⚙️",
            onClick = onShowSettings,
            color = Color.White.copy(alpha = 0.2f),
            size = size
        )
    }
}

@Composable
private fun HeaderIconButton(
    icon: String,
    onClick: () -> Unit,
    color: Color,
    size: HeaderActionSize
) {
    val buttonSize = when (size) {
        HeaderActionSize.COMPACT -> 40.dp
        HeaderActionSize.MEDIUM -> 44.dp
        HeaderActionSize.EXPANDED -> 48.dp
    }
    
    val elevation = when (size) {
        HeaderActionSize.COMPACT -> 0.dp
        HeaderActionSize.MEDIUM -> 4.dp
        HeaderActionSize.EXPANDED -> 6.dp
    }
    
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(buttonSize)
            .clip(CircleShape)
            .background(
                if (size == HeaderActionSize.EXPANDED) {
                    Brush.radialGradient(
                        colors = listOf(color, color.copy(alpha = 0.8f))
                    )
                } else {
                    Brush.radialGradient(
                        colors = listOf(color, color)
                    )
                }
            )
            .let { if (elevation > 0.dp) it.shadow(elevation = elevation, shape = CircleShape) else it }
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}

enum class BalanceBadgeStyle {
    FULL_WIDTH, CARD, ELEVATED
}

enum class HeaderActionSize {
    COMPACT, MEDIUM, EXPANDED
}