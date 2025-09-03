package org.ttpss930141011.bj.domain

/**
 * Domain representation of the betting table visual state
 * Pure domain logic - no UI dependencies
 */
data class BettingTableState(
    val currentBet: Int,
    val availableBalance: Int,
    val chipComposition: List<ChipInSpot>,
    val availableChips: List<ChipValue>,
    val dealerMessage: String = "Waiting for betting"
) {
    
    // Domain queries
    val canDeal: Boolean = currentBet > 0
    val isEmpty: Boolean = chipComposition.isEmpty()
    val totalChipsInSpot: Int = chipComposition.sumOf { it.count }
    
    companion object {
        /**
         * Create betting table state from game domain model
         */
        fun fromGame(game: Game): BettingTableState {
            require(game.phase == GamePhase.WAITING_FOR_BETS) { 
                "Can only create betting table from WAITING_FOR_BETS phase" 
            }
            require(game.hasPlayer) { "Game must have a player" }
            
            val chipComposition = if (game.currentBet > 0) {
                // Convert bet amount to optimal chip composition
                calculateOptimalChipComposition(game.currentBet)
            } else {
                emptyList()
            }
            
            return BettingTableState(
                currentBet = game.currentBet,
                availableBalance = game.player!!.chips,
                chipComposition = chipComposition,
                availableChips = ChipValue.standardCasinoChips(),
                dealerMessage = "Waiting for betting"
            )
        }
        
        /**
         * Calculate optimal chip composition for a given amount
         * Uses greedy algorithm with largest chips first
         */
        private fun calculateOptimalChipComposition(amount: Int): List<ChipInSpot> {
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
    }
    
    /**
     * Domain behavior - add chip to betting spot
     */
    fun addChip(chipValue: ChipValue): BettingTableState {
        require(currentBet + chipValue.value <= availableBalance) { 
            "Adding chip would exceed available balance" 
        }
        
        val newBet = currentBet + chipValue.value
        val newBalance = availableBalance - chipValue.value
        
        // Update chip composition
        val newComposition = addChipToComposition(chipComposition, chipValue)
        
        return copy(
            currentBet = newBet,
            availableBalance = newBalance,
            chipComposition = newComposition
        )
    }
    
    /**
     * Domain behavior - try to add chip with validation
     */
    fun tryAddChip(chipValue: ChipValue): AddChipResult {
        return if (currentBet + chipValue.value > availableBalance) {
            AddChipResult(
                success = false,
                errorMessage = "Insufficient balance",
                bettingTable = this
            )
        } else {
            AddChipResult(
                success = true,
                errorMessage = null,
                bettingTable = addChip(chipValue)
            )
        }
    }
    
    /**
     * Domain behavior - clear all chips from betting spot
     */
    fun clearBet(): BettingTableState {
        return copy(
            currentBet = 0,
            availableBalance = availableBalance + currentBet, // Restore chips to balance
            chipComposition = emptyList()
        )
    }
    
    /**
     * Convert to Game domain for game flow
     */
    fun toGameBet(game: Game): Game {
        require(game.phase == GamePhase.WAITING_FOR_BETS) { 
            "Can only apply bet in WAITING_FOR_BETS phase" 
        }
        
        return if (currentBet > 0) {
            // Game.placeBet handles validation and chip deduction
            val playerWithRestoredChips = game.player!!.copy(chips = availableBalance + currentBet)
            game.copy(player = playerWithRestoredChips).placeBet(currentBet)
        } else {
            game
        }
    }
    
    private fun addChipToComposition(
        currentComposition: List<ChipInSpot>, 
        newChip: ChipValue
    ): List<ChipInSpot> {
        val existingChipIndex = currentComposition.indexOfFirst { it.value == newChip }
        
        return if (existingChipIndex >= 0) {
            // Stack existing chip
            val updatedChip = currentComposition[existingChipIndex]
                .copy(count = currentComposition[existingChipIndex].count + 1)
            currentComposition.toMutableList().apply {
                this[existingChipIndex] = updatedChip
            }
        } else {
            // Add new chip stack
            currentComposition + ChipInSpot(newChip, 1)
        }
    }
}

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
    
    val totalValue: Int = value.value * count
}

/**
 * Result of attempting to add a chip
 */
data class AddChipResult(
    val success: Boolean,
    val errorMessage: String?,
    val bettingTable: BettingTableState
)

/**
 * Domain enum for chip denominations
 */
enum class ChipValue(val value: Int) {
    FIVE(5),
    TEN(10), 
    TWENTY_FIVE(25),
    FIFTY(50),
    ONE_HUNDRED(100),
    TWO_HUNDRED(200),
    FIVE_HUNDRED(500);
    
    companion object {
        /**
         * Standard casino chip denominations
         */
        fun standardCasinoChips(): List<ChipValue> = listOf(
            FIVE, TEN, TWENTY_FIVE, FIFTY, ONE_HUNDRED, FIVE_HUNDRED
        )
    }
}