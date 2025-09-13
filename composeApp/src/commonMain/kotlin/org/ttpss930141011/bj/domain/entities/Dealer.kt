package org.ttpss930141011.bj.domain.entities

import org.ttpss930141011.bj.domain.valueobjects.Hand
import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.DomainConstants

/**
 * Dealer entity representing the house in blackjack
 * 
 * @param hand Dealer's visible cards
 * @param holeCard Dealer's hidden card (face down)
 */
data class Dealer(
    val hand: Hand? = null,
    val holeCard: Card? = null
) {
    
    /**
     * Deals initial face-up card to dealer
     * 
     * @param upCard The face-up card
     * @return New dealer with face-up card
     */
    fun dealInitialCard(upCard: Card): Dealer {
        return copy(hand = Hand(listOf(upCard)))
    }
    
    /**
     * Deals initial two cards to dealer (one up, one down)
     * 
     * @param upCard The face-up card
     * @param holeCard The face-down card
     * @return New dealer with both cards
     */
    fun dealInitialCards(upCard: Card, holeCard: Card): Dealer {
        return copy(
            hand = Hand(listOf(upCard)),
            holeCard = holeCard
        )
    }
    
    /**
     * Reveals the hole card by adding it to the visible hand
     * 
     * @return New dealer with hole card revealed
     * @throws IllegalArgumentException if no hand or hole card
     */
    fun revealHoleCard(): Dealer {
        require(hand != null) { "No cards dealt to dealer yet" }
        require(holeCard != null) { "No hole card to reveal" }
        val allCards = hand.cards + holeCard
        return copy(hand = Hand(allCards))
    }
    
    /**
     * Adds a card to dealer's hand (hit)
     * 
     * @param card Card to add to hand
     * @return New dealer with additional card
     * @throws IllegalArgumentException if no initial hand
     */
    fun hit(card: Card): Dealer {
        require(hand != null) { "No cards dealt to dealer yet" }
        val newCards = hand.cards + card
        return copy(hand = Hand(newCards))
    }
    
    /** The dealer's face-up card */
    val upCard: Card? = hand?.cards?.firstOrNull()
    
    /** 
     * Whether dealer should hit according to house rules
     * Dealer hits on soft 17, stands on hard 17 and above
     */
    val shouldHit: Boolean = hand?.let { dealerHand ->
        if (dealerHand.isBusted) return@let false
        
        val value = dealerHand.bestValue
        when {
            value < DomainConstants.BlackjackValues.DEALER_STAND_HARD -> true
            value > DomainConstants.BlackjackValues.DEALER_STAND_HARD -> false
            value == DomainConstants.BlackjackValues.DEALER_STAND_HARD && dealerHand.isSoft -> true
            else -> false
        }
    } ?: false
}