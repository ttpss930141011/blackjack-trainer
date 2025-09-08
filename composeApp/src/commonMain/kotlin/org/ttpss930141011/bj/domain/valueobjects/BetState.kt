package org.ttpss930141011.bj.domain.valueobjects

/**
 * BetState - Simple, correct betting state management following Linus's "good taste" principles.
 * 
 * Eliminates the pendingBet/currentBet duality with the minimal viable solution:
 * - Single amount field (no complex chip composition)
 * - Single boolean for commit state
 * - Zero performance overhead
 * - Obvious implementation that requires no explanation
 * 
 * "Good programmers worry about data structures and their relationships" - Linus
 * This data structure has exactly what it needs, nothing more.
 */
data class BetState(
    val amount: Int = 0,
    val isCommitted: Boolean = false
) {
    
    init {
        require(amount >= 0) { "Bet amount cannot be negative" }
    }
    
    /**
     * Whether this bet has an amount but is not yet committed.
     */
    val isPending: Boolean get() = amount > 0 && !isCommitted
    
    /**
     * Whether this bet is empty (no amount).
     */
    val isEmpty: Boolean get() = amount == 0
    
    /**
     * Whether this bet has a committed amount.
     */
    val hasCommittedBet: Boolean get() = amount > 0 && isCommitted
    
    /**
     * Adds amount to the current bet, creating a new state.
     * 
     * @param moreAmount Amount to add
     * @return New BetState with the amount added
     * @throws IllegalArgumentException if trying to modify committed bet
     */
    fun add(moreAmount: Int): BetState {
        require(!isCommitted) { "Cannot modify committed bet" }
        require(moreAmount > 0) { "Amount to add must be positive" }
        
        return copy(amount = amount + moreAmount)
    }
    
    /**
     * Commits the current bet amount, making it immutable.
     * 
     * @return New BetState with committed status
     * @throws IllegalArgumentException if bet is empty
     */
    fun commit(): BetState {
        require(amount > 0) { "Cannot commit empty bet" }
        return copy(isCommitted = true)
    }
    
    /**
     * Clears the current bet.
     * 
     * @return Empty BetState
     * @throws IllegalArgumentException if bet is committed
     */
    fun clear(): BetState {
        require(!isCommitted) { "Cannot clear committed bet" }
        return BetState()
    }
    
    /**
     * Creates a template for "repeat last bet" functionality.
     * 
     * @return New BetState with same amount but uncommitted status
     */
    fun toTemplate(): BetState {
        return copy(isCommitted = false)
    }
    
    /**
     * Validates that the bet amount is affordable.
     * 
     * @param availableChips Total chips available to the player
     * @return True if the bet is affordable
     */
    fun isAffordable(availableChips: Int): Boolean {
        return amount <= availableChips
    }
}