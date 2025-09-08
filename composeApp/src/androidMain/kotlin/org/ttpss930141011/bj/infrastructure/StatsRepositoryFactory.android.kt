package org.ttpss930141011.bj.infrastructure

import android.content.Context
import org.ttpss930141011.bj.domain.services.StatsRepository

/**
 * Android implementation of StatsRepositoryFactory.
 * 
 * For now, uses in-memory storage. Room implementation can be added later
 * when we have proper Android context injection.
 */
actual object StatsRepositoryFactory {
    actual fun create(): StatsRepository {
        return InMemoryStatsRepository()
    }
}