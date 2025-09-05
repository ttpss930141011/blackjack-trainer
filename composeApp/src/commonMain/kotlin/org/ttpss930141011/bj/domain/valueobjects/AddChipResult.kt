package org.ttpss930141011.bj.domain.valueobjects

/**
 * Result of attempting to add a chip
 */
data class AddChipResult(
    val success: Boolean,
    val errorMessage: String?,
    val bettingTable: BettingTableState
)