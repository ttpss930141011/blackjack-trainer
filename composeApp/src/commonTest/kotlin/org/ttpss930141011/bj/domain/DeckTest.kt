package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import org.ttpss930141011.bj.domain.valueobjects.Deck
import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.valueobjects.Suit
import org.ttpss930141011.bj.domain.valueobjects.Rank

class DeckTest {
    
    @Test
    fun `given standard 6-deck when created then should have 312 cards`() {
        // Given & When
        val deck = Deck.createStandardDeck(6)
        
        // Then
        assertEquals(312, deck.remainingCards) // 52 * 6
        assertEquals(0, deck.cardsDealt)
    }
    
    @Test
    fun `given deck when dealing one card then should reduce remaining and increase dealt`() {
        // Given
        val testCards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.KING)
        )
        val deck = Deck.createTestDeck(testCards)
        
        // When
        val (card, newDeck) = deck.dealCard()
        
        // Then
        assertEquals(Card(Suit.HEARTS, Rank.ACE), card)
        assertEquals(1, newDeck.remainingCards)
        assertEquals(1, newDeck.cardsDealt)
    }
    
    @Test
    fun `given deck when dealing multiple cards then should deal in order`() {
        // Given
        val testCards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.KING),
            Card(Suit.CLUBS, Rank.QUEEN)
        )
        val deck = Deck.createTestDeck(testCards)
        
        // When
        val (cards, newDeck) = deck.dealCards(2)
        
        // Then
        assertEquals(2, cards.size)
        assertEquals(Card(Suit.HEARTS, Rank.ACE), cards[0])
        assertEquals(Card(Suit.SPADES, Rank.KING), cards[1])
        assertEquals(1, newDeck.remainingCards)
    }
    
    @Test
    fun `given deck with few cards when checking needs shuffle then should return true`() {
        // Given - 只剩20張牌
        val fewCards = (1..20).map { Card(Suit.HEARTS, Rank.ACE) }
        val deck = Deck.createTestDeck(fewCards)
        
        // When & Then
        assertTrue(deck.needsShuffle)
    }
    
    @Test
    fun `given deck with many cards when checking needs shuffle then should return false`() {
        // Given - 還有很多牌
        val manyCards = (1..100).map { Card(Suit.HEARTS, Rank.ACE) }
        val deck = Deck.createTestDeck(manyCards)
        
        // When & Then
        assertFalse(deck.needsShuffle)
    }
}