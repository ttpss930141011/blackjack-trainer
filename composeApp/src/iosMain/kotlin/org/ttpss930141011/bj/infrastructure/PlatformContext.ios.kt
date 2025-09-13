package org.ttpss930141011.bj.infrastructure

/**
 * iOS implementation of PlatformContext
 * 
 * iOS不需要context，直接返回null
 */
actual object PlatformContext {
    actual fun initialize(context: Any?) {
        // iOS doesn't need context
    }
    
    actual fun get(): Any? = null
}