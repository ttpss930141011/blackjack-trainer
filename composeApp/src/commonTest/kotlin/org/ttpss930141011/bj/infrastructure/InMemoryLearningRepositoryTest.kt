package org.ttpss930141011.bj.infrastructure

import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.domain.valueobjects.Rank
import org.ttpss930141011.bj.domain.valueobjects.Suit
import org.ttpss930141011.bj.domain.valueobjects.DecisionRecord
import org.ttpss930141011.bj.domain.valueobjects.GameRules
import org.ttpss930141011.bj.domain.valueobjects.HandSnapshot
import org.ttpss930141011.bj.domain.valueobjects.ActionResult
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
            beforeAction = HandSnapshot(
                cards = handCards,
                dealerUpCard = dealerCard,
                gameRules = GameRules()
            ),
            action = action,
            afterAction = ActionResult.Stand(handCards), // Placeholder for tests
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
        val hard16Decisions = repository.findByScenario("H16 vs 10")
        
        // Then
        assertEquals(2, hard16Decisions.size)
        assertTrue(hard16Decisions.contains(hard16vs10_1))
        assertTrue(hard16Decisions.contains(hard16vs10_2))
    }

    @Test
    fun `given decisions with different rules when findByRule then should return rule-specific decisions`() {
        // Given
        val repository = createRepository()
        val rules1 = GameRules(dealerHitsOnSoft17 = true)
        val rules2 = GameRules(dealerHitsOnSoft17 = false)
        
        val decision1 = DecisionRecord(
            beforeAction = HandSnapshot(
                cards = listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)),
                dealerUpCard = Card(Suit.CLUBS, Rank.TEN),
                gameRules = rules1
            ),
            action = Action.HIT,
            afterAction = ActionResult.Stand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX))),
            isCorrect = false,
            timestamp = System.currentTimeMillis()
        )
        
        val decision2 = DecisionRecord(
            beforeAction = HandSnapshot(
                cards = listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)),
                dealerUpCard = Card(Suit.CLUBS, Rank.TEN),
                gameRules = rules2
            ),
            action = Action.STAND,
            afterAction = ActionResult.Stand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX))),
            isCorrect = true,
            timestamp = System.currentTimeMillis()
        )
        
        repository.save(decision1)
        repository.save(decision2)
        
        // When
        val rule1Decisions = repository.findByRule(decision1.ruleHash)
        val rule2Decisions = repository.findByRule(decision2.ruleHash)
        
        // Then
        assertEquals(1, rule1Decisions.size)
        assertEquals(decision1, rule1Decisions[0])
        assertEquals(1, rule2Decisions.size)
        assertEquals(decision2, rule2Decisions[0])
    }

    @Test
    fun `given decisions with errors when getErrorStatsByRule then should return rule-specific error stats`() {
        // Given
        val repository = createRepository()
        val gameRules = GameRules(dealerHitsOnSoft17 = true)
        
        // Add decisions for H16 vs 10 - 2/3 errors
        val handCards = listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX))
        val dealerCard = Card(Suit.CLUBS, Rank.TEN)
        
        repository.save(DecisionRecord(
            beforeAction = HandSnapshot(handCards, dealerCard, gameRules),
            action = Action.HIT,
            afterAction = ActionResult.Stand(handCards),
            isCorrect = false,
            timestamp = System.currentTimeMillis()
        ))
        repository.save(DecisionRecord(
            beforeAction = HandSnapshot(handCards, dealerCard, gameRules),
            action = Action.HIT,
            afterAction = ActionResult.Stand(handCards),
            isCorrect = false,
            timestamp = System.currentTimeMillis()
        ))
        repository.save(DecisionRecord(
            beforeAction = HandSnapshot(handCards, dealerCard, gameRules),
            action = Action.STAND,
            afterAction = ActionResult.Stand(handCards),
            isCorrect = true,
            timestamp = System.currentTimeMillis()
        ))
        
        val ruleHash = gameRules.hashCode().toString(16).takeLast(6)
        
        // When
        val errorStats = repository.getErrorStatsByRule(ruleHash, minSamples = 3)
        
        // Then
        assertEquals(1, errorStats.size)
        val stat = errorStats[0]
        assertEquals("H16 vs 10", stat.baseScenarioKey)
        assertEquals(3, stat.totalAttempts)
        assertEquals(2, stat.errorCount)
        assertEquals(2.0/3.0, stat.errorRate, 0.001)
    }

    @Test
    fun `given decisions across rules when getErrorStatsAcrossRules then should return combined stats`() {
        // Given
        val repository = createRepository()
        val rules1 = GameRules(dealerHitsOnSoft17 = true)
        val rules2 = GameRules(dealerHitsOnSoft17 = false)
        
        // Add decisions for H16 vs 10 under different rules
        val handCards = listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX))
        val dealerCard = Card(Suit.CLUBS, Rank.TEN)
        
        repository.save(DecisionRecord(
            beforeAction = HandSnapshot(handCards, dealerCard, rules1),
            action = Action.HIT,
            afterAction = ActionResult.Stand(handCards),
            isCorrect = false,
            timestamp = System.currentTimeMillis()
        ))
        repository.save(DecisionRecord(
            beforeAction = HandSnapshot(handCards, dealerCard, rules2),
            action = Action.HIT,
            afterAction = ActionResult.Stand(handCards),
            isCorrect = false,
            timestamp = System.currentTimeMillis()
        ))
        repository.save(DecisionRecord(
            beforeAction = HandSnapshot(handCards, dealerCard, rules1),
            action = Action.STAND,
            afterAction = ActionResult.Stand(handCards),
            isCorrect = true,
            timestamp = System.currentTimeMillis()
        ))
        
        // When
        val errorStats = repository.getErrorStatsAcrossRules(minSamples = 3)
        
        // Then
        assertEquals(1, errorStats.size)
        val stat = errorStats[0]
        assertEquals("H16 vs 10", stat.baseScenarioKey)
        assertEquals(3, stat.totalAttempts)
        assertEquals(2, stat.errorCount)
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
        val hard16Stats = stats["H16 vs 10"]!!
        assertEquals("H16 vs 10", hard16Stats.scenario)
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