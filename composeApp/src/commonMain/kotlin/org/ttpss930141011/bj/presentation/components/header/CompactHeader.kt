package org.ttpss930141011.bj.presentation.components.header

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.presentation.responsive.WindowInfo
import org.ttpss930141011.bj.presentation.responsive.getPadding
import org.ttpss930141011.bj.presentation.shared.*

/**
 * Compact header layout for small screens
 * Two-row layout with title/settings above and balance below
 */

@Composable
fun CompactHeader(
    balance: Int,
    onShowSettings: () -> Unit,
    hasStats: Boolean,
    onShowSummary: () -> Unit,
    windowInfo: WindowInfo
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = windowInfo.getPadding())
    ) {
        // First row: Title and Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderTitle(
                title = "BJ Strategy Trainer",
                subtitle = "Master basic strategy",
                isCompact = true,
                modifier = Modifier.weight(1f)
            )
            
            HeaderActions(
                hasStats = hasStats,
                onShowSummary = onShowSummary,
                onShowSettings = onShowSettings,
                size = HeaderActionSize.COMPACT
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Second row: Balance badge
        BalanceBadge(
            balance = balance,
            style = BalanceBadgeStyle.FULL_WIDTH
        )
    }
}