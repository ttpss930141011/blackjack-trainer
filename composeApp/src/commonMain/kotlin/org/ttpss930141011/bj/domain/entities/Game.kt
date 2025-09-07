package org.ttpss930141011.bj.domain.entities

import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.domain.enums.HandStatus
import org.ttpss930141011.bj.domain.enums.GamePhase
import org.ttpss930141011.bj.domain.enums.ChipValue
import org.ttpss930141011.bj.domain.services.RoundManager
import org.ttpss930141011.bj.domain.services.SettlementService

enum class RoundOutcome { WIN, LOSS, PUSH, UNKNOWN }

// Game - Simplified aggregate root replacing Table → Seat → SeatHand complexity
data class Game(
    val player: Player?,
    val playerHands: List<PlayerHand>,
    val currentHandIndex: Int,
    val pendingBet: Int = 0,      // Uncommitted bet amount during betting phase
    val currentBet: Int,          // Committed bet amount after dealing
    val dealer: Dealer,
    val deck: Deck,
    val rules: GameRules,
    val phase: GamePhase = GamePhase.WAITING_FOR_BETS,
    val isSettled: Boolean = false
) {
    
    companion object {
        fun create(rules: GameRules): Game {
            return Game(
                player = null,
                playerHands = emptyList(),
                currentHandIndex = 0,
                pendingBet = 0,
                currentBet = 0,
                dealer = Dealer(),
                deck = Deck.shuffled(),
                rules = rules
            )
        }
        
        fun createForTest(rules: GameRules, testDeck: Deck): Game {
            return Game(
                player = null,
                playerHands = emptyList(),
                currentHandIndex = 0,
                pendingBet = 0,
                currentBet = 0,
                dealer = Dealer(),
                deck = testDeck,
                rules = rules
            )
        }
    }
    
    // Domain queries
    val hasPlayer: Boolean = player != null
    val hasPendingBet: Boolean = pendingBet > 0
    val hasCommittedBet: Boolean = currentBet > 0
    val hasBet: Boolean = currentBet > 0  // Keep for backward compatibility
    val canDealCards: Boolean = hasPendingBet && phase == GamePhase.WAITING_FOR_BETS
    val currentHand: PlayerHand? = playerHands.getOrNull(currentHandIndex)
    val canAct: Boolean = hasPlayer && currentHand != null && currentHand.status == HandStatus.ACTIVE
    val allHandsComplete: Boolean = playerHands.all { it.isCompleted }
    
    // Game Over logic - domain-driven minimum bet requirement
    // Only check game over when waiting for bets (between rounds)
    val isGameOver: Boolean 
        get() = player?.let { p -> 
            p.chips < rules.minimumBet && phase == GamePhase.WAITING_FOR_BETS 
        } ?: false
    
    // Rich domain behavior - player management
    fun addPlayer(newPlayer: Player): Game {
        require(!hasPlayer) { "Game already has a player" }
        return copy(player = newPlayer)
    }
    
    fun placeBet(amount: Int): Game {
        require(hasPlayer) { "No player in game" }
        require(amount > 0) { "Bet must be positive" }
        require(player!!.chips >= amount) { "Insufficient chips" }
        
        val updatedPlayer = player.deductChips(amount)
        return copy(
            player = updatedPlayer,
            currentBet = amount
        )
    }
    
    fun clearBet(): Game {
        require(hasPlayer) { "No player in game" }
        require(phase == GamePhase.WAITING_FOR_BETS) { "Can only clear bet during betting phase" }
        
        // Restore chips to player if there was a bet
        val updatedPlayer = if (currentBet > 0) {
            player!!.addChips(currentBet)
        } else {
            player!!
        }
        
        return copy(
            player = updatedPlayer,
            currentBet = 0
        )
    }
    
    // New pending bet behavior - for BettingTableState replacement
    fun addToPendingBet(amount: Int): Game {
        require(hasPlayer) { "No player in game" }
        require(phase == GamePhase.WAITING_FOR_BETS) { "Can only add to pending bet during betting phase" }
        require(amount > 0) { "Amount must be positive" }
        require(player!!.chips >= (pendingBet + amount)) { 
            "Insufficient chips for additional bet" 
        }
        
        return copy(pendingBet = pendingBet + amount)
    }
    
    fun clearPendingBet(): Game {
        require(phase == GamePhase.WAITING_FOR_BETS) { "Can only clear pending bet during betting phase" }
        return copy(pendingBet = 0)
    }
    
    fun commitPendingBet(): Game {
        require(hasPendingBet) { "No pending bet to commit" }
        require(hasPlayer) { "No player in game" }
        require(player!!.chips >= pendingBet) { "Insufficient chips" }
        
        val updatedPlayer = player.deductChips(pendingBet)
        return copy(
            player = updatedPlayer,
            currentBet = pendingBet,
            pendingBet = 0
        )
    }
    
    /**
     * Try to add chip to pending bet with validation
     * Returns result indicating success/failure with updated game state
     */
    fun tryAddChipToPendingBet(chipValue: ChipValue): AddChipResult {
        if (!hasPlayer) {
            return AddChipResult(
                success = false,
                errorMessage = "No player in game",
                updatedGame = this
            )
        }
        
        if (phase != GamePhase.WAITING_FOR_BETS) {
            return AddChipResult(
                success = false,
                errorMessage = "Can only add chips during betting phase",
                updatedGame = this
            )
        }
        
        if (player!!.chips < (pendingBet + chipValue.value)) {
            return AddChipResult(
                success = false,
                errorMessage = "Insufficient chips",
                updatedGame = this
            )
        }
        
        try {
            val updatedGame = addToPendingBet(chipValue.value)
            return AddChipResult(
                success = true,
                errorMessage = null,
                updatedGame = updatedGame
            )
        } catch (e: IllegalArgumentException) {
            return AddChipResult(
                success = false,
                errorMessage = e.message,
                updatedGame = this
            )
        }
    }
    
    // Delegate to domain services for complex operations
    fun dealRound(): Game = RoundManager().dealRound(this)
    
    fun playerAction(action: Action): Game = RoundManager().processPlayerAction(this, action)
    
    fun dealerPlayAutomated(): Game = RoundManager().processDealerTurn(this)
    
    fun settleRound(): Game = SettlementService().settleRound(this)
    
    // Reset for new round - preserve player, clear game state
    fun resetForNewRound(): Game {
        return copy(
            playerHands = emptyList(),           // 清空所有手牌
            currentHandIndex = 0,                // 重設手牌索引
            pendingBet = 0,                      // 清空待確認賭注
            currentBet = 0,                      // 清空已確認賭注
            dealer = Dealer(),                   // 重設dealer (清空hand)
            deck = Deck.shuffled(),              // 新牌組
            phase = GamePhase.WAITING_FOR_BETS, // 回到下注階段
            isSettled = false                   // 重設結算狀態
            // player = player (保持不變，透過copy的預設行為)
        )
    }
    
    // Available actions for current hand
    fun availableActions(): Set<Action> {
        if (!canAct) return emptySet()
        
        val baseActions = currentHand!!.availableActions(rules)
        
        // Additional game-level constraints
        val constrainedActions = baseActions.toMutableSet()
        
        // Remove split if max splits reached
        if (baseActions.contains(Action.SPLIT) && 
            playerHands.size >= rules.maxSplits + 1) {
            constrainedActions.remove(Action.SPLIT)
        }
        
        // Remove double if player cannot afford it
        if (baseActions.contains(Action.DOUBLE) && 
            (player?.chips ?: 0) < (currentHand?.bet ?: 0)) {
            constrainedActions.remove(Action.DOUBLE)
        }
        
        return constrainedActions
    }
    
    // Round outcome determination - domain logic belongs here
    fun getRoundOutcome(): RoundOutcome {
        require(phase == GamePhase.SETTLEMENT) { "Game must be in settlement phase" }
        
        return if (playerHands.isNotEmpty()) {
            val firstHand = playerHands[0]
            when (firstHand.status) {
                HandStatus.WIN -> RoundOutcome.WIN
                HandStatus.LOSS, HandStatus.BUSTED -> RoundOutcome.LOSS
                HandStatus.PUSH -> RoundOutcome.PUSH
                HandStatus.SURRENDERED -> RoundOutcome.LOSS
                else -> RoundOutcome.UNKNOWN
            }
        } else RoundOutcome.UNKNOWN
    }
    
    // Auto-advance logic - Game knows when it should progress
    fun shouldAutoAdvance(): Boolean {
        return when (phase) {
            GamePhase.DEALER_TURN -> allHandsComplete
            GamePhase.SETTLEMENT -> !isSettled
            else -> false
        }
    }
}
