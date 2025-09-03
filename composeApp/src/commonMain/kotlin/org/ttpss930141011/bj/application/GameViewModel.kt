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
            
            val outcome = sessionService.determineRoundOutcome(_game!!)
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
        initializeBettingTableState()
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
    
    fun clearFeedback() {
        _feedback = null
    }
    
    // New betting table methods for chip-by-chip betting
    private var _bettingTableState by mutableStateOf<BettingTableState?>(null)
    val bettingTableState: BettingTableState? get() = _bettingTableState
    
    fun addChipToBet(chipValue: ChipValue) {
        val currentGame = _game ?: return
        if (currentGame.phase != GamePhase.WAITING_FOR_BETS) return
        
        try {
            val currentTable = _bettingTableState ?: BettingTableState.fromGame(currentGame)
            val result = currentTable.tryAddChip(chipValue)
            
            if (result.success) {
                _bettingTableState = result.bettingTable
                // Update the game state with the new bet
                _game = result.bettingTable.toGameBet(currentGame)
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
            val currentTable = _bettingTableState ?: BettingTableState.fromGame(currentGame)
            val clearedTable = currentTable.clearBet()
            _bettingTableState = clearedTable
            
            // Restore player's chips and clear bet in game
            val restoredPlayer = currentGame.player!!.copy(chips = clearedTable.availableBalance + clearedTable.currentBet)
            _game = currentGame.copy(
                player = restoredPlayer,
                currentBet = 0
            )
            _errorMessage = null
            
        } catch (e: Exception) {
            _errorMessage = e.message
        }
    }
    
    fun dealCards() {
        val currentGame = _game ?: return
        val currentTable = _bettingTableState ?: return
        
        if (currentGame.phase != GamePhase.WAITING_FOR_BETS || !currentTable.canDeal) {
            _errorMessage = "Cannot deal cards at this time"
            return
        }
        
        try {
            // Convert betting table state to final game bet and deal
            val gameWithBet = currentTable.toGameBet(currentGame)
            _game = gameService.dealRound(gameWithBet)
            _bettingTableState = null // Clear betting state
            _feedback = null
            _errorMessage = null
            
        } catch (e: Exception) {
            _errorMessage = e.message
        }
    }
    
    // Initialize betting table state when entering betting phase
    private fun initializeBettingTableState() {
        val currentGame = _game
        if (currentGame?.phase == GamePhase.WAITING_FOR_BETS) {
            _bettingTableState = BettingTableState.fromGame(currentGame)
        }
    }
}