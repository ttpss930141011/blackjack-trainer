package org.ttpss930141011.bj.application

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.ttpss930141011.bj.domain.entities.Game
import org.ttpss930141011.bj.domain.entities.Player
import org.ttpss930141011.bj.domain.entities.RoundOutcome
import org.ttpss930141011.bj.domain.valueobjects.ActionResult
import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.valueobjects.ChipInSpot
import org.ttpss930141011.bj.domain.valueobjects.DecisionFeedback
import org.ttpss930141011.bj.domain.valueobjects.GameRules
import org.ttpss930141011.bj.domain.valueobjects.PlayerDecision
import org.ttpss930141011.bj.domain.valueobjects.PlayerHand
import org.ttpss930141011.bj.domain.valueobjects.RoundHistory
import org.ttpss930141011.bj.domain.valueobjects.RoundStartContext
import org.ttpss930141011.bj.domain.valueobjects.SessionStats
import org.ttpss930141011.bj.domain.valueobjects.UserPreferences
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.domain.enums.ChipValue
import org.ttpss930141011.bj.domain.enums.GamePhase
import org.ttpss930141011.bj.domain.enums.RoundResult
import org.ttpss930141011.bj.domain.services.AudioManager
import org.ttpss930141011.bj.domain.services.ChipCompositionService
import org.ttpss930141011.bj.domain.services.GameService
import org.ttpss930141011.bj.infrastructure.DataLoader
import org.ttpss930141011.bj.infrastructure.CachingDataLoader
import org.ttpss930141011.bj.infrastructure.audio.AudioModule

/**
 * GameViewModel - Coordinator for game, feedback, analytics, betting, and preferences.
 *
 * Delegates to six focused managers. Exposes a unified API for the UI layer.
 */
class GameViewModel(
    private val gameService: GameService = GameService(),
    private val decisionEvaluator: DecisionEvaluator = DecisionEvaluator(),
    private val persistenceService: PersistenceService = PersistenceService(),
    private val chipCompositionService: ChipCompositionService = ChipCompositionService(),
    private val dataLoader: DataLoader = CachingDataLoader(),
    private val audioManager: AudioManager = AudioModule.getAudioManager(),
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    // --- Managers ---
    private val gameStateManager = GameStateManager(gameService)
    private val feedbackManager = FeedbackManager(decisionEvaluator, audioManager, coroutineScope)
    private val analyticsManager = AnalyticsManager(persistenceService)
    private val bettingManager = BettingManager(gameStateManager)
    private val preferencesManager = PreferencesManager(persistenceService, audioManager, dataLoader, coroutineScope)
    val uiStateManager = UIStateManager(chipCompositionService)

    // --- Core state exposure ---
    val game: Game? get() = gameStateManager.game
    val feedback: DecisionFeedback? get() = feedbackManager.feedback
    val sessionStats: SessionStats get() = analyticsManager.sessionStats
    val roundDecisions: List<PlayerDecision> get() = feedbackManager.roundDecisions
    val errorMessage: String? get() = uiStateManager.errorMessage
    val isGameOver: Boolean get() = gameStateManager.isGameOver

    // --- Preferences ---
    val userPreferences: UserPreferences get() = preferencesManager.userPreferences

    // --- Betting ---
    val lastBetAmount: Int? get() = bettingManager.lastBetAmount
    val currentBetAmount: Int get() = game?.betState?.amount ?: 0
    val canDealCards: Boolean get() = game?.canDealCards ?: false
    val chipComposition: List<ChipInSpot> get() = uiStateManager.calculateChipComposition(currentBetAmount)
    val availableBalance: Int get() = game?.player?.chips ?: 0

    // --- Round history ---
    private var _recentRounds by mutableStateOf<List<RoundHistory>>(emptyList())
    val recentRounds: List<RoundHistory> get() = _recentRounds
    private var roundStartTime: Long = 0

    val currentGameRules: GameRules? get() = game?.rules

    // ===== GAME LIFECYCLE =====

    fun initializeGame(gameRules: GameRules, player: Player) {
        gameStateManager.initializeGame(gameRules, player)
        feedbackManager.reset()
        analyticsManager.resetSession()
        uiStateManager.clearError()
    }

    fun dealCards() {
        bettingManager.rememberLastBet(currentBetAmount)
        roundStartTime = TimeProvider.currentTimeMillis()

        when (val result = gameStateManager.dealCards()) {
            is GameStateResult.Success -> {
                feedbackManager.reset()
                uiStateManager.clearError()
                coroutineScope.launch { audioManager.playCardSound() }
                initializeRoundContext()
            }
            is GameStateResult.Error -> uiStateManager.setError(result.message)
        }
    }

    fun playerAction(action: Action) {
        val handBefore = game?.currentHand?.copy(cards = game?.currentHand?.cards?.toList() ?: emptyList())
        val dealerUp = game?.dealer?.upCard
        val rules = game?.rules

        executePlayerAction(action)

        if (feedback != null && handBefore != null && dealerUp != null && rules != null) {
            val handAfter = game?.currentHand
            val actionResult = ActionResultFactory.create(action, handBefore, handAfter, game)
            recordDecision(action, handBefore, dealerUp, rules, actionResult)
        }
        handleAutoTransitions()
    }

    fun dealerTurn() {
        when (val result = gameStateManager.processDealerTurn()) {
            is GameStateResult.Success -> {
                if (game?.phase == GamePhase.SETTLEMENT && game?.isSettled == true) {
                    completeRoundWithHistory()
                }
                uiStateManager.clearError()
            }
            is GameStateResult.Error -> uiStateManager.setError(result.message)
        }
    }

    fun nextRound() {
        when (val result = gameStateManager.startNewRound()) {
            is GameStateResult.Success -> {
                feedbackManager.startNewRound()
                uiStateManager.clearError()
                bettingManager.onNewRound()
                roundStartTime = 0
            }
            is GameStateResult.Error -> uiStateManager.setError(result.message)
        }
    }

    // ===== BETTING =====

    fun addChipToBet(chipValue: ChipValue) {
        when (val result = bettingManager.addChipToBet(chipValue)) {
            is GameStateResult.Success -> uiStateManager.clearError()
            is GameStateResult.Error -> uiStateManager.setError(result.message)
        }
    }

    fun clearBet() {
        when (val result = bettingManager.clearBet()) {
            is GameStateResult.Success -> {
                bettingManager.onBetCleared()
                uiStateManager.clearError()
            }
            is GameStateResult.Error -> uiStateManager.setError(result.message)
        }
    }

    fun repeatLastBet(): Boolean {
        val ok = bettingManager.repeatLastBet()
        if (ok) uiStateManager.clearError()
        return ok
    }

    // ===== PREFERENCES =====

    fun loadUserPreferences() {
        preferencesManager.load { prefs ->
            bettingManager.restoreFromPreferences(prefs.lastBetAmount)
        }
    }

    fun updateUserPreferences(newPreferences: UserPreferences) {
        preferencesManager.update(newPreferences)
        bettingManager.restoreFromPreferences(newPreferences.lastBetAmount)
    }

    // ===== ROUND HISTORY =====

    fun loadRecentRounds() {
        coroutineScope.launch {
            _recentRounds = persistenceService.getRecentRounds()
        }
    }

    fun refreshAllData() { loadRecentRounds() }

    fun refreshRoundHistory() {
        dataLoader.invalidate("recent_rounds")
        loadRecentRounds()
    }

    // ===== RULES =====

    fun handleRuleChange(newRules: GameRules) {
        val currentGame = game ?: return
        uiStateManager.handleRuleChangeNotification(
            currentRules = currentGame.rules,
            newRules = newRules,
            sessionStats = sessionStats
        )
        gameStateManager.handleRuleChange(newRules)
    }

    // ===== UI HELPERS =====

    fun clearError() { uiStateManager.clearError() }
    fun clearFeedback() { feedbackManager.clearFeedback() }

    fun clearAllLearningData() {
        coroutineScope.launch {
            persistenceService.clearAllLearningData()
            loadRecentRounds()
        }
    }

    // ===== PRIVATE =====

    private fun initializeRoundContext() {
        val g = game ?: return
        analyticsManager.startRound(
            RoundStartContext(
                sessionId = analyticsManager.currentSessionId,
                gameRules = g.rules,
                betAmount = g.betState.amount,
                initialPlayerHands = g.playerHands,
                dealerVisibleCard = g.dealer.upCard ?: Card.UNKNOWN_CARD,
                startTimestamp = roundStartTime
            )
        )
    }

    private fun completeRoundWithHistory() {
        val g = game ?: return
        val duration = if (roundStartTime > 0) TimeProvider.currentTimeMillis() - roundStartTime else 0
        val netChip = when (g.getRoundOutcome()) {
            RoundOutcome.WIN -> g.betState.amount
            RoundOutcome.LOSS -> -g.betState.amount
            RoundOutcome.PUSH, RoundOutcome.UNKNOWN -> 0
        }
        g.dealer.hand?.let { dealerHand ->
            analyticsManager.completeRound(
                finalPlayerHands = g.playerHands,
                dealerFinalHand = dealerHand,
                roundResult = g.getRoundOutcome().toRoundResult(),
                netChipChange = netChip,
                roundDurationMs = duration
            )
        }
        refreshRoundHistory()
    }

    private fun executePlayerAction(action: Action) {
        val result = gameStateManager.executePlayerAction(action)
        if (result != null) {
            feedbackManager.evaluatePlayerAction(
                handBeforeAction = result.handBeforeAction,
                dealerUpCard = game!!.dealer.upCard!!,
                playerAction = action,
                rules = game!!.rules
            )
            uiStateManager.clearError()
        } else {
            uiStateManager.setError("Failed to execute player action")
        }
    }

    private fun recordDecision(
        action: Action, hand: PlayerHand, dealerUp: Card,
        rules: GameRules, actionResult: ActionResult
    ) {
        val fb = feedback ?: return
        analyticsManager.recordPlayerAction(
            handBeforeAction = hand, dealerUpCard = dealerUp,
            playerAction = action, isCorrect = fb.isCorrect,
            gameRules = rules, actionResult = actionResult
        )
    }

    private fun handleAutoTransitions() {
        val g = game ?: return
        if (g.shouldAutoAdvance() && g.phase == GamePhase.DEALER_TURN) dealerTurn()
    }
}

private fun RoundOutcome.toRoundResult(): RoundResult = when (this) {
    RoundOutcome.WIN -> RoundResult.PLAYER_WIN
    RoundOutcome.LOSS -> RoundResult.DEALER_WIN
    RoundOutcome.PUSH -> RoundResult.PUSH
    RoundOutcome.UNKNOWN -> RoundResult.DEALER_WIN
}
