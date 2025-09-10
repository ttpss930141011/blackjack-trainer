package org.ttpss930141011.bj.infrastructure.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.ttpss930141011.bj.domain.enums.Action

/**
 * Room entity for individual decision records.
 * 
 * Stores atomic decision data for cross-game analytics in Stats page.
 * Separate from RoundHistory for clean data separation.
 */
@Entity(tableName = "decision_records")
data class DecisionRecordEntity(
    @PrimaryKey
    val timestamp: Long,
    
    // Hand context (JSON array)
    val handCardsJson: String,
    val dealerUpCardJson: String,
    
    // Decision data
    val playerAction: Action,
    val isCorrect: Boolean,
    
    // Analytics keys
    val baseScenarioKey: String,
    val ruleHash: String,
    
    // Optional context
    val handValue: Int,
    val isHandSoft: Boolean,
    val canDouble: Boolean,
    val canSplit: Boolean
)