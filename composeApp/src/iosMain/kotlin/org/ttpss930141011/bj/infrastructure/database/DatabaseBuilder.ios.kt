package org.ttpss930141011.bj.infrastructure.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSHomeDirectory

/**
 * iOS platform-specific database builder
 */
@OptIn(ExperimentalForeignApi::class)
fun getDatabaseBuilder(): RoomDatabase.Builder<BlackjackDatabase> {
    val dbFilePath = NSHomeDirectory() + "/blackjack_room.db"
    return Room.databaseBuilder<BlackjackDatabase>(
        name = dbFilePath
    )
}