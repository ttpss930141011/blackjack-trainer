package org.ttpss930141011.bj.domain.valueobjects

import kotlinx.serialization.Serializable
import org.ttpss930141011.bj.domain.enums.RoundResult
import org.ttpss930141011.bj.domain.enums.Action
import kotlin.random.Random

/**
 * RoundHistory - BREAKING CHANGE: Completely redesigned for accurate decision tracking
 * 
 * Core principle: A round is a sequence of decisions, nothing more.
 * No complex inference, no guessing about hand evolution.
 * Just: what decisions were made and what were the outcomes.
 * 
 * Design principles:
 * - Decision-first: Everything revolves around the decision sequence
 * - No state duplication: Don't store both initial/final hands AND decisions
 * - Simple derivation: All other info can be derived from decisions
 * - Clear ownership: This is the definitive record of what happened
 */
@Serializable
data class RoundHistory(
    // Round identification
    val roundId: String = generateRoundId(),
    val timestamp: Long,
    val sessionId: String,
    
    // Basic context
    val gameRules: GameRules,
    val initialBet: Int,
    
    // The core data: what actually happened
    val decisions: List<DecisionRecord>,
    
    // Final outcome
    val roundResult: RoundResult,
    val netChipChange: Int,
    val roundDurationMs: Long = 0
) {
    
    companion object {
        private fun generateRoundId(): String = 
            "round_${Random.nextLong(100000000000L, 999999999999L)}_${Random.nextInt(1000, 9999)}"
    }
    
    init {
        require(sessionId.isNotBlank()) { "Session ID cannot be blank" }
        require(initialBet > 0) { "Initial bet must be positive" }
        require(decisions.isNotEmpty()) { "Round must have at least one decision" }
        require(decisions.all { it.timestamp >= timestamp }) { "Decision timestamps must be after round start" }
    }
    
    // Derived properties - everything can be calculated from decisions
    val correctDecisionCount: Int get() = decisions.count { it.isCorrect }
    val totalDecisionCount: Int get() = decisions.size
    val roundAccuracy: Double get() = if (totalDecisionCount > 0) correctDecisionCount.toDouble() / totalDecisionCount else 0.0
    
    val isWinningRound: Boolean get() = roundResult == RoundResult.PLAYER_WIN || roundResult == RoundResult.PLAYER_BLACKJACK
    val isPushRound: Boolean get() = roundResult == RoundResult.PUSH
    
    // Split detection - much simpler now
    val hasSplit: Boolean get() = decisions.any { it.action == Action.SPLIT }
    val splitCount: Int get() = decisions.count { it.action == Action.SPLIT }
    val finalHandCount: Int get() = 1 + splitCount  // One initial hand + one per split
    
    // Round summary for quick display
    val summaryText: String get() {
        val handSummary = if (hasSplit) {
            "Split ($finalHandCount hands)"
        } else {
            val firstDecision = decisions.first()
            "${firstDecision.beforeAction.handValue}"
        }
        val resultText = when (roundResult) {
            RoundResult.PLAYER_WIN, RoundResult.PLAYER_BLACKJACK -> "WIN (+$netChipChange)"
            RoundResult.DEALER_WIN -> "LOSS ($netChipChange)"
            RoundResult.PUSH -> "PUSH"
            RoundResult.SURRENDER -> "SURRENDER ($netChipChange)"
        }
        val dealerUpCard = decisions.firstOrNull()?.beforeAction?.dealerUpCard
        return if (dealerUpCard != null) {
            "$handSummary vs ${dealerUpCard.rank.name} → $resultText"
        } else {
            "$handSummary → $resultText"
        }
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