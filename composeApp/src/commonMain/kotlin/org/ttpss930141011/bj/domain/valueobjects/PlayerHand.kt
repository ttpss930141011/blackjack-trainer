package org.ttpss930141011.bj.domain.valueobjects

import org.ttpss930141011.bj.domain.enums.HandStatus
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.domain.DomainConstants

/**
 * Player's hand in a blackjack game, containing cards, bet amount, and game state.
 * This value object encapsulates all hand-specific logic including card calculations,
 * available actions, and state transitions during gameplay.
 * 
 * Replaces the complex SeatHand + Seat abstraction with a simplified domain model.
 * 
 * @property cards List of cards in this hand
 * @property bet Amount wagered on this hand
 * @property status Current state of the hand (active, standing, busted, etc.)
 * @property isFromSplit Whether this hand was created from splitting a pair
 * @property hasDoubled Whether player has doubled down on this hand
 */
data class PlayerHand(
    val cards: List<Card>,
    val bet: Int,
    val status: HandStatus = HandStatus.ACTIVE,
    val isFromSplit: Boolean = false,
    val hasDoubled: Boolean = false
) {
    
    companion object {
        /**
         * Creates an initial player hand with the given cards and bet amount.
         * Sets status to ACTIVE for immediate gameplay.
         */
        fun initial(cards: List<Card>, bet: Int): PlayerHand {
            return PlayerHand(cards, bet, HandStatus.ACTIVE)
        }
        
        /**
         * Creates a player hand from an existing Hand object and bet amount.
         * Useful for converting between hand representations.
         */
        fun create(hand: Hand, bet: Int): PlayerHand {
            return PlayerHand(hand.cards, bet, HandStatus.ACTIVE)
        }
    }
    
    val hand: Hand = Hand(cards)
    val bestValue: Int = hand.bestValue
    val isSoft: Boolean = hand.isSoft
    val isBusted: Boolean = hand.isBusted
    val isBlackjack: Boolean = hand.isBlackjack && !isFromSplit
    val canDouble: Boolean = hand.canDouble
    val canSplit: Boolean = hand.canSplit
    val canAct: Boolean = status == HandStatus.ACTIVE
    
    val isCompleted: Boolean = status != HandStatus.ACTIVE
    val isWin: Boolean = status == HandStatus.WIN
    val isLoss: Boolean = status == HandStatus.LOSS
    val isPush: Boolean = status == HandStatus.PUSH
    
    /**
     * Adds a card to this hand (hit action).
     * Automatically determines new status based on card value.
     * 
     * @param card The card to add to the hand
     * @return New PlayerHand with the additional card and updated status
     * @throws IllegalArgumentException if hand is not active
     */
    fun hit(card: Card): PlayerHand {
        require(status == HandStatus.ACTIVE) { "Hand not active" }
        
        val newCards = cards + card
        val newHand = Hand(newCards)
        
        val newStatus = when {
            newHand.isBusted -> HandStatus.BUSTED
            newHand.bestValue == DomainConstants.BlackjackValues.BLACKJACK_TOTAL -> HandStatus.STANDING
            else -> HandStatus.ACTIVE
        }
        
        return copy(cards = newCards, status = newStatus)
    }
    
    /**
     * Stands on current hand value (no more cards requested).
     * 
     * @return New PlayerHand with STANDING status
     * @throws IllegalArgumentException if hand is not active
     */
    fun stand(): PlayerHand {
        require(status == HandStatus.ACTIVE) { "Hand not active" }
        return copy(status = HandStatus.STANDING)
    }
    
    /**
     * Doubles the bet and takes exactly one more card.
     * Hand is automatically completed after doubling.
     * 
     * @param card The single card received when doubling
     * @return New PlayerHand with doubled bet and final status
     * @throws IllegalArgumentException if hand cannot be doubled
     */
    fun doubleDown(card: Card): PlayerHand {
        require(status == HandStatus.ACTIVE) { "Hand not active" }
        require(canDouble) { "Cannot double this hand" }
        
        val newCards = cards + card
        val newHand = Hand(newCards)
        
        val newStatus = if (newHand.isBusted) HandStatus.BUSTED else HandStatus.STANDING
        
        return copy(
            cards = newCards, 
            bet = bet * 2, 
            status = newStatus,
            hasDoubled = true
        )
    }
    
    /**
     * Splits a pair into two separate hands.
     * Each hand receives one new card and is marked as split hand.
     * 
     * @param newCard1 Card for the first split hand
     * @param newCard2 Card for the second split hand
     * @return Pair of new PlayerHands created from the split
     * @throws IllegalArgumentException if hand cannot be split
     */
    fun split(newCard1: Card, newCard2: Card): Pair<PlayerHand, PlayerHand> {
        require(status == HandStatus.ACTIVE) { "Hand not active" }
        require(canSplit) { "Cannot split this hand" }
        
        val card1 = cards[0]
        val card2 = cards[1]
        
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
    
    /**
     * Surrenders the hand, forfeiting half the bet.
     * 
     * @return New PlayerHand with SURRENDERED status
     * @throws IllegalArgumentException if hand is not active
     */
    fun surrender(): PlayerHand {
        require(status == HandStatus.ACTIVE) { "Hand not active" }
        return copy(status = HandStatus.SURRENDERED)
    }
    
    /**
     * Determines which actions are available for this hand based on game rules.
     * Considers hand value, card count, split status, and rule variations.
     * 
     * @param rules Game rules that affect available actions
     * @return Set of legal actions for this hand
     */
    fun availableActions(rules: GameRules = GameRules()): Set<Action> {
        if (status != HandStatus.ACTIVE) return emptySet()
        
        val actions = mutableSetOf<Action>()
        
        if (bestValue == DomainConstants.BlackjackValues.BLACKJACK_TOTAL) {
            actions.add(Action.STAND)
            return actions
        }
        
        if (bestValue < DomainConstants.BlackjackValues.BLACKJACK_TOTAL) {
            actions.add(Action.HIT)
        }
        actions.add(Action.STAND)
        
        if (canDouble && (!isFromSplit || rules.doubleAfterSplitAllowed)) {
            actions.add(Action.DOUBLE)
        }
        
        if (canSplit) {
            actions.add(Action.SPLIT)
        }
        
        if (rules.surrenderAllowed && 
            cards.size == DomainConstants.HandLimits.INITIAL_HAND_SIZE && 
            bestValue < DomainConstants.BlackjackValues.BLACKJACK_TOTAL &&
            !isFromSplit) {
            actions.add(Action.SURRENDER)
        }
        
        return actions
    }
}