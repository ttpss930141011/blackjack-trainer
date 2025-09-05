package org.ttpss930141011.bj.domain.entities

// Player value object
data class Player(
    val id: String,
    val chips: Int
) {
    fun deductChips(amount: Int): Player {
        require(chips >= amount) { "Insufficient chips" }
        return copy(chips = chips - amount)
    }
    
    fun addChips(amount: Int): Player = copy(chips = chips + amount)
}