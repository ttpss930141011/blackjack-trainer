package org.ttpss930141011.bj.infrastructure

import android.content.Context
import org.ttpss930141011.bj.domain.services.PersistenceRepository
import org.ttpss930141011.bj.infrastructure.database.BlackjackDatabase
import org.ttpss930141011.bj.infrastructure.database.getRoomDatabase
import org.ttpss930141011.bj.infrastructure.database.getDatabaseBuilder

/**
 * Android implementation of PersistenceRepositoryFactory.
 * 
 * 使用 Room SQLite 數據庫進行持久化存儲
 * 按照 Android Room KMP 最佳實踐：context管理
 */
actual object PersistenceRepositoryFactory {
    
    private var database: BlackjackDatabase? = null
    
    actual fun create(): PersistenceRepository {
        if (database == null) {
            val context = PlatformContext.get() as? Context
                ?: throw IllegalStateException("Android context not initialized. Call PlatformContext.initialize() first.")
            database = getRoomDatabase(getDatabaseBuilder(context))
        }
        return RoomPersistenceRepository(database!!)
    }
}