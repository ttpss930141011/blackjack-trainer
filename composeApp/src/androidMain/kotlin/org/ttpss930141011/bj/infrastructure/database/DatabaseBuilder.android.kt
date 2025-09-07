package org.ttpss930141011.bj.infrastructure.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Android platform-specific database builder
 */
fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<BlackjackDatabase> {
    val dbFile = context.getDatabasePath("blackjack_room.db")
    return Room.databaseBuilder<BlackjackDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}