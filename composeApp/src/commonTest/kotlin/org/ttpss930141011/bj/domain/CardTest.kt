package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.valueobjects.Suit
import org.ttpss930141011.bj.domain.valueobjects.Rank

class CardTest {
    
    @Test
    fun `given ace when getting blackjack value then should return 1`() {
        // Given
        val card = Card(Suit.HEARTS, Rank.ACE)
        
        // When  
        val value = card.blackjackValue
        
        // Then
        assertEquals(1, value)
    }
    
    @Test
    fun `given jack when getting blackjack value then should return 10`() {
        // Given
        val card = Card(Suit.SPADES, Rank.JACK)
        
        // When
        val value = card.blackjackValue
        
        // Then
        assertEquals(10, value)
    }
    
    @Test
    fun `given seven when getting blackjack value then should return 7`() {
        // Given
        val card = Card(Suit.DIAMONDS, Rank.SEVEN)
        
        // When
        val value = card.blackjackValue
        
        // Then
        assertEquals(7, value)
    }
}