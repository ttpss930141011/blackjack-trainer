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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.presentation.layout.BreakpointLayout
import org.ttpss930141011.bj.presentation.layout.Layout
import org.ttpss930141011.bj.presentation.design.Tokens

/**
 * Game header with adaptive layout
 */
@Composable
fun Header(
    balance: Int,
    onShowSettings: () -> Unit,
    hasStats: Boolean,
    onShowSummary: () -> Unit,
    drawerButton: (@Composable () -> Unit)? = null
) {
    BreakpointLayout(
        compact = { CompactLayout(balance, onShowSettings, hasStats, onShowSummary, drawerButton) },
        expanded = { ExpandedLayout(balance, onShowSettings, hasStats, onShowSummary, drawerButton) }
    )
}

@Composable
private fun CompactLayout(
    balance: Int,
    onShowSettings: () -> Unit,
    hasStats: Boolean,
    onShowSummary: () -> Unit,
    drawerButton: (@Composable () -> Unit)?
) {
    Layout { screenWidth ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Tokens.padding(screenWidth)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左側：Drawer 按鈕
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                drawerButton?.invoke()
            }
            
            // 中間：標題
            Box(
                modifier = Modifier.weight(2f),
                contentAlignment = Alignment.Center
            ) {
                Title(compact = true)
            }
            
            // 右側：餘額 + 設定
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompactBalanceBadge(balance, screenWidth)
                    if (hasStats) {
                        IconButton(
                            onClick = onShowSummary
                        ) {
                            Text(
                                text = "📊",
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    }
                    IconButton(
                        onClick = onShowSettings
                    ) {
                        Text(
                            text = "⚙️",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandedLayout(
    balance: Int,
    onShowSettings: () -> Unit,
    hasStats: Boolean,
    onShowSummary: () -> Unit,
    drawerButton: (@Composable () -> Unit)?
) {
    Layout { screenWidth ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Tokens.padding(screenWidth)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左側：Drawer 按鈕
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                drawerButton?.invoke()
            }
            
            // 中間：標題
            Box(
                modifier = Modifier.weight(2f),
                contentAlignment = Alignment.Center
            ) {
                Title(compact = false)
            }
            
            // 右側：餘額 + 設定
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BalanceCard(balance, fullWidth = false, screenWidth = screenWidth)
                    if (hasStats) {
                        IconButton(
                            onClick = onShowSummary
                        ) {
                            Text(
                                text = "📊",
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        }
                    }
                    IconButton(
                        onClick = onShowSettings
                    ) {
                        Text(
                            text = "⚙️",
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Title(
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (compact) "BlackJack Trainer" else "BlackJack Strategy Trainer",
            style = if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = if (compact) 16.sp else 24.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = if (compact) "Master Strategy" else "Learn Optimal Basic Strategy",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFA5D6A7),
            fontSize = if (compact) 11.sp else 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CompactBalanceBadge(
    balance: Int, 
    screenWidth: org.ttpss930141011.bj.presentation.layout.ScreenWidth
) {
    val badgeHeight = when (screenWidth) {
        org.ttpss930141011.bj.presentation.layout.ScreenWidth.COMPACT -> 40.dp
        org.ttpss930141011.bj.presentation.layout.ScreenWidth.MEDIUM -> 44.dp
        else -> 48.dp
    }
    
    val cornerRadius = when (screenWidth) {
        org.ttpss930141011.bj.presentation.layout.ScreenWidth.COMPACT -> 20.dp
        org.ttpss930141011.bj.presentation.layout.ScreenWidth.MEDIUM -> 22.dp
        else -> 24.dp
    }
    
    val fontSize = when (screenWidth) {
        org.ttpss930141011.bj.presentation.layout.ScreenWidth.COMPACT -> 16.sp
        org.ttpss930141011.bj.presentation.layout.ScreenWidth.MEDIUM -> 17.sp
        else -> 18.sp
    }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFC107)
        ),
        shape = RoundedCornerShape(cornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .height(badgeHeight)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor = Color.Black.copy(alpha = 0.25f)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = when (screenWidth) {
                        org.ttpss930141011.bj.presentation.layout.ScreenWidth.COMPACT -> 12.dp
                        org.ttpss930141011.bj.presentation.layout.ScreenWidth.MEDIUM -> 16.dp
                        else -> 20.dp
                    },
                    vertical = 6.dp
                )
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (screenWidth != org.ttpss930141011.bj.presentation.layout.ScreenWidth.COMPACT) {
                Text(
                    text = "💰",
                    fontSize = (fontSize.value - 1).sp,
                    color = Color.Black.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = "$$balance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                fontSize = fontSize,
                color = Color.Black,
                letterSpacing = if (screenWidth == org.ttpss930141011.bj.presentation.layout.ScreenWidth.COMPACT) 
                    0.3.sp else 0.5.sp
            )
        }
    }
}

@Composable
private fun BalanceCard(
    balance: Int,
    fullWidth: Boolean,
    screenWidth: org.ttpss930141011.bj.presentation.layout.ScreenWidth
) {
    Card(
        modifier = Modifier.let {
            if (fullWidth) Modifier.fillMaxWidth() else Modifier
        },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107)),
        shape = RoundedCornerShape(Tokens.cornerRadius(screenWidth))
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
                        horizontal = Tokens.padding(screenWidth),
                        vertical = Tokens.spacing(screenWidth)
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
    onShowSettings: () -> Unit,
    screenWidth: org.ttpss930141011.bj.presentation.layout.ScreenWidth
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Tokens.spacing(screenWidth)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasStats) {
            ActionButton(
                icon = "📊",
                onClick = onShowSummary,
                color = Color(0xFF4CAF50),
                screenWidth = screenWidth
            )
        }
        IconButton(
            onClick = onShowSettings
        ) {
            Text(
                text = "⚙️",
                fontSize = 20.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: String,
    onClick: () -> Unit,
    color: Color,
    screenWidth: org.ttpss930141011.bj.presentation.layout.ScreenWidth
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(Tokens.iconSize(screenWidth))
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