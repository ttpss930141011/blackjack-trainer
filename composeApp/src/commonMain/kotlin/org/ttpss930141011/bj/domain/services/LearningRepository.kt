package org.ttpss930141011.bj.domain.services

import org.ttpss930141011.bj.domain.valueobjects.DecisionRecord
import org.ttpss930141011.bj.domain.valueobjects.ScenarioErrorStat

/**
 * LearningRepository - Domain service interface for decision persistence and retrieval.
 * 
 * Simplified interface focused on core user needs:
 * 1. Current rule decisions tracking
 * 2. Cross-rule error statistics comparison
 * 
 * Following DDD principles, this is an abstract domain service that will be
 * implemented in the infrastructure layer.
 */
interface LearningRepository {
    
    /**
     * Save a single decision record
     */
    fun save(decision: DecisionRecord)
    
    /**
     * Retrieve all decision records (for cross-game analysis)
     */
    fun getAll(): List<DecisionRecord>
    
    /**
     * Get recent decision records (for session analysis)
     * @param limit maximum number of recent decisions to retrieve
     */
    fun getRecent(limit: Int = 50): List<DecisionRecord>
    
    /**
     * Find decisions made under specific rule set
     * @param ruleHash the rule hash to filter by
     */
    fun findByRule(ruleHash: String): List<DecisionRecord>
    
    /**
     * Find decisions for specific base scenarios (cross-rule analysis)
     * @param baseScenarioKey the base scenario pattern (e.g., "H16 vs 10")
     */
    fun findByScenario(baseScenarioKey: String): List<DecisionRecord>
    
    /**
     * Get error statistics for scenarios under specific rule set
     * @param ruleHash the rule hash to filter by
     * @param minSamples minimum number of attempts required for meaningful statistics
     * @return list of scenario error statistics, sorted by worst performance
     */
    fun getErrorStatsByRule(ruleHash: String, minSamples: Int = 3): List<ScenarioErrorStat>
    
    /**
     * Get error statistics across all rules (for cross-rule comparison)
     * @param minSamples minimum number of attempts required for meaningful statistics
     * @return list of scenario error statistics, sorted by worst performance
     */
    fun getErrorStatsAcrossRules(minSamples: Int = 3): List<ScenarioErrorStat>
    
    /**
     * Clear all stored decisions (for testing or reset)
     */
    fun clear()
}