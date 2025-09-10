package org.ttpss930141011.bj.infrastructure

import org.ttpss930141011.bj.domain.services.PersistenceRepository

/**
 * Factory for creating platform-specific PersistenceRepository implementations.
 * 
 * This follows Linus's approach: "Good code has no special cases"
 * - Each platform provides its own implementation
 * - Simple 3-method interface replaces 18-method bloat
 * - No complex abstraction, just elegant simplicity
 */
expect object PersistenceRepositoryFactory {
    fun create(): PersistenceRepository
}