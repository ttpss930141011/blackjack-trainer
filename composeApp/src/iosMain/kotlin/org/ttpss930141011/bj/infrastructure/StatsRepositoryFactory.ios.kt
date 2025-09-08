package org.ttpss930141011.bj.infrastructure

import org.ttpss930141011.bj.domain.services.StatsRepository

/**
 * iOS implementation of StatsRepositoryFactory.
 * 
 * Uses in-memory storage for simplicity. Room implementation can be added later.
 */
actual object StatsRepositoryFactory {
    actual fun create(): StatsRepository {
        return InMemoryStatsRepository()
    }
}