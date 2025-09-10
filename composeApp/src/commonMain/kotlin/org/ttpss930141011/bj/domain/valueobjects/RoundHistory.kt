package org.ttpss930141011.bj.domain.valueobjects

import kotlinx.serialization.Serializable
import org.ttpss930141011.bj.domain.enums.RoundResult
import kotlin.random.Random

/**
 * RoundHistory - Complete blackjack round record for user replay and analysis
 * 
 * This is the primary data structure for the History page, containing ALL context
 * needed to fully reconstruct and understand a blackjack round.
 * 
 * Design principles:
 * - Complete round lifecycle: from initial deal to final settlement
 * - Immutable value object: all data captured at round completion
 * - Self-contained: no external dependencies for display
 * - DecisionRecord composition: incorporates individual decisions with full context
 * 
 * Data relationship: RoundHistory = List<DecisionRecord> + Complete Context
 */
@Serializable
data class RoundHistory(
    // Round identification
    val sessionId: String,
    val roundId: String = generateRoundId(),
    val timestamp: Long,
    
    // Game context
    val gameRules: GameRules,
    val betAmount: Int,
    
    // Complete hand evolution
    val initialPlayerHands: List<PlayerHand>,   // State after initial deal
    val finalPlayerHands: List<PlayerHand>,     // State after all actions
    val dealerVisibleCard: Card,                // What player saw during decisions
    val dealerFinalHand: Hand,                  // Complete dealer hand after dealer turn
    
    // Decision sequence - the core learning data
    val decisions: List<DecisionRecord>,
    
    // Round outcome
    val roundResult: RoundResult,
    val netChipChange: Int,                     // Positive = win, negative = loss
    
    // Performance metrics
    val roundDurationMs: Long = 0,
    val correctDecisionCount: Int = decisions.count { it.isCorrect },
    val totalDecisionCount: Int = decisions.size
) {
    
    companion object {
        private fun generateRoundId(): String = 
            "round_${Random.nextLong(100000000000L, 999999999999L)}_${Random.nextInt(1000, 9999)}"
    }
    
    init {
        require(sessionId.isNotBlank()) { "Session ID cannot be blank" }
        require(betAmount > 0) { "Bet amount must be positive" }
        require(initialPlayerHands.isNotEmpty()) { "Must have at least one player hand" }
        require(finalPlayerHands.size >= initialPlayerHands.size) { "Final hands cannot be fewer than initial hands" }
        require(decisions.isNotEmpty()) { "Round must have at least one decision" }
        require(decisions.all { it.timestamp >= timestamp }) { "Decision timestamps must be after round start" }
        require(decisions.all { it.ruleHash == DomainConstants.generateRuleHash(gameRules) }) { 
            "All decisions must use same rules as round" 
        }
    }
    
    // Convenience properties for UI display
    val roundAccuracy: Double 
        get() = if (totalDecisionCount > 0) correctDecisionCount.toDouble() / totalDecisionCount else 0.0
    
    val isWinningRound: Boolean 
        get() = roundResult == RoundResult.PLAYER_WIN || roundResult == RoundResult.PLAYER_BLACKJACK
    
    val isPushRound: Boolean 
        get() = roundResult == RoundResult.PUSH
    
    val primaryHand: PlayerHand 
        get() = finalPlayerHands.first()
    
    val hasSplitHands: Boolean 
        get() = finalPlayerHands.size > 1
    
    val splitHandCount: Int 
        get() = finalPlayerHands.size
    
    // Rule consistency check
    val isRuleConsistent: Boolean
        get() = decisions.all { it.ruleHash == DomainConstants.generateRuleHash(gameRules) }
    
    // Round summary for quick display
    val summaryText: String
        get() {
            val handSummary = if (hasSplitHands) {
                "Split (${splitHandCount} hands)"
            } else {
                primaryHand.hand.displayValue
            }
            val resultText = when (roundResult) {
                RoundResult.PLAYER_WIN, RoundResult.PLAYER_BLACKJACK -> "WIN (+$netChipChange)"
                RoundResult.DEALER_WIN -> "LOSS ($netChipChange)"
                RoundResult.PUSH -> "PUSH"
                RoundResult.SURRENDER -> "SURRENDER ($netChipChange)"
            }
            return "$handSummary vs ${dealerFinalHand.displayValue} → $resultText"
        }
    
    /**
     * Validates that this round history is internally consistent
     */
    fun validateConsistency(): List<String> {
        val errors = mutableListOf<String>()
        
        // Check decision timestamps
        decisions.forEach { decision ->
            if (decision.timestamp < timestamp) {
                errors.add("Decision timestamp ${decision.timestamp} is before round timestamp $timestamp")
            }
        }
        
        // Check rule consistency
        if (!isRuleConsistent) {
            errors.add("Not all decisions use the same game rules")
        }
        
        // Check hand count consistency
        if (initialPlayerHands.size != finalPlayerHands.size) {
            errors.add("Initial hand count (${initialPlayerHands.size}) != final hand count (${finalPlayerHands.size})")
        }
        
        // Check decision count consistency
        if (correctDecisionCount > totalDecisionCount) {
            errors.add("Correct decisions ($correctDecisionCount) > total decisions ($totalDecisionCount)")
        }
        
        return errors
    }
    
    /**
     * Creates a simplified version for serialization or storage
     */
    fun toStorageFormat(): Map<String, Any> {
        return mapOf(
            "sessionId" to sessionId,
            "roundId" to roundId,
            "timestamp" to timestamp,
            "gameRules" to gameRules,
            "betAmount" to betAmount,
            "initialPlayerHands" to initialPlayerHands,
            "finalPlayerHands" to finalPlayerHands,
            "dealerVisibleCard" to dealerVisibleCard,
            "dealerFinalHand" to dealerFinalHand,
            "decisions" to decisions,
            "roundResult" to roundResult,
            "netChipChange" to netChipChange,
            "roundDurationMs" to roundDurationMs
        )
    }
}

/**
 * Helper data class for capturing round context at the start
 */
data class RoundStartContext(
    val sessionId: String,
    val gameRules: GameRules,
    val betAmount: Int,
    val initialPlayerHands: List<PlayerHand>,
    val dealerVisibleCard: Card,
    val startTimestamp: Long
)

/**
 * Extension functions for working with round collections
 */
fun List<RoundHistory>.filterByResult(result: RoundResult): List<RoundHistory> =
    filter { it.roundResult == result }

fun List<RoundHistory>.filterByAccuracy(minAccuracy: Double): List<RoundHistory> =
    filter { it.roundAccuracy >= minAccuracy }

fun List<RoundHistory>.groupBySession(): Map<String, List<RoundHistory>> =
    groupBy { it.sessionId }

fun List<RoundHistory>.calculateTotalNetChange(): Int =
    sumOf { it.netChipChange }

fun List<RoundHistory>.calculateOverallAccuracy(): Double {
    val totalCorrect = sumOf { it.correctDecisionCount }
    val totalDecisions = sumOf { it.totalDecisionCount }
    return if (totalDecisions > 0) totalCorrect.toDouble() / totalDecisions else 0.0
}