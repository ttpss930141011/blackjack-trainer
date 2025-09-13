package org.ttpss930141011.bj.domain

/**
 * Domain-wide constants for blackjack game rules and mechanics.
 * Eliminates magic numbers and centralizes business rule values.
 * 
 * Organized by functional categories to improve maintainability and discoverability.
 */
object DomainConstants {
    
    /**
     * Core blackjack game values and thresholds.
     */
    object BlackjackValues {
        const val BLACKJACK_TOTAL = 21
        const val DEALER_STAND_HARD = 17
        const val ACE_HIGH_VALUE = 11
        const val ACE_LOW_VALUE = 1
        const val FACE_CARD_VALUE = 10
        const val BUST_THRESHOLD = 21
    }
    
    /**
     * Standard deck composition and shuffle parameters.
     */
    object DeckComposition {
        const val CARDS_PER_SUIT = 13
        const val SUITS_PER_DECK = 4
        const val CARDS_PER_DECK = 52
        const val STANDARD_DECK_COUNT = 6
        const val TOTAL_CARDS_STANDARD = 312
        const val SHUFFLE_THRESHOLD = 75
    }
    
    /**
     * Standard casino rules and payout multipliers.
     */
    object GameRules {
        const val BLACKJACK_PAYOUT_MULTIPLIER = 1.5
        const val REGULAR_WIN_MULTIPLIER = 1.0
        const val PUSH_MULTIPLIER = 0.0
        const val MAX_SPLIT_HANDS = 4
        const val INSURANCE_PAYOUT_MULTIPLIER = 2.0
    }
    
    /**
     * Hand size constraints and composition limits.
     */
    object HandLimits {
        const val INITIAL_HAND_SIZE = 2
        const val MIN_HAND_SIZE = 1
        const val BLACKJACK_HAND_SIZE = 2
        const val SPLIT_REQUIRED_HAND_SIZE = 2
        const val DOUBLE_DOWN_HAND_SIZE = 2
    }
    
    /**
     * Key values used in basic strategy decisions.
     */
    object StrategyValues {
        const val SOFT_17_VALUE = 17
        const val HARD_16_VALUE = 16
        const val HARD_15_VALUE = 15
        const val DEALER_ACE_VALUE = 1
        const val DEALER_TEN_VALUE = 10
        const val DEALER_NINE_VALUE = 9
    }
    
    /**
     * Default game configuration values.
     */
    object Defaults {
        const val PLAYER_STARTING_CHIPS = 1000
        const val MINIMUM_BET = 5
        const val MAXIMUM_BET = 500
    }
}