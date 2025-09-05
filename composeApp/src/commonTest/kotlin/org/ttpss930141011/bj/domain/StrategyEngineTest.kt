package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import org.ttpss930141011.bj.domain.services.StrategyEngine
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.Action

class StrategyEngineTest {
    
    private val engine = StrategyEngine()
    private val rules = GameRules(surrenderAllowed = false) // No surrender by default
    private val rulesWithSurrender = GameRules(surrenderAllowed = true)
    
    // Test basic strategy for hard hands
    @Test
    fun `given hard 16 vs dealer 10 when getting recommendation then should hit`() {
        // Given - No surrender allowed, so should hit
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN)
        
        // When
        val action = engine.getOptimalAction(playerHand, dealerUpCard, rules)
        
        // Then
        assertEquals(Action.HIT, action)
    }
    
    @Test
    fun `given hard 16 vs dealer 6 when getting recommendation then should stand`() {
        // Given
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.SIX)
        
        // When
        val action = engine.recommend(playerHand, dealerUpCard)
        
        // Then
        assertEquals(Action.STAND, action)
    }
    
    @Test
    fun `given hard 11 vs dealer 6 when getting recommendation then should double`() {
        // Given
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.FIVE), Card(Suit.SPADES, Rank.SIX)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.SIX)
        
        // When
        val action = engine.recommend(playerHand, dealerUpCard)
        
        // Then
        assertEquals(Action.DOUBLE, action)
    }
    
    @Test
    fun `given hard 11 vs dealer ace when getting recommendation then should hit not double`() {
        // Given
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.FIVE), Card(Suit.SPADES, Rank.SIX)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.ACE)
        
        // When
        val action = engine.recommend(playerHand, dealerUpCard)
        
        // Then
        assertEquals(Action.HIT, action) // 11 vs Ace should hit, not double
    }
    
    // Test split strategy
    @Test
    fun `given pair of aces when getting recommendation then should split`() {
        // Given
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.ACE)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN) // Any dealer card
        
        // When
        val action = engine.recommend(playerHand, dealerUpCard)
        
        // Then
        assertEquals(Action.SPLIT, action) // Aces always split
    }
    
    @Test
    fun `given pair of eights when getting recommendation then should split`() {
        // Given
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.EIGHT), Card(Suit.SPADES, Rank.EIGHT)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN) // Even vs strong dealer card
        
        // When
        val action = engine.recommend(playerHand, dealerUpCard)
        
        // Then
        assertEquals(Action.SPLIT, action) // 8,8 always split
    }
    
    @Test
    fun `given pair of tens when getting recommendation then should stand not split`() {
        // Given
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.TEN)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.SIX) // Even vs weak dealer
        
        // When
        val action = engine.recommend(playerHand, dealerUpCard)
        
        // Then
        assertEquals(Action.STAND, action) // 10,10 never split
    }
    
    @Test
    fun `given pair of fives when getting recommendation then should double not split`() {
        // Given
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.FIVE), Card(Suit.SPADES, Rank.FIVE)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.SIX)
        
        // When
        val action = engine.recommend(playerHand, dealerUpCard)
        
        // Then
        assertEquals(Action.DOUBLE, action) // 5,5 = 10, treat as hard 10
    }
    
    @Test
    fun `given pair of nines vs dealer 7 when getting recommendation then should stand`() {
        // Given
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.NINE), Card(Suit.SPADES, Rank.NINE)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.SEVEN)
        
        // When
        val action = engine.recommend(playerHand, dealerUpCard)
        
        // Then
        assertEquals(Action.STAND, action) // 9,9 vs 7 stands
    }
    
    @Test
    fun `given pair of nines vs dealer 6 when getting recommendation then should split`() {
        // Given
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.NINE), Card(Suit.SPADES, Rank.NINE)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.SIX)
        
        // When
        val action = engine.recommend(playerHand, dealerUpCard)
        
        // Then
        assertEquals(Action.SPLIT, action) // 9,9 vs 6 splits
    }
    
    // Test soft hand strategy
    @Test
    fun `given soft 18 vs dealer 6 when getting recommendation then should double`() {
        // Given - A,7 = soft 18
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.SEVEN)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.SIX)
        
        // When
        val action = engine.recommend(playerHand, dealerUpCard)
        
        // Then
        assertEquals(Action.DOUBLE, action)
    }
    
    @Test
    fun `given soft 18 vs dealer 9 when getting recommendation then should hit`() {
        // Given - A,7 = soft 18
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.SEVEN)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.NINE)
        
        // When
        val action = engine.recommend(playerHand, dealerUpCard)
        
        // Then
        assertEquals(Action.HIT, action)
    }
    
    @Test
    fun `given soft 18 vs dealer 8 when getting recommendation then should stand`() {
        // Given - A,7 = soft 18
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.SEVEN)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.EIGHT)
        
        // When
        val action = engine.recommend(playerHand, dealerUpCard)
        
        // Then
        assertEquals(Action.STAND, action)
    }
    
    @Test
    fun `given soft 19 when getting recommendation then should stand`() {
        // Given - A,8 = soft 19
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.EIGHT)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN) // Any dealer card
        
        // When
        val action = engine.recommend(playerHand, dealerUpCard)
        
        // Then
        assertEquals(Action.STAND, action)
    }
    
    // Test surrender strategy
    @Test
    fun `given hard 16 vs dealer 10 with surrender allowed when getting recommendation then should surrender`() {
        // Given
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN)
        
        // When
        val action = engine.getOptimalAction(playerHand, dealerUpCard, rulesWithSurrender)
        
        // Then
        assertEquals(Action.SURRENDER, action)
    }
    
    @Test
    fun `given hard 15 vs dealer 10 with surrender allowed when getting recommendation then should surrender`() {
        // Given
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.FIVE)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN)
        
        // When
        val action = engine.getOptimalAction(playerHand, dealerUpCard, rulesWithSurrender)
        
        // Then
        assertEquals(Action.SURRENDER, action)
    }
    
    @Test
    fun `given hard 16 vs dealer 9 with surrender allowed when getting recommendation then should surrender`() {
        // Given
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.NINE)
        
        // When
        val action = engine.getOptimalAction(playerHand, dealerUpCard, rulesWithSurrender)
        
        // Then
        assertEquals(Action.SURRENDER, action)
    }
    
    @Test
    fun `given hard 16 vs dealer ace with surrender allowed when getting recommendation then should surrender`() {
        // Given
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.ACE)
        
        // When
        val action = engine.getOptimalAction(playerHand, dealerUpCard, rulesWithSurrender)
        
        // Then
        assertEquals(Action.SURRENDER, action)
    }
    
    @Test
    fun `given pair of eights with surrender allowed when getting recommendation then should split not surrender`() {
        // Given - Split takes priority over surrender
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.EIGHT), Card(Suit.SPADES, Rank.EIGHT)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN)
        
        // When
        val action = engine.getOptimalAction(playerHand, dealerUpCard, rulesWithSurrender)
        
        // Then
        assertEquals(Action.SPLIT, action) // Split has priority over surrender
    }
    
    @Test
    fun `given hard 14 vs dealer 10 with surrender allowed when getting recommendation then should hit not surrender`() {
        // Given - Only 15,16 surrender vs 10
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.FOUR)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN)
        
        // When
        val action = engine.getOptimalAction(playerHand, dealerUpCard, rulesWithSurrender)
        
        // Then
        assertEquals(Action.HIT, action)
    }
    
    // Test edge cases
    @Test
    fun `given blackjack when getting recommendation then should stand`() {
        // Given
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.KING)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN)
        
        // When
        val action = engine.recommend(playerHand, dealerUpCard)
        
        // Then
        assertEquals(Action.STAND, action)
    }
    
    @Test
    fun `given hard 5 when getting recommendation then should hit`() {
        // Given - Minimum possible hand
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TWO), Card(Suit.SPADES, Rank.THREE)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN)
        
        // When
        val action = engine.recommend(playerHand, dealerUpCard)
        
        // Then
        assertEquals(Action.HIT, action)
    }
    
    @Test
    fun `given three-card hand when getting recommendation then should not consider split or surrender`() {
        // Given - 3 cards, so split/surrender not possible
        val cards = listOf(
            Card(Suit.HEARTS, Rank.FIVE),
            Card(Suit.SPADES, Rank.FIVE), 
            Card(Suit.CLUBS, Rank.SIX)
        ) // 16 total
        val playerHand = Hand(cards)
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.TEN)
        
        // When
        val action = engine.getOptimalAction(playerHand, dealerUpCard, rulesWithSurrender)
        
        // Then
        assertEquals(Action.HIT, action) // Should hit, not consider surrender
    }
}