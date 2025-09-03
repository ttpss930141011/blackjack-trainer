package org.ttpss930141011.bj.presentation.components.header

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.presentation.responsive.WindowInfo
import org.ttpss930141011.bj.presentation.responsive.getPadding
import org.ttpss930141011.bj.presentation.shared.*

/**
 * Expanded header layout for large desktop screens
 * Single row with enhanced styling and larger elements
 */

@Composable
fun ExpandedHeader(
    balance: Int,
    onShowSettings: () -> Unit,
    hasStats: Boolean,
    onShowSummary: () -> Unit,
    windowInfo: WindowInfo
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = windowInfo.getPadding()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderTitle(
            title = "Blackjack Strategy Trainer",
            subtitle = "Master optimal basic strategy",
            isCompact = false
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BalanceBadge(
                balance = balance,
                style = BalanceBadgeStyle.ELEVATED
            )
            
            HeaderActions(
                hasStats = hasStats,
                onShowSummary = onShowSummary,
                onShowSettings = onShowSettings,
                size = HeaderActionSize.EXPANDED
            )
        }
    }
}