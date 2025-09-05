package org.ttpss930141011.bj.domain.valueobjects

import org.ttpss930141011.bj.domain.entities.Game
import org.ttpss930141011.bj.domain.enums.GamePhase
import org.ttpss930141011.bj.domain.enums.ChipValue

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
                BettingTableState.calculateOptimalChipComposition(game.currentBet)
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
        fun calculateOptimalChipComposition(amount: Int): List<ChipInSpot> {
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
        
        // Always recalculate optimal composition for consolidation
        val newComposition = BettingTableState.calculateOptimalChipComposition(newBet)
        
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
     * Apply betting table state to game domain model.
     * 
     * This method bridges the betting UI state with the core game logic by:
     * 1. Restoring the player's total chip balance (available + bet)
     * 2. Using Game.placeBet() for proper validation and state management
     * 
     * Critical: This prevents double chip deduction since we restore the full
     * balance before calling placeBet(), which will correctly deduct the bet amount.
     * 
     * @param game Current game in WAITING_FOR_BETS phase
     * @return Game with bet applied and chips properly deducted
     * @throws IllegalArgumentException if game is not in betting phase
     */
    fun toGameBet(game: Game): Game {
        require(game.phase == GamePhase.WAITING_FOR_BETS) { 
            "Can only apply bet in WAITING_FOR_BETS phase" 
        }
        
        return if (currentBet > 0) {
            // Step 1: Restore player's total chip balance to prevent double deduction
            val totalChips = availableBalance + currentBet
            val playerWithRestoredChips = game.player!!.copy(chips = totalChips)
            val gameWithPlayer = game.copy(player = playerWithRestoredChips)
            
            // Step 2: Apply bet using domain logic (will correctly deduct chips)
            gameWithPlayer.placeBet(currentBet)
        } else {
            game
        }
    }
    
}

