package org.ttpss930141011.bj.domain.valueobjects

import org.ttpss930141011.bj.domain.DomainConstants

/**
 * Represents a blackjack hand with automatic hard/soft value calculation
 * 
 * @param cards List of cards in the hand (must not be empty)
 */
data class Hand(internal val cards: List<Card>) {
    
    init {
        require(cards.isNotEmpty()) { "Hand cannot be empty" }
    }
    
    /** Hard value treating all aces as 1 */
    val hardValue: Int = cards.sumOf { it.blackjackValue }
    
    /** Soft value with optimal ace counting (max one ace as 11) */
    val softValue: Int = run {
        val aces = cards.count { it.rank == Rank.ACE }
        if (aces > 0 && hardValue + DomainConstants.BlackjackValues.ACE_HIGH_VALUE - DomainConstants.BlackjackValues.ACE_LOW_VALUE <= DomainConstants.BlackjackValues.BLACKJACK_TOTAL) {
            hardValue + DomainConstants.BlackjackValues.ACE_HIGH_VALUE - DomainConstants.BlackjackValues.ACE_LOW_VALUE
        } else {
            hardValue
        }
    }
    
    /** True if hand contains an ace counted as 11 */
    val isSoft: Boolean = softValue != hardValue
    
    /** Best possible value (soft value if available, hard value otherwise) */
    val bestValue: Int = if (isSoft) softValue else hardValue
    
    /** True if hand exceeds 21 points */
    val isBusted: Boolean = hardValue > DomainConstants.BlackjackValues.BUST_THRESHOLD
    
    /** True if hand can be split (two cards of same rank) */
    val canSplit: Boolean = run {
        if (cards.size != DomainConstants.HandLimits.SPLIT_REQUIRED_HAND_SIZE) return@run false
        cards[0].rank == cards[1].rank
    }
    
    /** True if hand can be doubled down (exactly two cards) */
    val canDouble: Boolean = cards.size == DomainConstants.HandLimits.DOUBLE_DOWN_HAND_SIZE
    
    /** True if hand is natural blackjack (21 with exactly two cards) */
    val isBlackjack: Boolean = cards.size == DomainConstants.HandLimits.BLACKJACK_HAND_SIZE && bestValue == DomainConstants.BlackjackValues.BLACKJACK_TOTAL
    
    /**
     * Creates a new hand with an additional card
     * 
     * @param card Card to add to the hand
     * @return New hand instance with the added card
     */
    fun addCard(card: Card): Hand = Hand(cards + card)
}