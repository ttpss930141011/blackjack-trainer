package org.ttpss930141011.bj.domain.services

import org.ttpss930141011.bj.domain.entities.Game
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.domain.enums.GamePhase

/**
 * Domain service responsible for managing round flow and card dealing.
 * Extracted from Game class to improve Single Responsibility Principle.
 */
class RoundManager {
    
    fun dealRound(game: Game): Game {
        require(game.hasBet) { "No bet placed" }
        
        var currentDeck = game.deck
        
        // Deal player cards
        val dealResult = currentDeck.dealCards(2)
        val playerCards = dealResult.first
        currentDeck = dealResult.second
        val playerHand = PlayerHand.initial(playerCards, game.currentBet)
        
        // Deal dealer cards (up card + hole card)
        val dealerResult = currentDeck.dealCards(2)
        val dealerCards = dealerResult.first
        val newDealer = game.dealer.dealInitialCards(dealerCards[0], dealerCards[1])
        val finalDeck = dealerResult.second
        
        return game.copy(
            playerHands = listOf(playerHand),
            currentHandIndex = 0,
            dealer = newDealer,
            deck = finalDeck,
            phase = GamePhase.PLAYER_ACTIONS
        )
    }
    
    fun processPlayerAction(game: Game, action: Action): Game {
        require(game.phase == GamePhase.PLAYER_ACTIONS) { "Not in player action phase" }
        require(game.canAct) { "Player cannot act at this time" }
        
        val hand = game.currentHand!!
        
        // Handle payment for DOUBLE before processing action
        val gameAfterPayment = if (action == Action.DOUBLE) {
            val additionalBet = hand.bet
            require((game.player?.chips ?: 0) >= additionalBet) { 
                "Insufficient balance for double down" 
            }
            game.copy(player = game.player?.deductChips(additionalBet))
        } else {
            game
        }
        
        val (newHands, newDeck, newIndex) = when (action) {
            Action.SPLIT -> handleSplit(gameAfterPayment, hand)
            else -> handleRegularAction(gameAfterPayment, hand, action)
        }
        
        // Check if all hands are complete to proceed to dealer turn
        val allComplete = newHands.all { it.isCompleted }
        val newPhase = if (allComplete) GamePhase.DEALER_TURN else GamePhase.PLAYER_ACTIONS
        
        return gameAfterPayment.copy(
            playerHands = newHands,
            currentHandIndex = newIndex,
            deck = newDeck,
            phase = newPhase
        )
    }
    
    fun processDealerTurn(game: Game): Game {
        require(game.phase == GamePhase.DEALER_TURN) { "Not in dealer turn phase" }
        require(game.dealer.hand != null) { "Dealer has no hand to play" }
        
        var currentGame = revealDealerCards(game)
        
        // Dealer hits until must stand
        while (shouldDealerHit(currentGame.dealer.hand!!, game.rules)) {
            val (newCard, newDeck) = currentGame.deck.dealCard()
            val newDealerHand = currentGame.dealer.hand!!.addCard(newCard)
            currentGame = currentGame.copy(
                dealer = currentGame.dealer.copy(hand = newDealerHand),
                deck = newDeck
            )
        }
        
        // Auto-settlement for better UX - no manual intervention needed
        val settledGame = currentGame.copy(phase = GamePhase.SETTLEMENT).settleRound()
        return settledGame
    }
    
    private fun handleSplit(game: Game, hand: PlayerHand): Triple<List<PlayerHand>, Deck, Int> {
        require(hand.canSplit) { "Hand cannot be split" }
        require(game.playerHands.size < game.rules.maxSplits + 1) { "Maximum splits exceeded" }
        
        val splitResult = hand.split(game.deck)
        val newHands = game.playerHands.toMutableList()
        
        // Replace current hand with two split hands
        newHands[game.currentHandIndex] = splitResult.firstHand
        newHands.add(game.currentHandIndex + 1, splitResult.secondHand)
        
        return Triple(newHands, splitResult.deck, game.currentHandIndex)
    }
    
    private fun handleRegularAction(
        game: Game, 
        hand: PlayerHand, 
        action: Action
    ): Triple<List<PlayerHand>, Deck, Int> {
        // Payment has already been handled in processPlayerAction
        val actionResult = when (action) {
            Action.HIT -> hand.hit(game.deck)
            Action.STAND -> PlayerHandActionResult(hand.stand(), game.deck)
            Action.DOUBLE -> hand.double(game.deck)
            Action.SURRENDER -> PlayerHandActionResult(hand.surrender(), game.deck)
            Action.SPLIT -> error("Split should be handled separately")
        }
        
        val newHands = game.playerHands.toMutableList()
        newHands[game.currentHandIndex] = actionResult.hand
        
        // Move to next hand if current hand is complete
        val nextIndex = if (actionResult.hand.isCompleted && game.currentHandIndex < newHands.size - 1) {
            game.currentHandIndex + 1
        } else {
            game.currentHandIndex
        }
        
        return Triple(newHands, actionResult.deck, nextIndex)
    }
    
    private fun revealDealerCards(game: Game): Game {
        require(game.dealer.hand != null) { "No dealer hand" }
        val revealedDealer = game.dealer.revealHoleCard()
        return game.copy(dealer = revealedDealer)
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
}