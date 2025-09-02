package org.ttpss930141011.bj.domain

enum class Suit {
    HEARTS, DIAMONDS, CLUBS, SPADES
}

enum class Rank(val blackjackValue: Int) {
    ACE(1),
    TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
    JACK(10), QUEEN(10), KING(10)
}

data class Card(val suit: Suit, val rank: Rank) {
    val blackjackValue: Int get() = rank.blackjackValue
}