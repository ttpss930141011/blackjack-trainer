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
        const val BUTTON_HEIGHT = 56
        const val CHIP_DISPLAY_SIZE = 120
        const val NOTIFICATION_TABLET_WIDTH = 350
        const val NOTIFICATION_DESKTOP_WIDTH = 300
        const val BETTING_CIRCLE_COMPACT = 120
        const val BETTING_CIRCLE_MEDIUM = 140
        const val BETTING_CIRCLE_EXPANDED = 160
        const val CHIP_SIZE_COMPACT = 60
        const val CHIP_SIZE_MEDIUM = 70
    }
    
    // Animation timings (milliseconds)
    object Animation {
        const val FAST = 300L
        const val MEDIUM = 1000L
        const val NOTIFICATION_TIMEOUT_MOBILE = 3000L
        const val NOTIFICATION_TIMEOUT_DESKTOP = 4000L
    }
    
    // Alpha transparency values
    object Alpha {
        const val SEMI_TRANSPARENT = 0.7f
        const val ACTIVE_BACKGROUND = 0.8f
        const val HIGHLIGHTED = 0.9f
        const val NOTIFICATION_BACKGROUND = 0.95f
        const val SUBTLE = 0.6f
        const val DISABLED = 0.5f
        const val PLACEHOLDER = 0.3f
        const val OVERLAY = 0.1f
    }
    
    // Layout ratios
    object Ratio {
        const val BUTTON_WIDTH_FRACTION = 0.8f
        const val DEALER_BUTTON_WIDTH_FRACTION = 0.6f
        const val DIALOG_WIDTH_FRACTION = 0.9f
    }
    
    // Offset values for chip stacking
    object ChipStack {
        const val HORIZONTAL_OFFSET = 8
        const val VERTICAL_OFFSET = 2
        const val STACK_HORIZONTAL_OFFSET = 2
        const val STACK_VERTICAL_OFFSET = 1
    }
}