package org.ttpss930141011.bj.infrastructure

/**
 * Platform context provider for KMP Room database initialization
 * 
 * 按照KMP最佳實踐：
 * - expect/actual pattern for platform-specific context
 * - Singleton pattern確保context一致性
 * - 解決Android Context dependency injection問題
 */
expect object PlatformContext {
    fun initialize(context: Any? = null)
    fun get(): Any?
}