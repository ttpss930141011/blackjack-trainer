package org.ttpss930141011.bj.domain.services

import org.ttpss930141011.bj.domain.valueobjects.DecisionRecord

/**
 * LearningRepository - Domain service interface for decision persistence and retrieval.
 * 
 * This interface belongs in the domain layer as it defines the contract for
 * learning data operations without specifying implementation details.
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
    fun getRecent(limit: Int): List<DecisionRecord>
    
    /**
     * Find decisions for specific scenarios (for targeted analysis)
     * @param scenarioKey the scenario pattern to match (e.g., "Hard 16 vs 10")
     */
    fun findByScenario(scenarioKey: String): List<DecisionRecord>
    
    /**
     * Get worst performing scenarios for learning recommendations
     * @param minSamples minimum number of attempts required for meaningful statistics
     * @return list of scenario keys with their error rates, sorted by worst performance
     */
    fun getWorstScenarios(minSamples: Int = 3): List<Pair<String, Double>>
    
    /**
     * Clear all stored decisions (for testing or reset)
     */
    fun clear()
}