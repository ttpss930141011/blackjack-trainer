package org.ttpss930141011.bj.infrastructure

import android.content.Context

/**
 * Android implementation of PlatformContext
 * 
 * 使用Application Context避免memory leaks
 */
actual object PlatformContext {
    private var context: Context? = null
    
    actual fun initialize(context: Any?) {
        require(context is Context) { "Android context must be provided" }
        this.context = context.applicationContext
    }
    
    actual fun get(): Any? = context
}