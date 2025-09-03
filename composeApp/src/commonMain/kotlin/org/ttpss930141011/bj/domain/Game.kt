package org.ttpss930141011.bj.domain

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
        
        // Additional game-level constraints for split
        return if (baseActions.contains(Action.SPLIT) && 
                   playerHands.size >= rules.maxSplits + 1) {
            baseActions - Action.SPLIT // Remove split if max splits reached
        } else {
            baseActions
        }
    }
}

enum class GamePhase {
    WAITING_FOR_BETS,
    DEALING,
    PLAYER_ACTIONS,
    DEALER_TURN,
    SETTLEMENT
}

