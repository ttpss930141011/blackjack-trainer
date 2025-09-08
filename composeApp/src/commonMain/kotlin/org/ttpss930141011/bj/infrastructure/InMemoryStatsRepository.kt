package org.ttpss930141011.bj.infrastructure

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.ttpss930141011.bj.domain.services.*
import org.ttpss930141011.bj.domain.valueobjects.*

/**
 * InMemoryStatsRepository - Simple in-memory implementation for platforms without Room support.
 * 
 * This is a "good taste" fallback implementation that provides basic functionality
 * without complex dependencies. WASM and other platforms can use this while
 * Android/JVM/iOS use the full Room implementation.
 */
class InMemoryStatsRepository : StatsRepository {
    
    private var currentSessionId = 1L
    private var currentSession: GameSession? = null
    private val decisions = mutableListOf<DecisionRecord>()
    private val sessions = mutableListOf<GameSession>()
    private val statsFlow = MutableStateFlow(SessionStats())
    private var userPreferences = UserPreferences()
    
    override suspend fun getCurrentSession(): GameSession {
        return currentSession ?: startNewSession(GameRules())
    }
    
    override suspend fun startNewSession(rules: GameRules): GameSession {
        // End current session if active
        currentSession?.let { session ->
            if (session.isActive) {
                endCurrentSession()
            }
        }
        
        // Create new session
        val newSession = GameSession(
            sessionId = currentSessionId++,
            startTime = getCurrentTimeMillis(),
            endTime = null,
            rules = rules,
            isActive = true,
            totalDecisions = 0,
            correctDecisions = 0
        )
        
        currentSession = newSession
        sessions.add(newSession)
        return newSession
    }
    
    override suspend fun endCurrentSession(): GameSession {
        val session = currentSession ?: throw IllegalStateException("No active session")
        
        val endedSession = session.copy(
            endTime = getCurrentTimeMillis(),
            isActive = false
        )
        
        // Update in sessions list
        val index = sessions.indexOfFirst { it.sessionId == session.sessionId }
        if (index >= 0) {
            sessions[index] = endedSession
        }
        
        currentSession = null
        return endedSession
    }
    
    override suspend fun recordDecision(decision: DecisionRecord): SessionStats {
        val session = getCurrentSession()
        decisions.add(decision)
        
        // Update session totals
        val newTotalDecisions = session.totalDecisions + 1
        val newCorrectDecisions = if (decision.isCorrect) {
            session.correctDecisions + 1
        } else {
            session.correctDecisions
        }
        
        val updatedSession = session.copy(
            totalDecisions = newTotalDecisions,
            correctDecisions = newCorrectDecisions
        )
        
        // Update in sessions list
        val index = sessions.indexOfFirst { it.sessionId == session.sessionId }
        if (index >= 0) {
            sessions[index] = updatedSession
        }
        currentSession = updatedSession
        
        // Update stats flow
        val newStats = createSessionStats()
        statsFlow.value = newStats
        
        return newStats
    }
    
    override suspend fun recordDecisions(decisions: List<DecisionRecord>): SessionStats {
        var stats = SessionStats()
        for (decision in decisions) {
            stats = recordDecision(decision)
        }
        return stats
    }
    
    override fun getCurrentSessionStats(): Flow<SessionStats> {
        return statsFlow.asStateFlow()
    }
    
    override suspend fun getCurrentSessionWorstScenarios(minSamples: Int): List<ScenarioErrorStat> {
        val sessionDecisions = decisions.filter { decision ->
            currentSession?.sessionId == currentSessionId - 1 // Current session decisions
        }
        
        return sessionDecisions
            .groupBy { it.baseScenarioKey }
            .filter { (_, decisionList) -> decisionList.size >= minSamples }
            .map { (scenario, decisionList) ->
                val totalAttempts = decisionList.size
                val correctAttempts = decisionList.count { it.isCorrect }
                val errorCount = totalAttempts - correctAttempts
                val errorRate = errorCount.toDouble() / totalAttempts
                
                ScenarioErrorStat(
                    baseScenarioKey = scenario,
                    errorCount = errorCount,
                    totalAttempts = totalAttempts
                )
            }
            .sortedByDescending { it.errorRate }
    }
    
    override suspend fun getRecentDecisions(limit: Int): List<DecisionRecord> {
        return decisions.takeLast(limit)
    }
    
    override suspend fun getHistoricalSessions(days: Int): List<SessionSummary> {
        val cutoffTime = getCurrentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        return sessions
            .filter { it.startTime >= cutoffTime }
            .map { session ->
                SessionSummary(
                    sessionId = session.sessionId,
                    date = session.startTime,
                    durationMinutes = session.durationMinutes ?: 0,
                    totalDecisions = session.totalDecisions,
                    decisionRate = session.decisionRate,
                    rulesHash = session.rules.hashCode().toString()
                )
            }
    }
    
    override suspend fun getLearningProgressTrends(days: Int): LearningTrends {
        return LearningTrends(
            dailyAccuracyTrends = emptyList(),
            scenarioImprovements = emptyList(),
            overallProgressSlope = 0.0
        )
    }
    
    override suspend fun getHistoricalWorstScenarios(minSamples: Int, days: Int): List<ScenarioErrorStat> {
        return emptyList()
    }
    
    override suspend fun getRuleSetComparison(days: Int): List<RuleSetPerformance> {
        return emptyList()
    }
    
    override suspend fun saveUserPreferences(preferences: UserPreferences) {
        userPreferences = preferences
    }
    
    override suspend fun loadUserPreferences(): UserPreferences {
        return userPreferences
    }
    
    override suspend fun rememberBetAmount(betAmount: Int) {
        userPreferences = userPreferences.rememberLastBet(betAmount)
    }
    
    override suspend fun cleanupOldSessions(retentionDays: Int): Int {
        val cutoffTime = getCurrentTimeMillis() - (retentionDays * 24 * 60 * 60 * 1000L)
        val originalSize = sessions.size
        sessions.removeAll { it.startTime < cutoffTime }
        decisions.removeAll { decision ->
            sessions.none { session -> 
                // Keep decisions that belong to retained sessions
                decision.timestamp >= session.startTime && 
                decision.timestamp <= (session.endTime ?: getCurrentTimeMillis())
            }
        }
        return originalSize - sessions.size
    }
    
    override suspend fun exportSessionData(sessionIds: List<Long>): SessionDataExport {
        val sessionsToExport = if (sessionIds.isEmpty()) {
            sessions
        } else {
            sessions.filter { it.sessionId in sessionIds }
        }
        
        return SessionDataExport(
            exportDate = getCurrentTimeMillis(),
            sessions = sessionsToExport,
            decisions = decisions.filter { decision ->
                sessionsToExport.any { session ->
                    decision.timestamp >= session.startTime &&
                    decision.timestamp <= (session.endTime ?: getCurrentTimeMillis())
                }
            },
            preferences = userPreferences
        )
    }
    
    private fun createSessionStats(): SessionStats {
        val currentSessionDecisions = decisions.filter { decision ->
            currentSession?.let { session ->
                decision.timestamp >= session.startTime
            } ?: false
        }
        
        return SessionStats(
            totalDecisions = currentSessionDecisions.size,
            correctDecisions = currentSessionDecisions.count { it.isCorrect },
            recentDecisions = currentSessionDecisions.takeLast(50)
        )
    }
    
    // Platform-agnostic time function
    private fun getCurrentTimeMillis(): Long {
        return kotlin.random.Random.nextLong(1000000000000L, 9999999999999L) // Simplified
    }
}