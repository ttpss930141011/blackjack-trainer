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
import org.ttpss930141011.bj.presentation.design.CasinoSemanticColors
import org.ttpss930141011.bj.presentation.design.CasinoTheme
import org.ttpss930141011.bj.presentation.components.navigation.NavigationPage

/**
 * Modern adaptive header for mobile-first design
 * Only shows full header on HOME page, minimal on others
 */
@Composable
fun Header(
    balance: Int,
    onShowSettings: () -> Unit,
    hasStats: Boolean,
    onShowSummary: () -> Unit,
    drawerButton: (@Composable () -> Unit)? = null,
    currentPage: NavigationPage? = null
) {
    when (currentPage) {
        NavigationPage.HOME -> {
            // Full header only on HOME page
            BreakpointLayout(
                compact = { HomeCompactHeader(balance, onShowSettings, hasStats, onShowSummary) },
                expanded = { HomeExpandedHeader(balance, onShowSettings, hasStats, onShowSummary, drawerButton) }
            )
        }
        else -> {
            // Minimal header for other pages
            BreakpointLayout(
                compact = { MinimalHeader(currentPage) },
                expanded = { MinimalExpandedHeader(currentPage, drawerButton) }
            )
        }
    }
}

@Composable
private fun HomeCompactHeader(
    balance: Int,
    onShowSettings: () -> Unit,
    hasStats: Boolean,
    onShowSummary: () -> Unit
) {
    Layout { screenWidth ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CasinoTheme.HeaderBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左側：空間保留對稱
            Box(modifier = Modifier.weight(1f))
            
            // 中間：現代化餘額顯示
            Box(
                modifier = Modifier.weight(2f),
                contentAlignment = Alignment.Center
            ) {
                ModernBalanceBadge(balance, screenWidth)
            }
            
            // 右側：統計按鈕
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (hasStats) {
                    IconButton(onClick = onShowSummary) {
                        Text(
                            text = "📊",
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
private fun HomeExpandedHeader(
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
                .background(CasinoTheme.HeaderBackground)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左側：Drawer按鈕
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                drawerButton?.invoke()
            }
            
            // 中間：應用標題
            Box(
                modifier = Modifier.weight(2f),
                contentAlignment = Alignment.Center
            ) {
                ModernTitle()
            }
            
            // 右側：餘額和動作
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ModernBalanceBadge(balance, screenWidth)
                    if (hasStats) {
                        IconButton(onClick = onShowSummary) {
                            Text(
                                text = "📊",
                                fontSize = 20.sp,
                                color = CasinoTheme.NavigationSelected
                            )
                        }
                    }
                    IconButton(onClick = onShowSettings) {
                        Text(
                            text = "⚙️",
                            fontSize = 20.sp,
                            color = CasinoTheme.NavigationUnselected
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MinimalHeader(currentPage: NavigationPage?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CasinoTheme.NavigationBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = getPageTitle(currentPage),
            style = MaterialTheme.typography.titleMedium,
            color = CasinoTheme.CasinoPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun MinimalExpandedHeader(
    currentPage: NavigationPage?,
    drawerButton: (@Composable () -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CasinoTheme.NavigationBackground)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            drawerButton?.invoke()
            Text(
                text = getPageTitle(currentPage),
                style = MaterialTheme.typography.titleLarge,
                color = CasinoTheme.CasinoPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun getPageTitle(page: NavigationPage?): String {
    return when (page) {
        NavigationPage.STRATEGY -> "Strategy Guide"
        NavigationPage.HISTORY -> "Game History"
        NavigationPage.STATISTICS -> "Statistics"
        NavigationPage.SETTINGS -> "Settings"
        else -> ""
    }
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
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左側：保留空間用於對稱
            Box(
                modifier = Modifier.weight(1f)
            )
            
            // 中間：只顯示餘額
            Box(
                modifier = Modifier.weight(2f),
                contentAlignment = Alignment.Center
            ) {
                CompactBalanceBadge(balance, screenWidth)
            }
            
            // 右側：統計按鈕（如果有的話）
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
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
                .fillMaxWidth(),
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
        colors = CasinoSemanticColors.balanceColors(),
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
        colors = CasinoSemanticColors.balanceColors(),
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
                        horizontal = Tokens.Space.s,
                        vertical = Tokens.Space.s
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

@Composable
private fun ModernTitle() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "BlackJack Trainer",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        Text(
            text = "Master Basic Strategy",
            style = MaterialTheme.typography.bodyMedium,
            color = CasinoTheme.NavigationUnselected,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ModernBalanceBadge(
    balance: Int, 
    screenWidth: org.ttpss930141011.bj.presentation.layout.ScreenWidth
) {
    val badgeHeight = when (screenWidth) {
        org.ttpss930141011.bj.presentation.layout.ScreenWidth.COMPACT -> 44.dp
        org.ttpss930141011.bj.presentation.layout.ScreenWidth.MEDIUM -> 48.dp
        else -> 52.dp
    }
    
    val fontSize = when (screenWidth) {
        org.ttpss930141011.bj.presentation.layout.ScreenWidth.COMPACT -> 16.sp
        org.ttpss930141011.bj.presentation.layout.ScreenWidth.MEDIUM -> 17.sp
        else -> 18.sp
    }
    
    Card(
        colors = CasinoSemanticColors.balanceColors(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.height(badgeHeight)
    ) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = when (screenWidth) {
                        org.ttpss930141011.bj.presentation.layout.ScreenWidth.COMPACT -> 16.dp
                        org.ttpss930141011.bj.presentation.layout.ScreenWidth.MEDIUM -> 20.dp
                        else -> 24.dp
                    },
                    vertical = 8.dp
                )
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💰",
                fontSize = (fontSize.value - 2).sp,
                color = CasinoTheme.CasinoPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$$balance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize,
                color = CasinoTheme.BalanceAccent,
                letterSpacing = 0.5.sp
            )
        }
    }
}