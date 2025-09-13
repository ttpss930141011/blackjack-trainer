package org.ttpss930141011.bj.domain.services

import org.ttpss930141011.bj.domain.entities.Game
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.DomainConstants

/**
 * Domain service responsible for managing blackjack round lifecycle.
 * Handles dealing cards, processing player actions, and managing dealer turn.
 */
class RoundManager {
    
    /**
     * Deals initial cards to start a new round.
     * 
     * @param game Current game state (must be in WAITING_FOR_BETS phase with committed bet)
     * @return Updated game state with cards dealt and phase set to PLAYER_TURN
     * @throws IllegalArgumentException if preconditions are not met
     */
    fun dealRound(game: Game): Game {
        require(game.betState.isCommitted) { "No bet committed" }
        require(game.phase == GamePhase.WAITING_FOR_BETS) { "Round already started" }
        
        val (playerCards, tempDeck1) = game.deck.dealCards(2)
        val (dealerCards, newDeck) = tempDeck1.dealCards(2)
        
        val playerHand = PlayerHand.initial(playerCards, game.betState.amount)
        val playerHands = listOf(playerHand)
        
        val newDealer = game.dealer.dealInitialCards(
            upCard = dealerCards[0], 
            holeCard = dealerCards[1]
        )
        
        return game.copy(
            playerHands = playerHands,
            currentHandIndex = 0,
            dealer = newDealer,
            deck = newDeck,
            phase = GamePhase.PLAYER_TURN
        )
    }
    
    /**
     * Processes a player action and updates the game state.
     * 
     * @param game Current game state (must allow actions on current hand)
     * @param action The action to process
     * @return Updated game state with action applied
     * @throws IllegalArgumentException if action cannot be performed
     */
    fun processPlayerAction(game: Game, action: Action): Game {
        require(game.canAct) { "Cannot act on current hand" }
        
        val currentHand = game.currentHand!!
        
        val updatedGame = when (action) {
            Action.SPLIT -> {
                val (newHands, newDeck, nextIndex) = handleSplit(game, currentHand)
                game.copy(
                    playerHands = newHands,
                    deck = newDeck,
                    currentHandIndex = nextIndex
                )
            }
            else -> {
                val (newHands, newDeck, nextIndex) = handleRegularAction(game, currentHand, action)
                game.copy(
                    playerHands = newHands,
                    deck = newDeck,
                    currentHandIndex = nextIndex
                )
            }
        }
        
        return if (updatedGame.allHandsComplete && updatedGame.phase == GamePhase.PLAYER_TURN) {
            updatedGame.copy(phase = GamePhase.DEALER_TURN)
        } else {
            updatedGame
        }
    }
    
    /**
     * Handles splitting a pair into two separate hands.
     * 
     * @param game Current game state
     * @param hand The hand to split (must be a valid pair)
     * @return Triple containing updated hands list, deck, and current hand index
     */
    private fun handleSplit(game: Game, hand: PlayerHand): Triple<List<PlayerHand>, Deck, Int> {
        require(hand.canSplit) { "Hand cannot be split" }
        require(game.playerHands.size < game.rules.maxSplits + 1) { "Maximum splits exceeded" }
        
        val (newCards, newDeck) = game.deck.dealCards(2)
        val (firstHand, secondHand) = hand.split(newCards[0], newCards[1])
        
        val newHands = game.playerHands.toMutableList()
        
        newHands[game.currentHandIndex] = firstHand
        newHands.add(game.currentHandIndex + 1, secondHand)
        
        return Triple(newHands, newDeck, game.currentHandIndex)
    }
    
    /**
     * Handles regular player actions (hit, stand, double, surrender).
     * 
     * @param game Current game state
     * @param hand The hand to act on
     * @param action The action to perform
     * @return Triple containing updated hands list, deck, and next hand index
     */
    private fun handleRegularAction(
        game: Game, 
        hand: PlayerHand, 
        action: Action
    ): Triple<List<PlayerHand>, Deck, Int> {
        val (newHand, newDeck) = when (action) {
            Action.HIT -> {
                val (card, deck) = game.deck.dealCard()
                val updatedHand = hand.hit(card)
                Pair(updatedHand, deck)
            }
            Action.STAND -> {
                Pair(hand.stand(), game.deck)
            }
            Action.DOUBLE -> {
                val (card, deck) = game.deck.dealCard()
                val updatedHand = hand.doubleDown(card)
                Pair(updatedHand, deck)
            }
            Action.SURRENDER -> {
                Pair(hand.surrender(), game.deck)
            }
            Action.SPLIT -> error("Split should be handled separately")
        }
        
        val newHands = game.playerHands.toMutableList()
        newHands[game.currentHandIndex] = newHand
        
        val nextIndex = if (newHand.isCompleted && game.currentHandIndex < newHands.size - 1) {
            game.currentHandIndex + 1
        } else {
            game.currentHandIndex
        }
        
        return Triple(newHands, newDeck, nextIndex)
    }
    
    /**
     * Processes the dealer's turn according to standard rules.
     * 
     * @param game Current game state (all player hands must be complete)
     * @return Updated game state with dealer turn completed
     * @throws IllegalArgumentException if player hands are not complete
     */
    fun processDealerTurn(game: Game): Game {
        require(game.allHandsComplete) { "Player hands not complete" }
        
        val playerHasWinningHands = game.playerHands.any { hand ->
            !hand.isBusted && hand.status != HandStatus.SURRENDERED
        }
        
        if (!playerHasWinningHands) {
            return game.copy(phase = GamePhase.SETTLEMENT)
        }
        
        var newDealer = game.dealer.revealHoleCard()
        var newDeck = game.deck
        
        while (shouldDealerHit(newDealer.hand!!, game.rules)) {
            val (card, deck) = newDeck.dealCard()
            newDealer = newDealer.hit(card)
            newDeck = deck
        }
        
        return game.copy(dealer = newDealer, deck = newDeck, phase = GamePhase.SETTLEMENT)
    }
    
    /**
     * Determines if the dealer should take another card based on standard rules.
     * 
     * @param hand The dealer's current hand
     * @param rules The game rules in effect
     * @return True if dealer should hit, false if dealer should stand
     */
    private fun shouldDealerHit(hand: Hand, rules: GameRules): Boolean {
        if (hand.isBusted) return false
        
        val value = hand.bestValue
        return when {
            value < DomainConstants.BlackjackValues.DEALER_STAND_HARD -> true
            value > DomainConstants.BlackjackValues.DEALER_STAND_HARD -> false
            value == DomainConstants.BlackjackValues.DEALER_STAND_HARD && hand.isSoft && rules.dealerHitsOnSoft17 -> true
            else -> false
        }
    }
}