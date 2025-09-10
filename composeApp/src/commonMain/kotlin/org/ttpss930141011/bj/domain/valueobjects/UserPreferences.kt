package org.ttpss930141011.bj.domain.valueobjects

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
 * DisplaySettings - UI customization preferences.
 * 
 * LINUS PRINCIPLE: This class exists for future features.
 * Currently empty because no display settings are actually implemented.
 * Add settings ONLY when the feature is working.
 */
data class DisplaySettings(
    // Placeholder for future settings - currently empty
    private val placeholder: Boolean = true
)