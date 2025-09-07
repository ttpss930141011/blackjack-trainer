package org.ttpss930141011.bj.domain.valueobjects

import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.domain.valueobjects.Rank
import org.ttpss930141011.bj.domain.valueobjects.Suit
import org.ttpss930141011.bj.domain.valueobjects.GameRules
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
            isCorrect = isCorrect,
            gameRules = GameRules()
        )
        
        // Then
        assertEquals("H16 vs K", decisionRecord.baseScenarioKey)
        assertTrue(decisionRecord.scenarioKey.startsWith("H16 vs K ["))
        assertTrue(decisionRecord.scenarioKey.endsWith("]"))
        assertEquals(6, decisionRecord.ruleHash.length) // Should be 6-char hex
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
            isCorrect = isCorrect,
            gameRules = GameRules()
        )
        
        // Then
        assertEquals("S17 vs 9", decisionRecord.baseScenarioKey)
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
            isCorrect = isCorrect,
            gameRules = GameRules()
        )
        
        // Then
        assertEquals("Pair 8s vs 5", decisionRecord.baseScenarioKey)
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
            isCorrect = isCorrect,
            gameRules = GameRules()
        )
        
        // Then
        assertEquals("BJ vs 7", decisionRecord.baseScenarioKey)
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
            gameRules = GameRules(),
            timestamp = baseTime
        )
        
        val laterDecision = DecisionRecord(
            handCards = handCards,
            dealerUpCard = dealerUpCard,
            playerAction = Action.STAND,
            isCorrect = true,
            gameRules = GameRules(),
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
            gameRules = GameRules(),
            timestamp = timestamp
        )
        
        // Then
        assertEquals(handCards, decisionRecord.handCards)
        assertEquals(dealerUpCard, decisionRecord.dealerUpCard)
        assertEquals(playerAction, decisionRecord.playerAction)
        assertEquals(isCorrect, decisionRecord.isCorrect)
        assertEquals(timestamp, decisionRecord.timestamp)
        assertEquals("H12 vs A", decisionRecord.baseScenarioKey)
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
            isCorrect = isCorrect,
            gameRules = GameRules()
        )
        
        // Then
        assertEquals("H10 vs 6", decisionRecord.baseScenarioKey)
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
            isCorrect = isCorrect,
            gameRules = GameRules()
        )
        
        // Then
        assertEquals("S17 vs 2", decisionRecord.baseScenarioKey)
    }

    @Test
    fun `given two decisions with same rules when comparing rules then should be same`() {
        // Given
        val gameRules = GameRules(dealerHitsOnSoft17 = true, doubleAfterSplitAllowed = false)
        val handCards = listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX))
        val dealerUpCard = Card(Suit.CLUBS, Rank.KING)
        
        val decision1 = DecisionRecord(
            handCards = handCards,
            dealerUpCard = dealerUpCard,
            playerAction = Action.HIT,
            isCorrect = true,
            gameRules = gameRules
        )
        
        val decision2 = DecisionRecord(
            handCards = handCards,
            dealerUpCard = dealerUpCard,
            playerAction = Action.STAND,
            isCorrect = false,
            gameRules = gameRules
        )
        
        // When & Then
        assertTrue(decision1.hasSameRules(decision2))
        assertTrue(decision2.hasSameRules(decision1))
    }

    @Test
    fun `given two decisions with different rules when comparing rules then should not be same`() {
        // Given
        val rules1 = GameRules(dealerHitsOnSoft17 = true)
        val rules2 = GameRules(dealerHitsOnSoft17 = false)
        val handCards = listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX))
        val dealerUpCard = Card(Suit.CLUBS, Rank.KING)
        
        val decision1 = DecisionRecord(
            handCards = handCards,
            dealerUpCard = dealerUpCard,
            playerAction = Action.HIT,
            isCorrect = true,
            gameRules = rules1
        )
        
        val decision2 = DecisionRecord(
            handCards = handCards,
            dealerUpCard = dealerUpCard,
            playerAction = Action.STAND,
            isCorrect = false,
            gameRules = rules2
        )
        
        // When & Then
        kotlin.test.assertFalse(decision1.hasSameRules(decision2))
        kotlin.test.assertFalse(decision2.hasSameRules(decision1))
    }

    @Test
    fun `given two decisions with same base scenario when comparing scenarios then should be same`() {
        // Given
        val handCards = listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX))
        val dealerUpCard = Card(Suit.CLUBS, Rank.KING)
        
        val decision1 = DecisionRecord(
            handCards = handCards,
            dealerUpCard = dealerUpCard,
            playerAction = Action.HIT,
            isCorrect = true,
            gameRules = GameRules(dealerHitsOnSoft17 = true)
        )
        
        val decision2 = DecisionRecord(
            handCards = handCards,
            dealerUpCard = dealerUpCard,
            playerAction = Action.STAND,
            isCorrect = false,
            gameRules = GameRules(dealerHitsOnSoft17 = false) // Different rules, same scenario
        )
        
        // When & Then
        assertTrue(decision1.hasSameBaseScenario(decision2))
        assertTrue(decision2.hasSameBaseScenario(decision1))
    }

    @Test
    fun `given empty hand cards when creating DecisionRecord then should throw exception`() {
        // Given & When & Then
        kotlin.test.assertFailsWith<IllegalArgumentException> {
            DecisionRecord(
                handCards = emptyList(),
                dealerUpCard = Card(Suit.CLUBS, Rank.KING),
                playerAction = Action.HIT,
                isCorrect = true,
                gameRules = GameRules()
            )
        }
    }
}