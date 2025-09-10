package org.ttpss930141011.bj.application

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.*
import org.ttpss930141011.bj.infrastructure.InMemoryLearningRepository
import org.ttpss930141011.bj.infrastructure.ScenarioStats
import org.ttpss930141011.bj.infrastructure.DataLoader
import org.ttpss930141011.bj.infrastructure.CachingDataLoader
import org.ttpss930141011.bj.infrastructure.InfrastructureConstants

/**
 * GameViewModel - BREAKING CHANGE: Complete rewrite for RoundHistory integration
 * 
 * NEW ARCHITECTURE:
 * - Dual-stream persistence: RoundHistory + DecisionRecord
 * - Round lifecycle management with complete context capture
 * - Session-aware round correlation via AnalyticsManager.sessionId
 * - Simplified API with removed complex aggregation methods
 * 
 * REMOVED LEGACY FEATURES:
 * - Complex decision aggregation in GameViewModel
 * - Direct access to getWorstScenarios(), getRecentDecisions(), etc.
 * - Mixed session/persistent state management
 * 
 * NEW FEATURES:
 * - Complete round history with all context
 * - Round start context capture
 * - Round completion with full settlement data
 * - Clean separation of History vs Stats data
 */
class GameViewModel(
    private val gameService: GameService = GameService(),
    private val decisionEvaluator: DecisionEvaluator = DecisionEvaluator(),
    private val persistenceService: PersistenceService = PersistenceService(),
    private val chipCompositionService: ChipCompositionService = ChipCompositionService(),
    private val dataLoader: DataLoader = CachingDataLoader(),
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    
    private val gameStateManager = GameStateManager(gameService)
    private val feedbackManager = FeedbackManager(decisionEvaluator)
    private val analyticsManager = AnalyticsManager(persistenceService)
    val uiStateManager = UIStateManager(chipCompositionService)
    
    // Core game state exposure
    val game: Game? get() = gameStateManager.game
    val feedback: DecisionFeedback? get() = feedbackManager.feedback
    val sessionStats: SessionStats get() = analyticsManager.sessionStats
    val roundDecisions: List<PlayerDecision> get() = feedbackManager.roundDecisions
    val errorMessage: String? get() = uiStateManager.errorMessage
    val isGameOver: Boolean get() = gameStateManager.isGameOver

    // User preferences state management
    private var _userPreferences by mutableStateOf(UserPreferences())
    val userPreferences: UserPreferences get() = _userPreferences
    
    // Last bet memory for UX improvement
    private var _lastBetAmount by mutableStateOf<Int?>(null)
    private var _userClearedBet by mutableStateOf(false)
    val lastBetAmount: Int? get() = _lastBetAmount
    
    // Data owned by ViewModel (Linus principle: clear data ownership)
    private var _recentRounds by mutableStateOf<List<RoundHistory>>(emptyList())
    val recentRounds: List<RoundHistory> get() = _recentRounds
    
    private var _scenarioStats by mutableStateOf<List<ScenarioErrorStat>>(emptyList())
    val scenarioStats: List<ScenarioErrorStat> get() = _scenarioStats
    
    private var _decisionHistory by mutableStateOf<List<DecisionRecord>>(emptyList())
    val decisionHistory: List<DecisionRecord> get() = _decisionHistory
    
    // Round timing
    private var roundStartTime: Long = 0

    // ===== GAME LIFECYCLE =====
    
    /**
     * Initializes a new game with specified rules and player
     * BREAKING CHANGE: Resets both round history and decision analytics
     */
    fun initializeGame(gameRules: GameRules, player: Player) {
        gameStateManager.initializeGame(gameRules, player)
        feedbackManager.reset()
        analyticsManager.resetSession()
        uiStateManager.clearError()
    }
    
    // ===== ROUND LIFECYCLE WITH HISTORY =====
    
    /**
     * Starts a new round with the specified bet amount
     * BREAKING CHANGE: Captures complete round start context for RoundHistory
     */
    fun startRound(betAmount: Int) {
        when (val result = gameStateManager.startRound(betAmount)) {
            is GameStateResult.Success -> {
                feedbackManager.startNewRound()
                uiStateManager.clearError()
                
                // BREAKING CHANGE: Capture round start context
                captureRoundStartContext()
            }
            is GameStateResult.Error -> uiStateManager.setError(result.message)
        }
    }
    
    /**
     * Deals initial cards to start the round
     * BREAKING CHANGE: Initializes round timing and context capture
     */
    fun dealCards() {
        rememberLastBet()
        roundStartTime = TimeProvider.currentTimeMillis()
        
        when (val result = gameStateManager.dealCards()) {
            is GameStateResult.Success -> {
                feedbackManager.reset()
                uiStateManager.clearError()
                
                // BREAKING CHANGE: Initialize round context after dealing
                initializeRoundContext()
            }
            is GameStateResult.Error -> uiStateManager.setError(result.message)
        }
    }
    
    /**
     * Executes a player action and handles all related state updates
     * BREAKING CHANGE: Uses new AnalyticsManager.recordPlayerAction
     */
    fun playerAction(action: Action) {
        executePlayerAction(action)
        // Only record if action execution set feedback (meaning it succeeded)
        if (feedback != null) {
            recordDecisionForLearning(action)
        }
        handleAutoTransitions()
    }
    
    /**
     * Processes the dealer's turn according to house rules
     * BREAKING CHANGE: Completes round with full RoundHistory generation
     */
    fun dealerTurn() {
        when (val result = gameStateManager.processDealerTurn()) {
            is GameStateResult.Success -> {
                if (game?.phase == GamePhase.SETTLEMENT && game?.isSettled == true) {
                    // BREAKING CHANGE: Complete round with full history
                    completeRoundWithHistory()
                }
                uiStateManager.clearError()
            }
            is GameStateResult.Error -> uiStateManager.setError(result.message)
        }
    }
    
    /**
     * Advances to the next round
     * BREAKING CHANGE: Cleans up round context
     */
    fun nextRound() {
        when (val result = gameStateManager.startNewRound()) {
            is GameStateResult.Success -> {
                feedbackManager.startNewRound()
                uiStateManager.clearError()
                _userClearedBet = false
                
                // BREAKING CHANGE: Clean round timing
                roundStartTime = 0
            }
            is GameStateResult.Error -> uiStateManager.setError(result.message)
        }
    }
    
    // ===== ROUND HISTORY IMPLEMENTATION =====
    
    /**
     * Capture round start context when bet is placed
     */
    private fun captureRoundStartContext() {
        val currentGame = game ?: return
        
        val context = RoundStartContext(
            sessionId = analyticsManager.currentSessionId,
            gameRules = currentGame.rules,
            betAmount = currentGame.betState.amount,
            initialPlayerHands = emptyList(), // Will be set after dealing
            dealerVisibleCard = Card.UNKNOWN_CARD, // Will be set after dealing
            startTimestamp = TimeProvider.currentTimeMillis()
        )
        
        analyticsManager.startRound(context)
    }
    
    /**
     * Initialize round context after cards are dealt
     */
    private fun initializeRoundContext() {
        val currentGame = game ?: return
        
        val context = RoundStartContext(
            sessionId = analyticsManager.currentSessionId,
            gameRules = currentGame.rules,
            betAmount = currentGame.betState.amount,
            initialPlayerHands = currentGame.playerHands,
            dealerVisibleCard = currentGame.dealer.upCard ?: Card.UNKNOWN_CARD,
            startTimestamp = roundStartTime
        )
        
        analyticsManager.startRound(context)
    }
    
    /**
     * Complete round and generate RoundHistory
     */
    private fun completeRoundWithHistory() {
        val currentGame = game ?: return
        
        val roundDurationMs = if (roundStartTime > 0) {
            TimeProvider.currentTimeMillis() - roundStartTime
        } else 0
        
        val netChipChange = calculateNetChipChange(currentGame)
        val roundResult = currentGame.getRoundOutcome().toRoundResult()
        
        currentGame.dealer.hand?.let { dealerHand ->
            analyticsManager.completeRound(
                finalPlayerHands = currentGame.playerHands,
                dealerFinalHand = dealerHand,
                roundResult = roundResult,
                netChipChange = netChipChange,
                roundDurationMs = roundDurationMs
            )
        }
        
        // Refresh round history for UI
        refreshRoundHistory()
    }
    
    /**
     * Calculate net chip change for the round
     */
    private fun calculateNetChipChange(game: Game): Int {
        // Simple calculation - in a real implementation, this would be more sophisticated
        return when (game.getRoundOutcome()) {
            RoundOutcome.WIN -> game.betState.amount
            RoundOutcome.LOSS -> -game.betState.amount
            RoundOutcome.PUSH -> 0
            RoundOutcome.UNKNOWN -> 0
        }
    }
    
    /**
     * Load recent game rounds from persistence layer.
     * Uses caching for performance optimization.
     */
    fun loadRecentRounds() {
        coroutineScope.launch {
            _recentRounds = persistenceService.getRecentRounds()
        }
    }
    
    /**
     * Load scenario statistics for analytics display.
     */
    fun loadScenarioStats() {
        coroutineScope.launch {
            _scenarioStats = persistenceService.calculateScenarioStats()
        }
    }
    
    /**
     * Load decision history for analytics display.
     */
    fun loadDecisionHistory() {
        coroutineScope.launch {
            _decisionHistory = persistenceService.getRecentDecisions(InfrastructureConstants.ANALYTICS_DECISIONS_LIMIT)
        }
    }
    
    /**
     * Refresh all analytics data in one operation.
     * Eliminates special cases and consolidates data loading.
     */
    fun refreshAllData() {
        loadRecentRounds()
        loadScenarioStats()
        loadDecisionHistory()
    }
    
    /**
     * Refresh round history cache.
     * Simplified without complex async handling.
     */
    fun refreshRoundHistory() {
        dataLoader.invalidate("recent_rounds")
        loadRecentRounds()
    }
    
    /**
     * Refresh scenario statistics cache.
     * Follows same pattern as refreshRoundHistory for consistency.
     */
    fun refreshScenarioStats() {
        dataLoader.invalidate("scenario_stats")
        loadScenarioStats()
    }
    
    // ===== DECISION RECORDING =====
    
    private fun executePlayerAction(action: Action) {
        val result = gameStateManager.executePlayerAction(action)
        if (result != null) {
            val currentGame = game!!
            val feedback = feedbackManager.evaluatePlayerAction(
                handBeforeAction = result.handBeforeAction,
                dealerUpCard = currentGame.dealer.upCard!!,
                playerAction = action,
                rules = currentGame.rules
            )
            uiStateManager.clearError()
        } else {
            uiStateManager.setError("Failed to execute player action")
        }
    }
    
    private fun recordDecisionForLearning(action: Action) {
        val currentGame = game ?: return
        val feedback = this.feedback ?: return
        
        analyticsManager.recordPlayerAction(
            handBeforeAction = currentGame.currentHand!!,
            dealerUpCard = currentGame.dealer.upCard!!,
            playerAction = action,
            isCorrect = feedback.isCorrect,
            gameRules = currentGame.rules
        )
        
        // Refresh scenario stats using same pattern as round history
        refreshScenarioStats()
    }
    
    private fun handleAutoTransitions() {
        game?.let { currentGame ->
            if (currentGame.shouldAutoAdvance() && currentGame.phase == GamePhase.DEALER_TURN) {
                dealerTurn()
            }
        }
    }
    
    // ===== BETTING INTERFACE =====
    
    val currentBetAmount: Int get() = game?.betState?.amount ?: 0
    val canDealCards: Boolean get() = game?.canDealCards ?: false
    val chipComposition: List<ChipInSpot> get() = uiStateManager.calculateChipComposition(currentBetAmount)
    val availableBalance: Int get() = game?.player?.chips ?: 0
    
    fun addChipToBet(chipValue: ChipValue) {
        when (val result = gameStateManager.addChipToBet(chipValue)) {
            is GameStateResult.Success -> uiStateManager.clearError()
            is GameStateResult.Error -> uiStateManager.setError(result.message)
        }
    }
    
    fun clearBet() {
        when (val result = gameStateManager.clearBet()) {
            is GameStateResult.Success -> {
                _userClearedBet = true
                uiStateManager.clearError()
            }
            is GameStateResult.Error -> uiStateManager.setError(result.message)
        }
    }
    
    // ===== ANALYTICS INTERFACE (SIMPLIFIED) =====
    
    /**
     * BREAKING CHANGE: Removed direct analytics methods.
     * Use PersistenceService directly for complex analytics.
     */
    
    /**
     * Clear all learning data and refresh views.
     * Consolidated operation without special cases.
     */
    fun clearAllLearningData() {
        persistenceService.clearAllLearningData()
        refreshAllData()
    }
    
    val currentGameRules: GameRules? get() = game?.rules
    
    fun handleRuleChange(newRules: GameRules) {
        val currentGame = game ?: return
        
        uiStateManager.handleRuleChangeNotification(
            currentRules = currentGame.rules,
            newRules = newRules,
            sessionStats = sessionStats
        )
        
        gameStateManager.handleRuleChange(newRules)
    }
    
    // ===== UI STATE MANAGEMENT =====
    
    fun clearError() {
        uiStateManager.clearError()
    }
    
    fun clearFeedback() {
        feedbackManager.clearFeedback()
    }
    
    // ===== BETTING MEMORY =====
    
    private fun rememberLastBet() {
        _lastBetAmount = game?.betState?.amount?.takeIf { it > 0 }
    }
    
    fun repeatLastBet(): Boolean {
        val lastAmount = _lastBetAmount ?: return false
        val currentGame = game ?: return false
        
        if (_userClearedBet) return false
        if (currentGame.player?.chips ?: 0 < lastAmount) return false
        if (currentGame.phase != GamePhase.WAITING_FOR_BETS) return false
        if (currentGame.betState.amount > 0) return false
        
        var remainingAmount = lastAmount
        val chipValues = ChipValue.values().sortedByDescending { it.value }
        
        for (chipValue in chipValues) {
            val chipsNeeded = remainingAmount / chipValue.value
            repeat(chipsNeeded) {
                val result = gameStateManager.addChipToBet(chipValue)
                if (result is GameStateResult.Error) {
                    return false
                }
                remainingAmount -= chipValue.value
            }
            if (remainingAmount == 0) break
        }
        
        uiStateManager.clearError()
        return remainingAmount == 0
    }
    
    // ===== USER PREFERENCES =====
    
    /**
     * Load user preferences from persistence layer.
     * Updates UI state with cached settings.
     */
    fun loadUserPreferences() {
        coroutineScope.launch {
            _userPreferences = persistenceService.loadUserPreferences()
            // Restore last bet amount for UX continuity
            _lastBetAmount = if (_userPreferences.lastBetAmount > 0) _userPreferences.lastBetAmount else null
        }
    }
    
    fun updateUserPreferences(newPreferences: UserPreferences) {
        _userPreferences = newPreferences
        _lastBetAmount = if (newPreferences.lastBetAmount > 0) newPreferences.lastBetAmount else null
        
        // 使緩存失效，確保下次載入時獲取最新數據
        dataLoader.invalidate("user_preferences")
        
        kotlinx.coroutines.MainScope().launch {
            try {
                persistenceService.saveUserPreferences(newPreferences)
            } catch (e: Exception) {
                println("Warning: Failed to save user preferences")
            }
        }
    }

}

/**
 * Extension function to convert RoundOutcome to RoundResult
 */
private fun RoundOutcome.toRoundResult(): RoundResult = when (this) {
    RoundOutcome.WIN -> RoundResult.PLAYER_WIN
    RoundOutcome.LOSS -> RoundResult.DEALER_WIN
    RoundOutcome.PUSH -> RoundResult.PUSH
    RoundOutcome.UNKNOWN -> RoundResult.DEALER_WIN // Default to dealer win for unknown outcomes
}