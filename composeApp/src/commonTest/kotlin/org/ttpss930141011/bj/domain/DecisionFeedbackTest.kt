package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DecisionFeedbackTest {
    
    private val strategyEngine = StrategyEngine()
    private val rules = GameRules()
    
    @Test
    fun `given player 16 vs dealer 10 when making correct surrender decision then should return positive feedback`() {
        // Given
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)))
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.TEN)
        val playerAction = Action.SURRENDER
        
        // When
        val feedback = DecisionFeedback.evaluate(
            playerHand = playerHand,
            dealerUpCard = dealerUpCard,
            playerAction = playerAction,
            strategyEngine = strategyEngine,
            rules = rules
        )
        
        // Then
        assertTrue(feedback.isCorrect)
        assertEquals(Action.SURRENDER, feedback.optimalAction)
        assertEquals(Action.SURRENDER, feedback.playerAction)
        assertTrue(feedback.explanation.isNotEmpty())
    }
    
    @Test
    fun `given player 16 vs dealer 10 when surrender disabled and hitting then should return positive feedback`() {
        // Given - Test with surrender disabled
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)))
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.TEN)
        val playerAction = Action.HIT
        val noSurrenderRules = GameRules(surrenderAllowed = false)
        
        // When
        val feedback = DecisionFeedback.evaluate(
            playerHand = playerHand,
            dealerUpCard = dealerUpCard,
            playerAction = playerAction,
            strategyEngine = strategyEngine,
            rules = noSurrenderRules
        )
        
        // Then
        assertTrue(feedback.isCorrect)
        assertEquals(Action.HIT, feedback.optimalAction)
        assertEquals(Action.HIT, feedback.playerAction)
        assertTrue(feedback.explanation.isNotEmpty())
    }
    
    @Test
    fun `given player 17 vs dealer 10 when making wrong decision then should return negative feedback`() {
        // Given
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SEVEN)))
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.TEN)
        val playerAction = Action.HIT // 錯誤決策，應該停牌
        
        // When
        val feedback = DecisionFeedback.evaluate(
            playerHand = playerHand,
            dealerUpCard = dealerUpCard,
            playerAction = playerAction,
            strategyEngine = strategyEngine,
            rules = rules
        )
        
        // Then
        assertFalse(feedback.isCorrect)
        assertEquals(Action.STAND, feedback.optimalAction)
        assertEquals(Action.HIT, feedback.playerAction)
        assertTrue(feedback.explanation.contains("should STAND"))
    }
    
    @Test
    fun `given soft 17 vs dealer 6 when doubling then should explain strategy reasoning`() {
        // Given
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.SIX)))
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.SIX)
        val playerAction = Action.DOUBLE
        
        // When
        val feedback = DecisionFeedback.evaluate(
            playerHand = playerHand,
            dealerUpCard = dealerUpCard,
            playerAction = playerAction,
            strategyEngine = strategyEngine,
            rules = rules
        )
        
        // Then
        assertTrue(feedback.isCorrect)
        assertTrue(feedback.explanation.contains("soft"))
        assertTrue(feedback.explanation.contains("double"))
    }
}