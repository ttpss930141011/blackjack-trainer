package org.ttpss930141011.bj.infrastructure.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.ttpss930141011.bj.infrastructure.database.dao.SessionStatsDao
import org.ttpss930141011.bj.infrastructure.database.entities.GameSessionEntity
import org.ttpss930141011.bj.infrastructure.database.entities.DecisionRecordEntity

/**
 * Room Database class for Blackjack Strategy Trainer
 */
@Database(
    entities = [
        GameSessionEntity::class,
        DecisionRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BlackjackDatabase : RoomDatabase() {
    abstract fun sessionStatsDao(): SessionStatsDao
}

/**
 * Get database instance with proper configuration
 */
fun getRoomDatabase(
    builder: RoomDatabase.Builder<BlackjackDatabase>
): BlackjackDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}