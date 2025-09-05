package org.ttpss930141011.bj.presentation.design

import androidx.compose.ui.graphics.Color
import org.ttpss930141011.bj.domain.enums.GamePhase
import org.ttpss930141011.bj.domain.enums.HandStatus

/**
 * Centralized color definitions for game status indicators.
 * Reduces duplication and provides consistent styling across components.
 */
object GameStatusColors {
    
    // Hand status colors
    val winColor = Color(0xFF4CAF50)      // Green
    val lossColor = Color(0xFFF44336)     // Red
    val pushColor = Color(0xFFFFC107)     // Gold
    val bustColor = Color(0xFFF44336)     // Red
    val activeColor = Color(0xFF4CAF50)   // Green
    
    // UI element colors
    val betColor = Color(0xFFFFC107)      // Casino gold
    val casinoGreen = Color(0xFF1B5E20)   // Casino table green
    val casinoGold = Color(0xFFFFC107)    // Casino gold
    
    // Action button colors
    val hitColor = Color(0xFF4CAF50)        // Green
    val standColor = Color(0xFFF44336)       // Red  
    val doubleColor = Color(0xFFFF9800)      // Orange
    val surrenderColor = Color(0xFF9E9E9E)   // Gray
    
    // Phase indicators
    val waitingColor = Color.White.copy(alpha = WAITING_ALPHA)
    val activePhaseColor = Color(0xFF4CAF50)
    val completedPhaseColor = Color(0xFF2E7D32)
    
    fun getHandStatusColor(status: HandStatus): Color {
        return when (status) {
            HandStatus.WIN -> winColor
            HandStatus.LOSS -> lossColor
            HandStatus.PUSH -> pushColor
            HandStatus.BUSTED -> bustColor
            HandStatus.ACTIVE -> activeColor
            HandStatus.SURRENDERED -> lossColor
            else -> Color.White
        }
    }
    
    fun getPhaseColor(phase: GamePhase): Color {
        return when (phase) {
            GamePhase.WAITING_FOR_BETS -> waitingColor
            GamePhase.PLAYER_TURN -> activePhaseColor
            GamePhase.DEALER_TURN -> activePhaseColor
            GamePhase.SETTLEMENT -> completedPhaseColor
            GamePhase.DEALING -> activePhaseColor
        }
    }
    
    // Transparency values
    const val OVERLAY_ALPHA = 0.5f // 半透明覆蓋層
    const val WAITING_ALPHA = 0.7f // 等待狀態透明度
    
    // Casino theme gradients
    val casinoBackgroundGradient = listOf(
        Color(0xFF1B5E20), // Dark green
        Color(0xFF2E7D32), // Medium green
        Color(0xFF388E3C)  // Lighter green
    )
    
    val casinoTableGradient = listOf(
        Color(0xFF2E7D32),
        Color(0xFF1B5E20)
    )
    
    // Overlay colors
    val statusOverlayColor = Color.Black.copy(alpha = OVERLAY_ALPHA)
}