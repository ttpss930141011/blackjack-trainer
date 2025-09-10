package org.ttpss930141011.bj.domain.valueobjects

import kotlinx.serialization.Serializable
import org.ttpss930141011.bj.domain.enums.Action

/**
 * HandDecision - Complete decision record with accurate history tracking
 * 
 * Replaces the old DecisionRecord with a clean design that solves the split display problem.
 * Core principle: Decision = Before + Action + After
 * 
 * This captures exactly what the player saw, what they did, and what happened.
 * Split decisions show the actual 4-4 pair they decided on, not the 4-5 result.
 */
@Serializable
data class HandDecision(
    // What the player saw when making the decision
    val beforeAction: HandSnapshot,
    // What action they took
    val action: Action,
    // What happened as a result
    val afterAction: ActionResult,
    // Whether the decision was correct
    val isCorrect: Boolean,
    val timestamp: Long
) {
    init {
        require(beforeAction.cards.isNotEmpty()) { "Hand must have cards" }
    }
    
    /**
     * Rule hash for grouping decisions by rule set.
     * Uses DomainConstants for consistent hash generation.
     */
    val ruleHash: String by lazy {
        DomainConstants.generateRuleHash(beforeAction.gameRules)
    }
    
    /**
     * Base scenario key without rule context for cross-rule analysis.
     * Format: "HandType vs DealerRank"
     * Examples: "H16 vs 10", "S17 vs A", "Pair 8s vs 9"
     */
    val baseScenarioKey: String by lazy {
        val hand = Hand(beforeAction.cards)
        val handType = DomainConstants.generateHandType(hand)
        val dealerRank = DomainConstants.getShortRankSymbol(beforeAction.dealerUpCard.rank)
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
    fun hasSameRules(other: HandDecision): Boolean {
        return this.ruleHash == other.ruleHash
    }
    
    /**
     * Check if this decision involves the same scenario (ignoring rules) as another decision.
     */
    fun hasSameBaseScenario(other: HandDecision): Boolean {
        return this.baseScenarioKey == other.baseScenarioKey
    }
    
    // Legacy compatibility properties for existing stats code
    val handCards: List<Card> get() = beforeAction.cards
    val dealerUpCard: Card get() = beforeAction.dealerUpCard
    val playerAction: Action get() = action
    val gameRules: GameRules get() = beforeAction.gameRules
}

/**
 * Complete snapshot of hand state at decision time
 */
@Serializable
data class HandSnapshot(
    val cards: List<Card>,
    val dealerUpCard: Card,
    val gameRules: GameRules,
    val handIndex: Int = 0,          // Which hand if multiple (after splits)
    val isFromSplit: Boolean = false // Whether this hand came from a split
) {
    val handValue: Int = Hand(cards).bestValue
    val isSoft: Boolean = Hand(cards).isSoft
    val canSplit: Boolean = Hand(cards).canSplit
    val canDouble: Boolean = Hand(cards).canDouble
}

/**
 * Result of executing an action - exactly what happened
 */
@Serializable
sealed class ActionResult {
    /**
     * Hit: received one new card
     */
    @Serializable
    data class Hit(
        val newCard: Card,
        val resultingHand: List<Card>
    ) : ActionResult()
    
    /**
     * Stand: no new cards, hand stays as is
     */
    @Serializable
    data class Stand(
        val finalHand: List<Card>
    ) : ActionResult()
    
    /**
     * Double: received exactly one card, bet doubled
     */
    @Serializable
    data class Double(
        val newCard: Card,
        val resultingHand: List<Card>
    ) : ActionResult()
    
    /**
     * Split: pair became two hands, each got one new card
     * This shows EXACTLY what happened to the original pair
     */
    @Serializable
    data class Split(
        val originalPair: List<Card>,    // The 4-4 that was split
        val hand1: List<Card>,          // First hand after split: 4 + new card
        val hand2: List<Card>           // Second hand after split: 4 + new card
    ) : ActionResult()
    
    /**
     * Surrender: gave up, lost half the bet
     */
    @Serializable
    data class Surrender(
        val surrenderedHand: List<Card>
    ) : ActionResult()
}

// Legacy type alias for compatibility
typealias DecisionRecord = HandDecision