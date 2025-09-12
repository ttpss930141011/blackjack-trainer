package org.ttpss930141011.bj.application

import org.ttpss930141011.bj.domain.services.PersistenceRepository
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.RoundResult
import org.ttpss930141011.bj.infrastructure.PersistenceRepositoryFactory
import org.ttpss930141011.bj.infrastructure.InfrastructureConstants
import kotlin.time.Duration.Companion.days

/**
 * PersistenceService - Dual-stream persistence architecture
 * 
 * BREAKING CHANGE: Complete rewrite from legacy single-stream design.
 * 
 * New Architecture:
 * - RoundHistory: Complete round records for user replay (History page)
 * - DecisionRecord: Atomic decision data for cross-game analytics (Stats page)
 * 
 * Data Relationship: RoundHistory = List<DecisionRecord> + Complete Context
 * 
 * Design Principles:
 * - Clean separation of concerns: History vs Analytics
 * - Different lifecycles: Session-scoped vs Permanent
 * - Optimized for different query patterns
 * - Zero complex aggregation logic - composition over computation
 */
class PersistenceService(
    private val repository: PersistenceRepository = PersistenceRepositoryFactory.create()
) {
    
    // ===== ROUND HISTORY OPERATIONS (History Page) =====
    
    /**
     * Save complete round history for replay functionality.
     * Primary data source for History page.
     */
    suspend fun saveRoundHistory(round: RoundHistory) {
        repository.save(round)
    }
    
    /**
     * Get recent rounds for user replay.
     * Optimized for History page display.
     */
    suspend fun getRecentRounds(limit: Int = InfrastructureConstants.DEFAULT_RECENT_ROUNDS_LIMIT): List<RoundHistory> {
        return repository.query(RoundHistory::class)
            .sortedByDescending { it.timestamp }
            .take(limit)
    }
    
    /**
     * Get rounds for specific session.
     * Enables session-based filtering in History page.
     */
    suspend fun getRoundsBySession(sessionId: String): List<RoundHistory> {
        return repository.query(RoundHistory::class, mapOf("sessionId" to sessionId))
            .sortedByDescending { it.timestamp }
    }
    
    /**
     * Get rounds filtered by result.
     * Enables "show only wins/losses" functionality.
     */
    suspend fun getRoundsByResult(result: RoundResult, limit: Int = InfrastructureConstants.DEFAULT_RECENT_ROUNDS_LIMIT): List<RoundHistory> {
        return repository.query(RoundHistory::class, mapOf("roundResult" to result))
            .sortedByDescending { it.timestamp }
            .take(limit)
    }
    
    /**
     * Clean old round history data.
     * Keeps History page responsive by limiting data volume.
     */
    fun cleanOldRoundHistory(olderThanDays: Int = InfrastructureConstants.DATA_CLEANUP_DAYS_THRESHOLD) {
        val cutoffTime = TimeProvider.currentTimeMillis() - (olderThanDays.days.inWholeMilliseconds)
        // Implementation depends on repository capabilities
        // TODO: Add repository.deleteWhere() method
    }
    
    // ===== DECISION ANALYTICS OPERATIONS (Stats Page) =====
    
    /**
     * Save individual decision for analytics.
     * Core building block for all cross-game statistics.
     */
    suspend fun saveDecision(decision: DecisionRecord) {
        repository.save(decision)
    }
    
    /**
     * Get all decisions for analytics.
     * Primary data source for Stats page calculations.
     */
    suspend fun getAllDecisions(): List<DecisionRecord> {
        return repository.query(DecisionRecord::class)
            .sortedByDescending { it.timestamp }
    }
    
    /**
     * Get decisions filtered by scenario.
     * Enables scenario-specific learning analysis.
     */
    suspend fun getDecisionsByScenario(scenarioKey: String): List<DecisionRecord> {
        return repository.query(DecisionRecord::class, mapOf("baseScenarioKey" to scenarioKey))
    }
    
    /**
     * Get decisions filtered by rule set.
     * Enables rule-specific performance tracking.
     */
    suspend fun getDecisionsByRuleHash(ruleHash: String): List<DecisionRecord> {
        return repository.query(DecisionRecord::class, mapOf("ruleHash" to ruleHash))
    }
    
    /**
     * Get recent decisions for current session context.
     * Used by GameViewModel for immediate feedback.
     */
    suspend fun getRecentDecisions(limit: Int = InfrastructureConstants.DEFAULT_RECENT_DECISIONS_LIMIT): List<DecisionRecord> {
        return repository.query(DecisionRecord::class)
            .sortedByDescending { it.timestamp }
            .take(limit)
    }
    
    // ===== ANALYTICS CALCULATIONS (Simplified) =====
    // Complex statistics removed - keeping only essential session data
    
    /**
     * Calculate mistake records with original card data.
     * Preserves original card information instead of parsed strings.
     */
    suspend fun calculateMistakeRecords(minSamples: Int = InfrastructureConstants.MIN_SAMPLES_FOR_STATISTICS): List<MistakeRecord> {
        val decisions = getAllDecisions()
        
        return decisions
            .filter { !it.isCorrect } // Only errors
            .groupBy { it.baseScenarioKey }
            .filter { (_, decisionList) -> decisionList.size >= minSamples }
            .map { (scenario, errorDecisions) ->
                // Take the first error as representative of this scenario
                val firstError = errorDecisions.first()
                MistakeRecord(
                    handCards = firstError.handCards,
                    dealerUpCard = firstError.dealerUpCard,
                    errorCount = errorDecisions.size,
                    baseScenarioKey = scenario
                )
            }
            .sortedByDescending { it.errorCount }
    }
    
    // Statistics conversion methods removed
    
    /**
     * Calculate current session statistics.
     * Simplified calculation without complex aggregation.
     */
    suspend fun calculateSessionStats(): SessionStats {
        val decisions = getRecentDecisions(InfrastructureConstants.SESSION_CONTEXT_SIZE)
        return SessionStats(
            totalDecisions = decisions.size,
            correctDecisions = decisions.count { it.isCorrect },
            recentDecisions = decisions.take(InfrastructureConstants.DEFAULT_RECENT_DECISIONS_LIMIT)
        )
    }
    
    /**
     * Calculate overall lifetime accuracy.
     * Cross-game performance metric.
     */
    suspend fun calculateOverallAccuracy(): Double {
        val decisions = getAllDecisions()
        return if (decisions.isEmpty()) {
            0.0
        } else {
            decisions.count { it.isCorrect }.toDouble() / decisions.size
        }
    }
    
    /**
     * Calculate performance by rule set.
     * Enables rule-specific improvement tracking.
     */
    suspend fun calculateRuleSetPerformance(): Map<String, Double> {
        val decisions = getAllDecisions()
        
        return decisions
            .groupBy { it.ruleHash }
            .mapValues { (_, ruleDecisions) ->
                if (ruleDecisions.isEmpty()) {
                    0.0
                } else {
                    ruleDecisions.count { it.isCorrect }.toDouble() / ruleDecisions.size
                }
            }
    }
    
    // ===== USER PREFERENCES =====
    
    /**
     * Save user preferences.
     * Simplified without async overhead.
     */
    suspend fun saveUserPreferences(preferences: UserPreferences) {
        repository.save(preferences)
    }
    
    /**
     * Load user preferences.
     * Simplified without async overhead.
     */
    suspend fun loadUserPreferences(): UserPreferences {
        return repository.load("preferences", UserPreferences::class) ?: UserPreferences()
    }
    
    // ===== DATA MANAGEMENT =====
    
    /**
     * Clear all learning data for reset functionality.
     * Clears both RoundHistory and DecisionRecord streams.
     */
    fun clearAllLearningData() {
        // Clear both data streams
        // TODO: Implement repository.clear() methods
        // repository.clear(RoundHistory::class)
        // repository.clear(DecisionRecord::class)
    }
    
    /**
     * Get data volume statistics for management.
     * Helps users understand storage usage.
     */
    suspend fun getDataVolumeStats(): DataVolumeStats {
        val rounds = repository.query(RoundHistory::class)
        val decisions = repository.query(DecisionRecord::class)
        
        return DataVolumeStats(
            totalRounds = rounds.size,
            totalDecisions = decisions.size,
            oldestRoundTimestamp = rounds.minOfOrNull { it.timestamp },
            newestRoundTimestamp = rounds.maxOfOrNull { it.timestamp }
        )
    }

    // Legacy methods removed - use the direct methods above
}

/**
 * Data volume statistics for user information
 */
data class DataVolumeStats(
    val totalRounds: Int,
    val totalDecisions: Int,
    val oldestRoundTimestamp: Long?,
    val newestRoundTimestamp: Long?
) {
    val hasData: Boolean = totalRounds > 0 || totalDecisions > 0
    
    val dataSpanDays: Int? = if (oldestRoundTimestamp != null && newestRoundTimestamp != null) {
        ((newestRoundTimestamp - oldestRoundTimestamp) / (24 * 60 * 60 * 1000)).toInt()
    } else null
}

/**
 * BREAKING CHANGE: Removed legacy extension functions
 * 
 * The following functions have been removed:
 * - saveDecisionAndUpdateStats() - Use saveDecision() + calculateSessionStats()
 * - getRecentIncorrectDecisions() - Use getAllDecisions().filter { !it.isCorrect }.take(limit)
 * 
 * Rationale: These convenience functions created tight coupling and encouraged
 * inefficient query patterns. Client code should compose operations explicitly.
 */