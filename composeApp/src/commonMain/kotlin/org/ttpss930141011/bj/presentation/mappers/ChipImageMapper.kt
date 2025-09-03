package org.ttpss930141011.bj.presentation.mappers

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import blackjack_strategy_trainer.composeapp.generated.resources.Res
import blackjack_strategy_trainer.composeapp.generated.resources.*

/**
 * Domain-to-UI mapping for chip images
 * Maps chip values to Compose Painter resources
 */
object ChipImageMapper {
    
    @Composable
    fun getChipPainter(value: Int): Painter {
        val resource = when (value) {
            1 -> Res.drawable.chip_1
            5 -> Res.drawable.chip_5
            10 -> Res.drawable.chip_10
            25 -> Res.drawable.chip_25
            50 -> Res.drawable.chip_50
            100 -> Res.drawable.chip_100
            200 -> Res.drawable.chip_200
            500 -> Res.drawable.chip_500
            else -> Res.drawable.chip_25 // Default 25-value chip
        }
        return painterResource(resource)
    }
    
    // Standard chip denominations available in the game
    val standardChipValues = listOf(5, 10, 25, 50, 100, 200, 500)
}