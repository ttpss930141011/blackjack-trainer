package org.ttpss930141011.bj.presentation.design

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Modern Casino Theme System
 * Redesigned with contemporary aesthetics and mobile-first approach
 * Premium look with sophisticated color palette and excellent accessibility
 */
object CasinoTheme {
    // Primary modern casino palette - sophisticated emerald
    val CasinoPrimary = Color(0xFF059669)      // Modern emerald green
    val CasinoPrimaryVariant = Color(0xFF047857) // Deep emerald
    val CasinoAccent = Color(0xFF10B981)       // Bright emerald accent
    
    // Premium accent system - refined and professional
    val CasinoAccentPrimary = Color(0xFF0891B2)    // Professional teal - better than red
    val CasinoAccentSecondary = Color(0xFFF59E0B)  // Warm amber
    val CasinoAccentTertiary = Color(0xFF8B5CF6)   // Luxurious purple
    
    // Modern neutral system
    val CasinoSurface = Color(0xFF0F172A)      // Rich dark slate
    val CasinoSurfaceVariant = Color(0xFF1E293B) // Medium slate
    val CasinoSurfaceLight = Color(0xFF334155)   // Light slate for contrast
    val CasinoBackground = Color(0xFF020617)    // Deep background
    
    // Clean backgrounds for different areas - updated to #216125
    val PageBackground = Color(0xFFBEE3DB)     // Light mint green for pages
    val CardTableBackground = Color(0xFF216125) // Dark green for card table
    val HitButtonBackground = Color(0xFF4CAF50) // Bright green for better contrast on table  
    val BalanceBadgeBackground = Color(0xFF59876B) // Medium green for badge
    val HoverEffectColor = Color(0xFFBCDCC4)   // Hover effect color
    val HeaderBackground = Color(0xFF216125)   // Dark green for header
    
    // Status colors - modern and clear
    val CasinoSuccess = Color(0xFF22C55E)      // Fresh success green
    val CasinoError = Color(0xFFEF4444)        // Clear error red  
    val CasinoWarning = Color(0xFFF59E0B)      // Warm warning amber
    val CasinoInfo = Color(0xFF3B82F6)         // Clean info blue
    
    // Navigation - clean modern design
    val NavigationSurface = Color(0xFF1E293B)     // Clean slate surface
    val NavigationSelected = Color(0xFFE11D48)    // Premium rose accent
    val NavigationUnselected = Color(0xFF64748B)  // Subtle slate gray
    val NavigationBackground = Color(0xFF216125)  // Dark green background for navbar
    
    // Interactive elements
    val ButtonPrimary = Color(0xFF059669)      // Emerald CTA
    val ButtonSecondary = Color(0xFF64748B)    // Subtle secondary
    val ButtonDanger = Color(0xFFEF4444)       // Clear danger
    
    // Balance and money display - premium look
    val BalancePrimary = Color(0xFF0F172A)     // Dark elegant background
    val BalanceAccent = Color.White            // White text for dark backgrounds
    val BalanceBorder = Color(0xFFE2E8F0)      // Subtle border
}

/**
 * Modern semantic color helpers for premium mobile experience
 */
object CasinoSemanticColors {
    @Composable
    fun activeHandColors() = CardDefaults.cardColors(
        containerColor = CasinoTheme.CasinoPrimary.copy(alpha = 0.95f),
        contentColor = Color.White
    )
    
    @Composable 
    fun inactiveHandColors() = CardDefaults.cardColors(
        containerColor = CasinoTheme.CasinoSurfaceLight.copy(alpha = 0.7f),
        contentColor = Color.White.copy(alpha = 0.8f)
    )
    
    @Composable
    fun balanceColors() = CardDefaults.cardColors(
        containerColor = CasinoTheme.BalanceBadgeBackground,
        contentColor = Color.White
    )
    
    @Composable
    fun navigationBarColors() = NavigationBarItemDefaults.colors(
        selectedIconColor = CasinoTheme.NavigationSelected,
        selectedTextColor = CasinoTheme.NavigationSelected,
        unselectedIconColor = CasinoTheme.NavigationUnselected,
        unselectedTextColor = CasinoTheme.NavigationUnselected,
        indicatorColor = CasinoTheme.NavigationSelected.copy(alpha = 0.1f)
    )
    
    @Composable
    fun primaryButtonColors() = ButtonDefaults.buttonColors(
        containerColor = CasinoTheme.ButtonPrimary,
        contentColor = Color.White
    )
    
    @Composable
    fun secondaryButtonColors() = ButtonDefaults.buttonColors(
        containerColor = CasinoTheme.ButtonSecondary,
        contentColor = Color.White
    )
    
    @Composable
    fun dangerButtonColors() = ButtonDefaults.buttonColors(
        containerColor = CasinoTheme.ButtonDanger,
        contentColor = Color.White
    )
}

/**
 * Material3 ColorScheme integration for casino theme
 */
val CasinoColorScheme = darkColorScheme(
    primary = CasinoTheme.CasinoPrimary,
    primaryContainer = CasinoTheme.CasinoPrimaryVariant,
    secondary = CasinoTheme.CasinoAccentSecondary,
    secondaryContainer = CasinoTheme.CasinoAccentPrimary,
    tertiary = CasinoTheme.CasinoAccent,
    surface = CasinoTheme.CasinoSurface,
    surfaceVariant = CasinoTheme.CasinoSurfaceVariant,
    background = CasinoTheme.CasinoBackground,
    error = CasinoTheme.CasinoError,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onSurface = Color.White,
    onBackground = Color.White
)

/**
 * Theme constants to avoid magic numbers
 */
object CasinoThemeConstants {
    // Elevation values
    const val ACTIVE_HAND_ELEVATION = 8
    const val INACTIVE_HAND_ELEVATION = 2
    const val BALANCE_ELEVATION = 6
    
    // Alpha values  
    const val ACTIVE_ALPHA = 0.9f
    const val INACTIVE_ALPHA = 0.5f
    const val OVERLAY_ALPHA = 0.3f
    
    // Animation durations (ms)
    const val HAND_TRANSITION_DURATION = 300
    const val THEME_TRANSITION_DURATION = 200
    
    // Border widths
    const val ACTIVE_BORDER_WIDTH = 2
    const val INACTIVE_BORDER_WIDTH = 1
}