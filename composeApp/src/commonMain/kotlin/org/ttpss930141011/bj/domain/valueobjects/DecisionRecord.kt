package org.ttpss930141011.bj.domain.valueobjects

import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.valueobjects.GameRules
import org.ttpss930141011.bj.domain.valueobjects.Hand
import org.ttpss930141011.bj.domain.valueobjects.Rank
import org.ttpss930141011.bj.domain.enums.Action

/**
 * DecisionRecord - Pure domain value object representing a single player decision
 * with complete context for learning analytics.
 * 
 * Enhanced with rule-aware context to prevent statistical contamination when
 * game rules change mid-session. Each decision is tagged with the specific 
 * rule set that determined its correctness.
 * 
 * Records the minimal but complete information needed for rule-specific 
 * cross-game statistics and error rate analysis.
 */
data class DecisionRecord(
    val handCards: List<Card>,
    val dealerUpCard: Card,
    val playerAction: Action,
    val isCorrect: Boolean,
    val gameRules: GameRules,
    val timestamp: Long = kotlin.random.Random.nextLong()
) {
    
    init {
        require(handCards.isNotEmpty()) { "Hand cards cannot be empty" }
    }
    
    /**
     * Rule version hash for grouping decisions by rule set.
     * Uses GameRules hashCode to create a stable rule identifier.
     */
    val ruleVersion: String by lazy {
        gameRules.hashCode().toString(16).takeLast(8) // 8-char hex hash
    }
    
    /**
     * Scenario key for grouping decisions by similar game states.
     * Format: "HandType Value vs DealerRank [RuleHash]" 
     * Examples: "Hard 16 vs 10 [a1b2c3d4]", "Soft 17 vs 6 [a1b2c3d4]"
     */
    val scenarioKey: String by lazy {
        val hand = Hand(handCards)
        val handDescription = when {
            hand.canSplit -> {
                val pairRank = when (handCards[0].rank) {
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
                "Pair ${pairRank}s"
            }
            hand.isBlackjack -> "Blackjack"
            hand.isSoft -> "Soft ${hand.bestValue}"
            else -> "Hard ${hand.bestValue}"
        }
        
        val dealerRank = when (dealerUpCard.rank) {
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
        
        "$handDescription vs $dealerRank [$ruleVersion]"
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
    
    /**
     * Base scenario key without rule context for cross-rule analysis.
     * Format: "HandType Value vs DealerRank"
     * Examples: "Hard 16 vs 10", "Soft 17 vs 6", "Pair 8s vs 9"
     */
    val baseScenarioKey: String by lazy {
        scenarioKey.substringBefore(" [")
    }
    
    /**
     * Check if this decision was made under the same rule set as another decision.
     */
    fun hasSameRules(other: DecisionRecord): Boolean {
        return this.gameRules == other.gameRules
    }
    
    /**
     * Check if this decision involves the same scenario (ignoring rules) as another decision.
     */
    fun hasSameBaseScenario(other: DecisionRecord): Boolean {
        return this.baseScenarioKey == other.baseScenarioKey
    }
}