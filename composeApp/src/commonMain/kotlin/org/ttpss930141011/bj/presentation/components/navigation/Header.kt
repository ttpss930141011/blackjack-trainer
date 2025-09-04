package org.ttpss930141011.bj.presentation.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.presentation.layout.BreakpointLayout
import org.ttpss930141011.bj.presentation.design.Tokens

/**
 * Game header with adaptive layout
 */
@Composable
fun Header(
    balance: Int,
    onShowSettings: () -> Unit,
    hasStats: Boolean,
    onShowSummary: () -> Unit
) {
    BreakpointLayout(
        compact = { CompactLayout(balance, onShowSettings, hasStats, onShowSummary) },
        expanded = { ExpandedLayout(balance, onShowSettings, hasStats, onShowSummary) }
    )
}

@Composable
private fun CompactLayout(
    balance: Int,
    onShowSettings: () -> Unit,
    hasStats: Boolean,
    onShowSummary: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Tokens.padding()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Title(compact = true)
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(Tokens.spacing()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompactBalanceBadge(balance)
            Actions(hasStats, onShowSummary, onShowSettings)
        }
    }
}

@Composable
private fun ExpandedLayout(
    balance: Int,
    onShowSettings: () -> Unit,
    hasStats: Boolean,
    onShowSummary: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Tokens.padding()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Title(compact = false)
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(Tokens.spacing()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BalanceCard(balance, fullWidth = false)
            Actions(hasStats, onShowSummary, onShowSettings)
        }
    }
}

@Composable
private fun Title(
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = if (compact) "BJ Trainer" else "Blackjack Strategy Trainer",
            style = if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = if (compact) 18.sp else 24.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = if (compact) "Master strategy" else "Master optimal basic strategy",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFA5D6A7),
            fontSize = if (compact) 12.sp else 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CompactBalanceBadge(balance: Int) {
    Badge(
        containerColor = Color(0xFFFFC107),
        contentColor = Color.Black
    ) {
        Text(
            text = "$$balance",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BalanceCard(
    balance: Int,
    fullWidth: Boolean
) {
    Card(
        modifier = Modifier.let {
            if (fullWidth) Modifier.fillMaxWidth() else Modifier
        },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107)),
        shape = RoundedCornerShape(Tokens.cornerRadius())
    ) {
        BreakpointLayout(
            compact = {
                // Compact: Show only $ icon with number
                Row(
                    modifier = Modifier.padding(
                        horizontal = Tokens.Space.m,
                        vertical = Tokens.Space.s
                    ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$$balance",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            expanded = {
                // Expanded: Show full text
                Row(
                    modifier = Modifier.padding(
                        horizontal = Tokens.padding(),
                        vertical = Tokens.spacing()
                    ),
                    horizontalArrangement = if (fullWidth) {
                        Arrangement.Center
                    } else {
                        Arrangement.spacedBy(Tokens.Space.s)
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
        )
    }
}

@Composable
private fun Actions(
    hasStats: Boolean,
    onShowSummary: () -> Unit,
    onShowSettings: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Tokens.spacing()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasStats) {
            ActionButton(
                icon = "📊",
                onClick = onShowSummary,
                color = Color(0xFF4CAF50)
            )
        }
        ActionButton(
            icon = "⚙️",
            onClick = onShowSettings,
            color = Color.White.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun ActionButton(
    icon: String,
    onClick: () -> Unit,
    color: Color
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(Tokens.iconSize())
            .clip(CircleShape)
            .background(
                Brush.radialGradient(colors = listOf(color, color.copy(alpha = 0.8f)))
            )
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}