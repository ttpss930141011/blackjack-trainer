package org.ttpss930141011.bj.infrastructure

import org.ttpss930141011.bj.domain.services.StatsRepository

/**
 * Factory for creating platform-specific StatsRepository implementations.
 * 
 * This follows Linus's approach: "Good code has no special cases"
 * - Each platform provides its own implementation
 * - Common interface provides unified API
 * - No complex abstraction, just simple factory pattern
 */
expect object StatsRepositoryFactory {
    fun create(): StatsRepository
}