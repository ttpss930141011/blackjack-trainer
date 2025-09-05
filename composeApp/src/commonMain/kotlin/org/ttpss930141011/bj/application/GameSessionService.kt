package org.ttpss930141011.bj.application

import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.*

class GameSessionService {
    
    private val strategyEngine = StrategyEngine()
    
    fun evaluatePlayerDecision(
        handBeforeAction: PlayerHand,
        dealerUpCard: Card,
        playerAction: Action,
        rules: GameRules
    ): DecisionFeedback {
        return DecisionFeedback.evaluate(
            playerHand = Hand(handBeforeAction.cards),
            dealerUpCard = dealerUpCard,
            playerAction = playerAction,
            strategyEngine = strategyEngine,
            rules = rules
        )
    }
    
    fun updateSessionStats(
        currentStats: SessionStats,
        roundDecisions: List<PlayerDecision>,
        roundOutcome: String
    ): SessionStats {
        return currentStats.recordRoundWithHistory(roundDecisions, roundOutcome)
    }
    
    fun determineRoundOutcome(game: Game): String {
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
    
    fun createPlayerDecision(action: Action, isCorrect: Boolean): PlayerDecision {
        return PlayerDecision(action, isCorrect)
    }
}