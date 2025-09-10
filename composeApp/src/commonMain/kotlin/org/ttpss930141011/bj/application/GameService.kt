package org.ttpss930141011.bj.application

import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.*

/**
 * Application service for game lifecycle management.
 * Coordinates high-level game operations and delegates to domain services.
 */
class GameService {
    
    /**
     * Creates a new game with specified rules and player.
     * 
     * @param rules The game rules to apply
     * @param player The player to add to the game
     * @return New game instance ready for betting
     */
    fun createNewGame(rules: GameRules, player: Player): Game {
        return Game.create(rules).addPlayer(player)
    }
    
    /**
     * Places a bet and immediately deals the initial cards.
     * 
     * @param game Current game state
     * @param betAmount Amount to bet
     * @return Game state with bet placed and cards dealt
     */
    fun placeBetAndDeal(game: Game, betAmount: Int): Game {
        return game.placeBet(betAmount).dealRound()
    }
    
    /**
     * Deals initial cards for the current round.
     * 
     * @param game Current game state (must have a committed bet)
     * @return Game state with initial cards dealt
     * @throws IllegalArgumentException if no bet is placed
     */
    fun dealRound(game: Game): Game {
        require(game.hasAnyBet) { "No bet placed" }
        return game.dealRound()
    }
    
    /**
     * Executes a player action and returns the result with context.
     * 
     * @param game Current game state (must be in player turn phase)
     * @param action The action to execute
     * @return GameActionResult containing updated game state and action context
     * @throws IllegalArgumentException if action cannot be performed
     */
    fun executePlayerAction(game: Game, action: Action): GameActionResult {
        require(game.phase == GamePhase.PLAYER_TURN) { "Not in player action phase" }
        require(game.canAct) { "Player cannot act" }
        
        val handBeforeAction = game.currentHand!!
        
        val updatedGame = game.playerAction(action)
        
        return GameActionResult(
            game = updatedGame,
            handBeforeAction = handBeforeAction,
            actionTaken = action
        )
    }
    
    /**
     * Processes the dealer's automated turn according to standard rules.
     * 
     * @param game Current game state (must be in dealer turn phase)
     * @return Game state after dealer completes their turn
     * @throws IllegalArgumentException if not in dealer turn phase
     */
    fun processDealerTurn(game: Game): Game {
        require(game.phase == GamePhase.DEALER_TURN) { "Not in dealer turn phase" }
        return game.dealerPlayAutomated()
    }
    
    /**
     * Settles the current round, calculating results and updating player balance.
     * 
     * @param game Current game state (must be in settlement phase)
     * @return Game state with round settled and balances updated
     * @throws IllegalArgumentException if not in settlement phase
     */
    fun settleRound(game: Game): Game {
        require(game.phase == GamePhase.SETTLEMENT) { "Not in settlement phase" }
        return game.settleRound()
    }
    
    /**
     * Resets the game state for a new round.
     * 
     * @param game Current game state
     * @return Game state reset and ready for new bets
     */
    fun startNewRound(game: Game): Game {
        return game.resetForNewRound()
    }
}

/**
 * Result of a player action execution containing the updated game state and action context.
 * 
 * @property game The updated game state after the action
 * @property handBeforeAction The hand state before the action was taken
 * @property actionTaken The action that was executed
 */
data class GameActionResult(
    val game: Game,
    val handBeforeAction: PlayerHand,
    val actionTaken: Action
)