package org.ttpss930141011.bj.infrastructure.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.infrastructure.database.entities.DecisionRecordEntity

/**
 * Data Access Object for decision records.
 * 
 * Supports analytics queries for Stats page.
 * Simple interface following Linus principle.
 */
@Dao
interface DecisionRecordDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecision(decision: DecisionRecordEntity)
    
    @Query("SELECT * FROM decision_records ORDER BY timestamp DESC")
    suspend fun getAllDecisions(): List<DecisionRecordEntity>
    
    @Query("SELECT * FROM decision_records ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentDecisions(limit: Int): List<DecisionRecordEntity>
    
    @Query("SELECT * FROM decision_records WHERE baseScenarioKey = :scenarioKey ORDER BY timestamp DESC")
    suspend fun getDecisionsByScenario(scenarioKey: String): List<DecisionRecordEntity>
    
    @Query("SELECT * FROM decision_records WHERE ruleHash = :ruleHash ORDER BY timestamp DESC")
    suspend fun getDecisionsByRuleHash(ruleHash: String): List<DecisionRecordEntity>
    
    @Query("SELECT * FROM decision_records WHERE isCorrect = :isCorrect ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getDecisionsByCorrectness(isCorrect: Boolean, limit: Int): List<DecisionRecordEntity>
    
    @Query("SELECT COUNT(*) FROM decision_records")
    suspend fun getTotalDecisionCount(): Int
    
    @Query("SELECT COUNT(*) FROM decision_records WHERE isCorrect = 1")
    suspend fun getCorrectDecisionCount(): Int
    
    @Query("DELETE FROM decision_records WHERE timestamp < :cutoffTime")
    suspend fun deleteOldDecisions(cutoffTime: Long): Int
    
    @Query("DELETE FROM decision_records")
    suspend fun deleteAllDecisions()
}