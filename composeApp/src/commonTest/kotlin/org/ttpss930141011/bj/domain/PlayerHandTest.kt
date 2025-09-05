package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*

class PlayerHandTest {
    
    private val rules = GameRules()
    
    @Test
    fun `given initial cards and bet when creating player hand then should have correct initial state`() {
        // Given
        val cards = listOf(Card(Suit.HEARTS, Rank.SEVEN), Card(Suit.SPADES, Rank.FIVE))
        
        // When
        val playerHand = PlayerHand.initial(cards, bet = 100)
        
        // Then
        assertEquals(100, playerHand.bet)
        assertEquals(12, playerHand.bestValue)
        assertEquals(HandStatus.ACTIVE, playerHand.status)
        assertFalse(playerHand.isCompleted)
        assertFalse(playerHand.isBlackjack)
        assertFalse(playerHand.isFromSplit)
        assertFalse(playerHand.hasDoubled)
    }
    
    @Test
    fun `given player hand with blackjack when created then should detect blackjack`() {
        // Given
        val cards = listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.KING))
        
        // When
        val playerHand = PlayerHand.initial(cards, bet = 100)
        
        // Then
        assertEquals(21, playerHand.bestValue)
        assertTrue(playerHand.isBlackjack)
        assertFalse(playerHand.isBusted)
        assertTrue(playerHand.canDouble) // Initial hand can double
    }
    
    @Test
    fun `given active hand when hitting then should add card and update status`() {
        // Given
        val initialCards = listOf(Card(Suit.HEARTS, Rank.SEVEN), Card(Suit.SPADES, Rank.FIVE))
        val playerHand = PlayerHand.initial(initialCards, bet = 100)
        val newCard = Card(Suit.CLUBS, Rank.THREE)
        
        // When
        val updatedHand = playerHand.hit(newCard)
        
        // Then
        assertEquals(3, updatedHand.cards.size)
        assertEquals(15, updatedHand.bestValue)
        assertEquals(HandStatus.ACTIVE, updatedHand.status) // Still active
        assertFalse(updatedHand.hasDoubled)
    }
    
    @Test
    fun `given hand when hitting and getting 21 then should auto-stand`() {
        // Given
        val initialCards = listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.FIVE))
        val playerHand = PlayerHand.initial(initialCards, bet = 100)
        val newCard = Card(Suit.CLUBS, Rank.SIX) // 10+5+6=21
        
        // When
        val updatedHand = playerHand.hit(newCard)
        
        // Then
        assertEquals(21, updatedHand.bestValue)
        assertEquals(HandStatus.STANDING, updatedHand.status)
        assertTrue(updatedHand.isCompleted)
        assertFalse(updatedHand.isBlackjack) // 3 cards, not blackjack
    }
    
    @Test
    fun `given hand when hitting and busting then should be busted`() {
        // Given
        val initialCards = listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.EIGHT))
        val playerHand = PlayerHand.initial(initialCards, bet = 100)
        val newCard = Card(Suit.CLUBS, Rank.SEVEN) // 10+8+7=25 (bust)
        
        // When
        val updatedHand = playerHand.hit(newCard)
        
        // Then
        assertEquals(25, updatedHand.bestValue)
        assertEquals(HandStatus.BUSTED, updatedHand.status)
        assertTrue(updatedHand.isCompleted)
        assertTrue(updatedHand.isBusted)
    }
    
    @Test
    fun `given active hand when standing then should change status to standing`() {
        // Given
        val cards = listOf(Card(Suit.HEARTS, Rank.NINE), Card(Suit.SPADES, Rank.SEVEN))
        val playerHand = PlayerHand.initial(cards, bet = 100)
        
        // When
        val stoodHand = playerHand.stand()
        
        // Then
        assertEquals(HandStatus.STANDING, stoodHand.status)
        assertTrue(stoodHand.isCompleted)
        assertEquals(16, stoodHand.bestValue)
        assertEquals(100, stoodHand.bet) // Bet unchanged
    }
    
    @Test
    fun `given two-card hand when doubling down then should double bet and add card`() {
        // Given
        val initialCards = listOf(Card(Suit.HEARTS, Rank.FIVE), Card(Suit.SPADES, Rank.SIX))
        val playerHand = PlayerHand.initial(initialCards, bet = 100)
        val newCard = Card(Suit.CLUBS, Rank.FOUR)
        
        // When
        val doubledHand = playerHand.doubleDown(newCard)
        
        // Then
        assertEquals(200, doubledHand.bet) // Doubled from 100
        assertTrue(doubledHand.hasDoubled)
        assertEquals(3, doubledHand.cards.size)
        assertEquals(15, doubledHand.bestValue)
        assertEquals(HandStatus.STANDING, doubledHand.status) // Auto-stand after double
        assertTrue(doubledHand.isCompleted)
    }
    
    @Test
    fun `given two-card hand when doubling and getting 21 then should stand`() {
        // Given
        val initialCards = listOf(Card(Suit.HEARTS, Rank.FIVE), Card(Suit.SPADES, Rank.SIX))
        val playerHand = PlayerHand.initial(initialCards, bet = 100)
        val newCard = Card(Suit.CLUBS, Rank.TEN) // 5+6+10=21
        
        // When
        val doubledHand = playerHand.doubleDown(newCard)
        
        // Then
        assertEquals(21, doubledHand.bestValue)
        assertEquals(HandStatus.STANDING, doubledHand.status)
        assertTrue(doubledHand.hasDoubled)
        assertFalse(doubledHand.isBlackjack) // 3 cards after double
    }
    
    @Test
    fun `given two-card hand when doubling and busting then should be busted`() {
        // Given
        val initialCards = listOf(Card(Suit.HEARTS, Rank.NINE), Card(Suit.SPADES, Rank.SEVEN))
        val playerHand = PlayerHand.initial(initialCards, bet = 100)
        val newCard = Card(Suit.CLUBS, Rank.EIGHT) // 9+7+8=24 (bust)
        
        // When
        val doubledHand = playerHand.doubleDown(newCard)
        
        // Then
        assertEquals(24, doubledHand.bestValue)
        assertEquals(HandStatus.BUSTED, doubledHand.status)
        assertTrue(doubledHand.hasDoubled)
        assertTrue(doubledHand.isBusted)
    }
    
    @Test
    fun `given pair when splitting then should create two hands`() {
        // Given
        val initialCards = listOf(Card(Suit.HEARTS, Rank.EIGHT), Card(Suit.SPADES, Rank.EIGHT))
        val playerHand = PlayerHand.initial(initialCards, bet = 100)
        val newCard1 = Card(Suit.CLUBS, Rank.THREE)
        val newCard2 = Card(Suit.DIAMONDS, Rank.SEVEN)
        
        // When
        val (hand1, hand2) = playerHand.split(newCard1, newCard2)
        
        // Then
        // First hand: 8 + 3 = 11
        assertEquals(2, hand1.cards.size)
        assertEquals(Card(Suit.HEARTS, Rank.EIGHT), hand1.cards[0])
        assertEquals(Card(Suit.CLUBS, Rank.THREE), hand1.cards[1])
        assertEquals(11, hand1.bestValue)
        assertEquals(100, hand1.bet) // Same bet amount
        assertTrue(hand1.isFromSplit)
        
        // Second hand: 8 + 7 = 15
        assertEquals(2, hand2.cards.size)
        assertEquals(Card(Suit.SPADES, Rank.EIGHT), hand2.cards[0])
        assertEquals(Card(Suit.DIAMONDS, Rank.SEVEN), hand2.cards[1])
        assertEquals(15, hand2.bestValue)
        assertEquals(100, hand2.bet)
        assertTrue(hand2.isFromSplit)
    }
    
    @Test
    fun `given aces when splitting then should handle correctly`() {
        // Given
        val initialCards = listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.ACE))
        val playerHand = PlayerHand.initial(initialCards, bet = 200)
        val newCard1 = Card(Suit.CLUBS, Rank.KING)    // Ace + King = 21
        val newCard2 = Card(Suit.DIAMONDS, Rank.NINE)  // Ace + Nine = 20 (soft)
        
        // When
        val (hand1, hand2) = playerHand.split(newCard1, newCard2)
        
        // Then
        assertEquals(21, hand1.bestValue) // Ace + King
        assertEquals(20, hand2.bestValue) // Ace + Nine (soft 20)
        assertTrue(hand1.isFromSplit)
        assertTrue(hand2.isFromSplit)
        assertFalse(hand1.isBlackjack) // Split hand, not natural blackjack
        assertFalse(hand2.isBlackjack) // Split hand, not natural blackjack
    }
    
    @Test
    fun `given hand when surrendering then should change status to surrendered`() {
        // Given
        val cards = listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX))
        val playerHand = PlayerHand.initial(cards, bet = 100)
        
        // When
        val surrenderedHand = playerHand.surrender()
        
        // Then
        assertEquals(HandStatus.SURRENDERED, surrenderedHand.status)
        assertTrue(surrenderedHand.isCompleted)
        assertEquals(16, surrenderedHand.bestValue)
    }
    
    @Test
    fun `given hand with 21 when checking available actions then should only allow stand`() {
        // Given - Create hand with 21
        val cards = listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.ACE))
        val playerHand = PlayerHand.initial(cards, bet = 100)
        
        // When
        val actions = playerHand.availableActions(rules)
        
        // Then
        assertEquals(1, actions.size)
        assertTrue(actions.contains(Action.STAND))
        assertFalse(actions.contains(Action.HIT))
    }
    
    @Test
    fun `given two-card hand when checking available actions then should include basic actions`() {
        // Given
        val cards = listOf(Card(Suit.HEARTS, Rank.FIVE), Card(Suit.SPADES, Rank.SIX))
        val playerHand = PlayerHand.initial(cards, bet = 100)
        
        // When
        val actions = playerHand.availableActions(rules)
        
        // Then
        assertTrue(actions.contains(Action.HIT))
        assertTrue(actions.contains(Action.STAND))
        assertTrue(actions.contains(Action.DOUBLE))
    }
    
    @Test
    fun `given splittable pair when checking available actions then should include split`() {
        // Given
        val cards = listOf(Card(Suit.HEARTS, Rank.EIGHT), Card(Suit.SPADES, Rank.EIGHT))
        val playerHand = PlayerHand.initial(cards, bet = 100)
        
        // When
        val actions = playerHand.availableActions(rules)
        
        // Then
        assertTrue(actions.contains(Action.HIT))
        assertTrue(actions.contains(Action.STAND))
        assertTrue(actions.contains(Action.DOUBLE))
        assertTrue(actions.contains(Action.SPLIT))
    }
    
    @Test
    fun `given two-card hand with surrender allowed when checking actions then should include surrender`() {
        // Given
        val rulesWithSurrender = GameRules(surrenderAllowed = true)
        val cards = listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX))
        val playerHand = PlayerHand.initial(cards, bet = 100)
        
        // When
        val actions = playerHand.availableActions(rulesWithSurrender)
        
        // Then
        assertTrue(actions.contains(Action.SURRENDER))
        assertTrue(actions.contains(Action.HIT))
        assertTrue(actions.contains(Action.STAND))
        assertTrue(actions.contains(Action.DOUBLE))
    }
    
    @Test
    fun `given completed hand when checking available actions then should have no actions`() {
        // Given
        val cards = listOf(Card(Suit.HEARTS, Rank.NINE), Card(Suit.SPADES, Rank.SEVEN))
        val playerHand = PlayerHand.initial(cards, bet = 100).stand() // Standing hand
        
        // When
        val actions = playerHand.availableActions(rules)
        
        // Then
        assertTrue(actions.isEmpty())
    }
    
    @Test
    fun `given three-card hand when checking available actions then should not allow double or split`() {
        // Given - Simulate a hand that was hit (3 cards)
        val cards = listOf(
            Card(Suit.HEARTS, Rank.FIVE), 
            Card(Suit.SPADES, Rank.TWO),
            Card(Suit.CLUBS, Rank.THREE)
        )
        val playerHand = PlayerHand(cards, bet = 100, status = HandStatus.ACTIVE)
        
        // When
        val actions = playerHand.availableActions(rules)
        
        // Then
        assertTrue(actions.contains(Action.HIT))
        assertTrue(actions.contains(Action.STAND))
        assertFalse(actions.contains(Action.DOUBLE)) // More than 2 cards
        assertFalse(actions.contains(Action.SPLIT)) // More than 2 cards
        assertFalse(actions.contains(Action.SURRENDER)) // More than 2 cards
    }
    
    @Test
    fun `given split hand when checking double availability then should respect rules`() {
        // Given - Split hand with rules that don't allow double after split
        val rulesNoDoubleAfterSplit = GameRules(doubleAfterSplitAllowed = false)
        val cards = listOf(Card(Suit.HEARTS, Rank.EIGHT), Card(Suit.CLUBS, Rank.THREE))
        val playerHand = PlayerHand(cards, bet = 100, status = HandStatus.ACTIVE, isFromSplit = true)
        
        // When
        val actions = playerHand.availableActions(rulesNoDoubleAfterSplit)
        
        // Then
        assertFalse(actions.contains(Action.DOUBLE)) // Not allowed after split
        assertTrue(actions.contains(Action.HIT))
        assertTrue(actions.contains(Action.STAND))
    }
    
    @Test
    fun `given split hand when checking surrender availability then should not allow surrender`() {
        // Given - Split hand with surrender allowed
        val rulesWithSurrender = GameRules(surrenderAllowed = true)
        val cards = listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX))
        val playerHand = PlayerHand(cards, bet = 100, status = HandStatus.ACTIVE, isFromSplit = true)
        
        // When
        val actions = playerHand.availableActions(rulesWithSurrender)
        
        // Then
        assertFalse(actions.contains(Action.SURRENDER)) // Can't surrender split hands
        assertTrue(actions.contains(Action.HIT))
        assertTrue(actions.contains(Action.STAND))
    }
    
    @Test
    fun `given non-active hand when performing actions then should throw exception`() {
        // Given
        val cards = listOf(Card(Suit.HEARTS, Rank.NINE), Card(Suit.SPADES, Rank.SEVEN))
        val standingHand = PlayerHand(cards, bet = 100, status = HandStatus.STANDING)
        val testCard = Card(Suit.CLUBS, Rank.FIVE)
        
        // When/Then
        assertFailsWith<IllegalArgumentException> {
            standingHand.hit(testCard)
        }
        
        assertFailsWith<IllegalArgumentException> {
            standingHand.stand()
        }
        
        assertFailsWith<IllegalArgumentException> {
            standingHand.doubleDown(testCard)
        }
        
        assertFailsWith<IllegalArgumentException> {
            standingHand.surrender()
        }
    }
}