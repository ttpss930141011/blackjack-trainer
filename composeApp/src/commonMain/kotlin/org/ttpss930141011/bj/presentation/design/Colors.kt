package org.ttpss930141011.bj.presentation.design

import androidx.compose.ui.graphics.Color
import org.ttpss930141011.bj.domain.enums.GamePhase
import org.ttpss930141011.bj.domain.enums.HandStatus

/**
 * DEPRECATED: Use CasinoTheme instead
 * Centralized color definitions for game status indicators.
 * This object is maintained for backward compatibility but should be migrated to CasinoTheme.
 * 
 * Migration Path:
 * - Replace direct usage with CasinoTheme.* equivalents
 * - Use MaterialTheme.colorScheme.* where appropriate
 * - Apply CasinoTheme wrapper at app level
 */
@Deprecated("Use CasinoTheme instead for consistent theming")
object GameStatusColors {
    
    // Hand status colors - redirected to CasinoTheme
    val winColor = CasinoTheme.CasinoSuccess
    val lossColor = CasinoTheme.CasinoError
    val pushColor = CasinoTheme.CasinoAccentSecondary
    val bustColor = CasinoTheme.CasinoError
    val activeColor = CasinoTheme.CasinoPrimary
    
    // UI element colors - redirected to CasinoTheme
    val betColor = CasinoTheme.CasinoAccentSecondary
    val casinoGreen = CasinoTheme.CasinoPrimary
    val casinoGold = CasinoTheme.CasinoAccentSecondary
    
    // Action button colors - redirected to CasinoTheme
    val hitColor = CasinoTheme.CasinoSuccess
    val standColor = CasinoTheme.CasinoError
    val doubleColor = CasinoTheme.CasinoWarning
    val surrenderColor = Color.Gray
    
    // Phase indicators
    val waitingColor = Color.White.copy(alpha = WAITING_ALPHA)
    val activePhaseColor = CasinoTheme.CasinoPrimary
    val completedPhaseColor = CasinoTheme.CasinoPrimaryVariant
    
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
    
    // Casino theme gradients - updated to use CasinoTheme
    val casinoBackgroundGradient = listOf(
        CasinoTheme.CasinoBackground,
        CasinoTheme.CasinoPrimary,
        CasinoTheme.CasinoAccent
    )
    
    val casinoTableGradient = listOf(
        CasinoTheme.CasinoSurface,
        CasinoTheme.CasinoPrimary
    )
    
    // Overlay colors
    val statusOverlayColor = Color.Black.copy(alpha = OVERLAY_ALPHA)
}