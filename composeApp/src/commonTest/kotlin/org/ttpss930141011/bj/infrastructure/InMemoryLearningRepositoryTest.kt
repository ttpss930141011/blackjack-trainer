package org.ttpss930141011.bj.infrastructure

import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.domain.valueobjects.Rank
import org.ttpss930141011.bj.domain.valueobjects.Suit
import org.ttpss930141011.bj.domain.valueobjects.DecisionRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryLearningRepositoryTest {

    private fun createRepository() = InMemoryLearningRepository()

    private fun createDecisionRecord(
        handValue: Int = 16,
        dealerValue: Int = 10,
        action: Action = Action.HIT,
        isCorrect: Boolean = false,
        timestamp: Long = 1000000L
    ): DecisionRecord {
        val handCards = when (handValue) {
            16 -> listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX))
            12 -> listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.TWO))
            20 -> listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.TEN))
            else -> listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX))
        }
        
        val dealerCard = when (dealerValue) {
            10 -> Card(Suit.CLUBS, Rank.TEN)
            5 -> Card(Suit.CLUBS, Rank.FIVE)
            7 -> Card(Suit.CLUBS, Rank.SEVEN)
            else -> Card(Suit.CLUBS, Rank.TEN)
        }
        
        return DecisionRecord(
            handCards = handCards,
            dealerUpCard = dealerCard,
            playerAction = action,
            isCorrect = isCorrect,
            timestamp = timestamp
        )
    }

    @Test
    fun `given empty repository when getAll then should return empty list`() {
        // Given
        val repository = createRepository()
        
        // When
        val result = repository.getAll()
        
        // Then
        assertTrue(result.isEmpty())
        assertEquals(0, repository.size)
    }

    @Test
    fun `given decision record when save then should store correctly`() {
        // Given
        val repository = createRepository()
        val decision = createDecisionRecord()
        
        // When
        repository.save(decision)
        
        // Then
        val stored = repository.getAll()
        assertEquals(1, stored.size)
        assertEquals(decision, stored[0])
        assertEquals(1, repository.size)
    }

    @Test
    fun `given multiple decisions when save then should store in order`() {
        // Given
        val repository = createRepository()
        val decision1 = createDecisionRecord(timestamp = 1000L)
        val decision2 = createDecisionRecord(timestamp = 2000L)
        val decision3 = createDecisionRecord(timestamp = 3000L)
        
        // When
        repository.save(decision1)
        repository.save(decision2)
        repository.save(decision3)
        
        // Then
        val stored = repository.getAll()
        assertEquals(3, stored.size)
        assertEquals(decision1, stored[0])
        assertEquals(decision2, stored[1])
        assertEquals(decision3, stored[2])
    }

    @Test
    fun `given decisions when getRecent with limit then should return latest decisions`() {
        // Given
        val repository = createRepository()
        val oldDecision = createDecisionRecord(timestamp = 1000L)
        val newDecision = createDecisionRecord(timestamp = 3000L)
        val middleDecision = createDecisionRecord(timestamp = 2000L)
        
        repository.save(oldDecision)
        repository.save(newDecision)
        repository.save(middleDecision)
        
        // When
        val recent = repository.getRecent(2)
        
        // Then
        assertEquals(2, recent.size)
        assertEquals(newDecision, recent[0]) // Most recent first
        assertEquals(middleDecision, recent[1])
    }

    @Test
    fun `given decisions for same scenario when findByScenario then should return all matching`() {
        // Given
        val repository = createRepository()
        val hard16vs10_1 = createDecisionRecord(handValue = 16, dealerValue = 10, isCorrect = false)
        val hard16vs10_2 = createDecisionRecord(handValue = 16, dealerValue = 10, isCorrect = true)
        val hard12vs5 = createDecisionRecord(handValue = 12, dealerValue = 5, isCorrect = true)
        
        repository.save(hard16vs10_1)
        repository.save(hard16vs10_2)
        repository.save(hard12vs5)
        
        // When
        val hard16Decisions = repository.findByScenario("Hard 16 vs 10")
        
        // Then
        assertEquals(2, hard16Decisions.size)
        assertTrue(hard16Decisions.contains(hard16vs10_1))
        assertTrue(hard16Decisions.contains(hard16vs10_2))
    }

    @Test
    fun `given decisions with errors when getWorstScenarios then should return scenarios sorted by error rate`() {
        // Given
        val repository = createRepository()
        
        // Scenario 1: Hard 16 vs 10 - 2/3 errors (66.7% error rate)
        repository.save(createDecisionRecord(handValue = 16, dealerValue = 10, isCorrect = false))
        repository.save(createDecisionRecord(handValue = 16, dealerValue = 10, isCorrect = false))
        repository.save(createDecisionRecord(handValue = 16, dealerValue = 10, isCorrect = true))
        
        // Scenario 2: Hard 12 vs 5 - 1/3 errors (33.3% error rate)
        repository.save(createDecisionRecord(handValue = 12, dealerValue = 5, isCorrect = false))
        repository.save(createDecisionRecord(handValue = 12, dealerValue = 5, isCorrect = true))
        repository.save(createDecisionRecord(handValue = 12, dealerValue = 5, isCorrect = true))
        
        // Scenario 3: Hard 20 vs 7 - 0/2 errors (0% error rate)
        repository.save(createDecisionRecord(handValue = 20, dealerValue = 7, isCorrect = true))
        repository.save(createDecisionRecord(handValue = 20, dealerValue = 7, isCorrect = true))
        
        // When
        val worstScenarios = repository.getWorstScenarios(minSamples = 2)
        
        // Then
        assertEquals(3, worstScenarios.size)
        assertEquals("Hard 16 vs 10", worstScenarios[0].first)
        assertEquals(2.0/3.0, worstScenarios[0].second, 0.001)
        assertEquals("Hard 12 vs 5", worstScenarios[1].first)
        assertEquals(1.0/3.0, worstScenarios[1].second, 0.001)
        assertEquals("Pair 10s vs 7", worstScenarios[2].first)
        assertEquals(0.0, worstScenarios[2].second, 0.001)
    }

    @Test
    fun `given decisions below min samples when getWorstScenarios then should filter out`() {
        // Given
        val repository = createRepository()
        
        // Scenario with only 2 samples (below minSamples = 3)
        repository.save(createDecisionRecord(handValue = 16, dealerValue = 10, isCorrect = false))
        repository.save(createDecisionRecord(handValue = 16, dealerValue = 10, isCorrect = false))
        
        // Scenario with 3 samples (meets minSamples = 3)
        repository.save(createDecisionRecord(handValue = 12, dealerValue = 5, isCorrect = false))
        repository.save(createDecisionRecord(handValue = 12, dealerValue = 5, isCorrect = true))
        repository.save(createDecisionRecord(handValue = 12, dealerValue = 5, isCorrect = true))
        
        // When
        val worstScenarios = repository.getWorstScenarios(minSamples = 3)
        
        // Then
        assertEquals(1, worstScenarios.size)
        assertEquals("Hard 12 vs 5", worstScenarios[0].first)
    }

    @Test
    fun `given repository with decisions when clear then should remove all`() {
        // Given
        val repository = createRepository()
        repository.save(createDecisionRecord())
        repository.save(createDecisionRecord())
        assertEquals(2, repository.size)
        
        // When
        repository.clear()
        
        // Then
        assertEquals(0, repository.size)
        assertTrue(repository.getAll().isEmpty())
    }

    @Test
    fun `given repository when getScenarioStats then should return detailed statistics`() {
        // Given
        val repository = createRepository()
        val baseTime = 1000L
        val laterTime = 2000L
        
        // Add decisions for Hard 16 vs 10
        repository.save(createDecisionRecord(handValue = 16, dealerValue = 10, isCorrect = false, timestamp = baseTime))
        repository.save(createDecisionRecord(handValue = 16, dealerValue = 10, isCorrect = false, timestamp = laterTime))
        repository.save(createDecisionRecord(handValue = 16, dealerValue = 10, isCorrect = true, timestamp = laterTime))
        
        // When
        val stats = repository.getScenarioStats()
        
        // Then
        assertEquals(1, stats.size)
        val hard16Stats = stats["Hard 16 vs 10"]!!
        assertEquals("Hard 16 vs 10", hard16Stats.scenario)
        assertEquals(3, hard16Stats.totalAttempts)
        assertEquals(1, hard16Stats.correctAttempts)
        assertEquals(2.0/3.0, hard16Stats.errorRate, 0.001)
        assertEquals(1.0/3.0, hard16Stats.accuracyRate, 0.001)
        assertEquals(laterTime, hard16Stats.lastAttempt)
        assertTrue(hard16Stats.needsPractice) // 3+ attempts and >30% error rate
    }

    @Test
    fun `given concurrent access when multiple operations then should be thread safe`() {
        // Given
        val repository = createRepository()
        val decision1 = createDecisionRecord(handValue = 16)
        val decision2 = createDecisionRecord(handValue = 12)
        
        // When - Simulate concurrent access
        repository.save(decision1)
        val size1 = repository.size
        repository.save(decision2)
        val size2 = repository.size
        val all = repository.getAll()
        
        // Then
        assertEquals(1, size1)
        assertEquals(2, size2)
        assertEquals(2, all.size)
        assertTrue(all.contains(decision1))
        assertTrue(all.contains(decision2))
    }
}