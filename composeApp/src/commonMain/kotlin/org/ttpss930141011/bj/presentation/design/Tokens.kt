package org.ttpss930141011.bj.presentation.design

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import org.ttpss930141011.bj.presentation.layout.ScreenWidth

/**
 * Unified design system - single source of truth for all sizing and spacing
 */
object Tokens {
    
    // Base spacing scale (8dp grid system)
    object Space {
        val xs = 4.dp
        val s = 8.dp
        val m = 12.dp
        val l = 16.dp
        val xl = 24.dp
        val xxl = 32.dp
        val xxxl = 48.dp
    }
    
    // Component sizing
    object Size {
        val iconSmall = 20.dp
        val iconMedium = 24.dp
        val iconLarge = 28.dp
        
        val buttonHeight = 48.dp
        val touchTarget = 48.dp
        val chipDiameter = 48.dp
        
        // Notification widths (moved to AppConstants)
        val notificationTablet = AppConstants.Dimensions.NOTIFICATION_TABLET_WIDTH.dp
        val notificationDesktop = AppConstants.Dimensions.NOTIFICATION_DESKTOP_WIDTH.dp
    }
    
    // Card dimensions
    data class CardDimensions(val width: Dp, val height: Dp)
    
    object Card {
        val small = CardDimensions(50.dp, 75.dp)
        val medium = CardDimensions(70.dp, 105.dp)
        val large = CardDimensions(90.dp, 135.dp)
    }
    
    // Radius values
    object Radius {
        val small = 8.dp
        val medium = 12.dp
        val large = 16.dp
        val xlarge = 24.dp
    }
    
    // Typography scale
    object Typography {
        // Action button text sizes
        val actionButtonIconCompact = 12.sp
        val actionButtonIconExpanded = 16.sp
        val actionButtonTextExpanded = 14.sp
        val actionButtonHintCompact = 10.sp
        val actionButtonHintExpanded = 14.sp
        
        // General text sizes
        val captionSmall = 10.sp
        val captionMedium = 12.sp
        val bodySmall = 14.sp
        val bodyMedium = 16.sp
        val titleSmall = 18.sp
    }
    
    // Adaptive functions - optimized single-source approach
    fun padding(screenWidth: ScreenWidth): Dp = when (screenWidth) {
        ScreenWidth.COMPACT -> Space.l
        ScreenWidth.MEDIUM -> Space.xl
        ScreenWidth.EXPANDED -> Space.xxl
    }
    
    fun spacing(screenWidth: ScreenWidth): Dp = when (screenWidth) {
        ScreenWidth.COMPACT -> Space.s
        ScreenWidth.MEDIUM -> Space.m
        ScreenWidth.EXPANDED -> Space.l
    }
    
    fun iconSize(screenWidth: ScreenWidth): Dp = when (screenWidth) {
        ScreenWidth.COMPACT -> Size.iconSmall
        ScreenWidth.MEDIUM -> Size.iconMedium
        ScreenWidth.EXPANDED -> Size.iconLarge
    }
    
    fun cornerRadius(screenWidth: ScreenWidth): Dp = when (screenWidth) {
        ScreenWidth.COMPACT -> Radius.medium
        ScreenWidth.MEDIUM -> Radius.large
        ScreenWidth.EXPANDED -> Radius.xlarge
    }
    
    fun notificationWidth(screenWidth: ScreenWidth): Dp? = when (screenWidth) {
        ScreenWidth.COMPACT -> null // fillMaxWidth
        ScreenWidth.MEDIUM -> AppConstants.Dimensions.NOTIFICATION_TABLET_WIDTH.dp
        ScreenWidth.EXPANDED -> AppConstants.Dimensions.NOTIFICATION_DESKTOP_WIDTH.dp
    }
    
    fun bettingCircleSize(screenWidth: ScreenWidth): Dp = when (screenWidth) {
        ScreenWidth.COMPACT -> AppConstants.Dimensions.BETTING_CIRCLE_COMPACT.dp
        ScreenWidth.MEDIUM -> AppConstants.Dimensions.BETTING_CIRCLE_MEDIUM.dp
        ScreenWidth.EXPANDED -> AppConstants.Dimensions.BETTING_CIRCLE_EXPANDED.dp
    }
    
    fun chipSize(screenWidth: ScreenWidth): Dp = when (screenWidth) {
        ScreenWidth.COMPACT -> AppConstants.Dimensions.CHIP_SIZE_COMPACT.dp
        ScreenWidth.MEDIUM -> AppConstants.Dimensions.CHIP_SIZE_MEDIUM.dp
        ScreenWidth.EXPANDED -> Size.chipDiameter
    }
}