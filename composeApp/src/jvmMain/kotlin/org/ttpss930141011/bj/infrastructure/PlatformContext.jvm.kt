package org.ttpss930141011.bj.infrastructure

/**
 * JVM implementation of PlatformContext
 * 
 * JVM不需要context，直接返回null
 */
actual object PlatformContext {
    actual fun initialize(context: Any?) {
        // JVM doesn't need context
    }
    
    actual fun get(): Any? = null
}