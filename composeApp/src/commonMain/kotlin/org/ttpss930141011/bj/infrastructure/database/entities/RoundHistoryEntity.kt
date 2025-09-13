package org.ttpss930141011.bj.infrastructure.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.ttpss930141011.bj.domain.enums.RoundResult

/**
 * Room entity for complete blackjack round history - CLEANED UP VERSION
 * 
 * Simplified data structure focused on decision sequence.
 * Follows Linus principle: simple data structure, no special cases.
 */
@Entity(tableName = "round_history")
data class RoundHistoryEntity(
    @PrimaryKey
    val roundId: String,
    
    // Round identification
    val sessionId: String,
    val timestamp: Long,
    
    // Game context (serialized as JSON strings)
    val gameRulesJson: String,
    val initialBet: Int,
    
    // The core data: decision sequence (JSON array)
    val decisionsJson: String,
    
    // Round outcome
    val roundResult: RoundResult,
    val netChipChange: Int,
    val roundDurationMs: Long
)