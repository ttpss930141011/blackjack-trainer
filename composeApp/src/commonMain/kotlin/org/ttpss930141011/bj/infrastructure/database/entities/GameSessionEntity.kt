package org.ttpss930141011.bj.infrastructure.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for game sessions.
 * 
 * Represents a complete blackjack training session with statistics.
 */
@Entity(tableName = "game_sessions")
data class GameSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Long = 0,
    
    val startTime: Long,
    val endTime: Long? = null,
    val rulesHash: String,
    val isActive: Boolean = true,
    val totalDecisions: Int = 0,
    val correctDecisions: Int = 0
)