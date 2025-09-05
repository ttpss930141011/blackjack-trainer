package org.ttpss930141011.bj.domain.services

import org.ttpss930141011.bj.domain.entities.Game
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.DomainConstants

class RoundManager {
    
    fun dealRound(game: Game): Game {
        require(game.hasCommittedBet) { "No bet committed" }
        require(game.phase == GamePhase.WAITING_FOR_BETS) { "Round already started" }
        
        // Deal two cards to player and dealer
        val (playerCards, tempDeck1) = game.deck.dealCards(2)
        val (dealerCards, newDeck) = tempDeck1.dealCards(2)
        
        // Create player hands
        val playerHand = PlayerHand.initial(playerCards, game.currentBet)
        val playerHands = listOf(playerHand)
        
        // Set up dealer with hole card
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
        
        // Auto-transition to dealer turn when all player hands are complete
        return if (updatedGame.allHandsComplete && updatedGame.phase == GamePhase.PLAYER_TURN) {
            updatedGame.copy(phase = GamePhase.DEALER_TURN)
        } else {
            updatedGame
        }
    }
    
    // Split handling
    private fun handleSplit(game: Game, hand: PlayerHand): Triple<List<PlayerHand>, Deck, Int> {
        require(hand.canSplit) { "Hand cannot be split" }
        require(game.playerHands.size < game.rules.maxSplits + 1) { "Maximum splits exceeded" }
        
        // Deal two new cards for split hands
        val (newCards, newDeck) = game.deck.dealCards(2)
        val (firstHand, secondHand) = hand.split(newCards[0], newCards[1])
        
        val newHands = game.playerHands.toMutableList()
        
        // Replace current hand with two split hands
        newHands[game.currentHandIndex] = firstHand
        newHands.add(game.currentHandIndex + 1, secondHand)
        
        return Triple(newHands, newDeck, game.currentHandIndex)
    }
    
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
        
        // Move to next hand if current hand is complete
        val nextIndex = if (newHand.isCompleted && game.currentHandIndex < newHands.size - 1) {
            game.currentHandIndex + 1
        } else {
            game.currentHandIndex
        }
        
        return Triple(newHands, newDeck, nextIndex)
    }
    
    fun processDealerTurn(game: Game): Game {
        require(game.allHandsComplete) { "Player hands not complete" }
        
        // 莊家需要動作嗎？
        val playerHasWinningHands = game.playerHands.any { hand ->
            !hand.isBusted && hand.status != HandStatus.SURRENDERED
        }
        
        if (!playerHasWinningHands) {
            // All player hands busted or surrendered, dealer doesn't need to act
            return game.copy(phase = GamePhase.SETTLEMENT)
        }
        
        // 翻開暗牌並根據規則要牌
        var newDealer = game.dealer.revealHoleCard()
        var newDeck = game.deck
        
        while (shouldDealerHit(newDealer.hand!!, game.rules)) {
            val (card, deck) = newDeck.dealCard()
            newDealer = newDealer.hit(card)
            newDeck = deck
        }
        
        return game.copy(dealer = newDealer, deck = newDeck, phase = GamePhase.SETTLEMENT)
    }
    
    private fun shouldDealerHit(hand: Hand, rules: GameRules): Boolean {
        // 首先檢查是否爆牌 - 爆牌絕對不能繼續要牌
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