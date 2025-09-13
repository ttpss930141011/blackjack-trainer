package org.ttpss930141011.bj.infrastructure.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.ttpss930141011.bj.domain.enums.RoundResult
import org.ttpss930141011.bj.infrastructure.database.entities.RoundHistoryEntity

/**
 * Data Access Object for round history.
 * 
 * Simple interface following Linus principle: "Good code has no special cases"
 */
@Dao
interface RoundHistoryDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: RoundHistoryEntity)
    
    @Query("SELECT * FROM round_history ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentRounds(limit: Int): List<RoundHistoryEntity>
    
    @Query("SELECT * FROM round_history WHERE sessionId = :sessionId ORDER BY timestamp DESC")
    suspend fun getRoundsBySession(sessionId: String): List<RoundHistoryEntity>
    
    @Query("SELECT * FROM round_history WHERE roundResult = :result ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRoundsByResult(result: RoundResult, limit: Int): List<RoundHistoryEntity>
    
    @Query("SELECT COUNT(*) FROM round_history")
    suspend fun getTotalRoundCount(): Int
    
    @Query("DELETE FROM round_history WHERE timestamp < :cutoffTime")
    suspend fun deleteOldRounds(cutoffTime: Long): Int
    
    @Query("DELETE FROM round_history")
    suspend fun deleteAllRounds()
}