package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import org.ttpss930141011.bj.domain.valueobjects.*

class HandTest {
    
    @Test
    fun `given empty card list when creating hand then should throw exception`() {
        // When/Then
        assertFailsWith<IllegalArgumentException> {
            Hand(emptyList())
        }
    }
    
    @Test
    fun `given single ace when calculating values then should return correct hard and soft values`() {
        // Given
        val cards = listOf(Card(Suit.HEARTS, Rank.ACE))
        val hand = Hand(cards)
        
        // Then
        assertEquals(1, hand.hardValue)
        assertEquals(11, hand.softValue) 
        assertTrue(hand.isSoft)
        assertEquals(11, hand.bestValue)
        assertFalse(hand.isBusted)
        assertFalse(hand.isBlackjack)
        assertFalse(hand.canSplit)
        assertFalse(hand.canDouble)
    }
    
    @Test
    fun `given ace and ten when calculating values then should be blackjack`() {
        // Given
        val cards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.TEN)
        )
        val hand = Hand(cards)
        
        // Then
        assertEquals(11, hand.hardValue)
        assertEquals(21, hand.softValue)
        assertTrue(hand.isSoft)
        assertEquals(21, hand.bestValue)
        assertTrue(hand.isBlackjack)
        assertFalse(hand.isBusted)
        assertTrue(hand.canDouble)
        assertFalse(hand.canSplit) // Different ranks
    }
    
    @Test
    fun `given ace and jack when calculating values then should be blackjack`() {
        // Given
        val cards = listOf(
            Card(Suit.CLUBS, Rank.ACE), 
            Card(Suit.HEARTS, Rank.JACK)
        )
        val hand = Hand(cards)
        
        // Then
        assertEquals(11, hand.hardValue)
        assertEquals(21, hand.softValue)
        assertTrue(hand.isSoft)
        assertEquals(21, hand.bestValue)
        assertTrue(hand.isBlackjack)
        assertFalse(hand.canSplit) // Ace != Jack
    }
    
    @Test
    fun `given two aces when calculating values then should handle correctly`() {
        // Given
        val cards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.ACE)
        )
        val hand = Hand(cards)
        
        // Then
        assertEquals(2, hand.hardValue) // Both aces as 1
        assertEquals(12, hand.softValue) // One ace as 11, one as 1
        assertTrue(hand.isSoft)
        assertEquals(12, hand.bestValue)
        assertFalse(hand.isBusted)
        assertFalse(hand.isBlackjack) // 12 is not 21
        assertTrue(hand.canSplit) // Same rank
        assertTrue(hand.canDouble)
    }
    
    @Test
    fun `given three aces when calculating values then should handle multiple aces`() {
        // Given
        val cards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.CLUBS, Rank.ACE)
        )
        val hand = Hand(cards)
        
        // Then
        assertEquals(3, hand.hardValue) // All aces as 1
        assertEquals(13, hand.softValue) // One ace as 11, others as 1
        assertTrue(hand.isSoft)
        assertEquals(13, hand.bestValue)
        assertFalse(hand.isBusted)
        assertFalse(hand.isBlackjack)
        assertFalse(hand.canSplit) // More than 2 cards
        assertFalse(hand.canDouble) // More than 2 cards
    }
    
    @Test
    fun `given hard hand totaling 20 when calculating values then should be hard 20`() {
        // Given
        val cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.SPADES, Rank.QUEEN)
        )
        val hand = Hand(cards)
        
        // Then
        assertEquals(20, hand.hardValue)
        assertEquals(20, hand.softValue) // No ace, so same as hard
        assertFalse(hand.isSoft)
        assertEquals(20, hand.bestValue)
        assertFalse(hand.isBusted)
        assertFalse(hand.isBlackjack) // 20, not 21
        assertFalse(hand.canSplit) // TEN != QUEEN
        assertTrue(hand.canDouble)
    }
    
    @Test
    fun `given two tens when calculating values then should be splittable`() {
        // Given
        val cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.SPADES, Rank.TEN)
        )
        val hand = Hand(cards)
        
        // Then
        assertEquals(20, hand.hardValue)
        assertEquals(20, hand.softValue)
        assertFalse(hand.isSoft)
        assertEquals(20, hand.bestValue)
        assertFalse(hand.isBusted)
        assertTrue(hand.canSplit) // Same rank
        assertTrue(hand.canDouble)
    }
    
    @Test
    fun `given busted hand when calculating values then should be busted`() {
        // Given
        val cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.CLUBS, Rank.EIGHT)
        )
        val hand = Hand(cards)
        
        // Then
        assertEquals(25, hand.hardValue)
        assertEquals(25, hand.softValue) // No aces
        assertFalse(hand.isSoft)
        assertEquals(25, hand.bestValue)
        assertTrue(hand.isBusted) // Over 21
        assertFalse(hand.isBlackjack)
        assertFalse(hand.canSplit)
        assertFalse(hand.canDouble)
    }
    
    @Test
    fun `given soft hand becoming hard when calculating values then should prefer soft when possible`() {
        // Given - Ace, 6, 4 = 21 or 11 (soft 21)
        val cards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.SIX),
            Card(Suit.CLUBS, Rank.FOUR)
        )
        val hand = Hand(cards)
        
        // Then
        assertEquals(11, hand.hardValue) // Ace as 1: 1+6+4=11
        assertEquals(21, hand.softValue) // Ace as 11: 11+6+4=21
        assertTrue(hand.isSoft)
        assertEquals(21, hand.bestValue) // Prefer soft 21
        assertFalse(hand.isBusted)
        assertFalse(hand.isBlackjack) // More than 2 cards
    }
    
    @Test
    fun `given soft hand that would bust when adding ace high then should use hard value`() {
        // Given - Ace, 7, 5 = 13 hard (1+7+5) or 23 soft (11+7+5, busted)
        val cards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.CLUBS, Rank.FIVE)
        )
        val hand = Hand(cards)
        
        // Then
        assertEquals(13, hand.hardValue) // 1+7+5=13
        assertEquals(13, hand.softValue) // 11+7+5=23 > 21, so use hard value
        assertFalse(hand.isSoft) // Can't use soft value
        assertEquals(13, hand.bestValue)
        assertFalse(hand.isBusted)
        assertFalse(hand.isBlackjack)
    }
    
    @Test
    fun `given hand when adding card then should return new hand with additional card`() {
        // Given
        val initialCards = listOf(Card(Suit.HEARTS, Rank.SEVEN))
        val hand = Hand(initialCards)
        val newCard = Card(Suit.SPADES, Rank.FIVE)
        
        // When
        val newHand = hand.addCard(newCard)
        
        // Then
        assertEquals(1, hand.cards.size) // Original unchanged
        assertEquals(2, newHand.cards.size)
        assertEquals(Card(Suit.HEARTS, Rank.SEVEN), newHand.cards[0])
        assertEquals(Card(Suit.SPADES, Rank.FIVE), newHand.cards[1])
        assertEquals(12, newHand.bestValue)
    }
    
    @Test
    fun `given ten and jack when checking split then should not be splittable`() {
        // Given - Both worth 10 points but different ranks
        val cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.SPADES, Rank.JACK)
        )
        val hand = Hand(cards)
        
        // Then
        assertFalse(hand.canSplit) // Different ranks, even though same value
    }
    
    @Test
    fun `given king and queen when checking split then should not be splittable`() {
        // Given - Both worth 10 points but different ranks
        val cards = listOf(
            Card(Suit.HEARTS, Rank.KING),
            Card(Suit.SPADES, Rank.QUEEN)
        )
        val hand = Hand(cards)
        
        // Then
        assertFalse(hand.canSplit) // Different ranks
    }
    
    @Test
    fun `given more than two cards when checking double and split then should not be allowed`() {
        // Given
        val cards = listOf(
            Card(Suit.HEARTS, Rank.FIVE),
            Card(Suit.SPADES, Rank.FIVE),
            Card(Suit.CLUBS, Rank.TWO)
        )
        val hand = Hand(cards)
        
        // Then
        assertFalse(hand.canDouble) // More than 2 cards
        assertFalse(hand.canSplit) // More than 2 cards
    }
}