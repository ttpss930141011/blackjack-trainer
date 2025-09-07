package org.ttpss930141011.bj.domain.entities

/**
 * Player entity with chip management
 * 
 * @param id Unique player identifier
 * @param chips Current chip balance
 */
data class Player(
    val id: String,
    val chips: Int
) {
    /**
     * Deducts chips from player balance
     * 
     * @param amount Amount to deduct
     * @return New player with reduced chip balance
     * @throws IllegalArgumentException if insufficient chips
     */
    fun deductChips(amount: Int): Player {
        require(chips >= amount) { "Insufficient chips" }
        return copy(chips = chips - amount)
    }
    
    /**
     * Adds chips to player balance
     * 
     * @param amount Amount to add
     * @return New player with increased chip balance
     */
    fun addChips(amount: Int): Player = copy(chips = chips + amount)
}