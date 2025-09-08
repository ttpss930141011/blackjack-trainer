package org.ttpss930141011.bj.infrastructure.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<BlackjackDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("blackjack_room.db")
    return Room.databaseBuilder<BlackjackDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}