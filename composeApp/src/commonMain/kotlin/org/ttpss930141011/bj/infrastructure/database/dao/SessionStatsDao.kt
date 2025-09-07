package org.ttpss930141011.bj.infrastructure.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.ttpss930141011.bj.infrastructure.database.entities.GameSessionEntity
import org.ttpss930141011.bj.infrastructure.database.entities.DecisionRecordEntity

/**
 * Data Access Object for session statistics and decision records
 */
@Dao
interface SessionStatsDao {
    
    // GameSession operations
    @Query("SELECT * FROM game_sessions WHERE isActive = 1 ORDER BY startTime DESC LIMIT 1")
    suspend fun getCurrentActiveSession(): GameSessionEntity?
    
    @Query("SELECT * FROM game_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<GameSessionEntity>>
    
    @Insert
    suspend fun insertSession(session: GameSessionEntity): Long
    
    @Update
    suspend fun updateSession(session: GameSessionEntity)
    
    @Delete
    suspend fun deleteSession(session: GameSessionEntity)
    
    // DecisionRecord operations
    @Query("SELECT * FROM decision_records WHERE sessionId = :sessionId ORDER BY timestamp DESC")
    suspend fun getDecisionRecordsForSession(sessionId: Long): List<DecisionRecordEntity>
    
    @Query("SELECT * FROM decision_records ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentDecisionRecords(limit: Int = 50): List<DecisionRecordEntity>
    
    @Insert
    suspend fun insertDecisionRecord(record: DecisionRecordEntity)
    
    @Insert
    suspend fun insertDecisionRecords(records: List<DecisionRecordEntity>)
    
    @Delete
    suspend fun deleteDecisionRecord(record: DecisionRecordEntity)
    
    // Analytics queries
    @Query("""
        SELECT scenario_key, 
               COUNT(*) as totalDecisions,
               SUM(CASE WHEN isCorrect = 1 THEN 0 ELSE 1 END) as incorrectDecisions
        FROM decision_records 
        WHERE sessionId = :sessionId
        GROUP BY scenario_key
        HAVING totalDecisions >= :minSamples
        ORDER BY (CAST(incorrectDecisions AS REAL) / totalDecisions) DESC
    """)
    suspend fun getWorstScenariosForSession(sessionId: Long, minSamples: Int = 3): List<ScenarioStats>
    
    @Query("""
        SELECT COUNT(*) as totalDecisions,
               SUM(CASE WHEN isCorrect = 1 THEN 1 ELSE 0 END) as correctDecisions
        FROM decision_records 
        WHERE sessionId = :sessionId
    """)
    suspend fun getSessionStats(sessionId: Long): SessionSummary?
}

data class ScenarioStats(
    val scenario_key: String,
    val totalDecisions: Int,
    val incorrectDecisions: Int
)

data class SessionSummary(
    val totalDecisions: Int,
    val correctDecisions: Int
)