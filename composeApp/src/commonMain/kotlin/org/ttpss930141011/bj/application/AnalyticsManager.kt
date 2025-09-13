package org.ttpss930141011.bj.application

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
// ScenarioStats import removed
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * AnalyticsManager - BREAKING CHANGE: Complete rewrite for dual-stream architecture
 * 
 * NEW DESIGN:
 * - Manages session state and current round context
 * - Delegates all persistence to PersistenceService
 * - Generates RoundHistory when rounds complete
 * - Maintains SessionId for round correlation
 * 
 * REMOVED COMPLEXITY:
 * - No complex decision aggregation logic
 * - No mutable state for async operations (bad pattern)
 * - No tight coupling between decisions and statistics
 * - No confusing mix of session vs persistent data
 */
@OptIn(ExperimentalUuidApi::class)
internal class AnalyticsManager(
    private val persistenceService: PersistenceService = PersistenceService(),
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    // Session management
    private var _sessionStats by mutableStateOf(SessionStats())
    val sessionStats: SessionStats get() = _sessionStats
    
    // Current session context
    val currentSessionId: String = generateSessionId()
    private var currentRoundContext: RoundStartContext? = null
    private val currentRoundDecisions = mutableListOf<DecisionRecord>()
    
    companion object {
        private fun generateSessionId(): String = "session_${Uuid.random()}"
    }
    
    // ===== ROUND LIFECYCLE MANAGEMENT =====
    
    /**
     * Initialize round context when round starts.
     * BREAKING CHANGE: Now captures full context, not just decisions.
     */
    fun startRound(context: RoundStartContext) {
        currentRoundContext = context
        currentRoundDecisions.clear()
    }
    
    /**
     * Record individual player decision.
     * BREAKING CHANGE: Immediately persists for Stats, stores in context for Round.
     */
    fun recordPlayerAction(
        handBeforeAction: PlayerHand,
        dealerUpCard: Card,
        playerAction: Action,
        isCorrect: Boolean,
        gameRules: GameRules,
        actionResult: ActionResult
    ) {
        val decision = DecisionRecord(
            beforeAction = HandSnapshot(
                cards = handBeforeAction.cards,
                dealerUpCard = dealerUpCard,
                gameRules = gameRules,
                handIndex = 0,
                isFromSplit = handBeforeAction.isFromSplit
            ),
            action = playerAction,
            afterAction = actionResult,
            isCorrect = isCorrect,
            timestamp = TimeProvider.currentTimeMillis()
        )
        
        // Dual stream: save for Stats immediately, store for Round completion
        currentRoundDecisions.add(decision)
        
        coroutineScope.launch {
            persistenceService.saveDecision(decision)
            updateSessionStats()
        }
    }
    
    /**
     * Complete round and generate RoundHistory.
     * BREAKING CHANGE: Creates complete round record, not just decision summary.
     */
    fun completeRound(
        finalPlayerHands: List<PlayerHand>,
        dealerFinalHand: Hand,
        roundResult: RoundResult,
        netChipChange: Int,
        roundDurationMs: Long = 0
    ) {
        val context = currentRoundContext ?: return
        
        val roundHistory = RoundHistory(
            sessionId = currentSessionId,
            timestamp = context.startTimestamp,
            gameRules = context.gameRules,
            initialBet = context.betAmount,
            decisions = currentRoundDecisions.toList(),
            roundResult = roundResult,
            netChipChange = netChipChange,
            roundDurationMs = roundDurationMs
        )
        
        // Save complete round for History
        coroutineScope.launch {
            persistenceService.saveRoundHistory(roundHistory)
        }
        
        // Update session statistics with round outcome
        updateSessionWithRound(roundHistory)
        
        // Clean up round context
        currentRoundContext = null
        currentRoundDecisions.clear()
    }
    
    // ===== SESSION STATISTICS =====
    
    /**
     * Update session stats from latest decisions.
     * BREAKING CHANGE: Simple calculation, no complex caching.
     */
    private suspend fun updateSessionStats() {
        _sessionStats = persistenceService.calculateSessionStats()
    }
    
    /**
     * Update session with completed round.
     * BREAKING CHANGE: Direct state update, no complex aggregation.
     */
    private fun updateSessionWithRound(round: RoundHistory) {
        _sessionStats = _sessionStats.copy(
            totalDecisions = _sessionStats.totalDecisions + round.totalDecisionCount,
            correctDecisions = _sessionStats.correctDecisions + round.correctDecisionCount
        )
    }
    
    /**
     * Reset session statistics.
     * BREAKING CHANGE: Only resets session state, not persistent data.
     */
    fun resetSession() {
        _sessionStats = SessionStats()
        currentRoundContext = null
        currentRoundDecisions.clear()
    }
    
    // Legacy methods removed - use PersistenceService directly
}

/**
 * BREAKING CHANGE SUMMARY:
 * 
 * REMOVED DEPRECATED METHODS:
 * - getWorstScenarios() → Use PersistenceService().calculateScenarioStats()
 * - getRecentDecisions() → Use PersistenceService().getRecentDecisions()
 * - getScenarioStats() → Use PersistenceService().calculateScenarioStats() + transform
 * - clearAllLearningData() → Use PersistenceService().clearAllLearningData()
 * - recordRound() → Use completeRound() instead
 * 
 * NEW FEATURES:
 * - Session ID management for round correlation
 * - RoundHistory generation and persistence
 * - Clean separation between session and persistent data
 * - Simplified analytics delegation to PersistenceService
 * 
 * MIGRATION PATH:
 * - Use PersistenceService() directly for all analytics
 * - Use new round lifecycle: startRound() → recordPlayerAction() → completeRound()
 */