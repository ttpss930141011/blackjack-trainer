package org.ttpss930141011.bj.application

import androidx.compose.runtime.*
import org.ttpss930141011.bj.domain.*

class GameViewModel(
    private val gameService: GameService = GameService(),
    private val sessionService: GameSessionService = GameSessionService()
) {
    
    private var _game by mutableStateOf<Game?>(null)
    val game: Game? get() = _game
    
    private var _feedback by mutableStateOf<DecisionFeedback?>(null)
    val feedback: DecisionFeedback? get() = _feedback
    
    private var _sessionStats by mutableStateOf(SessionStats())
    val sessionStats: SessionStats get() = _sessionStats
    
    private var _roundDecisions by mutableStateOf<List<PlayerDecision>>(emptyList())
    val roundDecisions: List<PlayerDecision> get() = _roundDecisions
    
    private var _showGameSummary by mutableStateOf(false)
    val showGameSummary: Boolean get() = _showGameSummary
    
    private var _errorMessage by mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage
    
    fun initializeGame(gameRules: GameRules, player: Player) {
        _game = gameService.createNewGame(gameRules, player)
        _feedback = null
        _sessionStats = SessionStats()
        _roundDecisions = emptyList()
        _showGameSummary = false
        _errorMessage = null
    }
    
    fun startRound(betAmount: Int) {
        val currentGame = _game ?: return
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
            
            val feedback = sessionService.evaluatePlayerDecision(
                handBeforeAction = result.handBeforeAction,
                dealerUpCard = currentGame.dealer.upCard!!,
                playerAction = action,
                rules = currentGame.rules
            )
            _feedback = feedback
            
            val playerDecision = sessionService.createPlayerDecision(action, feedback.isCorrect)
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
            _errorMessage = null
        } catch (e: Exception) {
            _errorMessage = e.message
        }
    }
    
    fun settleRound() {
        val currentGame = _game ?: return
        try {
            _game = gameService.settleRound(currentGame)
            
            val outcome = sessionService.determineRoundOutcome(currentGame)
            _sessionStats = sessionService.updateSessionStats(_sessionStats, _roundDecisions, outcome)
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
    
    fun showGameSummary() {
        _showGameSummary = true
    }
    
    fun hideGameSummary() {
        _showGameSummary = false
    }
    
    fun clearError() {
        _errorMessage = null
    }
}