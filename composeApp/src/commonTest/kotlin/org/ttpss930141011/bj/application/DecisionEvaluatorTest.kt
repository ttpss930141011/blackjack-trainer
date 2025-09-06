package org.ttpss930141011.bj.application

import org.ttpss930141011.bj.domain.valueobjects.PlayerHand
import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.valueobjects.Hand
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.domain.valueobjects.Rank
import org.ttpss930141011.bj.domain.valueobjects.Suit
import org.ttpss930141011.bj.domain.valueobjects.GameRules
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DecisionEvaluatorTest {

    private val evaluator = DecisionEvaluator()
    private val standardRules = GameRules(surrenderAllowed = false)

    @Test
    fun `given player hand 16 vs dealer 10 when hit then should evaluate as correct`() {
        // Given
        val playerHand = PlayerHand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)), bet = 50)
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN)
        val playerAction = Action.HIT
        
        // When
        val feedback = evaluator.evaluateDecision(playerHand, dealerUpCard, playerAction, standardRules)
        
        // Then
        assertTrue(feedback.isCorrect)
        assertEquals(Action.HIT, feedback.optimalAction)
        assertEquals(playerAction, feedback.playerAction)
    }

    @Test
    fun `given player hand 16 vs dealer 10 when stand then should evaluate as incorrect`() {
        // Given
        val playerHand = PlayerHand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)), bet = 50)
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN)
        val playerAction = Action.STAND
        
        // When
        val feedback = evaluator.evaluateDecision(playerHand, dealerUpCard, playerAction, standardRules)
        
        // Then
        assertFalse(feedback.isCorrect)
        assertEquals(Action.HIT, feedback.optimalAction)
        assertEquals(playerAction, feedback.playerAction)
    }

    @Test
    fun `given player hand 20 vs dealer 6 when stand then should evaluate as correct`() {
        // Given
        val playerHand = PlayerHand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.TEN)), bet = 50)
        val dealerUpCard = Card(Suit.CLUBS, Rank.SIX)
        val playerAction = Action.STAND
        
        // When
        val feedback = evaluator.evaluateDecision(playerHand, dealerUpCard, playerAction, standardRules)
        
        // Then
        assertTrue(feedback.isCorrect)
        assertEquals(Action.STAND, feedback.optimalAction)
        assertEquals(playerAction, feedback.playerAction)
    }

    @Test
    fun `given soft 17 vs dealer 3 when double then should evaluate as correct`() {
        // Given
        val playerHand = PlayerHand(listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.SIX)), bet = 50)
        val dealerUpCard = Card(Suit.CLUBS, Rank.THREE)
        val playerAction = Action.DOUBLE
        
        // When
        val feedback = evaluator.evaluateDecision(playerHand, dealerUpCard, playerAction, standardRules)
        
        // Then
        assertTrue(feedback.isCorrect)
        assertEquals(Action.DOUBLE, feedback.optimalAction)
    }

    @Test
    fun `given pair 8s vs dealer 6 when split then should evaluate as correct`() {
        // Given
        val playerHand = PlayerHand(listOf(Card(Suit.HEARTS, Rank.EIGHT), Card(Suit.SPADES, Rank.EIGHT)), bet = 50)
        val dealerUpCard = Card(Suit.CLUBS, Rank.SIX)
        val playerAction = Action.SPLIT
        
        // When
        val feedback = evaluator.evaluateDecision(playerHand, dealerUpCard, playerAction, standardRules)
        
        // Then
        assertTrue(feedback.isCorrect)
        assertEquals(Action.SPLIT, feedback.optimalAction)
    }

    @Test
    fun `given hard 11 vs dealer 5 when double then should evaluate as correct`() {
        // Given
        val playerHand = PlayerHand(listOf(Card(Suit.HEARTS, Rank.FIVE), Card(Suit.SPADES, Rank.SIX)), bet = 50)
        val dealerUpCard = Card(Suit.CLUBS, Rank.FIVE)
        val playerAction = Action.DOUBLE
        
        // When
        val feedback = evaluator.evaluateDecision(playerHand, dealerUpCard, playerAction, standardRules)
        
        // Then
        assertTrue(feedback.isCorrect)
        assertEquals(Action.DOUBLE, feedback.optimalAction)
    }

    @Test
    fun `given optimal action method with Hand when called then should return correct action`() {
        // Given
        val hand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)))
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN)
        
        // When
        val optimalAction = evaluator.getOptimalAction(hand, dealerUpCard, standardRules)
        
        // Then
        assertEquals(Action.HIT, optimalAction)
    }

    @Test
    fun `given optimal action method with PlayerHand when called then should return correct action`() {
        // Given
        val playerHand = PlayerHand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)), bet = 50)
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN)
        
        // When
        val optimalAction = evaluator.getOptimalAction(playerHand, dealerUpCard, standardRules)
        
        // Then
        assertEquals(Action.HIT, optimalAction)
    }

    @Test
    fun `given blackjack vs dealer 7 when stand then should evaluate as correct`() {
        // Given
        val playerHand = PlayerHand(listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.KING)), bet = 50)
        val dealerUpCard = Card(Suit.CLUBS, Rank.SEVEN)
        val playerAction = Action.STAND
        
        // When
        val feedback = evaluator.evaluateDecision(playerHand, dealerUpCard, playerAction, standardRules)
        
        // Then
        assertTrue(feedback.isCorrect)
        assertEquals(Action.STAND, feedback.optimalAction)
    }

    @Test
    fun `given multiple card hand when evaluating then should work correctly`() {
        // Given - Hand with 3 cards: 5+3+2 = 10
        val playerHand = PlayerHand(listOf(
            Card(Suit.HEARTS, Rank.FIVE),
            Card(Suit.SPADES, Rank.THREE),
            Card(Suit.CLUBS, Rank.TWO)
        ), bet = 50)
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.SIX)
        val playerAction = Action.DOUBLE // Should be incorrect with 3 cards
        
        // When
        val feedback = evaluator.evaluateDecision(playerHand, dealerUpCard, playerAction, standardRules)
        
        // Then
        assertFalse(feedback.isCorrect) // Can't double with 3 cards
        assertEquals(Action.HIT, feedback.optimalAction) // Should hit hard 10 vs 6
    }
}