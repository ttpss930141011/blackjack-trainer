package org.ttpss930141011.bj.presentation.design

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
        val chipDiameter = 80.dp
        
        // Notification widths
        val notificationTablet = 350.dp
        val notificationDesktop = 300.dp
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
    
    // Adaptive functions
    @Composable
    fun padding(): Dp {
        var result = Space.xxl
        BoxWithConstraints {
            result = when {
                maxWidth < 600.dp -> Space.l
                maxWidth < 840.dp -> Space.xl
                else -> Space.xxl
            }
        }
        return result
    }
    
    @Composable
    fun spacing(): Dp {
        var result = Space.l
        BoxWithConstraints {
            result = when {
                maxWidth < 600.dp -> Space.s
                maxWidth < 840.dp -> Space.m
                else -> Space.l
            }
        }
        return result
    }
    
    @Composable
    fun iconSize(): Dp {
        var result = Size.iconLarge
        BoxWithConstraints {
            result = when {
                maxWidth < 600.dp -> Size.iconSmall
                maxWidth < 840.dp -> Size.iconMedium
                else -> Size.iconLarge
            }
        }
        return result
    }
    
    @Composable
    fun cornerRadius(): Dp {
        var result = Radius.xlarge
        BoxWithConstraints {
            result = when {
                maxWidth < 600.dp -> Radius.medium
                maxWidth < 840.dp -> Radius.large
                else -> Radius.xlarge
            }
        }
        return result
    }
    
    @Composable
    fun notificationWidth(): Dp? {
        var result: Dp? = Size.notificationDesktop
        BoxWithConstraints {
            result = when {
                maxWidth < 600.dp -> null // fillMaxWidth
                maxWidth < 840.dp -> Size.notificationTablet
                else -> Size.notificationDesktop
            }
        }
        return result
    }
    
    @Composable
    fun bettingCircleSize(): Dp {
        var result = 160.dp
        BoxWithConstraints {
            result = when {
                maxWidth < 600.dp -> 120.dp  // Smaller on compact screens
                maxWidth < 840.dp -> 140.dp
                else -> 160.dp
            }
        }
        return result
    }
    
    @Composable
    fun chipSize(): Dp {
        var result = Size.chipDiameter
        BoxWithConstraints {
            result = when {
                maxWidth < 600.dp -> 60.dp    // Smaller chips on compact
                maxWidth < 840.dp -> 70.dp
                else -> Size.chipDiameter     // 80.dp
            }
        }
        return result
    }
}