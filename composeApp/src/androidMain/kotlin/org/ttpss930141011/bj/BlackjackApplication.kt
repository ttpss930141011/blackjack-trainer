package org.ttpss930141011.bj

import android.app.Application
import org.ttpss930141011.bj.infrastructure.PlatformContext

/**
 * Application class for initializing Room database with proper Android context
 * 
 * 按照Android Room KMP最佳實踐：
 * - 使用Application context避免memory leaks
 * - 早期初始化PlatformContext
 * - 支援KMP expect/actual pattern
 */
class BlackjackApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize platform context for database
        PlatformContext.initialize(this)
    }
}