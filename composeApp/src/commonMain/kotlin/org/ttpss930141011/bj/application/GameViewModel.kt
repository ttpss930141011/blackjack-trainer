package org.ttpss930141011.bj.application

import androidx.compose.runtime.*
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.*
import org.ttpss930141011.bj.infrastructure.InMemoryLearningRepository

class GameViewModel(
    private val gameService: GameService = GameService(),
    private val decisionEvaluator: DecisionEvaluator = DecisionEvaluator(),
    private val learningRecorder: LearningRecorder = LearningRecorder(InMemoryLearningRepository()),
    private val chipCompositionService: ChipCompositionService = ChipCompositionService()
) {
    
    private var _game by mutableStateOf<Game?>(null)
    val game: Game? get() = _game
    
    private var _feedback by mutableStateOf<DecisionFeedback?>(null)
    val feedback: DecisionFeedback? get() = _feedback
    
    private var _sessionStats by mutableStateOf(SessionStats())
    val sessionStats: SessionStats get() = _sessionStats
    
    private var _roundDecisions by mutableStateOf<List<PlayerDecision>>(emptyList())
    val roundDecisions: List<PlayerDecision> get() = _roundDecisions
    
    
    private var _errorMessage by mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage
    
    private var _ruleChangeNotification by mutableStateOf<String?>(null)
    val ruleChangeNotification: String? get() = _ruleChangeNotification
    
    // Game over integration from Domain layer
    val isGameOver: Boolean
        get() = _game?.isGameOver ?: false
    
    fun initializeGame(gameRules: GameRules, player: Player) {
        _game = gameService.createNewGame(gameRules, player)
        _feedback = null
        _sessionStats = SessionStats()
        _roundDecisions = emptyList()
        _errorMessage = null
    }
    
    fun startRound(betAmount: Int) {
        val currentGame = _game ?: return
        if (isGameOver) {
            _errorMessage = "Game Over! Insufficient chips to place minimum bet."
            return
        }
        try {
            _game = gameService.placeBetAndDeal(currentGame, betAmount)
            _feedback = null
            _errorMessage = null
        } catch (e: Exception) {
            _errorMessage = e.message
        }
    }
    
    fun playerAction(action: Action) {
        val currentGame = _game ?: return
        try {
            val result = gameService.executePlayerAction(currentGame, action)
            _game = result.game
            
            val feedback = decisionEvaluator.evaluateDecision(
                handBeforeAction = result.handBeforeAction,
                dealerUpCard = currentGame.dealer.upCard!!,
                playerAction = action,
                rules = currentGame.rules
            )
            _feedback = feedback
            
            // Record decision for learning analytics
            learningRecorder.recordDecision(
                handBeforeAction = result.handBeforeAction,
                dealerUpCard = currentGame.dealer.upCard!!,
                playerAction = action,
                isCorrect = feedback.isCorrect,
                gameRules = currentGame.rules
            )
            
            val playerDecision = PlayerDecision(action, feedback.isCorrect)
            _roundDecisions = _roundDecisions + playerDecision
            _errorMessage = null
            
        } catch (e: Exception) {
            _errorMessage = e.message
        }
    }
    
    fun dealerTurn() {
        val currentGame = _game ?: return
        try {
            _game = gameService.processDealerTurn(currentGame)
            
            // 自動結算：當進入 SETTLEMENT 階段時立即結算
            if (_game?.phase == GamePhase.SETTLEMENT && _game?.isSettled == false) {
                _game = gameService.settleRound(_game!!)
                
                val outcome = determineRoundOutcome(_game!!)
                _sessionStats = _sessionStats.recordRound(_roundDecisions)
            }
            
            _errorMessage = null
        } catch (e: Exception) {
            _errorMessage = e.message
        }
    }
    
    
    fun nextRound() {
        val currentGame = _game ?: return
        _game = gameService.startNewRound(currentGame)
        _feedback = null
        _roundDecisions = emptyList()
        _errorMessage = null
    }
    
    
    fun clearError() {
        _errorMessage = null
    }
    
    fun clearFeedback() {
        _feedback = null
    }
    
    // Domain-based chip display properties for UI layer
    val currentBetAmount: Int get() = _game?.pendingBet ?: 0
    val canDealCards: Boolean get() = _game?.canDealCards ?: false
    val chipComposition: List<ChipInSpot> 
        get() = _game?.pendingBet?.let { amount ->
            if (amount > 0) chipCompositionService.calculateOptimalComposition(amount)
            else emptyList()
        } ?: emptyList()
    val availableBalance: Int get() = _game?.player?.chips ?: 0
    
    fun addChipToBet(chipValue: ChipValue) {
        val currentGame = _game ?: return
        if (currentGame.phase != GamePhase.WAITING_FOR_BETS) return
        if (isGameOver) {
            _errorMessage = "Game Over! Insufficient chips to place bets."
            return
        }
        
        try {
            val result = currentGame.tryAddChipToPendingBet(chipValue)
            
            if (result.success) {
                _game = result.updatedGame
                _errorMessage = null
            } else {
                _errorMessage = result.errorMessage
            }
        } catch (e: Exception) {
            _errorMessage = e.message
        }
    }
    
    fun clearBet() {
        val currentGame = _game ?: return
        if (currentGame.phase != GamePhase.WAITING_FOR_BETS) return
        
        try {
            _game = currentGame.clearPendingBet()
            _errorMessage = null
        } catch (e: Exception) {
            _errorMessage = e.message
        }
    }
    
    fun dealCards() {
        val currentGame = _game ?: return
        
        if (!currentGame.canDealCards) {
            _errorMessage = "Cannot deal cards at this time"
            return
        }
        
        try {
            // Commit pending bet and deal
            val gameWithCommittedBet = currentGame.commitPendingBet()
            _game = gameService.dealRound(gameWithCommittedBet)
            _feedback = null
            _errorMessage = null
        } catch (e: Exception) {
            _errorMessage = e.message
        }
    }
    
    private fun determineRoundOutcome(game: Game): String {
        require(game.phase == GamePhase.SETTLEMENT) { "Game must be in settlement phase" }
        
        return if (game.playerHands.isNotEmpty()) {
            val firstHand = game.playerHands[0]
            when (firstHand.status) {
                HandStatus.WIN -> "WIN"
                HandStatus.LOSS, HandStatus.BUSTED -> "LOSS"
                HandStatus.PUSH -> "PUSH"
                else -> "UNKNOWN"
            }
        } else "UNKNOWN"
    }
    
    // Learning analytics access
    fun getWorstScenarios(minSamples: Int = 3): List<Pair<String, Double>> {
        return learningRecorder.getWorstScenarios(minSamples)
    }
    
    fun getRecentDecisions(limit: Int = 50): List<DecisionRecord> {
        return learningRecorder.getRecentDecisions(limit)
    }
    
    /**
     * Clear all learning data (for testing or reset)
     */
    fun clearAllLearningData() {
        learningRecorder.clearAllData()
    }
    
    /**
     * Get scenario statistics from the learning repository
     */
    fun getScenarioStats(): Map<String, org.ttpss930141011.bj.infrastructure.ScenarioStats> {
        return learningRecorder.getScenarioStats()
    }
    
    // === Rule-Aware Analytics Methods ===
    
    /**
     * Handle game rule changes during active session.
     * Shows notification about rule change impact on statistics.
     */
    fun handleRuleChange(newRules: GameRules) {
        val currentGame = _game ?: return
        
        // Check if rules actually changed
        if (currentGame.rules == newRules) {
            _ruleChangeNotification = null
            return
        }
        
        // Check if this creates rule change notification
        if (_sessionStats.hasRuleChanged(newRules)) {
            _ruleChangeNotification = "⚠️ Rule change detected: Starting fresh analytics for new rule set"
            
            // Show comparison if available
            _sessionStats.getRuleComparisonSummary()?.let { comparison ->
                _ruleChangeNotification = "⚠️ $comparison"
            }
        }
        
        // Update game with new rules (without resetting state)
        _game = currentGame.copy(rules = newRules)
    }
    
    /**
     * Get rule-specific worst scenarios (clean analytics).
     */
    fun getCurrentRuleWorstScenarios(minSamples: Int = 3): List<Pair<String, Double>> {
        return _sessionStats.getCurrentRuleWorstScenarios(minSamples)
    }
    
    /**
     * Get statistics for current rule set only.
     */
    fun getCurrentRuleStats(): RuleSegmentStats? {
        return _sessionStats.getCurrentRuleStats()
    }
    
    /**
     * Dismiss rule change notification.
     */
    fun dismissRuleChangeNotification() {
        _ruleChangeNotification = null
    }
    
}