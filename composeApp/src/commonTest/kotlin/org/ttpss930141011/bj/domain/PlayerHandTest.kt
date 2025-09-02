package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class PlayerHandTest {
    
    @Test
    fun `given initial hand when created then should have correct properties`() {
        // Given - initial cards and bet
        val cards = listOf(Card(Suit.HEARTS, Rank.KING), Card(Suit.SPADES, Rank.SEVEN))
        val bet = 25
        
        // When - creating PlayerHand
        val playerHand = PlayerHand.initial(cards, bet)
        
        // Then - should have correct properties
        assertEquals(cards, playerHand.cards)
        assertEquals(bet, playerHand.bet)
        assertEquals(17, playerHand.bestValue)
        assertEquals(HandStatus.ACTIVE, playerHand.status)
        assertFalse(playerHand.isBusted)
        assertFalse(playerHand.isCompleted)
    }
    
    @Test
    fun `given QQ hand when checking split capability then should be splittable`() {
        // Given - QQ hand
        val cards = listOf(Card(Suit.HEARTS, Rank.QUEEN), Card(Suit.SPADES, Rank.QUEEN))
        val playerHand = PlayerHand.initial(cards, 25)
        
        // When - checking split capability
        val canSplit = playerHand.canSplit
        
        // Then - should be splittable
        assertTrue(canSplit, "QQ hand should be splittable")
    }
    
    @Test
    fun `given JJ hand when splitting then should create two separate hands`() {
        // Given - JJ hand
        val cards = listOf(Card(Suit.HEARTS, Rank.JACK), Card(Suit.CLUBS, Rank.JACK))
        val playerHand = PlayerHand.initial(cards, 25)
        val deck = Deck.shuffled()
        
        // When - splitting hand
        val splitResult = playerHand.split(deck)
        val firstHand = splitResult.firstHand
        val secondHand = splitResult.secondHand
        val newDeck = splitResult.deck
        
        // Then - should have two hands with one J each plus new card
        assertEquals(Rank.JACK, firstHand.cards[0].rank)
        assertEquals(Rank.JACK, secondHand.cards[0].rank)
        assertEquals(2, firstHand.cards.size)
        assertEquals(2, secondHand.cards.size)
        assertEquals(25, firstHand.bet)
        assertEquals(25, secondHand.bet)
        assertEquals(HandStatus.ACTIVE, firstHand.status)
        assertEquals(HandStatus.ACTIVE, secondHand.status)
        assertTrue(newDeck.remainingCards < deck.remainingCards) // Cards were dealt
    }
    
    @Test
    fun `given player hand when hitting then should add card and update status`() {
        // Given - hand that can hit (16)
        val cards = listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX))
        val playerHand = PlayerHand.initial(cards, 25)
        val deck = Deck.shuffled()
        
        // When - hitting
        val hitResult = playerHand.hit(deck)
        val newHand = hitResult.hand
        val newDeck = hitResult.deck
        
        // Then - should have additional card
        assertEquals(3, newHand.cards.size)
        assertEquals(cards[0], newHand.cards[0])
        assertEquals(cards[1], newHand.cards[1])
        // Status depends on new card value (active if <21, standing if 21, busted if >21)
        assertTrue(newDeck.remainingCards < deck.remainingCards)
    }
    
    @Test
    fun `given player hand when standing then should change status to standing`() {
        // Given - any active hand
        val cards = listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SEVEN))
        val playerHand = PlayerHand.initial(cards, 25)
        
        // When - standing
        val standResult = playerHand.stand()
        
        // Then - should change status only
        assertEquals(playerHand.cards, standResult.cards)
        assertEquals(playerHand.bet, standResult.bet)
        assertEquals(HandStatus.STANDING, standResult.status)
        assertTrue(standResult.isCompleted)
    }
    
    @Test
    fun `given player hand when doubling then should add one card and double bet`() {
        // Given - initial hand that can double
        val cards = listOf(Card(Suit.HEARTS, Rank.FIVE), Card(Suit.SPADES, Rank.SIX))
        val playerHand = PlayerHand.initial(cards, 25)
        val deck = Deck.shuffled()
        
        // When - doubling
        val doubleResult = playerHand.double(deck)
        val newHand = doubleResult.hand
        val newDeck = doubleResult.deck
        
        // Then - should have one more card, double bet, and be completed
        assertEquals(3, newHand.cards.size)
        assertEquals(50, newHand.bet) // Double the original bet
        assertTrue(newHand.isCompleted) // Always completed after double
        assertTrue(newDeck.remainingCards < deck.remainingCards)
    }
}