package org.ttpss930141011.bj.infrastructure

import org.ttpss930141011.bj.domain.services.PersistenceRepository

/**
 * iOS implementation of PersistenceRepositoryFactory.
 * 
 * Uses elegant in-memory storage with 3-method interface.
 * CoreData implementation can be added later for iOS persistence.
 */
actual object PersistenceRepositoryFactory {
    actual fun create(): PersistenceRepository {
        return InMemoryPersistenceRepository()
    }
}