package org.ttpss930141011.bj.infrastructure.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.ttpss930141011.bj.infrastructure.database.dao.DecisionRecordDao
import org.ttpss930141011.bj.infrastructure.database.dao.GameSessionDao
import org.ttpss930141011.bj.infrastructure.database.dao.RoundHistoryDao
import org.ttpss930141011.bj.infrastructure.database.entities.DecisionRecordEntity
import org.ttpss930141011.bj.infrastructure.database.entities.GameSessionEntity
import org.ttpss930141011.bj.infrastructure.database.entities.RoundHistoryEntity

/**
 * Room database for blackjack strategy trainer.
 *
 * Following official Android Room KMP documentation:
 * https://developer.android.com/kotlin/multiplatform/room#defining-database
 */
@Database(
    entities = [
        GameSessionEntity::class,
        RoundHistoryEntity::class,
        DecisionRecordEntity::class
    ], 
    version = 2, 
    exportSchema = true
)
@ConstructedBy(BlackjackDatabaseConstructor::class)
abstract class BlackjackDatabase : RoomDatabase() {
    abstract fun gameSessionDao(): GameSessionDao
    abstract fun roundHistoryDao(): RoundHistoryDao
    abstract fun decisionRecordDao(): DecisionRecordDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object BlackjackDatabaseConstructor : RoomDatabaseConstructor<BlackjackDatabase> {
    override fun initialize(): BlackjackDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<BlackjackDatabase>
): BlackjackDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}