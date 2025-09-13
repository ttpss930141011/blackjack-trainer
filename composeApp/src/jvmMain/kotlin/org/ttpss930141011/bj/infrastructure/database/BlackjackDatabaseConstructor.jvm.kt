package org.ttpss930141011.bj.infrastructure.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File


fun getDatabaseBuilder(): RoomDatabase.Builder<BlackjackDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "blackjack_room.db")
    return Room.databaseBuilder<BlackjackDatabase>(
        name = dbFile.absolutePath,
    )
}