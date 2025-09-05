package org.ttpss930141011.bj.domain.valueobjects

import org.ttpss930141011.bj.domain.enums.HandStatus
import org.ttpss930141011.bj.domain.enums.Action

// PlayerHand - Simplified value object replacing SeatHand + Seat abstraction
data class PlayerHand(
    val cards: List<Card>,
    val bet: Int,
    val status: HandStatus = HandStatus.ACTIVE
) {
    
    companion object {
        fun initial(cards: List<Card>, bet: Int): PlayerHand {
            return PlayerHand(cards, bet, HandStatus.ACTIVE)
        }
    }
    
    // Hand calculation (delegate to Hand logic for consistency)
    private val hand: Hand = Hand(cards)
    val bestValue: Int = hand.bestValue
    val isSoft: Boolean = hand.isSoft
    val isBusted: Boolean = hand.isBusted
    val isBlackjack: Boolean = hand.isBlackjack
    val canDouble: Boolean = hand.canDouble
    val canSplit: Boolean = hand.canSplit
    
    // Hand state queries
    val isCompleted: Boolean = status != HandStatus.ACTIVE
    val isWin: Boolean = status == HandStatus.WIN
    val isLoss: Boolean = status == HandStatus.LOSS
    val isPush: Boolean = status == HandStatus.PUSH
    
    // Pure domain actions - returning new instances (immutable)
    fun hit(deck: Deck): PlayerHandActionResult {
        require(status == HandStatus.ACTIVE) { "Hand not active" }
        
        val (newCard, newDeck) = deck.dealCard()
        val newCards = cards + newCard
        val newHand = Hand(newCards)
        
        // Calculate new status
        val newStatus = when {
            newHand.isBusted -> HandStatus.BUSTED
            newHand.bestValue == 21 -> HandStatus.STANDING // Auto-stand on 21
            else -> HandStatus.ACTIVE
        }
        
        return PlayerHandActionResult(
            hand = copy(cards = newCards, status = newStatus),
            deck = newDeck
        )
    }
    
    fun stand(): PlayerHand {
        require(status == HandStatus.ACTIVE) { "Hand not active" }
        return copy(status = HandStatus.STANDING)
    }
    
    fun double(deck: Deck): PlayerHandActionResult {
        require(status == HandStatus.ACTIVE) { "Hand not active" }
        require(canDouble) { "Cannot double this hand" }
        
        val (newCard, newDeck) = deck.dealCard()
        val newCards = cards + newCard
        val newHand = Hand(newCards)
        
        // Double always completes the hand
        val newStatus = if (newHand.isBusted) HandStatus.BUSTED else HandStatus.STANDING
        
        return PlayerHandActionResult(
            hand = copy(cards = newCards, bet = bet * 2, status = newStatus),
            deck = newDeck
        )
    }
    
    fun split(deck: Deck): PlayerHandSplitResult {
        require(status == HandStatus.ACTIVE) { "Hand not active" }
        require(canSplit) { "Cannot split this hand" }
        
        val card1 = cards[0]
        val card2 = cards[1]
        
        // Deal two new cards
        val (newCards, newDeck) = deck.dealCards(2)
        
        // Create two new hands
        val firstHand = copy(cards = listOf(card1, newCards[0]))
        val secondHand = copy(cards = listOf(card2, newCards[1]))
        
        return PlayerHandSplitResult(
            firstHand = firstHand,
            secondHand = secondHand,
            deck = newDeck
        )
    }
    
    fun surrender(): PlayerHand {
        require(status == HandStatus.ACTIVE) { "Hand not active" }
        return copy(status = HandStatus.SURRENDERED)
    }
    
    // Available actions for this hand
    fun availableActions(rules: GameRules = GameRules()): Set<Action> {
        if (status != HandStatus.ACTIVE) return emptySet()
        
        val actions = mutableSetOf<Action>()
        
        // 21 can only stand
        if (bestValue == 21) {
            actions.add(Action.STAND)
            return actions
        }
        
        // Basic actions
        if (bestValue < 21) {
            actions.add(Action.HIT)
        }
        actions.add(Action.STAND)
        
        // Double (first two cards only)
        if (canDouble) {
            actions.add(Action.DOUBLE)
        }
        
        // Split (same rank pairs only)
        if (canSplit) {
            actions.add(Action.SPLIT)
        }
        
        // Surrender conditions (根據 docs/blackjack-rules.md)
        if (rules.surrenderAllowed && 
            cards.size == 2 && 
            bestValue < 21) {
            actions.add(Action.SURRENDER)
        }
        
        return actions
    }
}

// Result objects for PlayerHand actions
data class PlayerHandActionResult(
    val hand: PlayerHand,
    val deck: Deck
)

data class PlayerHandSplitResult(
    val firstHand: PlayerHand,
    val secondHand: PlayerHand,
    val deck: Deck
)