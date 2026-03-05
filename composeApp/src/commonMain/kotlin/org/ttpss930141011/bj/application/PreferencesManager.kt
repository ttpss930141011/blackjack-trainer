package org.ttpss930141011.bj.application

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.ttpss930141011.bj.domain.services.AudioManager
import org.ttpss930141011.bj.domain.valueobjects.UserPreferences
import org.ttpss930141011.bj.infrastructure.DataLoader

/**
 * PreferencesManager - Loads, saves, and applies user preferences.
 *
 * Extracted from GameViewModel to reduce its responsibilities.
 */
internal class PreferencesManager(
    private val persistenceService: PersistenceService,
    private val audioManager: AudioManager,
    private val dataLoader: DataLoader,
    private val coroutineScope: CoroutineScope
) {
    private var _userPreferences by mutableStateOf(UserPreferences())
    val userPreferences: UserPreferences get() = _userPreferences

    fun load(onLoaded: (UserPreferences) -> Unit = {}) {
        coroutineScope.launch {
            _userPreferences = persistenceService.loadUserPreferences()
            applyAudio()
            onLoaded(_userPreferences)
        }
    }

    fun update(newPreferences: UserPreferences) {
        _userPreferences = newPreferences
        applyAudio()
        dataLoader.invalidate("user_preferences")

        coroutineScope.launch {
            try {
                persistenceService.saveUserPreferences(newPreferences)
            } catch (_: Exception) {
                // TODO: surface error to UI
            }
        }
    }

    private fun applyAudio() {
        audioManager.setEnabled(_userPreferences.displaySettings.soundEnabled)
        audioManager.setVolume(_userPreferences.displaySettings.soundVolume)
    }
}
