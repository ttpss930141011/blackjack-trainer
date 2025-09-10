package org.ttpss930141011.bj.infrastructure

import org.ttpss930141011.bj.domain.services.PersistenceRepository
import org.ttpss930141011.bj.infrastructure.database.BlackjackDatabase
import org.ttpss930141011.bj.infrastructure.database.getRoomDatabase
import org.ttpss930141011.bj.infrastructure.database.getDatabaseBuilder

/**
 * JVM implementation of PersistenceRepositoryFactory.
 * 
 * 使用 Room SQLite 數據庫進行持久化存儲
 * 按照 Linus 原則：簡單、可靠、沒有特殊情況
 */
actual object PersistenceRepositoryFactory {
    
    private var database: BlackjackDatabase? = null
    
    actual fun create(): PersistenceRepository {
        if (database == null) {
            database = getRoomDatabase(getDatabaseBuilder())
        }
        return RoomPersistenceRepository(database!!)
    }
}