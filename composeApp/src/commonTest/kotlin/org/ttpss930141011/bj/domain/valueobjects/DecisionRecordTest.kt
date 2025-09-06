package org.ttpss930141011.bj.domain.valueobjects

import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.domain.valueobjects.Rank
import org.ttpss930141011.bj.domain.valueobjects.Suit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DecisionRecordTest {

    @Test
    fun `given hand and dealer card when creating DecisionRecord then should generate correct scenario key`() {
        // Given
        val handCards = listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX))
        val dealerUpCard = Card(Suit.CLUBS, Rank.KING)
        val playerAction = Action.HIT
        val isCorrect = false
        
        // When
        val decisionRecord = DecisionRecord(
            handCards = handCards,
            dealerUpCard = dealerUpCard,
            playerAction = playerAction,
            isCorrect = isCorrect
        )
        
        // Then
        assertEquals("Hard 16 vs K", decisionRecord.scenarioKey)
        assertEquals(playerAction, decisionRecord.playerAction)
        assertEquals(isCorrect, decisionRecord.isCorrect)
    }

    @Test
    fun `given soft hand when creating DecisionRecord then should generate soft scenario key`() {
        // Given
        val handCards = listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.SIX))
        val dealerUpCard = Card(Suit.CLUBS, Rank.NINE)
        val playerAction = Action.STAND
        val isCorrect = true
        
        // When
        val decisionRecord = DecisionRecord(
            handCards = handCards,
            dealerUpCard = dealerUpCard,
            playerAction = playerAction,
            isCorrect = isCorrect
        )
        
        // Then
        assertEquals("Soft 17 vs 9", decisionRecord.scenarioKey)
    }

    @Test
    fun `given pair when creating DecisionRecord then should generate pair scenario key`() {
        // Given
        val handCards = listOf(Card(Suit.HEARTS, Rank.EIGHT), Card(Suit.SPADES, Rank.EIGHT))
        val dealerUpCard = Card(Suit.CLUBS, Rank.FIVE)
        val playerAction = Action.SPLIT
        val isCorrect = true
        
        // When
        val decisionRecord = DecisionRecord(
            handCards = handCards,
            dealerUpCard = dealerUpCard,
            playerAction = playerAction,
            isCorrect = isCorrect
        )
        
        // Then
        assertEquals("Pair 8s vs 5", decisionRecord.scenarioKey)
    }

    @Test
    fun `given blackjack when creating DecisionRecord then should generate blackjack scenario key`() {
        // Given
        val handCards = listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.KING))
        val dealerUpCard = Card(Suit.CLUBS, Rank.SEVEN)
        val playerAction = Action.STAND
        val isCorrect = true
        
        // When
        val decisionRecord = DecisionRecord(
            handCards = handCards,
            dealerUpCard = dealerUpCard,
            playerAction = playerAction,
            isCorrect = isCorrect
        )
        
        // Then
        assertEquals("Blackjack vs 7", decisionRecord.scenarioKey)
    }

    @Test
    fun `given decision record when comparing timestamps then should be chronologically ordered`() {
        // Given
        val baseTime = 1000000L
        val laterTime = 1000001L
        
        val handCards = listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX))
        val dealerUpCard = Card(Suit.CLUBS, Rank.KING)
        
        // When
        val earlierDecision = DecisionRecord(
            handCards = handCards,
            dealerUpCard = dealerUpCard,
            playerAction = Action.HIT,
            isCorrect = false,
            timestamp = baseTime
        )
        
        val laterDecision = DecisionRecord(
            handCards = handCards,
            dealerUpCard = dealerUpCard,
            playerAction = Action.STAND,
            isCorrect = true,
            timestamp = laterTime
        )
        
        // Then
        assertTrue(earlierDecision.timestamp < laterDecision.timestamp)
    }

    @Test
    fun `given decision record when accessing properties then should return correct values`() {
        // Given
        val handCards = listOf(Card(Suit.HEARTS, Rank.QUEEN), Card(Suit.SPADES, Rank.TWO))
        val dealerUpCard = Card(Suit.CLUBS, Rank.ACE)
        val playerAction = Action.DOUBLE
        val isCorrect = false
        val timestamp = 123456789L
        
        // When
        val decisionRecord = DecisionRecord(
            handCards = handCards,
            dealerUpCard = dealerUpCard,
            playerAction = playerAction,
            isCorrect = isCorrect,
            timestamp = timestamp
        )
        
        // Then
        assertEquals(handCards, decisionRecord.handCards)
        assertEquals(dealerUpCard, decisionRecord.dealerUpCard)
        assertEquals(playerAction, decisionRecord.playerAction)
        assertEquals(isCorrect, decisionRecord.isCorrect)
        assertEquals(timestamp, decisionRecord.timestamp)
        assertEquals("Hard 12 vs A", decisionRecord.scenarioKey)
    }

    @Test
    fun `given three cards when creating DecisionRecord then should generate correct scenario key`() {
        // Given
        val handCards = listOf(
            Card(Suit.HEARTS, Rank.FIVE),
            Card(Suit.SPADES, Rank.THREE),
            Card(Suit.CLUBS, Rank.TWO)
        )
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.SIX)
        val playerAction = Action.STAND
        val isCorrect = true
        
        // When
        val decisionRecord = DecisionRecord(
            handCards = handCards,
            dealerUpCard = dealerUpCard,
            playerAction = playerAction,
            isCorrect = isCorrect
        )
        
        // Then
        assertEquals("Hard 10 vs 6", decisionRecord.scenarioKey)
    }

    @Test
    fun `given soft hand with multiple aces when creating DecisionRecord then should generate correct scenario key`() {
        // Given
        val handCards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.CLUBS, Rank.FIVE)
        )
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.TWO)
        val playerAction = Action.HIT
        val isCorrect = true
        
        // When
        val decisionRecord = DecisionRecord(
            handCards = handCards,
            dealerUpCard = dealerUpCard,
            playerAction = playerAction,
            isCorrect = isCorrect
        )
        
        // Then
        assertEquals("Soft 17 vs 2", decisionRecord.scenarioKey)
    }
}