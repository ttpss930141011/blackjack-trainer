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
    suspend fun cleanOldRoundHistory(olderThanDays: Int = InfrastructureConstants.DATA_CLEANUP_DAYS_THRESHOLD) {
        val cutoffTime = TimeProvider.currentTimeMillis() - (olderThanDays.days.inWholeMilliseconds)
        repository.deleteWhere(RoundHistory::class, mapOf("timestampBefore" to cutoffTime))
        repository.deleteWhere(DecisionRecord::class, mapOf("timestampBefore" to cutoffTime))
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
    
    // ===== ANALYTICS CALCULATIONS =====
    
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
    suspend fun clearAllLearningData() {
        repository.clear(RoundHistory::class)
        repository.clear(DecisionRecord::class)
    }
    
}