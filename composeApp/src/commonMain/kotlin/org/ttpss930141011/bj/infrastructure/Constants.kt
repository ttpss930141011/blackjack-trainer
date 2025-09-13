package org.ttpss930141011.bj.infrastructure

/**
 * Infrastructure layer constants for system operations and defaults.
 */
internal object InfrastructureConstants {
    
    // Data loading and caching
    const val DEFAULT_RECENT_DECISIONS_LIMIT = 50
    const val DEFAULT_RECENT_ROUNDS_LIMIT = 50
    const val ANALYTICS_DECISIONS_LIMIT = 100
    const val DATA_CLEANUP_DAYS_THRESHOLD = 30
    const val MIN_SAMPLES_FOR_STATISTICS = 1
    const val WORST_SCENARIOS_LIMIT = 10
    
    // Database versioning
    const val DATABASE_VERSION = 2
    
    // Timeouts and delays (in milliseconds)
    const val ERROR_AUTO_DISMISS_DELAY_MS = 3000L
    const val RULE_CHANGE_NOTIFICATION_DELAY_MS = 5000L
    const val FEEDBACK_DISPLAY_DURATION_SECONDS = 2.5f
    
    // Session management
    const val SESSION_CONTEXT_SIZE = 100
    const val CACHE_CLEANUP_THRESHOLD_SIZE = 1000
}

/**
 * Application layer constants for business logic and user interaction.
 */
internal object ApplicationConstants {
    
    // Player defaults
    const val DEFAULT_STARTING_CHIPS = 1000
    const val DEFAULT_PLAYER_ID = "player1"
    
    // Betting limits
    const val MIN_BET_AMOUNT = 5
    const val MAX_BET_AMOUNT = 500
    const val DEFAULT_BET_AMOUNT = 10
    
    // Game mechanics
    const val BLACKJACK_VALUE = 21
    const val DEALER_HIT_THRESHOLD = 17
    const val DECK_COUNT_STANDARD = 6
    
    // Payout rates
    const val BLACKJACK_PAYOUT_RATIO = 1.5
    const val STANDARD_WIN_PAYOUT_RATIO = 1.0
    const val SURRENDER_PAYOUT_RATIO = 0.5
}

/**
 * UI constants for presentation layer components.
 */
internal object PresentationConstants {
    
    // Animation and timing
    const val CARD_FLIP_ANIMATION_DURATION_MS = 300
    const val FEEDBACK_FADE_DURATION_MS = 250
    const val NOTIFICATION_AUTO_DISMISS_DELAY_MS = 4000L
    
    // Layout dimensions (in dp)
    const val CARD_WIDTH_DP = 60
    const val CARD_HEIGHT_DP = 84
    const val CHIP_SIZE_DP = 40
    const val MINIMUM_TOUCH_TARGET_DP = 48
    
    // Visual feedback
    const val SUCCESS_ALPHA = 0.8f
    const val ERROR_ALPHA = 0.9f
    const val DISABLED_ALPHA = 0.6f
    
    // Grid and spacing
    const val STRATEGY_CHART_COLUMNS = 10
    const val COMPACT_SCREEN_BREAKPOINT_DP = 600
    const val EXPANDED_SCREEN_BREAKPOINT_DP = 840
}