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
    val phase: GamePhase = GamePhase.WAITING_FOR_BETS
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
    
    // Rich domain behavior - game flow
    fun dealRound(): Game {
        require(hasBet) { "No bet placed" }
        
        var currentDeck = deck
        
        // Deal player cards
        val dealResult = currentDeck.dealCards(2)
        val playerCards = dealResult.first
        currentDeck = dealResult.second
        val playerHand = PlayerHand.initial(playerCards, currentBet)
        
        // Deal dealer cards (up card + hole card)
        val dealerResult = currentDeck.dealCards(2)
        val dealerCards = dealerResult.first
        val newDealer = dealer.dealInitialCards(dealerCards[0], dealerCards[1])
        val finalDeck = dealerResult.second
        
        return copy(
            playerHands = listOf(playerHand),
            currentHandIndex = 0,
            dealer = newDealer,
            deck = finalDeck,
            phase = GamePhase.PLAYER_ACTIONS
        )
    }
    
    fun playerAction(action: Action): Game {
        require(phase == GamePhase.PLAYER_ACTIONS) { "Not in player action phase" }
        require(canAct) { "Player cannot act at this time" }
        
        val hand = currentHand!!
        val (newHands, newDeck, newIndex) = when (action) {
            Action.SPLIT -> handleSplit(hand)
            else -> handleRegularAction(hand, action)
        }
        
        // Check if all hands are complete to proceed to dealer turn
        val allComplete = newHands.all { it.isCompleted }
        val newPhase = if (allComplete) GamePhase.DEALER_TURN else GamePhase.PLAYER_ACTIONS
        
        return copy(
            playerHands = newHands,
            currentHandIndex = newIndex,
            deck = newDeck,
            phase = newPhase
        )
    }
    
    private fun handleSplit(hand: PlayerHand): Triple<List<PlayerHand>, Deck, Int> {
        require(hand.canSplit) { "Hand cannot be split" }
        require(playerHands.size < rules.maxSplits + 1) { "Maximum splits exceeded" }
        
        val splitResult = hand.split(deck)
        val newHands = playerHands.toMutableList()
        
        // Replace current hand with two split hands
        newHands[currentHandIndex] = splitResult.firstHand
        newHands.add(currentHandIndex + 1, splitResult.secondHand)
        
        return Triple(newHands, splitResult.deck, currentHandIndex)
    }
    
    private fun handleRegularAction(hand: PlayerHand, action: Action): Triple<List<PlayerHand>, Deck, Int> {
        val actionResult = when (action) {
            Action.HIT -> hand.hit(deck)
            Action.STAND -> PlayerHandActionResult(hand.stand(), deck)
            Action.DOUBLE -> hand.double(deck)
            Action.SURRENDER -> PlayerHandActionResult(hand.surrender(), deck)
            Action.SPLIT -> error("Split should be handled separately")
        }
        
        val newHands = playerHands.toMutableList()
        newHands[currentHandIndex] = actionResult.hand
        
        // Move to next hand if current hand is complete
        val nextIndex = if (actionResult.hand.isCompleted && currentHandIndex < newHands.size - 1) {
            currentHandIndex + 1
        } else {
            currentHandIndex
        }
        
        return Triple(newHands, actionResult.deck, nextIndex)
    }
    
    // Rich domain behavior - dealer logic
    fun dealerPlayAutomated(): Game {
        require(phase == GamePhase.DEALER_TURN) { "Not in dealer turn phase" }
        require(dealer.hand != null) { "Dealer has no hand to play" }
        
        var currentGame = this.revealDealerCards()
        
        // Dealer hits until must stand
        while (shouldDealerHit(currentGame.dealer.hand!!, rules)) {
            val (newCard, newDeck) = currentGame.deck.dealCard()
            val newDealerHand = currentGame.dealer.hand!!.addCard(newCard)
            currentGame = currentGame.copy(
                dealer = currentGame.dealer.copy(hand = newDealerHand),
                deck = newDeck
            )
        }
        
        return currentGame.copy(phase = GamePhase.SETTLEMENT)
    }
    
    private fun revealDealerCards(): Game {
        require(dealer.hand != null) { "No dealer hand" }
        val revealedDealer = dealer.revealHoleCard()
        return copy(dealer = revealedDealer)
    }
    
    private fun shouldDealerHit(hand: Hand, rules: GameRules): Boolean {
        val value = hand.bestValue
        return when {
            value < 17 -> true
            value > 17 -> false
            value == 17 && hand.isSoft && rules.dealerHitsOnSoft17 -> true
            else -> false
        }
    }
    
    // Rich domain behavior - settlement (pure domain logic, no workflow control)
    fun settleRound(): Game {
        require(phase == GamePhase.SETTLEMENT) { "Not in settlement phase" }
        require(dealer.hand != null) { "Dealer hand required for settlement" }
        require(hasPlayer) { "No player to settle" }
        
        val dealerHand = dealer.hand!!
        var totalWinnings = 0
        val settledHands = playerHands.map { hand ->
            val result = determineResult(hand, dealerHand)
            val handWinnings = calculateWinnings(hand.bet, result)
            totalWinnings += handWinnings
            
            val newStatus = when (result) {
                RoundResult.PLAYER_WIN, RoundResult.PLAYER_BLACKJACK -> HandStatus.WIN
                RoundResult.DEALER_WIN -> HandStatus.LOSS
                RoundResult.PUSH -> HandStatus.PUSH
            }
            
            hand.copy(status = newStatus)
        }
        
        val updatedPlayer = player!!.addChips(totalWinnings)
        
        // Domain layer calculates settlement results only - UI/Application layer controls workflow
        return copy(
            player = updatedPlayer,
            playerHands = settledHands
            // phase remains SETTLEMENT - let Application layer control transitions
        )
    }
    
    // Domain service methods
    private fun determineResult(playerHand: PlayerHand, dealerHand: Hand): RoundResult {
        val playerValue = playerHand.bestValue
        val dealerValue = dealerHand.bestValue
        val playerBlackjack = playerHand.isBlackjack
        val dealerBlackjack = dealerHand.isBlackjack
        
        return when {
            playerHand.status == HandStatus.SURRENDERED -> RoundResult.DEALER_WIN
            playerHand.status == HandStatus.BUSTED -> RoundResult.DEALER_WIN
            dealerHand.isBusted -> RoundResult.PLAYER_WIN
            playerBlackjack && dealerBlackjack -> RoundResult.PUSH
            playerBlackjack && !dealerBlackjack -> RoundResult.PLAYER_BLACKJACK
            !playerBlackjack && dealerBlackjack -> RoundResult.DEALER_WIN
            playerValue > dealerValue -> RoundResult.PLAYER_WIN
            playerValue < dealerValue -> RoundResult.DEALER_WIN
            else -> RoundResult.PUSH
        }
    }
    
    private fun calculateWinnings(bet: Int, result: RoundResult): Int {
        return when (result) {
            RoundResult.PLAYER_WIN -> bet * 2 // Return bet + win equal amount
            RoundResult.PLAYER_BLACKJACK -> (bet * 2.5).toInt() // Return bet + win 1.5x
            RoundResult.PUSH -> bet // Return bet only
            RoundResult.DEALER_WIN -> 0 // Lose bet (already deducted)
        }
    }
    
    // Reset for new round - preserve player, clear game state
    fun resetForNewRound(): Game {
        return copy(
            playerHands = emptyList(),           // 清空所有手牌
            currentHandIndex = 0,                // 重設手牌索引
            currentBet = 0,                      // 清空賭注
            dealer = Dealer(),                   // 重設dealer (清空hand)
            deck = Deck.shuffled(),              // 新牌組
            phase = GamePhase.WAITING_FOR_BETS  // 回到下注階段
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

