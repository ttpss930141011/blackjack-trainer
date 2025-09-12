package org.ttpss930141011.bj.application

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.ttpss930141011.bj.infrastructure.audio.AudioModule
import org.ttpss930141011.bj.infrastructure.audio.AudioManagerImpl

/**
 * ApplicationService - Application-level service initialization and lifecycle management
 * 
 * Linus Principle: "Initialize once, use everywhere"
 * - Centralized initialization logic
 * - Proper lifecycle management
 * - No UI dependencies
 */
class ApplicationService {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var isInitialized = false
    
    /**
     * Initializes all application services.
     * Should be called once at app startup.
     */
    suspend fun initialize() {
        if (isInitialized) return
        
        applicationScope.launch {
            try {
                // Initialize audio system
                val audioManager = AudioModule.getAudioManager()
                if (audioManager is AudioManagerImpl) {
                    audioManager.initialize()
                }
                
                isInitialized = true
            } catch (e: Exception) {
                // Log error but don't crash the app
                println("Application initialization failed: ${e.message}")
            }
        }
    }
    
    /**
     * Releases all application resources.
     * Should be called when app is destroyed.
     */
    fun release() {
        AudioModule.release()
        isInitialized = false
    }
    
    companion object {
        @Volatile
        private var INSTANCE: ApplicationService? = null
        
        fun getInstance(): ApplicationService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ApplicationService().also { INSTANCE = it }
            }
        }
    }
}