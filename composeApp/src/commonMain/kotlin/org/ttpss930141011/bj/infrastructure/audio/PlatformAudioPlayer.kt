package org.ttpss930141011.bj.infrastructure.audio

import org.ttpss930141011.bj.domain.services.SoundType

/**
 * Platform-specific audio player interface using expect/actual pattern
 * 
 * This interface defines the contract that each platform must implement
 * for playing audio files. Each platform will handle resource loading
 * and audio playback using their native audio systems.
 */
expect class PlatformAudioPlayer() {
    
    /**
     * Initializes the audio player with preloaded sound resources.
     * This should be called once during app initialization.
     * 
     * @return true if initialization succeeded, false if audio is not available
     */
    suspend fun initialize(): Boolean
    
    /**
     * Plays the specified sound type.
     * 
     * @param soundType The type of sound to play
     * @return true if sound was played successfully, false otherwise
     */
    suspend fun playSound(soundType: SoundType): Boolean
    
    /**
     * Releases audio resources and cleans up the player.
     * Should be called when the app is being destroyed.
     */
    fun release()
    
    /**
     * Sets the volume for all sounds.
     * 
     * @param volume Volume level between 0.0 (silent) and 1.0 (full volume)
     */
    fun setVolume(volume: Float)
}