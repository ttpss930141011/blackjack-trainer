package org.ttpss930141011.bj.presentation.components

import androidx.compose.runtime.Composable
import org.ttpss930141011.bj.presentation.components.navigation.Header

/**
 * Casino header - delegates to unified Header component
 * @deprecated Use Header directly instead
 */
@Composable
fun CasinoHeader(
    balance: Int,
    onShowSettings: () -> Unit,
    hasStats: Boolean,
    onShowSummary: () -> Unit
) {
    Header(balance, onShowSettings, hasStats, onShowSummary)
}