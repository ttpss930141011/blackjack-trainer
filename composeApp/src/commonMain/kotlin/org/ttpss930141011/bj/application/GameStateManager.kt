package org.ttpss930141011.bj.application

import androidx.compose.runtime.*
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*

/**
 * GameStateManager - Focuses ONLY on game state management
 * 
 * Linus: "One job, do it well. No UI shit, no analytics shit, just game state."
 */
internal class GameStateManager(
    private val gameService: GameService
) {
    private var _game by mutableStateOf<Game?>(null)
    val game: Game? get() = _game
    
    val isGameOver: Boolean get() = _game?.isGameOver ?: false
    
    fun initializeGame(gameRules: GameRules, player: Player) {
        _game = gameService.createNewGame(gameRules, player)
    }
    
    fun startRound(betAmount: Int): GameStateResult {
        val currentGame = _game ?: return GameStateResult.error("No game initialized")
        
        if (isGameOver) {
            return GameStateResult.error("Game Over! Insufficient chips to place minimum bet.")
        }
        
        return try {
            _game = gameService.placeBetAndDeal(currentGame, betAmount)
            GameStateResult.success()
        } catch (e: Exception) {
            GameStateResult.error(e.message ?: "Unknown error")
        }
    }
    
    fun executePlayerAction(action: Action): GameActionResult? {
        val currentGame = _game ?: return null
        
        return try {
            val result = gameService.executePlayerAction(currentGame, action)
            _game = result.game
            result
        } catch (e: Exception) {
            null
        }
    }
    
    fun processDealerTurn(): GameStateResult {
        val currentGame = _game ?: return GameStateResult.error("No game initialized")
        
        return try {
            _game = gameService.processDealerTurn(currentGame)
            
            // Let Game handle auto settlement
            if (_game?.shouldAutoAdvance() == true && _game?.phase == GamePhase.SETTLEMENT) {
                _game = gameService.settleRound(_game!!)
            }
            
            GameStateResult.success()
        } catch (e: Exception) {
            GameStateResult.error(e.message ?: "Unknown error")
        }
    }
    
    fun startNewRound(): GameStateResult {
        val currentGame = _game ?: return GameStateResult.error("No game initialized")
        
        return try {
            _game = gameService.startNewRound(currentGame)
            GameStateResult.success()
        } catch (e: Exception) {
            GameStateResult.error(e.message ?: "Unknown error")
        }
    }
    
    fun addChipToBet(chipValue: ChipValue): GameStateResult {
        val currentGame = _game ?: return GameStateResult.error("No game initialized")
        
        if (currentGame.phase != GamePhase.WAITING_FOR_BETS) {
            return GameStateResult.success() // Silently ignore if not in betting phase
        }
        
        if (isGameOver) {
            return GameStateResult.error("Game Over! Insufficient chips to place bets.")
        }
        
        return try {
            val result = currentGame.tryAddChipToPendingBet(chipValue)
            if (result.success) {
                _game = result.updatedGame
                GameStateResult.success()
            } else {
                GameStateResult.error(result.errorMessage ?: "Failed to add chip")
            }
        } catch (e: Exception) {
            GameStateResult.error(e.message ?: "Unknown error")
        }
    }
    
    fun clearBet(): GameStateResult {
        val currentGame = _game ?: return GameStateResult.error("No game initialized")
        
        if (currentGame.phase != GamePhase.WAITING_FOR_BETS) {
            return GameStateResult.success() // Silently ignore
        }
        
        return try {
            _game = currentGame.clearBet()
            GameStateResult.success()
        } catch (e: Exception) {
            GameStateResult.error(e.message ?: "Unknown error")
        }
    }
    
    fun dealCards(): GameStateResult {
        val currentGame = _game ?: return GameStateResult.error("No game initialized")
        
        if (!currentGame.canDealCards) {
            return GameStateResult.error("Cannot deal cards at this time")
        }
        
        return try {
            val gameWithCommittedBet = currentGame.commitPendingBet()
            _game = gameService.dealRound(gameWithCommittedBet)
            GameStateResult.success()
        } catch (e: Exception) {
            GameStateResult.error(e.message ?: "Unknown error")
        }
    }
    
    fun handleRuleChange(newRules: GameRules) {
        val currentGame = _game ?: return
        _game = currentGame.copy(rules = newRules)
    }
}

/**
 * Simple result classes - no over-engineering
 */
internal sealed class GameStateResult {
    object Success : GameStateResult()
    data class Error(val message: String) : GameStateResult()
    
    companion object {
        fun success() = Success
        fun error(message: String) = Error(message)
    }
}