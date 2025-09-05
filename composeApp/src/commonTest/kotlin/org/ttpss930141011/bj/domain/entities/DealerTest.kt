package org.ttpss930141011.bj.domain.entities

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertNotNull
import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.valueobjects.Suit
import org.ttpss930141011.bj.domain.valueobjects.Rank
import org.ttpss930141011.bj.domain.valueobjects.Hand

class DealerTest {
    
    @Test
    fun `given new dealer when created then should have no cards`() {
        // Given & When
        val dealer = Dealer()
        
        // Then
        assertNull(dealer.hand)
        assertNull(dealer.holeCard)
        assertNull(dealer.upCard)
        assertFalse(dealer.shouldHit)
    }
    
    @Test
    fun `given dealer when dealing initial card then should have up card`() {
        // Given
        val dealer = Dealer()
        val upCard = Card(Suit.HEARTS, Rank.KING)
        
        // When
        val newDealer = dealer.dealInitialCard(upCard)
        
        // Then
        assertNotNull(newDealer.hand)
        assertEquals(1, newDealer.hand!!.cards.size)
        assertEquals(upCard, newDealer.upCard)
        assertNull(newDealer.holeCard)
    }
    
    @Test
    fun `given dealer when dealing initial cards then should have up card and hole card`() {
        // Given
        val dealer = Dealer()
        val upCard = Card(Suit.HEARTS, Rank.KING)
        val holeCard = Card(Suit.SPADES, Rank.ACE)
        
        // When
        val newDealer = dealer.dealInitialCards(upCard, holeCard)
        
        // Then
        assertNotNull(newDealer.hand)
        assertEquals(1, newDealer.hand!!.cards.size)
        assertEquals(upCard, newDealer.upCard)
        assertEquals(holeCard, newDealer.holeCard)
    }
    
    @Test
    fun `given dealer with hole card when revealing then should combine all cards`() {
        // Given
        val upCard = Card(Suit.HEARTS, Rank.KING)
        val holeCard = Card(Suit.SPADES, Rank.ACE)
        val dealer = Dealer().dealInitialCards(upCard, holeCard)
        
        // When
        val revealedDealer = dealer.revealHoleCard()
        
        // Then
        assertNotNull(revealedDealer.hand)
        assertEquals(2, revealedDealer.hand!!.cards.size)
        assertTrue(revealedDealer.hand!!.cards.contains(upCard))
        assertTrue(revealedDealer.hand!!.cards.contains(holeCard))
        assertEquals(holeCard, revealedDealer.holeCard) // holeCard still stored
    }
    
    @Test
    fun `given dealer with no cards when revealing hole card then should throw exception`() {
        // Given
        val dealer = Dealer()
        
        // When & Then
        assertFailsWith<IllegalArgumentException> {
            dealer.revealHoleCard()
        }
    }
    
    @Test
    fun `given dealer with no hole card when revealing then should throw exception`() {
        // Given
        val upCard = Card(Suit.HEARTS, Rank.KING)
        val dealer = Dealer().dealInitialCard(upCard)
        
        // When & Then
        assertFailsWith<IllegalArgumentException> {
            dealer.revealHoleCard()
        }
    }
    
    @Test
    fun `given dealer with cards when hitting then should add card to hand`() {
        // Given
        val upCard = Card(Suit.HEARTS, Rank.KING)
        val dealer = Dealer().dealInitialCard(upCard)
        val hitCard = Card(Suit.CLUBS, Rank.FIVE)
        
        // When
        val newDealer = dealer.hit(hitCard)
        
        // Then
        assertNotNull(newDealer.hand)
        assertEquals(2, newDealer.hand!!.cards.size)
        assertTrue(newDealer.hand!!.cards.contains(upCard))
        assertTrue(newDealer.hand!!.cards.contains(hitCard))
    }
    
    @Test
    fun `given dealer with no cards when hitting then should throw exception`() {
        // Given
        val dealer = Dealer()
        val hitCard = Card(Suit.CLUBS, Rank.FIVE)
        
        // When & Then
        assertFailsWith<IllegalArgumentException> {
            dealer.hit(hitCard)
        }
    }
    
    // Dealer should hit tests (based on dealer rules)
    @Test
    fun `given dealer with 16 when checking should hit then should return true`() {
        // Given
        val dealer = Dealer(hand = Hand(listOf(
            Card(Suit.HEARTS, Rank.KING),
            Card(Suit.CLUBS, Rank.SIX)
        )))
        
        // When & Then
        assertTrue(dealer.shouldHit)
    }
    
    @Test
    fun `given dealer with hard 17 when checking should hit then should return false`() {
        // Given
        val dealer = Dealer(hand = Hand(listOf(
            Card(Suit.HEARTS, Rank.KING),
            Card(Suit.CLUBS, Rank.SEVEN)
        )))
        
        // When & Then
        assertFalse(dealer.shouldHit)
    }
    
    @Test
    fun `given dealer with soft 17 when checking should hit then should return true`() {
        // Given - Ace (11) + 6 = soft 17
        val dealer = Dealer(hand = Hand(listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.CLUBS, Rank.SIX)
        )))
        
        // When & Then
        assertTrue(dealer.shouldHit) // Dealer hits on soft 17
    }
    
    @Test
    fun `given dealer with soft 18 when checking should hit then should return false`() {
        // Given - Ace (11) + 7 = soft 18
        val dealer = Dealer(hand = Hand(listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.CLUBS, Rank.SEVEN)
        )))
        
        // When & Then
        assertFalse(dealer.shouldHit) // Dealer stands on soft 18+
    }
    
    @Test
    fun `given dealer with 18 when checking should hit then should return false`() {
        // Given
        val dealer = Dealer(hand = Hand(listOf(
            Card(Suit.HEARTS, Rank.KING),
            Card(Suit.CLUBS, Rank.EIGHT)
        )))
        
        // When & Then
        assertFalse(dealer.shouldHit)
    }
    
    @Test
    fun `given dealer with 21 when checking should hit then should return false`() {
        // Given
        val dealer = Dealer(hand = Hand(listOf(
            Card(Suit.HEARTS, Rank.KING),
            Card(Suit.CLUBS, Rank.ACE)
        )))
        
        // When & Then
        assertFalse(dealer.shouldHit)
    }
    
    @Test
    fun `given dealer with busted hand when checking should hit then should return false`() {
        // Given
        val dealer = Dealer(hand = Hand(listOf(
            Card(Suit.HEARTS, Rank.KING),
            Card(Suit.CLUBS, Rank.QUEEN),
            Card(Suit.DIAMONDS, Rank.FIVE)
        )))
        
        // When & Then
        assertFalse(dealer.shouldHit) // Don't hit when already busted
    }
    
    @Test
    fun `given dealer with multiple aces when checking should hit then should evaluate correctly`() {
        // Given - Ace + Ace + 5 = 17 (soft)
        val dealer = Dealer(hand = Hand(listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.CLUBS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.FIVE)
        )))
        
        // When & Then
        assertTrue(dealer.shouldHit) // Soft 17, should hit
    }
    
    @Test
    fun `given dealer with blackjack when checking should hit then should return false`() {
        // Given
        val dealer = Dealer(hand = Hand(listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.CLUBS, Rank.KING)
        )))
        
        // When & Then
        assertFalse(dealer.shouldHit) // Blackjack = 21, don't hit
    }
}