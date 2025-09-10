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
import org.ttpss930141011.bj.domain.valueobjects.GameRules

/**
 * Modern adaptive header for mobile-first design
 * Only shows full header on HOME page, minimal on others
 */
@Composable
fun Header(
    balance: Int,
    drawerButton: (@Composable () -> Unit)? = null,
    currentPage: NavigationPage? = null,
) {
    when (currentPage) {
        NavigationPage.HOME -> {
            // Full header only on HOME page - Stats button removed since Stats/History available via navigation
            BreakpointLayout(
                compact = { HomeCompactHeader(balance) },
                expanded = { HomeExpandedHeader(balance, drawerButton) }
            )
        }
        else -> {
            // Minimal header for other pages
            BreakpointLayout(
                compact = { MinimalHeader(currentPage, drawerButton) },
                expanded = { MinimalExpandedHeader(currentPage, drawerButton) }
            )
        }
    }
}

@Composable
private fun HomeCompactHeader(
    balance: Int,
) {
    Layout { screenWidth ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CasinoTheme.HeaderBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            ModernBalanceBadge(balance)
        }
    }
}

@Composable
private fun HomeExpandedHeader(
    balance: Int,
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
                    ModernBalanceBadge(balance)
                }
            }
        }
    }
}

@Composable
private fun MinimalHeader(
    currentPage: NavigationPage?,
    drawerButton: (@Composable () -> Unit)? = null
) {
    // Match Home page header style with proper background and padding
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CasinoTheme.HeaderBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Hamburger menu positioned on the left
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Hamburger menu
            Box(modifier = Modifier.width(48.dp)) {
                drawerButton?.invoke()
            }
            
            // Center: Page title
            Text(
                text = getPageTitle(currentPage),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            // Right: Balance space for symmetry  
            Box(modifier = Modifier.width(48.dp))
        }
    }
}

@Composable
private fun MinimalExpandedHeader(
    currentPage: NavigationPage?,
    drawerButton: (@Composable () -> Unit)?
) {
    // Match Home page header style with proper background and padding
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CasinoTheme.HeaderBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Hamburger menu
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            drawerButton?.invoke()
        }
        
        // Center: Page title with icon (matching Home page style)
        Box(
            modifier = Modifier.weight(2f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = getPageTitle(currentPage),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Right: Empty space for symmetry (matching Home page layout)
        Box(modifier = Modifier.weight(1f))
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

// Modern UI components
@Composable
private fun ModernTitle() {
    Text(
        text = "Blackjack Trainer",
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

@Composable 
private fun ModernBalanceBadge(
    balance: Int, 
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = CasinoTheme.BalanceBadgeBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "$$balance",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}