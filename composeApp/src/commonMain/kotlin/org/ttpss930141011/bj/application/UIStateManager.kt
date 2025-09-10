package org.ttpss930141011.bj.application

import androidx.compose.runtime.*
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.ChipCompositionService

/**
 * UIStateManager - Focuses ONLY on UI state and notifications
 * 
 * Linus: "Handle error messages and UI state. No game logic shit, no analytics shit."
 */
class UIStateManager(
    private val chipCompositionService: ChipCompositionService
) {
    private var _errorMessage by mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage
    
    private var _ruleChangeNotification by mutableStateOf<String?>(null)
    val ruleChangeNotification: String? get() = _ruleChangeNotification
    
    fun setError(message: String?) {
        _errorMessage = message
    }
    
    fun clearError() {
        _errorMessage = null
    }
    
    fun setRuleChangeNotification(notification: String?) {
        _ruleChangeNotification = notification
    }
    
    fun dismissRuleChangeNotification() {
        _ruleChangeNotification = null
    }
    
    // Chip display utilities
    fun calculateChipComposition(amount: Int): List<ChipInSpot> {
        return if (amount > 0) {
            chipCompositionService.calculateOptimalComposition(amount)
        } else {
            emptyList()
        }
    }
    
    fun handleRuleChangeNotification(
        currentRules: GameRules,
        newRules: GameRules,
        sessionStats: SessionStats
    ) {
        if (currentRules == newRules) {
            _ruleChangeNotification = null
            return
        }
        
        // Simple rule change notification without complex rule tracking
        _ruleChangeNotification = "⚠️ Rule change detected: Starting fresh analytics for new rule set"
    }
}