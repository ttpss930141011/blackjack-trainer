package org.ttpss930141011.bj.presentation.design

/**
 * Application-wide constants to eliminate magic numbers and ensure consistency
 */
object AppConstants {
    
    // Default game values
    object Defaults {
        const val PLAYER_STARTING_CHIPS = 1000
        const val PLAYER_ID = "player1"
    }
    
    // UI dimensions (dp values)
    object Dimensions {
        const val NOTIFICATION_TABLET_WIDTH = 350
        const val NOTIFICATION_DESKTOP_WIDTH = 300
        const val BETTING_CIRCLE_COMPACT = 140
        const val BETTING_CIRCLE_MEDIUM = 160
        const val BETTING_CIRCLE_EXPANDED = 180
        const val CHIP_SIZE_COMPACT = 70
        const val CHIP_SIZE_MEDIUM = 80
    }

    
    // Offset values for chip stacking
    object ChipStack {
        const val HORIZONTAL_OFFSET = 8
        const val VERTICAL_OFFSET = 2
        const val STACK_HORIZONTAL_OFFSET = 2
        const val STACK_VERTICAL_OFFSET = 1
    }
    
    // Card overlapping display ratios
    object Card {
        const val DEFAULT_OVERLAP_RATIO = 0.5f      // Standard 50% overlap
        const val COMPACT_OVERLAP_RATIO = 0.6f      // More overlap for mobile
        const val EXPANDED_OVERLAP_RATIO = 0.4f     // Less overlap for desktop
        const val MINIMUM_VISIBLE_RATIO = 0.3f      // Minimum visible portion
    }
}