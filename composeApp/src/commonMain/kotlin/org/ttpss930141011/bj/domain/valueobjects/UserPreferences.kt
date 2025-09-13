package org.ttpss930141011.bj.domain.valueobjects

import kotlinx.serialization.Serializable

/**
 * UserPreferences - Single source of truth for all user customizations.
 * 
 * This eliminates the problem of recreating GameRules and bet preferences
 * every time by providing persistent user settings. Follows the principle
 * of "good taste" - no special cases, just data.
 * 
 * @property preferredRules User's preferred game rules configuration
 * @property lastBetAmount User's last bet amount for "repeat last bet"
 * @property displaySettings UI customization preferences
 */
@Serializable
data class UserPreferences(
    val preferredRules: GameRules = GameRules(),
    val lastBetAmount: Int = 0,
    val displaySettings: DisplaySettings = DisplaySettings()
) {
    
    /**
     * Updates the preferred game rules.
     * 
     * @param newRules New game rules to set as preferred
     * @return Updated UserPreferences with new rules
     */
    fun updatePreferredRules(newRules: GameRules): UserPreferences {
        return copy(preferredRules = newRules)
    }
    
    /**
     * Updates the last bet amount (typically after a successful bet).
     * 
     * @param betAmount New bet amount to remember
     * @return Updated UserPreferences with new bet amount
     */
    fun rememberLastBet(betAmount: Int): UserPreferences {
        require(betAmount >= 0) { "Bet amount cannot be negative" }
        return copy(lastBetAmount = betAmount)
    }
    
    /**
     * Updates display settings.
     * Currently a no-op since no display settings are implemented.
     * 
     * @param newDisplaySettings New display settings to apply
     * @return Updated UserPreferences with new display settings
     */
    fun updateDisplaySettings(newDisplaySettings: DisplaySettings): UserPreferences {
        return copy(displaySettings = newDisplaySettings)
    }
    
    /**
     * Gets the amount for repeating the last bet.
     * 
     * @return Bet amount ready for use in new round
     */
    fun getRepeatBetAmount(): Int {
        return if (lastBetAmount <= 0) {
            // Provide sensible default if no previous bet
            preferredRules.minimumBet
        } else {
            lastBetAmount
        }
    }
    
    /**
     * Checks if user has a meaningful last bet to repeat.
     * 
     * @return True if there's a non-zero bet amount to repeat
     */
    fun hasRepeatableBet(): Boolean {
        return lastBetAmount > 0
    }
}

/**
 * DisplaySettings - UI customization and audio preferences.
 * 
 * LINUS PRINCIPLE: Simple data structure for working features.
 * No complex nested objects, just the essential settings users need.
 */
@Serializable
data class DisplaySettings(
    /**
     * Whether sound effects are enabled.
     * Controls all game audio: card dealing, feedback sounds, etc.
     */
    val soundEnabled: Boolean = true,
    
    /**
     * Audio volume level for all game sounds.
     * Range: 0.0 (silent) to 1.0 (full volume)
     */
    val soundVolume: Float = 0.8f
) {
    /**
     * Creates a copy with updated sound enabled state.
     */
    fun withSoundEnabled(enabled: Boolean): DisplaySettings {
        return copy(soundEnabled = enabled)
    }
    
    /**
     * Creates a copy with updated sound volume.
     * Volume is automatically clamped to valid range [0.0, 1.0].
     */
    fun withSoundVolume(volume: Float): DisplaySettings {
        val clampedVolume = volume.coerceIn(0.0f, 1.0f)
        return copy(soundVolume = clampedVolume)
    }
}