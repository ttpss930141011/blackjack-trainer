package org.ttpss930141011.bj.presentation.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import org.ttpss930141011.bj.presentation.design.Strings
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.presentation.design.CasinoSemanticColors
import org.ttpss930141011.bj.presentation.design.CasinoTheme
import org.ttpss930141011.bj.presentation.components.navigation.NavigationPage
import org.ttpss930141011.bj.domain.valueobjects.GameRules

/**
 * Modern adaptive header for mobile-first design
 * Home: Balance (center) + Menu (right)
 * Sub-pages: Back (left) + Title (center) + Menu (right)
 */
@Composable
fun Header(
    balance: Int,
    currentPage: NavigationPage? = null,
    onBackClick: (() -> Unit)? = null,
    isMenuExpanded: Boolean = false,
    onMenuExpandedChange: (Boolean) -> Unit = {},
    onNavigate: (NavigationPage) -> Unit = {},
    sessionCorrect: Int = 0,
    sessionTotal: Int = 0,
) {
    when (currentPage) {
        NavigationPage.HOME -> {
            HomeHeader(balance, isMenuExpanded, onMenuExpandedChange, onNavigate, sessionCorrect, sessionTotal)
        }
        else -> {
            // Sub-page header: Back (left) + Title (center) + Menu (right)
            SubPageHeader(currentPage, onBackClick, isMenuExpanded, onMenuExpandedChange, onNavigate)
        }
    }
}

@Composable
private fun HomeHeader(
    balance: Int,
    isMenuExpanded: Boolean,
    onMenuExpandedChange: (Boolean) -> Unit,
    onNavigate: (NavigationPage) -> Unit,
    sessionCorrect: Int,
    sessionTotal: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CasinoTheme.HeaderBackground)
            .padding(horizontal = Tokens.Space.l, vertical = Tokens.Space.m)
    ) {
        // Accuracy badge on the left
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (sessionTotal > 0) {
                val pct = (sessionCorrect * 100) / sessionTotal
                val color = when {
                    pct >= 80 -> Color(0xFF4CAF50)
                    pct >= 60 -> Color(0xFFFFB74D)
                    else -> Color(0xFFF44336)
                }
                Text(
                    text = "$pct%",
                    color = color,
                    fontSize = Tokens.Typography.actionButtonTextExpanded,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " ($sessionCorrect/$sessionTotal)",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = Tokens.Typography.actionButtonHintCompact
                )
            }
        }

        // Balance badge in center
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            ModernBalanceBadge(balance)
        }
        
        // Menu button on the right with dropdown menu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Box {
                NavigationMenuButton(onClick = { onMenuExpandedChange(true) })
                
                // NavigationMenu positioned relative to this Box (Material 3 standard)
                NavigationMenu(
                    expanded = isMenuExpanded,
                    onDismiss = { onMenuExpandedChange(false) },
                    onNavigate = { page ->
                        onNavigate(page)
                        onMenuExpandedChange(false)
                    }
                )
            }
        }
    }
}


@Composable
private fun SubPageHeader(
    currentPage: NavigationPage?,
    onBackClick: (() -> Unit)?,
    isMenuExpanded: Boolean,
    onMenuExpandedChange: (Boolean) -> Unit,
    onNavigate: (NavigationPage) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CasinoTheme.HeaderBackground)
            .padding(horizontal = Tokens.Space.l, vertical = Tokens.Space.m)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Back button
            IconButton(onClick = { onBackClick?.invoke() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            
            // Center: Page title
            Text(
                text = getPageTitle(currentPage),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            // Right: Menu button with dropdown menu
            Box {
                NavigationMenuButton(onClick = { onMenuExpandedChange(true) })
                
                // NavigationMenu positioned relative to this Box (Material 3 standard)
                NavigationMenu(
                    expanded = isMenuExpanded,
                    onDismiss = { onMenuExpandedChange(false) },
                    onNavigate = { page ->
                        onNavigate(page)
                        onMenuExpandedChange(false)
                    }
                )
            }
        }
    }
}


private fun getPageTitle(page: NavigationPage?): String {
    return when (page) {
        NavigationPage.STRATEGY -> Strings.Nav.STRATEGY_GUIDE
        NavigationPage.HISTORY -> Strings.Nav.GAME_HISTORY
        NavigationPage.SETTINGS -> Strings.Nav.SETTINGS
        else -> ""
    }
}

// Modern UI components

@Composable 
private fun ModernBalanceBadge(
    balance: Int, 
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = CasinoTheme.BalanceBadgeBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Tokens.Space.xs)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Tokens.Space.m, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Tokens.Space.xs)
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