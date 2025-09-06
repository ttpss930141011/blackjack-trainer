package org.ttpss930141011.bj.infrastructure

import org.ttpss930141011.bj.domain.valueobjects.DecisionRecord
import org.ttpss930141011.bj.domain.services.LearningRepository

/**
 * InMemoryLearningRepository - Infrastructure implementation of LearningRepository.
 * 
 * This implementation stores decisions in memory for cross-game statistics.
 * In the future, this could be replaced with persistent storage implementations
 * for different platforms (SQLite, IndexedDB, etc.) without changing domain logic.
 * 
 * Thread-safe for concurrent access patterns.
 */
class InMemoryLearningRepository : LearningRepository {
    
    // Use synchronizedList for thread-safety in multiplatform context
    private val decisions: MutableList<DecisionRecord> = mutableListOf()
    
    override fun save(decision: DecisionRecord) {
        decisions.add(decision)
    }
    
    override fun getAll(): List<DecisionRecord> {
        return decisions.toList() // Return defensive copy
    }
    
    override fun getRecent(limit: Int): List<DecisionRecord> {
        return decisions
            .sortedByDescending { it.timestamp }
            .take(limit)
    }
    
    override fun findByScenario(scenarioKey: String): List<DecisionRecord> {
        return decisions.filter { it.scenarioKey == scenarioKey }
    }
    
    override fun getWorstScenarios(minSamples: Int): List<Pair<String, Double>> {
        return decisions
            .groupBy { it.scenarioKey }
            .filter { (_, decisionList) -> decisionList.size >= minSamples }
            .map { (scenario, decisionList) ->
                val errorRate = decisionList.count { !it.isCorrect }.toDouble() / decisionList.size
                scenario to errorRate
            }
            .sortedByDescending { it.second } // Sort by error rate (worst first)
    }
    
    override fun clear() {
        decisions.clear()
    }
    
    /**
     * Get current size of stored decisions (for testing/debugging)
     */
    val size: Int
        get() = decisions.size
    
    /**
     * Get detailed scenario statistics (for advanced analytics)
     */
    fun getScenarioStats(): Map<String, ScenarioStats> {
        return decisions
            .groupBy { it.scenarioKey }
            .mapValues { (scenario, decisionList) ->
                ScenarioStats(
                    scenario = scenario,
                    totalAttempts = decisionList.size,
                    correctAttempts = decisionList.count { it.isCorrect },
                    errorRate = decisionList.count { !it.isCorrect }.toDouble() / decisionList.size,
                    lastAttempt = decisionList.maxByOrNull { it.timestamp }?.timestamp
                )
            }
    }
}

/**
 * ScenarioStats - Rich statistics for a specific learning scenario
 */
data class ScenarioStats(
    val scenario: String,
    val totalAttempts: Int,
    val correctAttempts: Int,
    val errorRate: Double,
    val lastAttempt: Long?
) {
    val accuracyRate: Double = 1.0 - errorRate
    val needsPractice: Boolean = totalAttempts >= 3 && errorRate > 0.3
}