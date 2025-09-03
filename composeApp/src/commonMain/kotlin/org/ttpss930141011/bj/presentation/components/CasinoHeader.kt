package org.ttpss930141011.bj.presentation.components

import androidx.compose.runtime.Composable
import org.ttpss930141011.bj.presentation.components.header.*
import org.ttpss930141011.bj.presentation.responsive.ResponsiveLayout

/**
 * Main casino header component with responsive layout switching
 * Delegates to specific header variants based on screen size
 */
@Composable
fun CasinoHeader(
    balance: Int,
    onShowSettings: () -> Unit,
    hasStats: Boolean,
    onShowSummary: () -> Unit
) {
    ResponsiveLayout { windowInfo ->
        when {
            windowInfo.isCompact -> CompactHeader(
                balance = balance,
                onShowSettings = onShowSettings,
                hasStats = hasStats,
                onShowSummary = onShowSummary,
                windowInfo = windowInfo
            )
            windowInfo.isMedium -> MediumHeader(
                balance = balance,
                onShowSettings = onShowSettings,
                hasStats = hasStats,
                onShowSummary = onShowSummary,
                windowInfo = windowInfo
            )
            else -> ExpandedHeader(
                balance = balance,
                onShowSettings = onShowSettings,
                hasStats = hasStats,
                onShowSummary = onShowSummary,
                windowInfo = windowInfo
            )
        }
    }
}