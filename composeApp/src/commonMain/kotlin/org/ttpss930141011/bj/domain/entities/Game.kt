package org.ttpss930141011.bj.domain.entities

import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.domain.enums.HandStatus
import org.ttpss930141011.bj.domain.enums.GamePhase
import org.ttpss930141011.bj.domain.services.RoundManager
import org.ttpss930141011.bj.domain.services.SettlementService

// Game - Simplified aggregate root replacing Table → Seat → SeatHand complexity
data class Game(
    val player: Player?,
    val playerHands: List<PlayerHand>,
    val currentHandIndex: Int,
    val currentBet: Int,
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
                currentBet = 0,
                dealer = Dealer(),
                deck = testDeck,
                rules = rules
            )
        }
    }
    
    // Domain queries
    val hasPlayer: Boolean = player != null
    val hasBet: Boolean = currentBet > 0
    val currentHand: PlayerHand? = playerHands.getOrNull(currentHandIndex)
    val canAct: Boolean = hasPlayer && currentHand != null && currentHand.status == HandStatus.ACTIVE
    val allHandsComplete: Boolean = playerHands.all { it.isCompleted }
    
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
            currentBet = 0,                      // 清空賭注
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
}
