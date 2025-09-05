package org.ttpss930141011.bj.domain.valueobjects

import org.ttpss930141011.bj.domain.entities.Game

/**
 * Result of attempting to add a chip to pending bet
 * Part of the chip-by-chip betting workflow
 */
data class AddChipResult(
    val success: Boolean,
    val errorMessage: String?,
    val updatedGame: Game
)