package org.ttpss930141011.bj.domain.valueobjects

import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.valueobjects.Hand
import org.ttpss930141011.bj.domain.enums.Action

/**
 * DecisionRecord - Pure domain value object representing a single player decision
 * with complete context for learning analytics.
 * 
 * Records the minimal but complete information needed for cross-game statistics
 * and error rate analysis.
 */
data class DecisionRecord(
    val handCards: List<Card>,
    val dealerUpCard: Card,
    val playerAction: Action,
    val isCorrect: Boolean,
    val timestamp: Long = System.currentTimeMillis()
) {
    
    init {
        require(handCards.isNotEmpty()) { "Hand cards cannot be empty" }
    }
    
    /**
     * Scenario key for grouping decisions by similar game states.
     * Format: "HandType Value vs DealerRank"
     * Examples: "Hard 16 vs 10", "Soft 17 vs 6", "Pair 8 vs 9"
     */
    val scenarioKey: String by lazy {
        val hand = Hand(handCards)
        val handDescription = when {
            hand.canSplit -> "Pair ${handCards[0].rank}"
            hand.isSoft -> "Soft ${hand.bestValue}"
            else -> "Hard ${hand.bestValue}"
        }
        "$handDescription vs ${dealerUpCard.rank}"
    }
    
    /**
     * Hand value at the time of decision for quick analysis
     */
    val handValue: Int by lazy { 
        Hand(handCards).bestValue 
    }
    
    /**
     * Whether the hand was soft at decision time
     */
    val wasSoft: Boolean by lazy { 
        Hand(handCards).isSoft 
    }
    
    /**
     * Whether the hand could be split at decision time
     */
    val couldSplit: Boolean by lazy { 
        Hand(handCards).canSplit 
    }
}