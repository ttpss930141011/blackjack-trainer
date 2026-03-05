package org.ttpss930141011.bj.application

import androidx.compose.runtime.*
import org.ttpss930141011.bj.domain.enums.ChipValue
import org.ttpss930141011.bj.domain.enums.GamePhase

/**
 * BettingManager - Manages bet memory and repeat-bet UX.
 *
 * Extracted from GameViewModel to reduce its responsibilities.
 */
internal class BettingManager(
    private val gameStateManager: GameStateManager
) {
    private var _lastBetAmount by mutableStateOf<Int?>(null)
    val lastBetAmount: Int? get() = _lastBetAmount

    private var _userClearedBet by mutableStateOf(false)
    val userClearedBet: Boolean get() = _userClearedBet

    fun rememberLastBet(currentBetAmount: Int) {
        _lastBetAmount = currentBetAmount.takeIf { it > 0 }
    }

    fun restoreFromPreferences(savedBetAmount: Int) {
        _lastBetAmount = if (savedBetAmount > 0) savedBetAmount else null
    }

    fun onBetCleared() {
        _userClearedBet = true
    }

    fun onNewRound() {
        _userClearedBet = false
    }

    fun addChipToBet(chipValue: ChipValue): GameStateResult {
        return gameStateManager.addChipToBet(chipValue)
    }

    fun clearBet(): GameStateResult {
        return gameStateManager.clearBet()
    }

    /**
     * Repeat last bet by adding chips one by one.
     * Returns true if full amount was replayed successfully.
     */
    fun repeatLastBet(): Boolean {
        val lastAmount = _lastBetAmount ?: return false
        val game = gameStateManager.game ?: return false

        if (_userClearedBet) return false
        if ((game.player?.chips ?: 0) < lastAmount) return false
        if (game.phase != GamePhase.WAITING_FOR_BETS) return false
        if (game.betState.amount > 0) return false

        var remaining = lastAmount
        val chipValues = ChipValue.values().sortedByDescending { it.value }

        for (chipValue in chipValues) {
            val count = remaining / chipValue.value
            repeat(count) {
                val result = gameStateManager.addChipToBet(chipValue)
                if (result is GameStateResult.Error) return false
                remaining -= chipValue.value
            }
            if (remaining == 0) break
        }
        return remaining == 0
    }
}
