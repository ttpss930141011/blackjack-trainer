package org.ttpss930141011.bj.domain.services

import org.ttpss930141011.bj.domain.entities.Game
import org.ttpss930141011.bj.domain.enums.ChipValue
import org.ttpss930141011.bj.domain.enums.GamePhase
import org.ttpss930141011.bj.domain.valueobjects.AddChipResult
import org.ttpss930141011.bj.domain.valueobjects.BetState

/**
 * Stateless domain service encapsulating all betting validation and state transitions.
 * Extracted from Game aggregate root to reduce its size.
 */
class BettingPolicy {

    fun addToPendingBet(game: Game, amount: Int): Game {
        require(game.hasPlayer) { "No player in game" }
        require(game.phase == GamePhase.WAITING_FOR_BETS) { "Can only add to pending bet during betting phase" }
        require(amount > 0) { "Amount must be positive" }
        require(game.player!!.chips >= (game.betState.amount + amount)) { "Insufficient chips" }

        return game.copy(betState = game.betState.add(amount))
    }

    fun clearBet(game: Game): Game {
        require(game.phase == GamePhase.WAITING_FOR_BETS) { "Can only clear bet during betting phase" }
        return game.copy(betState = game.betState.clear())
    }

    fun commitPendingBet(game: Game): Game {
        require(game.betState.isPending) { "No pending bet to commit" }
        require(game.hasPlayer) { "No player in game" }
        require(game.betState.isAffordable(game.player!!.chips)) { "Insufficient chips" }

        return game.copy(
            player = game.player.deductChips(game.betState.amount),
            betState = game.betState.commit()
        )
    }

    fun tryAddChip(game: Game, chipValue: ChipValue): AddChipResult {
        if (!game.hasPlayer) return AddChipResult(false, "No player in game", game)
        if (game.phase != GamePhase.WAITING_FOR_BETS) return AddChipResult(false, "Can only add chips during betting phase", game)
        if (game.player!!.chips < (game.betState.amount + chipValue.value)) return AddChipResult(false, "Insufficient chips", game)

        return try {
            val updated = addToPendingBet(game, chipValue.value)
            AddChipResult(true, null, updated)
        } catch (e: IllegalArgumentException) {
            AddChipResult(false, e.message, game)
        }
    }

    fun placeBet(game: Game, amount: Int): Game {
        require(game.hasPlayer) { "No player in game" }
        require(amount > 0) { "Bet must be positive" }
        require(game.player!!.chips >= amount) { "Insufficient chips" }

        return game.copy(
            player = game.player.deductChips(amount),
            betState = BetState(amount, isCommitted = true)
        )
    }
}
