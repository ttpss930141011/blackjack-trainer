package org.ttpss930141011.bj.infrastructure

import org.ttpss930141011.bj.domain.services.PersistenceRepository
import org.ttpss930141011.bj.domain.valueobjects.DecisionRecord
import org.ttpss930141011.bj.domain.valueobjects.RoundHistory
import org.ttpss930141011.bj.domain.valueobjects.UserPreferences
import kotlin.reflect.KClass

/**
 * InMemoryPersistenceRepository - Simple, bulletproof implementation.
 * 
 * This shows how a 3-method interface can handle ALL persistence needs
 * without complexity, special cases, or enterprise bloat.
 * 
 * 100 lines of code replace 500+ lines of overengineered Repository classes.
 * This is "good taste" in action.
 */
class InMemoryPersistenceRepository : PersistenceRepository {
    
    // Simple storage: type -> key -> object
    private val storage = mutableMapOf<String, MutableMap<String, Any>>()
    
    override suspend fun save(data: Any) {
        val typeName = data::class.simpleName ?: "Unknown"
        val key = generateKey(data)
        
        storage.getOrPut(typeName) { mutableMapOf() }[key] = data
    }
    
    override suspend fun <T : Any> load(key: String, type: KClass<T>): T? {
        val typeName = type.simpleName
        return storage[typeName]?.get(key) as? T
    }
    
    override suspend fun <T : Any> query(type: KClass<T>, criteria: Map<String, Any>): List<T> {
        val typeName = type.simpleName
        val objects = storage[typeName]?.values ?: emptyList<Any>()
        val filteredObjects = objects.mapNotNull { it as? T }
        
        if (criteria.isEmpty()) {
            return filteredObjects
        }
        
        return filteredObjects.filter { obj ->
            criteria.all { (fieldName, expectedValue) ->
                getFieldValue(obj, fieldName) == expectedValue
            }
        }
    }
    
    /**
     * Generate storage key for any object.
     * DecisionRecord uses timestamp, RoundHistory uses roundId, UserPreferences uses constant key.
     */
    private fun generateKey(data: Any): String {
        return when (data) {
            is DecisionRecord -> data.timestamp.toString()
            is RoundHistory -> data.roundId
            is UserPreferences -> "preferences"
            else -> data.hashCode().toString()
        }
    }
    
    /**
     * Extract field value using simple reflection-like access.
     * This handles the common query cases without complex reflection.
     */
    private fun getFieldValue(obj: Any, fieldName: String): Any? {
        return when (obj) {
            is DecisionRecord -> when (fieldName) {
                "isCorrect" -> obj.isCorrect
                "baseScenarioKey" -> obj.baseScenarioKey
                "ruleHash" -> obj.ruleHash
                "playerAction" -> obj.playerAction
                else -> null
            }
            is RoundHistory -> when (fieldName) {
                "sessionId" -> obj.sessionId
                "roundResult" -> obj.roundResult
                "netChipChange" -> obj.netChipChange
                "timestamp" -> obj.timestamp
                else -> null
            }
            is UserPreferences -> when (fieldName) {
                "lastBetAmount" -> obj.lastBetAmount
                else -> null
            }
            else -> null
        }
    }
}

/**
 * Simple statistics calculation functions.
 * These show how to use the simple repository for complex queries.
 */
object PersistenceStats {
    
    /**
     * Calculate error rate for scenarios.
     * This replaces the complex ScenarioErrorStat methods.
     */
    suspend fun calculateScenarioErrors(
        repository: PersistenceRepository,
        minSamples: Int = InfrastructureConstants.MIN_SAMPLES_FOR_STATISTICS
    ): Map<String, Double> {
        val decisions = repository.query(DecisionRecord::class)
        
        return decisions
            .groupBy { it.baseScenarioKey }
            .filter { (_, decisionList) -> decisionList.size >= minSamples }
            .mapValues { (_, decisionList) ->
                val errorCount = decisionList.count { !it.isCorrect }
                errorCount.toDouble() / decisionList.size
            }
    }
    
    /**
     * Get recent decisions.
     * This replaces the getRecentDecisions() method.
     */
    suspend fun getRecentDecisions(
        repository: PersistenceRepository,
        limit: Int = InfrastructureConstants.DEFAULT_RECENT_DECISIONS_LIMIT
    ): List<DecisionRecord> {
        return repository.query(DecisionRecord::class)
            .sortedByDescending { it.timestamp }
            .take(limit)
    }
    
    /**
     * Get worst performing scenarios.
     * This replaces multiple "worst scenario" methods.
     */
    suspend fun getWorstScenarios(
        repository: PersistenceRepository,
        minSamples: Int = InfrastructureConstants.MIN_SAMPLES_FOR_STATISTICS,
        limit: Int = InfrastructureConstants.WORST_SCENARIOS_LIMIT
    ): List<Pair<String, Double>> {
        return calculateScenarioErrors(repository, minSamples)
            .toList()
            .sortedByDescending { (_, errorRate) -> errorRate }
            .take(limit)
    }
}