package org.ttpss930141011.bj.application

import androidx.compose.runtime.*
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.*
import org.ttpss930141011.bj.infrastructure.InMemoryLearningRepository
import org.ttpss930141011.bj.infrastructure.ScenarioStats

/**
 * REFACTORED GameViewModel - Linus Style
 * 
 * "Split the God Object into focused controllers, but keep the same public API 
 * so nothing breaks." - Linus approach
 * 
 * Internal structure:
 * - GameStateManager: Core game state and business logic
 * - FeedbackManager: Decision evaluation and feedback
 * - AnalyticsManager: Learning data and statistics
 * - UIStateManager: Error messages and notifications
 */
class GameViewModel(
    private val gameService: GameService = GameService(),
    private val decisionEvaluator: DecisionEvaluator = DecisionEvaluator(),
    private val learningRecorder: LearningRecorder = LearningRecorder(InMemoryLearningRepository()),
    private val chipCompositionService: ChipCompositionService = ChipCompositionService()
) {
    
    private val gameStateManager = GameStateManager(gameService)
    private val feedbackManager = FeedbackManager(decisionEvaluator)
    private val analyticsManager = AnalyticsManager(learningRecorder)
    private val uiStateManager = UIStateManager(chipCompositionService)
    val game: Game? get() = gameStateManager.game
    val feedback: DecisionFeedback? get() = feedbackManager.feedback
    val sessionStats: SessionStats get() = analyticsManager.sessionStats
    val roundDecisions: List<PlayerDecision> get() = feedbackManager.roundDecisions
    val errorMessage: String? get() = uiStateManager.errorMessage
    val ruleChangeNotification: String? get() = uiStateManager.ruleChangeNotification
    
    val isGameOver: Boolean get() = gameStateManager.isGameOver
    
    /**
     * Initializes a new game with specified rules and player
     * 
     * @param gameRules The blackjack rules to use
     * @param player The player instance
     */
    fun initializeGame(gameRules: GameRules, player: Player) {
        gameStateManager.initializeGame(gameRules, player)
        feedbackManager.reset()
        analyticsManager.resetSession()
        uiStateManager.clearError()
    }
    
    /**
     * Starts a new round with the specified bet amount
     * 
     * @param betAmount Amount to bet for this round
     */
    fun startRound(betAmount: Int) {
        when (val result = gameStateManager.startRound(betAmount)) {
            is GameStateResult.Success -> {
                feedbackManager.startNewRound()
                uiStateManager.clearError()
            }
            is GameStateResult.Error -> uiStateManager.setError(result.message)
        }
    }
    
    /**
     * Executes a player action and handles all related state updates
     * 
     * @param action The action to take (HIT, STAND, DOUBLE, SPLIT, SURRENDER)
     */
    fun playerAction(action: Action) {
        executePlayerAction(action)
        recordDecisionForLearning(action)
        handleAutoTransitions()
    }
    
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
    }
    
    private fun handleAutoTransitions() {
        game?.let { currentGame ->
            if (currentGame.shouldAutoAdvance() && currentGame.phase == GamePhase.DEALER_TURN) {
                dealerTurn()
            }
        }
    }
    
    /**
     * Processes the dealer's turn according to house rules
     */
    fun dealerTurn() {
        when (val result = gameStateManager.processDealerTurn()) {
            is GameStateResult.Success -> {
                if (game?.phase == GamePhase.SETTLEMENT && game?.isSettled == true) {
                    analyticsManager.recordRound(roundDecisions)
                }
                uiStateManager.clearError()
            }
            is GameStateResult.Error -> uiStateManager.setError(result.message)
        }
    }
    
    /**
     * Advances to the next round
     */
    fun nextRound() {
        when (val result = gameStateManager.startNewRound()) {
            is GameStateResult.Success -> {
                feedbackManager.startNewRound()
                uiStateManager.clearError()
            }
            is GameStateResult.Error -> uiStateManager.setError(result.message)
        }
    }
    
    /**
     * Clears any current error message
     */
    fun clearError() {
        uiStateManager.clearError()
    }
    
    /**
     * Clears current decision feedback
     */
    fun clearFeedback() {
        feedbackManager.clearFeedback()
    }
    /** Current pending bet amount */
    val currentBetAmount: Int get() = game?.pendingBet ?: 0
    
    /** True if cards can be dealt (bet placed and game ready) */
    val canDealCards: Boolean get() = game?.canDealCards ?: false
    
    /** Visual representation of chips in betting circle */
    val chipComposition: List<ChipInSpot>
        get() = uiStateManager.calculateChipComposition(currentBetAmount)
    
    /** Player's available chip balance */
    val availableBalance: Int get() = game?.player?.chips ?: 0
    
    /**
     * Adds a chip to the current bet
     * 
     * @param chipValue Value of chip to add
     */
    fun addChipToBet(chipValue: ChipValue) {
        when (val result = gameStateManager.addChipToBet(chipValue)) {
            is GameStateResult.Success -> uiStateManager.clearError()
            is GameStateResult.Error -> uiStateManager.setError(result.message)
        }
    }
    
    /**
     * Clears all chips from the current bet
     */
    fun clearBet() {
        when (val result = gameStateManager.clearBet()) {
            is GameStateResult.Success -> uiStateManager.clearError()
            is GameStateResult.Error -> uiStateManager.setError(result.message)
        }
    }
    
    /**
     * Deals initial cards to start the round
     */
    fun dealCards() {
        when (val result = gameStateManager.dealCards()) {
            is GameStateResult.Success -> {
                feedbackManager.reset()
                uiStateManager.clearError()
            }
            is GameStateResult.Error -> uiStateManager.setError(result.message)
        }
    }
    
    /**
     * Gets scenarios where player made the most mistakes
     * 
     * @param minSamples Minimum number of samples required
     * @return List of scenarios sorted by error rate
     */
    fun getWorstScenarios(minSamples: Int = 3): List<ScenarioErrorStat> {
        return analyticsManager.getWorstScenarios(minSamples)
    }
    
    /**
     * Gets the most recent player decisions
     * 
     * @param limit Maximum number of decisions to return
     * @return List of recent decisions ordered by timestamp
     */
    fun getRecentDecisions(limit: Int = 50): List<DecisionRecord> {
        return analyticsManager.getRecentDecisions(limit)
    }
    
    /**
     * Clears all recorded learning data and statistics
     */
    fun clearAllLearningData() {
        analyticsManager.clearAllLearningData()
    }
    
    /**
     * Gets recent decisions filtered by current game rules
     * 
     * @param limit Maximum number of decisions to return
     * @return List of decisions for current rule set
     */
    fun getRecentDecisionsForCurrentRule(limit: Int = 50): List<DecisionRecord> {
        return analyticsManager.getRecentDecisionsForCurrentRule(currentGameRules, limit)
    }
    
    /**
     * Gets detailed statistics for all played scenarios
     * 
     * @return Map of scenario ID to statistics
     */
    fun getScenarioStats(): Map<String, ScenarioStats> {
        return analyticsManager.getScenarioStats()
    }
    
    /** Current game rules or null if no game active */
    val currentGameRules: GameRules? get() = game?.rules
    
    /**
     * Handles game rule changes and shows notification
     * 
     * @param newRules The new rules to apply
     */
    fun handleRuleChange(newRules: GameRules) {
        val currentGame = game ?: return
        
        uiStateManager.handleRuleChangeNotification(
            currentRules = currentGame.rules,
            newRules = newRules,
            sessionStats = sessionStats
        )
        
        gameStateManager.handleRuleChange(newRules)
    }
    
    /**
     * Gets worst scenarios for current rule set only
     * 
     * @param minSamples Minimum number of samples required
     * @return List of scenarios for current rules sorted by error rate
     */
    fun getCurrentRuleWorstScenarios(minSamples: Int = 3): List<ScenarioErrorStat> {
        return analyticsManager.getCurrentRuleWorstScenarios(currentGameRules, minSamples)
    }
    
    /**
     * Gets statistics segmented by current rule set
     * 
     * @return Rule-specific statistics or null if no current rules
     */
    fun getCurrentRuleStats(): RuleSegmentStats? {
        return analyticsManager.getCurrentRuleStats()
    }
    
    /**
     * Dismisses the rule change notification banner
     */
    fun dismissRuleChangeNotification() {
        uiStateManager.dismissRuleChangeNotification()
    }
}