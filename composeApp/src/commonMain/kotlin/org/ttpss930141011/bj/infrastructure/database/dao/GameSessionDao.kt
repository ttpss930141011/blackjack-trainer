package org.ttpss930141011.bj.infrastructure.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.ttpss930141011.bj.infrastructure.database.entities.GameSessionEntity

/**
 * Data Access Object for game sessions.
 */
@Dao
interface GameSessionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: GameSessionEntity): Long
    
    @Update
    suspend fun updateSession(session: GameSessionEntity)
    
    @Query("SELECT * FROM game_sessions WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveSession(): GameSessionEntity?
    
    @Query("SELECT * FROM game_sessions WHERE startTime >= :cutoffTime ORDER BY startTime DESC")
    suspend fun getRecentSessions(cutoffTime: Long): List<GameSessionEntity>
    
    @Query("SELECT * FROM game_sessions ORDER BY startTime DESC")
    fun getAllSessionsFlow(): Flow<List<GameSessionEntity>>
    
    @Query("UPDATE game_sessions SET isActive = 0, endTime = :endTime WHERE sessionId = :sessionId")
    suspend fun endSession(sessionId: Long, endTime: Long)
    
    @Query("DELETE FROM game_sessions WHERE startTime < :cutoffTime")
    suspend fun deleteOldSessions(cutoffTime: Long): Int
}