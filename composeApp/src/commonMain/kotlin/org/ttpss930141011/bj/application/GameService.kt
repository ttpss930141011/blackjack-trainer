package org.ttpss930141011.bj.application

import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.*

class GameService {
    
    fun createNewGame(rules: GameRules, player: Player): Game {
        return Game.create(rules).addPlayer(player)
    }
    
    fun placeBetAndDeal(game: Game, betAmount: Int): Game {
        return game.placeBet(betAmount).dealRound()
    }
    
    fun dealRound(game: Game): Game {
        require(game.hasBet) { "No bet placed" }
        return game.dealRound()
    }
    
    fun executePlayerAction(game: Game, action: Action): GameActionResult {
        require(game.phase == GamePhase.PLAYER_TURN) { "Not in player action phase" }
        require(game.canAct) { "Player cannot act" }
        
        val handBeforeAction = game.currentHand!!
        
        // Let domain layer handle all validation and payment logic
        // Domain's availableActions() already validates balance for DOUBLE
        val updatedGame = game.playerAction(action)
        
        return GameActionResult(
            game = updatedGame,
            handBeforeAction = handBeforeAction,
            actionTaken = action
        )
    }
    
    fun processDealerTurn(game: Game): Game {
        require(game.phase == GamePhase.DEALER_TURN) { "Not in dealer turn phase" }
        return game.dealerPlayAutomated()
    }
    
    fun settleRound(game: Game): Game {
        require(game.phase == GamePhase.SETTLEMENT) { "Not in settlement phase" }
        return game.settleRound()
    }
    
    fun startNewRound(game: Game): Game {
        return game.resetForNewRound()
    }
}

data class GameActionResult(
    val game: Game,
    val handBeforeAction: PlayerHand,
    val actionTaken: Action
)