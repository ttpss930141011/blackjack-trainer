package org.ttpss930141011.bj.infrastructure.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.ttpss930141011.bj.domain.enums.RoundResult

/**
 * Room entity for complete blackjack round history.
 * 
 * Stores complete round context for user replay in History page.
 * Follows Linus principle: simple data structure, no special cases.
 */
@Entity(tableName = "round_history")
data class RoundHistoryEntity(
    @PrimaryKey
    val roundId: String,
    
    // Round identification
    val sessionId: String,
    val timestamp: Long,
    
    // Game context (serialized as JSON strings - Room KSP 問題，暫時用字串)
    val gameRulesJson: String,
    val betAmount: Int,
    
    // Hand evolution (JSON arrays)
    val initialPlayerHandsJson: String,
    val finalPlayerHandsJson: String,
    val dealerVisibleCardJson: String,
    val dealerFinalHandJson: String,
    
    // Embedded decisions (JSON array)
    val decisionsJson: String,
    
    // Round outcome
    val roundResult: RoundResult,
    val netChipChange: Int,
    
    // Performance metrics
    val roundDurationMs: Long,
    val correctDecisionCount: Int,
    val totalDecisionCount: Int
)