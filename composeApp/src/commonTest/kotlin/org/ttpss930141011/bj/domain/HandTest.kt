package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HandTest {
    
    @Test
    fun `given ace and 6 when calculating value then should be soft 17`() {
        // Given
        val cards = listOf(
            Card(Suit.HEARTS, Rank.ACE), 
            Card(Suit.SPADES, Rank.SIX)
        )
        val hand = Hand(cards)
        
        // When
        val value = hand.bestValue
        val isSoft = hand.isSoft
        
        // Then
        assertEquals(17, value)
        assertTrue(isSoft)
    }
    
    @Test
    fun `given ace 6 and 5 when calculating value then should be hard 12`() {
        // Given - Ace + 6 + 5 = 22 if Ace=11, so Ace becomes 1
        val cards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.SIX),
            Card(Suit.DIAMONDS, Rank.FIVE)
        )
        val hand = Hand(cards)
        
        // When
        val value = hand.bestValue
        val isSoft = hand.isSoft
        
        // Then
        assertEquals(12, value)
        assertFalse(isSoft)
    }
    
    @Test
    fun `given 10 and 7 when calculating value then should be hard 17`() {
        // Given
        val cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.SPADES, Rank.SEVEN)
        )
        val hand = Hand(cards)
        
        // When
        val value = hand.bestValue
        val isSoft = hand.isSoft
        
        // Then
        assertEquals(17, value)
        assertFalse(isSoft)
    }
    
    @Test
    fun `given two aces when calculating value then one should be soft`() {
        // Given - A + A = 12 (one Ace=11, one Ace=1)
        val cards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.ACE)
        )
        val hand = Hand(cards)
        
        // When
        val value = hand.bestValue
        val isSoft = hand.isSoft
        
        // Then
        assertEquals(12, value)
        assertTrue(isSoft)
    }
    
    @Test
    fun `given two 8s when checking can split then should return true`() {
        // Given
        val cards = listOf(
            Card(Suit.HEARTS, Rank.EIGHT),
            Card(Suit.SPADES, Rank.EIGHT)
        )
        val hand = Hand(cards)
        
        // When
        val canSplit = hand.canSplit
        
        // Then
        assertTrue(canSplit)
    }
    
    @Test
    fun `given 10 and jack when checking can split then should return false`() {
        // Given - 根據 docs/blackjack-rules.md 第78行: 10,10永遠Stand (不分牌)
        val cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.SPADES, Rank.JACK)
        )
        val hand = Hand(cards)
        
        // When
        val canSplit = hand.canSplit
        
        // Then
        assertFalse(canSplit)
    }
}