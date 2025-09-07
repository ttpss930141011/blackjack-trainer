package org.ttpss930141011.bj.domain.valueobjects

/**
 * DomainConstants - Pure domain constants for scenario generation and card representation
 * 
 * Centralizes all magic string generation and card symbol mapping logic.
 * Eliminates scattered string construction and provides consistent domain vocabulary.
 * 
 * Design principles:
 * - No external dependencies (pure domain)
 * - Immutable and stateless
 * - Clear separation of concerns
 * - Easy to test and maintain
 */
object DomainConstants {
    
    /**
     * Generate hand type description for scenario keys.
     * Handles pairs, blackjack, soft hands, and hard hands.
     */
    fun generateHandType(hand: Hand): String = when {
        hand.canSplit -> {
            val rankSymbol = getShortRankSymbol(hand.cards[0].rank)
            "Pair ${rankSymbol}s"
        }
        hand.isBlackjack -> "BJ"
        hand.isSoft -> "S${hand.bestValue}"
        else -> "H${hand.bestValue}"
    }
    
    /**
     * Generate complete scenario key from hand type and dealer card.
     * Format: "HandType vs DealerRank"
     * Examples: "H16 vs 10", "S17 vs A", "Pair 8s vs 9"
     */
    fun generateScenarioKey(handType: String, dealerRank: String): String = 
        "$handType vs $dealerRank"
    
    /**
     * Get short rank symbol for compact scenario representation.
     * Uses single characters where possible for brevity.
     */
    fun getShortRankSymbol(rank: Rank): String = when (rank) {
        Rank.ACE -> "A"
        Rank.TWO -> "2"
        Rank.THREE -> "3"
        Rank.FOUR -> "4"
        Rank.FIVE -> "5"
        Rank.SIX -> "6"
        Rank.SEVEN -> "7"
        Rank.EIGHT -> "8"
        Rank.NINE -> "9"
        Rank.TEN -> "10"
        Rank.JACK -> "J"
        Rank.QUEEN -> "Q"
        Rank.KING -> "K"
    }
    
    /**
     * Generate rule hash from GameRules for consistent rule identification.
     * Uses 6-character hex hash for compact representation.
     */
    fun generateRuleHash(gameRules: GameRules): String {
        return gameRules.hashCode().toString(16).takeLast(6)
    }
    
    /**
     * Validation constants for domain constraints
     */
    object Constraints {
        const val MIN_SCENARIO_SAMPLES = 3
        const val RULE_HASH_LENGTH = 6
        const val MAX_DECISION_HISTORY = 1000
    }
}