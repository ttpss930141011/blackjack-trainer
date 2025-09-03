package org.ttpss930141011.bj.presentation.shared

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Standardized sizes for game components
 * Centralized sizing ensures UI consistency
 */

enum class CardSize(val width: Dp, val height: Dp) {
    SMALL(50.dp, 70.dp),
    MEDIUM(80.dp, 112.dp), 
    LARGE(100.dp, 140.dp)
}

enum class ChipSize(val diameter: Dp) {
    SMALL(60.dp),
    MEDIUM(85.dp), 
    LARGE(110.dp)
}

/**
 * Additional standard sizes for other UI components
 */
object ComponentSizes {
    val iconButtonSmall = 40.dp
    val iconButtonMedium = 44.dp  
    val iconButtonLarge = 48.dp
    
    val cardCornerRadius = 4.dp
    val chipElevation = 4.dp
    val cardElevation = 6.dp
    
    val headerPaddingCompact = 12.dp
    val headerPaddingMedium = 16.dp
    val headerPaddingExpanded = 20.dp
}