package org.ttpss930141011.bj.presentation.layout

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * Clean layout system based on Material 3 breakpoints
 */

enum class ScreenWidth {
    COMPACT,    // < 600dp
    MEDIUM,     // 600dp - 840dp
    EXPANDED    // >= 840dp
}

@Composable
fun Layout(
    content: @Composable (ScreenWidth) -> Unit
) {
    BoxWithConstraints {
        val screenWidth = when {
            maxWidth < 600.dp -> ScreenWidth.COMPACT
            maxWidth < 840.dp -> ScreenWidth.MEDIUM
            else -> ScreenWidth.EXPANDED
        }
        content(screenWidth)
    }
}

@Composable
fun BreakpointLayout(
    compact: @Composable () -> Unit,
    medium: @Composable () -> Unit = compact,
    expanded: @Composable () -> Unit = medium
) {
    Layout { screenWidth ->
        when (screenWidth) {
            ScreenWidth.COMPACT -> compact()
            ScreenWidth.MEDIUM -> medium()
            ScreenWidth.EXPANDED -> expanded()
        }
    }
}

val ScreenWidth.isCompact: Boolean
    get() = this == ScreenWidth.COMPACT

val ScreenWidth.isMedium: Boolean  
    get() = this == ScreenWidth.MEDIUM

val ScreenWidth.isExpanded: Boolean
    get() = this == ScreenWidth.EXPANDED