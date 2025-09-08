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
 * Separates display concerns from game logic for clean architecture.
 */
data class DisplaySettings(
    val showCardValues: Boolean = true,
    val animationSpeed: Float = 1.0f,
    val soundEnabled: Boolean = true,
    val showProbabilities: Boolean = false,
    val autoAdvanceRounds: Boolean = false,
    val compactLayout: Boolean = false
) {
    
    init {
        require(animationSpeed in 0.1f..3.0f) { 
            "Animation speed must be between 0.1 and 3.0" 
        }
    }
    
    /**
     * Updates a specific display setting.
     */
    fun withShowCardValues(show: Boolean): DisplaySettings = copy(showCardValues = show)
    fun withAnimationSpeed(speed: Float): DisplaySettings = copy(animationSpeed = speed)
    fun withSoundEnabled(enabled: Boolean): DisplaySettings = copy(soundEnabled = enabled)
    fun withShowProbabilities(show: Boolean): DisplaySettings = copy(showProbabilities = show)
    fun withAutoAdvanceRounds(auto: Boolean): DisplaySettings = copy(autoAdvanceRounds = auto)
    fun withCompactLayout(compact: Boolean): DisplaySettings = copy(compactLayout = compact)
}