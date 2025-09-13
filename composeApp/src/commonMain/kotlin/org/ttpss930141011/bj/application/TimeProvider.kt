package org.ttpss930141011.bj.application

/**
 * Platform-independent time provider for timestamps.
 * 
 * Following Linus principle: "Simple abstraction for a simple need"
 */
expect object TimeProvider {
    fun currentTimeMillis(): Long
}