package org.ttpss930141011.bj.infrastructure

import org.ttpss930141011.bj.domain.valueobjects.DecisionRecord
import org.ttpss930141011.bj.domain.valueobjects.ScenarioErrorStat
import org.ttpss930141011.bj.domain.services.LearningRepository

/**
 * InMemoryLearningRepository - Infrastructure implementation of LearningRepository.
 * 
 * Simplified implementation focused on user core needs:
 * 1. Current rule decisions tracking
 * 2. Cross-rule error statistics comparison
 * 
 * This implementation stores decisions in memory for cross-game statistics.
 * In the future, this could be replaced with persistent storage implementations
 * for different platforms (SQLite, IndexedDB, etc.) without changing domain logic.
 */
class InMemoryLearningRepository : LearningRepository {
    
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
    
    override fun findByRule(ruleHash: String): List<DecisionRecord> {
        return decisions.filter { it.ruleHash == ruleHash }
    }
    
    override fun findByScenario(baseScenarioKey: String): List<DecisionRecord> {
        return decisions.filter { it.baseScenarioKey == baseScenarioKey }
    }
    
    override fun getErrorStatsByRule(ruleHash: String, minSamples: Int): List<ScenarioErrorStat> {
        return decisions
            .filter { it.ruleHash == ruleHash }
            .groupBy { it.baseScenarioKey }
            .filter { (_, decisionList) -> decisionList.size >= minSamples }
            .map { (scenario, decisionList) ->
                ScenarioErrorStat(
                    baseScenarioKey = scenario,
                    totalAttempts = decisionList.size,
                    errorCount = decisionList.count { !it.isCorrect }
                )
            }
            .sortedByDescending { it.errorRate } // Sort by error rate (worst first)
    }
    
    override fun getErrorStatsAcrossRules(minSamples: Int): List<ScenarioErrorStat> {
        return decisions
            .groupBy { it.baseScenarioKey }
            .filter { (_, decisionList) -> decisionList.size >= minSamples }
            .map { (scenario, decisionList) ->
                ScenarioErrorStat(
                    baseScenarioKey = scenario,
                    totalAttempts = decisionList.size,
                    errorCount = decisionList.count { !it.isCorrect }
                )
            }
            .sortedByDescending { it.errorRate } // Sort by error rate (worst first)
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
     * Get scenario statistics organized by rule hash (for backward compatibility)
     * Returns the old ScenarioStats format for existing UI components
     */
    fun getScenarioStats(): Map<String, ScenarioStats> {
        return decisions
            .groupBy { it.baseScenarioKey }
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
 * ScenarioStats - Legacy statistics format for backward compatibility
 * TODO: Replace with ScenarioErrorStat in UI components
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