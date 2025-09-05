package org.ttpss930141011.bj.domain.valueobjects

import org.ttpss930141011.bj.domain.enums.HandStatus
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.domain.DomainConstants

// PlayerHand - Simplified value object replacing SeatHand + Seat abstraction
data class PlayerHand(
    val cards: List<Card>,
    val bet: Int,
    val status: HandStatus = HandStatus.ACTIVE,
    val isFromSplit: Boolean = false,
    val hasDoubled: Boolean = false
) {
    
    companion object {
        fun initial(cards: List<Card>, bet: Int): PlayerHand {
            return PlayerHand(cards, bet, HandStatus.ACTIVE)
        }
        
        fun create(hand: Hand, bet: Int): PlayerHand {
            return PlayerHand(hand.cards, bet, HandStatus.ACTIVE)
        }
    }
    
    // Hand calculation (delegate to Hand logic for consistency)
    val hand: Hand = Hand(cards)
    val bestValue: Int = hand.bestValue
    val isSoft: Boolean = hand.isSoft
    val isBusted: Boolean = hand.isBusted
    val isBlackjack: Boolean = hand.isBlackjack && !isFromSplit // Split hands can't have natural blackjack
    val canDouble: Boolean = hand.canDouble
    val canSplit: Boolean = hand.canSplit
    val canAct: Boolean = status == HandStatus.ACTIVE
    
    // Hand state queries
    val isCompleted: Boolean = status != HandStatus.ACTIVE
    val isWin: Boolean = status == HandStatus.WIN
    val isLoss: Boolean = status == HandStatus.LOSS
    val isPush: Boolean = status == HandStatus.PUSH
    
    // Pure domain actions - returning new instances (immutable)
    fun hit(card: Card): PlayerHand {
        require(status == HandStatus.ACTIVE) { "Hand not active" }
        
        val newCards = cards + card
        val newHand = Hand(newCards)
        
        // Calculate new status
        val newStatus = when {
            newHand.isBusted -> HandStatus.BUSTED
            newHand.bestValue == DomainConstants.BlackjackValues.BLACKJACK_TOTAL -> HandStatus.STANDING // Auto-stand on 21
            else -> HandStatus.ACTIVE
        }
        
        return copy(cards = newCards, status = newStatus)
    }
    
    fun stand(): PlayerHand {
        require(status == HandStatus.ACTIVE) { "Hand not active" }
        return copy(status = HandStatus.STANDING)
    }
    
    fun doubleDown(card: Card): PlayerHand {
        require(status == HandStatus.ACTIVE) { "Hand not active" }
        require(canDouble) { "Cannot double this hand" }
        
        val newCards = cards + card
        val newHand = Hand(newCards)
        
        // Double always completes the hand
        val newStatus = if (newHand.isBusted) HandStatus.BUSTED else HandStatus.STANDING
        
        return copy(
            cards = newCards, 
            bet = bet * 2, 
            status = newStatus,
            hasDoubled = true
        )
    }
    
    fun split(newCard1: Card, newCard2: Card): Pair<PlayerHand, PlayerHand> {
        require(status == HandStatus.ACTIVE) { "Hand not active" }
        require(canSplit) { "Cannot split this hand" }
        
        val card1 = cards[0]
        val card2 = cards[1]
        
        // Create two new hands marked as from split
        val firstHand = copy(
            cards = listOf(card1, newCard1),
            isFromSplit = true
        )
        val secondHand = copy(
            cards = listOf(card2, newCard2),
            isFromSplit = true
        )
        
        return Pair(firstHand, secondHand)
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
        if (bestValue == DomainConstants.BlackjackValues.BLACKJACK_TOTAL) {
            actions.add(Action.STAND)
            return actions
        }
        
        // Basic actions
        if (bestValue < DomainConstants.BlackjackValues.BLACKJACK_TOTAL) {
            actions.add(Action.HIT)
        }
        actions.add(Action.STAND)
        
        // Double (first two cards only, and optionally after split)
        if (canDouble && (!isFromSplit || rules.doubleAfterSplitAllowed)) {
            actions.add(Action.DOUBLE)
        }
        
        // Split (same rank pairs only)
        if (canSplit) {
            actions.add(Action.SPLIT)
        }
        
        // Surrender conditions (根據 docs/blackjack-rules.md)
        if (rules.surrenderAllowed && 
            cards.size == DomainConstants.HandLimits.INITIAL_HAND_SIZE && 
            bestValue < DomainConstants.BlackjackValues.BLACKJACK_TOTAL &&
            !isFromSplit) { // Can't surrender split hands
            actions.add(Action.SURRENDER)
        }
        
        return actions
    }
}