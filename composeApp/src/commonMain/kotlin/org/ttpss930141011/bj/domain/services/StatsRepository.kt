package org.ttpss930141011.bj.domain.services

import kotlinx.coroutines.flow.Flow
import org.ttpss930141011.bj.domain.valueobjects.*

/**
 * StatsRepository - Unified interface for statistics and learning data.
 * 
 * This eliminates the fragmented statistics architecture (SessionStats + DecisionRecord + 
 * GameSessionEntity) by providing a single source of truth for all learning analytics.
 * 
 * Follows Linus's principle: "Good code has no special cases" - one interface handles
 * both session-scoped and historical statistics without duplication.
 * 
 * Design goals:
 * - Single responsibility: All stats operations through one interface
 * - No special cases: Same interface for current session and historical data
 * - Testability: Easy to mock for testing
 * - Performance: Optimized queries with proper indexing
 */
interface StatsRepository {
    
    // === Session Management ===
    
    /**
     * Gets the current active session, creating one if needed.
     * 
     * @return Current session or new session if none exists
     */
    suspend fun getCurrentSession(): GameSession
    
    /**
     * Starts a new game session with given rules.
     * 
     * @param rules Game rules for this session
     * @return New active session
     */
    suspend fun startNewSession(rules: GameRules): GameSession
    
    /**
     * Ends the current active session.
     * 
     * @return Completed session with final statistics
     */
    suspend fun endCurrentSession(): GameSession
    
    // === Decision Recording ===
    
    /**
     * Records a single player decision in the current session.
     * 
     * @param decision Decision record to store
     * @return Updated session statistics
     */
    suspend fun recordDecision(decision: DecisionRecord): SessionStats
    
    /**
     * Records multiple decisions as a batch (for performance).
     * 
     * @param decisions List of decisions to record
     * @return Updated session statistics
     */
    suspend fun recordDecisions(decisions: List<DecisionRecord>): SessionStats
    
    // === Current Session Analytics ===
    
    /**
     * Gets real-time statistics for the current session.
     * 
     * @return Flow of session statistics that updates with new decisions
     */
    fun getCurrentSessionStats(): Flow<SessionStats>
    
    /**
     * Gets the worst-performing scenarios in the current session.
     * 
     * @param minSamples Minimum number of decisions required per scenario
     * @return List of scenarios with error rates, sorted by worst performance
     */
    suspend fun getCurrentSessionWorstScenarios(minSamples: Int = 3): List<ScenarioErrorStat>
    
    /**
     * Gets recent decision history for the current session.
     * 
     * @param limit Maximum number of recent decisions to return
     * @return List of recent decisions, most recent first
     */
    suspend fun getRecentDecisions(limit: Int = 50): List<DecisionRecord>
    
    // === Historical Analytics ===
    
    /**
     * Gets historical session summaries.
     * 
     * @param days Number of days of history to include
     * @return List of session summaries, most recent first
     */
    suspend fun getHistoricalSessions(days: Int = 30): List<SessionSummary>
    
    /**
     * Gets cross-session learning progress trends.
     * 
     * @param days Number of days of history to analyze
     * @return Learning progress data for charting/analysis
     */
    suspend fun getLearningProgressTrends(days: Int = 30): LearningTrends
    
    /**
     * Gets worst scenarios across all sessions.
     * 
     * @param minSamples Minimum number of decisions required per scenario
     * @param days Number of days of history to analyze
     * @return List of historically worst scenarios
     */
    suspend fun getHistoricalWorstScenarios(
        minSamples: Int = 10,
        days: Int = 30
    ): List<ScenarioErrorStat>
    
    /**
     * Gets performance comparison between different rule sets.
     * 
     * @param days Number of days of history to analyze
     * @return Rule-specific performance comparisons
     */
    suspend fun getRuleSetComparison(days: Int = 30): List<RuleSetPerformance>
    
    // === User Preferences Integration ===
    
    /**
     * Saves user preferences (rules, betting patterns, display settings).
     * 
     * @param preferences User preferences to persist
     */
    suspend fun saveUserPreferences(preferences: UserPreferences)
    
    /**
     * Loads saved user preferences.
     * 
     * @return User preferences or default if none saved
     */
    suspend fun loadUserPreferences(): UserPreferences
    
    /**
     * Records successful bet amount for "repeat last bet" functionality.
     * 
     * @param betAmount Bet amount to remember
     */
    suspend fun rememberBetAmount(betAmount: Int)
    
    // === Data Management ===
    
    /**
     * Cleans up old session data beyond retention policy.
     * 
     * @param retentionDays Number of days to retain (default 90)
     * @return Number of sessions cleaned up
     */
    suspend fun cleanupOldSessions(retentionDays: Int = 90): Int
    
    /**
     * Exports session data for backup or analysis.
     * 
     * @param sessionIds Specific sessions to export (empty = all recent)
     * @return Exportable data structure
     */
    suspend fun exportSessionData(sessionIds: List<Long> = emptyList()): SessionDataExport
}

// === Supporting Data Classes ===

/**
 * GameSession - Represents a single game session with metadata.
 */
data class GameSession(
    val sessionId: Long,
    val startTime: Long,
    val endTime: Long? = null,
    val rules: GameRules,
    val isActive: Boolean = true,
    val totalDecisions: Int = 0,
    val correctDecisions: Int = 0
) {
    val decisionRate: Double = if (totalDecisions > 0) {
        correctDecisions.toDouble() / totalDecisions
    } else 0.0
    
    val durationMinutes: Long? = endTime?.let { (it - startTime) / 60_000 }
}

/**
 * SessionSummary - Lightweight session overview for historical analysis.
 */
data class SessionSummary(
    val sessionId: Long,
    val date: Long,
    val durationMinutes: Long,
    val totalDecisions: Int,
    val decisionRate: Double,
    val rulesHash: String
)

/**
 * LearningTrends - Progress trend data for visualization.
 */
data class LearningTrends(
    val dailyAccuracyTrends: List<DailyAccuracy>,
    val scenarioImprovements: List<ScenarioImprovement>,
    val overallProgressSlope: Double
)

data class DailyAccuracy(
    val date: Long,
    val accuracy: Double,
    val decisionsCount: Int
)

data class ScenarioImprovement(
    val scenario: String,
    val initialAccuracy: Double,
    val currentAccuracy: Double,
    val improvementRate: Double
)

/**
 * RuleSetPerformance - Performance comparison across different rule configurations.
 */
data class RuleSetPerformance(
    val rulesHash: String,
    val rules: GameRules,
    val sessionsCount: Int,
    val totalDecisions: Int,
    val averageAccuracy: Double,
    val lastUsed: Long
)

/**
 * SessionDataExport - Exportable session data structure.
 */
data class SessionDataExport(
    val exportDate: Long,
    val sessions: List<GameSession>,
    val decisions: List<DecisionRecord>,
    val preferences: UserPreferences
)