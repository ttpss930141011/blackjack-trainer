package org.ttpss930141011.bj.domain.valueobjects

import org.ttpss930141011.bj.domain.enums.ChipValue

/**
 * Value object representing a chip stack in the betting spot
 */
data class ChipInSpot(
    val value: ChipValue,
    val count: Int
) {
    init {
        require(count > 0) { "Chip count must be positive" }
    }
}