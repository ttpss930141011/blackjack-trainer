package org.ttpss930141011.bj.domain.services

import org.ttpss930141011.bj.domain.enums.ChipValue
import org.ttpss930141011.bj.domain.valueobjects.ChipInSpot

/**
 * Domain service for chip composition calculations
 * Handles optimal chip representation using greedy algorithm
 */
class ChipCompositionService {
    
    /**
     * Calculate optimal chip composition for a given amount
     * Uses greedy algorithm with largest chips first
     * 
     * @param amount The total amount to compose
     * @return List of ChipInSpot representing optimal composition
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
     * Calculate total value of chip composition
     * 
     * @param composition List of ChipInSpot
     * @return Total value of all chips
     */
    fun calculateTotalValue(composition: List<ChipInSpot>): Int {
        return composition.sumOf { it.value.value * it.count }
    }
    
    /**
     * Add a chip to existing composition and return optimized result
     * 
     * @param currentComposition Existing chip composition
     * @param chipToAdd Chip value to add
     * @return New optimized composition
     */
    fun addChipToComposition(currentComposition: List<ChipInSpot>, chipToAdd: ChipValue): List<ChipInSpot> {
        val currentTotal = calculateTotalValue(currentComposition)
        val newTotal = currentTotal + chipToAdd.value
        return calculateOptimalComposition(newTotal)
    }
}