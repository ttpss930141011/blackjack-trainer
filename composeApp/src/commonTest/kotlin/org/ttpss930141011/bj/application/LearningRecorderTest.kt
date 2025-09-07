package org.ttpss930141011.bj.application

import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.domain.valueobjects.Rank
import org.ttpss930141011.bj.domain.valueobjects.Suit
import org.ttpss930141011.bj.domain.valueobjects.PlayerHand
import org.ttpss930141011.bj.domain.services.LearningRepository
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.infrastructure.InMemoryLearningRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class LearningRecorderTest {

    private fun createRecorder(): LearningRecorder {
        return LearningRecorder(InMemoryLearningRepository())
    }

    @Test
    fun `given player decision when recordDecision then should save to repository`() {
        // Given
        val recorder = createRecorder()
        val playerHand = PlayerHand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)), bet = 50)
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN)
        val playerAction = Action.HIT
        val isCorrect = true
        
        // When
        val decisionRecord = recorder.recordDecision(playerHand, dealerUpCard, playerAction, isCorrect, GameRules())
        
        // Then
        assertEquals(playerHand.cards, decisionRecord.handCards)
        assertEquals(dealerUpCard, decisionRecord.dealerUpCard)
        assertEquals(playerAction, decisionRecord.playerAction)
        assertEquals(isCorrect, decisionRecord.isCorrect)
        
        // Verify it was saved
        val allDecisions = recorder.getAllDecisions()
        assertEquals(1, allDecisions.size)
        assertEquals(decisionRecord, allDecisions[0])
    }

    @Test
    fun `given multiple decisions when recordDecision then should store all`() {
        // Given
        val recorder = createRecorder()
        val playerHand1 = PlayerHand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)), bet = 50)
        val playerHand2 = PlayerHand(listOf(Card(Suit.HEARTS, Rank.FIVE), Card(Suit.SPADES, Rank.SEVEN)), bet = 50)
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN)
        
        // When
        recorder.recordDecision(playerHand1, dealerUpCard, Action.HIT, true, GameRules())
        recorder.recordDecision(playerHand2, dealerUpCard, Action.STAND, false, GameRules())
        
        // Then
        val allDecisions = recorder.getAllDecisions()
        assertEquals(2, allDecisions.size)
    }

    @Test
    fun `given valid game state when recordDecision with game then should create decision record`() {
        // Given
        val recorder = createRecorder()
        val game = createGameWithCurrentHand()
        val playerAction = Action.HIT
        val isCorrect = true
        
        // When
        val decisionRecord = recorder.recordDecision(game, playerAction, isCorrect, GameRules())
        
        // Then
        assertEquals(game.currentHand!!.cards, decisionRecord.handCards)
        assertEquals(game.dealer.upCard, decisionRecord.dealerUpCard)
        assertEquals(playerAction, decisionRecord.playerAction)
        assertEquals(isCorrect, decisionRecord.isCorrect)
    }

    @Test
    fun `given game without current hand when recordDecision then should throw exception`() {
        // Given
        val recorder = createRecorder()
        val game = createGameWithoutCurrentHand()
        val playerAction = Action.HIT
        val isCorrect = true
        
        // When/Then
        assertFailsWith<IllegalArgumentException> {
            recorder.recordDecision(game, playerAction, isCorrect, GameRules())
        }
    }

    @Test
    fun `given game without dealer up card when recordDecision then should throw exception`() {
        // Given
        val recorder = createRecorder()
        val game = createGameWithoutDealerUpCard()
        val playerAction = Action.HIT
        val isCorrect = true
        
        // When/Then
        assertFailsWith<IllegalArgumentException> {
            recorder.recordDecision(game, playerAction, isCorrect, GameRules())
        }
    }

    @Test
    fun `given decisions in repository when getWorstScenarios then should return sorted by error rate`() {
        // Given
        val repository = InMemoryLearningRepository()
        val recorder = LearningRecorder(repository)
        
        // Add multiple decisions for different scenarios
        val playerHand16 = PlayerHand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)), bet = 50)
        val playerHand12 = PlayerHand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.TWO)), bet = 50)
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN)
        
        // Scenario 1: Hard 16 vs 10 - 2/3 errors
        recorder.recordDecision(playerHand16, dealerUpCard, Action.STAND, false, GameRules())
        recorder.recordDecision(playerHand16, dealerUpCard, Action.STAND, false, GameRules())
        recorder.recordDecision(playerHand16, dealerUpCard, Action.HIT, true, GameRules())
        
        // Scenario 2: Hard 12 vs 10 - 1/3 errors
        recorder.recordDecision(playerHand12, dealerUpCard, Action.STAND, false, GameRules())
        recorder.recordDecision(playerHand12, dealerUpCard, Action.HIT, true, GameRules())
        recorder.recordDecision(playerHand12, dealerUpCard, Action.HIT, true, GameRules())
        
        // When
        val worstScenarios = recorder.getWorstScenarios(minSamples = 3)
        
        // Then
        assertEquals(2, worstScenarios.size)
        assertEquals("H16 vs 10", worstScenarios[0].baseScenarioKey)
        assertEquals(2.0/3.0, worstScenarios[0].errorRate, 0.001)
        assertEquals("H12 vs 10", worstScenarios[1].baseScenarioKey)
        assertEquals(1.0/3.0, worstScenarios[1].errorRate, 0.001)
    }

    @Test
    fun `given decisions in repository when getRecentDecisions then should return limited by count`() {
        // Given
        val recorder = createRecorder()
        val playerHand = PlayerHand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)), bet = 50)
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN)
        
        // Add 5 decisions
        repeat(5) {
            recorder.recordDecision(playerHand, dealerUpCard, Action.HIT, true, GameRules())
        }
        
        // When
        val recent = recorder.getRecentDecisions(3)
        
        // Then
        assertEquals(3, recent.size)
        assertTrue(recent.all { it.baseScenarioKey == "H16 vs 10" })
    }

    @Test
    fun `given empty repository when getRecentDecisions then should return empty list`() {
        // Given
        val recorder = createRecorder()
        
        // When
        val recent = recorder.getRecentDecisions(10)
        
        // Then
        assertTrue(recent.isEmpty())
    }

    @Test
    fun `given repository when clearAllData then should remove all decisions`() {
        // Given
        val recorder = createRecorder()
        val playerHand = PlayerHand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)), bet = 50)
        val dealerUpCard = Card(Suit.CLUBS, Rank.TEN)
        
        recorder.recordDecision(playerHand, dealerUpCard, Action.HIT, true, GameRules())
        recorder.recordDecision(playerHand, dealerUpCard, Action.STAND, false, GameRules())
        assertEquals(2, recorder.getAllDecisions().size)
        
        // When
        recorder.clearAllData()
        
        // Then
        assertTrue(recorder.getAllDecisions().isEmpty())
    }

    // Helper methods to create game states for testing
    private fun createGameWithCurrentHand(): Game {
        val playerHand = PlayerHand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)), bet = 50)
        val dealer = Dealer().dealInitialCard(Card(Suit.CLUBS, Rank.TEN))
        
        return Game(
            player = Player("Test", 1000),
            playerHands = listOf(playerHand),
            currentHandIndex = 0,
            pendingBet = 0,
            currentBet = 50,
            dealer = dealer,
            deck = Deck.shuffled(),
            rules = GameRules()
        )
    }
    
    private fun createGameWithoutCurrentHand(): Game {
        val dealer = Dealer().dealInitialCard(Card(Suit.CLUBS, Rank.TEN))
        
        return Game(
            player = Player("Test", 1000),
            playerHands = emptyList(),
            currentHandIndex = 0,
            pendingBet = 0,
            currentBet = 50,
            dealer = dealer,
            deck = Deck.shuffled(),
            rules = GameRules()
        )
    }
    
    private fun createGameWithoutDealerUpCard(): Game {
        val playerHand = PlayerHand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)), bet = 50)
        val dealer = Dealer() // No cards dealt to dealer
        
        return Game(
            player = Player("Test", 1000),
            playerHands = listOf(playerHand),
            currentHandIndex = 0,
            pendingBet = 0,
            currentBet = 50,
            dealer = dealer,
            deck = Deck.shuffled(),
            rules = GameRules()
        )
    }
}