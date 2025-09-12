package org.ttpss930141011.bj.domain.services

/**
 * AudioManager - Domain service for game sound effects
 * 
 * Linus Principle: "Simple interface, no special cases."
 * - Three sounds for three clear game events
 * - Boolean enabled state, no complex volume controls
 * - Suspend functions for non-blocking audio playback
 * 
 * This interface stays in domain layer because it represents
 * business capability (audio feedback) without technical implementation details.
 */
interface AudioManager {
    
    /**
     * Plays the card dealing sound effect.
     * Used when cards are dealt to player or dealer.
     */
    suspend fun playCardSound()
    
    /**
     * Plays the correct decision sound effect.
     * Used when player makes the optimal strategy choice.
     */
    suspend fun playCorrectSound()
    
    /**
     * Plays the wrong decision sound effect.
     * Used when player makes a suboptimal strategy choice.
     */
    suspend fun playWrongSound()
    
    /**
     * Enables or disables all sound effects.
     * When disabled, all play* methods become no-ops.
     * 
     * @param enabled true to enable sounds, false to disable
     */
    fun setEnabled(enabled: Boolean)
    
    /**
     * Current sound enabled state.
     * 
     * @return true if sounds are enabled, false if disabled
     */
    val isEnabled: Boolean
}

/**
 * Sound types supported by the audio system.
 * 
 * This enum provides type safety and clear mapping
 * between game events and audio resources.
 */
enum class SoundType {
    /**
     * Card dealing sound - played when cards are dealt
     */
    CARD_DEAL,
    
    /**
     * Correct decision sound - played for optimal strategy choices
     */
    CORRECT_FEEDBACK,
    
    /**
     * Wrong decision sound - played for suboptimal strategy choices
     */
    WRONG_FEEDBACK
}