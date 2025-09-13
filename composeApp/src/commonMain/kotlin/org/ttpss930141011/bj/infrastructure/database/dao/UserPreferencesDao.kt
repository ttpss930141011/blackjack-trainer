package org.ttpss930141011.bj.infrastructure.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.ttpss930141011.bj.infrastructure.database.entities.UserPreferencesEntity

/**
 * Data Access Object for user preferences.
 * 
 * Simple single-record storage for user settings.
 * Follows Linus principle: "simple operations, no complex logic".
 */
@Dao
interface UserPreferencesDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreferences(preferences: UserPreferencesEntity)
    
    @Query("SELECT * FROM user_preferences WHERE id = 1 LIMIT 1")
    suspend fun getPreferences(): UserPreferencesEntity?
    
    @Query("DELETE FROM user_preferences")
    suspend fun deleteAllPreferences()
}