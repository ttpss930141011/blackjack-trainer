package org.ttpss930141011.bj.domain.services

import org.ttpss930141011.bj.domain.enums.ChipValue
import org.ttpss930141011.bj.domain.valueobjects.ChipInSpot

/**
 * Domain service for chip composition calculations.
 * Handles optimal chip representation using greedy algorithm for efficient chip stacking.
 */
class ChipCompositionService {
    
    /**
     * Calculates optimal chip composition for a given amount using greedy algorithm.
     * Uses largest denomination chips first to minimize total chip count.
     * 
     * @param amount The total amount to compose (must be non-negative)
     * @return List of ChipInSpot representing optimal composition, empty if amount is 0
     * @throws IllegalArgumentException if amount is negative
     */
    fun calculateOptimalComposition(amount: Int): List<ChipInSpot> {
        require(amount >= 0) { "Amount must be non-negative" }
        
        if (amount == 0) return emptyList()
        
        val chipValues = ChipValue.standardCasinoChips().reversed() // Largest first
        val composition = mutableListOf<ChipInSpot>()
        var remaining = amount
        
        for (chipValue in chipValues) {
            val count = remaining / chipValue.value
            if (count > 0) {
                composition.add(ChipInSpot(chipValue, count))
                remaining -= count * chipValue.value
            }
        }
        
        return composition
    }
    
    /**
     * Calculates the total value of a chip composition.
     * 
     * @param composition List of ChipInSpot to sum
     * @return Total value of all chips in the composition
     */
    fun calculateTotalValue(composition: List<ChipInSpot>): Int {
        return composition.sumOf { it.value.value * it.count }
    }
    
    /**
     * Adds a chip to existing composition and returns optimized result.
     * Recalculates the entire composition to ensure optimal chip distribution.
     * 
     * @param currentComposition Existing chip composition
     * @param chipToAdd Chip value to add to the composition
     * @return New optimized composition with the additional chip value
     */
    fun addChipToComposition(currentComposition: List<ChipInSpot>, chipToAdd: ChipValue): List<ChipInSpot> {
        val currentTotal = calculateTotalValue(currentComposition)
        val newTotal = currentTotal + chipToAdd.value
        return calculateOptimalComposition(newTotal)
    }
}