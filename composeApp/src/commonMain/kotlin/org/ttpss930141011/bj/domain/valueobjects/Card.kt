package org.ttpss930141011.bj.domain.valueobjects

import kotlinx.serialization.Serializable

/**
 * Card suit enumeration for blackjack cards
 */
@Serializable
enum class Suit {
    HEARTS, DIAMONDS, CLUBS, SPADES
}

/**
 * Card rank enumeration with blackjack point values
 * Ace defaults to 1, face cards are worth 10
 * 
 * @param blackjackValue The point value for blackjack calculation
 */
@Serializable
enum class Rank(val blackjackValue: Int) {
    ACE(1),
    TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
    JACK(10), QUEEN(10), KING(10)
}

/**
 * Immutable playing card with suit and rank
 * 
 * @param suit The card suit
 * @param rank The card rank with associated blackjack value
 */
@Serializable
data class Card(val suit: Suit, val rank: Rank) {
    /** The blackjack point value of this card */
    val blackjackValue: Int get() = rank.blackjackValue
    
    companion object {
        /** Unknown card placeholder for incomplete game states */
        val UNKNOWN_CARD = Card(Suit.SPADES, Rank.ACE)
    }
}