package org.ttpss930141011.bj.domain.valueobjects

import kotlinx.serialization.Serializable
import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.valueobjects.GameRules
import org.ttpss930141011.bj.domain.valueobjects.Hand
import org.ttpss930141011.bj.domain.enums.Action

/**
 * DecisionRecord - Pure domain value object representing a single player decision
 * 
 * Simplified design focused on essential data for learning analytics.
 * Uses DomainConstants for consistent scenario generation and eliminates complex computed properties.
 * 
 * Design principles:
 * - Minimal but complete information
 * - No complex computed logic (delegated to DomainConstants)
 * - Clear separation of concerns
 * - Easy to test and maintain
 */
@Serializable
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
     * Rule hash for grouping decisions by rule set.
     * Uses DomainConstants for consistent hash generation.
     */
    val ruleHash: String by lazy {
        DomainConstants.generateRuleHash(gameRules)
    }
    
    /**
     * Base scenario key without rule context for cross-rule analysis.
     * Format: "HandType vs DealerRank"
     * Examples: "H16 vs 10", "S17 vs A", "Pair 8s vs 9"
     */
    val baseScenarioKey: String by lazy {
        val hand = Hand(handCards)
        val handType = DomainConstants.generateHandType(hand)
        val dealerRank = DomainConstants.getShortRankSymbol(dealerUpCard.rank)
        DomainConstants.generateScenarioKey(handType, dealerRank)
    }
    
    /**
     * Complete scenario key with rule context for rule-specific analysis.
     * Format: "HandType vs DealerRank [RuleHash]"
     * Examples: "H16 vs 10 [abc123]", "S17 vs A [def456]"
     */
    val scenarioKey: String by lazy {
        "$baseScenarioKey [$ruleHash]"
    }
    
    /**
     * Check if this decision was made under the same rule set as another decision.
     */
    fun hasSameRules(other: DecisionRecord): Boolean {
        return this.ruleHash == other.ruleHash
    }
    
    /**
     * Check if this decision involves the same scenario (ignoring rules) as another decision.
     */
    fun hasSameBaseScenario(other: DecisionRecord): Boolean {
        return this.baseScenarioKey == other.baseScenarioKey
    }
}