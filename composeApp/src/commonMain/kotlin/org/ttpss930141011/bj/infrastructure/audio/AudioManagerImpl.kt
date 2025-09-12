package org.ttpss930141011.bj.infrastructure.audio

import org.ttpss930141011.bj.domain.services.AudioManager
import org.ttpss930141011.bj.domain.services.SoundType

/**
 * AudioManagerImpl - Infrastructure implementation of AudioManager
 * 
 * Linus Principle: "Simple implementation, handle failures gracefully"
 * - Uses expect/actual pattern for cross-platform audio
 * - Graceful degradation if audio initialization fails
 * - Boolean enabled state controls all audio playback
 * - No exceptions thrown - audio is optional, game continues
 */
class AudioManagerImpl : AudioManager {
    
    private val platformPlayer = PlatformAudioPlayer()
    private var _isEnabled: Boolean = true
    private var isInitialized: Boolean = false
    
    override val isEnabled: Boolean get() = _isEnabled && isInitialized
    
    /**
     * Initializes the audio system.
     * Should be called during app startup.
     * If initialization fails, audio will be silently disabled.
     */
    suspend fun initialize() {
        try {
            isInitialized = platformPlayer.initialize()
        } catch (e: Exception) {
            isInitialized = false
        }
    }
    
    override suspend fun playCardSound() {
        playSound(SoundType.CARD_DEAL)
    }
    
    override suspend fun playCorrectSound() {
        playSound(SoundType.CORRECT_FEEDBACK)
    }
    
    override suspend fun playWrongSound() {
        playSound(SoundType.WRONG_FEEDBACK)
    }
    
    override fun setEnabled(enabled: Boolean) {
        _isEnabled = enabled
    }
    
    /**
     * Releases audio resources.
     * Should be called when app is being destroyed.
     */
    fun release() {
        try {
            platformPlayer.release()
        } catch (e: Exception) {
            println("Error releasing audio resources: ${e.message}")
        }
    }
    
    /**
     * Sets audio volume.
     * 
     * @param volume Volume between 0.0 and 1.0
     */
    fun setVolume(volume: Float) {
        val clampedVolume = volume.coerceIn(0.0f, 1.0f)
        try {
            platformPlayer.setVolume(clampedVolume)
        } catch (e: Exception) {
            println("Error setting audio volume: ${e.message}")
        }
    }
    
    private suspend fun playSound(soundType: SoundType) {
        if (!isEnabled) return
        
        try {
            platformPlayer.playSound(soundType)
        } catch (e: Exception) {
            // Audio errors should not crash the game - fail silently
        }
    }
}