package org.ttpss930141011.bj.presentation.responsive

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Screen size classifications for responsive design
 */
enum class ScreenSize {
    COMPACT,    // < 600dp (phones)
    MEDIUM,     // 600-840dp (tablets)
    EXPANDED    // > 840dp (desktop)
}

/**
 * Orientation classifications
 */
enum class Orientation {
    PORTRAIT,
    LANDSCAPE
}

/**
 * Window information for responsive decisions
 */
data class WindowInfo(
    val screenSize: ScreenSize,
    val orientation: Orientation,
    val width: Dp,
    val height: Dp,
    val isCompact: Boolean,
    val isMedium: Boolean,
    val isExpanded: Boolean
)

/**
 * Responsive container that provides window information to content
 */
@Composable
fun ResponsiveLayout(
    content: @Composable BoxWithConstraintsScope.(WindowInfo) -> Unit
) {
    BoxWithConstraints {
        val screenSize = when {
            maxWidth < 600.dp -> ScreenSize.COMPACT
            maxWidth < 840.dp -> ScreenSize.MEDIUM
            else -> ScreenSize.EXPANDED
        }
        
        val orientation = if (maxWidth > maxHeight) {
            Orientation.LANDSCAPE
        } else {
            Orientation.PORTRAIT
        }
        
        val windowInfo = WindowInfo(
            screenSize = screenSize,
            orientation = orientation,
            width = maxWidth,
            height = maxHeight,
            isCompact = screenSize == ScreenSize.COMPACT,
            isMedium = screenSize == ScreenSize.MEDIUM,
            isExpanded = screenSize == ScreenSize.EXPANDED
        )
        
        content(windowInfo)
    }
}

/**
 * Get responsive padding based on screen size
 */
fun WindowInfo.getResponsivePadding(): Dp = when (screenSize) {
    ScreenSize.COMPACT -> 8.dp
    ScreenSize.MEDIUM -> 16.dp
    ScreenSize.EXPANDED -> 24.dp
}

/**
 * Get responsive spacing based on screen size
 */
fun WindowInfo.getResponsiveSpacing(): Dp = when (screenSize) {
    ScreenSize.COMPACT -> 8.dp
    ScreenSize.MEDIUM -> 12.dp
    ScreenSize.EXPANDED -> 16.dp
}

/**
 * Get card corner radius based on screen size
 */
fun WindowInfo.getCardCornerRadius(): Dp = when (screenSize) {
    ScreenSize.COMPACT -> 12.dp
    ScreenSize.MEDIUM -> 16.dp
    ScreenSize.EXPANDED -> 20.dp
}

/**
 * Check if device is mobile-sized
 */
val WindowInfo.isMobile: Boolean
    get() = screenSize == ScreenSize.COMPACT

/**
 * Check if device needs touch-friendly sizing
 */
val WindowInfo.needsTouchOptimization: Boolean
    get() = screenSize == ScreenSize.COMPACT

/**
 * Get button height for touch optimization
 */
fun WindowInfo.getButtonHeight(): Dp = when {
    needsTouchOptimization -> 56.dp
    else -> 48.dp
}

/**
 * Get minimum touch target size
 */
fun WindowInfo.getMinTouchTarget(): Dp = if (needsTouchOptimization) 48.dp else 44.dp