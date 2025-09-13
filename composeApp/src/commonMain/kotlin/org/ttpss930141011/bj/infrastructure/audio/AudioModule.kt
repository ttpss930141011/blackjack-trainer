package org.ttpss930141011.bj.infrastructure.audio

import org.ttpss930141011.bj.domain.services.AudioManager

/**
 * AudioModule - Dependency injection for audio services
 * 
 * Linus Principle: "Simple factory, clear dependencies"
 * - Single responsibility: Create and configure audio services
 * - No magic: Explicit dependency creation
 * - Testable: Easy to inject mocks
 */
object AudioModule {
    
    private var _audioManager: AudioManager? = null
    
    /**
     * Gets or creates the singleton AudioManager instance.
     * Simple lazy initialization for KMP.
     */
    fun getAudioManager(): AudioManager {
        return _audioManager ?: createAudioManager().also { _audioManager = it }
    }
    
    /**
     * Sets a custom AudioManager (for testing).
     */
    fun setAudioManager(audioManager: AudioManager) {
        _audioManager = audioManager
    }
    
    /**
     * Releases audio resources and resets the singleton.
     * Should be called when app is destroyed.
     */
    fun release() {
        _audioManager?.let { manager ->
            if (manager is AudioManagerImpl) {
                manager.release()
            }
        }
        _audioManager = null
    }
    
    private fun createAudioManager(): AudioManager {
        return AudioManagerImpl()
    }
}