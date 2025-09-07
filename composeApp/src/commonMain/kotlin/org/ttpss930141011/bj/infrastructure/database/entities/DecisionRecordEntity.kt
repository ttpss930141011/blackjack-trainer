package org.ttpss930141011.bj.infrastructure.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.ColumnInfo

/**
 * Room Entity for storing individual decision records
 */
@Entity(
    tableName = "decision_records",
    foreignKeys = [
        ForeignKey(
            entity = GameSessionEntity::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DecisionRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val recordId: Long = 0,
    val sessionId: Long,
    val handCardsJson: String, // JSON representation of List<Card>
    val dealerUpCardJson: String, // JSON representation of Card
    val playerAction: String, // Action enum as string
    val isCorrect: Boolean,
    val gameRulesJson: String, // JSON representation of GameRules
    val timestamp: Long,
    val ruleHash: String,
    val baseScenarioKey: String,
    @ColumnInfo(name = "scenario_key")
    val scenarioKey: String
)