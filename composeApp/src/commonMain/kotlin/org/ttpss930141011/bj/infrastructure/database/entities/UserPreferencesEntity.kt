package org.ttpss930141011.bj.infrastructure.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for user preferences.
 * 
 * Single-row storage for user settings with JSON serialization.
 * Follows same pattern as other entities with JSON fields for complex objects.
 */
@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey
    val id: Int = 1, // Fixed ID since we only store one preferences record
    
    // JSON serialized fields following DecisionRecordEntity pattern
    val preferredRulesJson: String,
    val lastBetAmount: Int,
    val displaySettingsJson: String,
    
    // Metadata
    val lastUpdated: Long
)