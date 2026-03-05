package org.ttpss930141011.bj.infrastructure

/**
 * Infrastructure layer constants for system operations and defaults.
 */
internal object InfrastructureConstants {
    
    // Data loading and caching
    const val DEFAULT_RECENT_DECISIONS_LIMIT = 50
    const val DEFAULT_RECENT_ROUNDS_LIMIT = 50
    const val DATA_CLEANUP_DAYS_THRESHOLD = 30
    
    // Timeouts and delays (in milliseconds)
    const val ERROR_AUTO_DISMISS_DELAY_MS = 3000L
    const val RULE_CHANGE_NOTIFICATION_DELAY_MS = 5000L
    const val FEEDBACK_DISPLAY_DURATION_SECONDS = 2.5f
    
    // Session management
    const val SESSION_CONTEXT_SIZE = 100
    const val CACHE_CLEANUP_THRESHOLD_SIZE = 1000
}