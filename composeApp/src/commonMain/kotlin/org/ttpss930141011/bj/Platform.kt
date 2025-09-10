package org.ttpss930141011.bj

/**
 * Platform abstraction interface for Kotlin Multiplatform
 * 
 * Provides a platform-agnostic way to access platform-specific information.
 */
interface Platform {
    /** Human-readable name of the current platform (e.g., "Android", "iOS", "JVM") */
    val name: String
}

/**
 * Expected function to get the current platform instance
 * 
 * This function is implemented differently on each platform to provide
 * platform-specific information.
 * 
 * @return Platform instance for the current runtime environment
 */
expect fun getPlatform(): Platform