package org.ttpss930141011.bj.domain.entities

import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.domain.enums.HandStatus
import org.ttpss930141011.bj.domain.enums.GamePhase
import org.ttpss930141011.bj.domain.enums.ChipValue
import org.ttpss930141011.bj.domain.services.BettingPolicy
import org.ttpss930141011.bj.domain.services.RoundManager
import org.ttpss930141011.bj.domain.services.SettlementService

enum class RoundOutcome { WIN, LOSS, PUSH, UNKNOWN }

/**
 * Core game aggregate root that manages the complete blackjack game state.
 * Simplifies the complex Table → Seat → SeatHand architecture into a single cohesive entity.
 * 
 * Encapsulates player management, betting logic, hand progression, and game phase transitions.
 * Delegates complex operations to domain services while maintaining invariants.
 * 
 * Enhanced with BetState support for better UX while maintaining backward compatibility.
 * 
 * @property player Currently active player (null if no player joined)
 * @property playerHands List of player hands (multiple when splitting)
 * @property currentHandIndex Index of the hand currently being played
 * @property betState Simple betting state with amount and commit status
 * @property dealer Dealer entity with cards and game logic
 * @property deck Current deck state for card dealing
 * @property rules Game rules configuration affecting gameplay
 * @property phase Current game phase (betting, dealing, playing, etc.)
 * @property isSettled Whether the current round has been settled
 */
data class Game(
    val player: Player?,
    val playerHands: List<PlayerHand>,
    val currentHandIndex: Int,
    val betState: BetState = BetState(),
    val dealer: Dealer,
    val deck: Deck,
    val rules: GameRules,
    val phase: GamePhase = GamePhase.WAITING_FOR_BETS,
    val isSettled: Boolean = false
) {
    
    companion object {
        private val betting = BettingPolicy()

        fun create(rules: GameRules): Game {
            return Game(
                player = null,
                playerHands = emptyList(),
                currentHandIndex = 0,
                betState = BetState(),
                dealer = Dealer(),
                deck = Deck.shuffled(),
                rules = rules
            )
        }
    }
    
    val hasPlayer: Boolean = player != null
    
    // Deprecated properties removed - use betState.* directly
    
    // New preferred properties
    val hasAnyBet: Boolean = betState.hasCommittedBet || betState.isPending
    val canDealCards: Boolean = betState.isPending && phase == GamePhase.WAITING_FOR_BETS
    val currentHand: PlayerHand? = playerHands.getOrNull(currentHandIndex)
    val canAct: Boolean = hasPlayer && currentHand != null && currentHand.status == HandStatus.ACTIVE
    val allHandsComplete: Boolean = playerHands.all { it.isCompleted }
    
    /**
     * Determines if game should end due to insufficient chips.
     * Only considers game over when waiting for bets (between rounds).
     */
    val isGameOver: Boolean 
        get() = player?.let { p -> 
            p.chips < rules.minimumBet && phase == GamePhase.WAITING_FOR_BETS 
        } ?: false
    
    /**
     * Adds a player to the game.
     * 
     * @param newPlayer Player to join the game
     * @return New Game instance with the player added
     * @throws IllegalArgumentException if game already has a player
     */
    fun addPlayer(newPlayer: Player): Game {
        require(!hasPlayer) { "Game already has a player" }
        return copy(player = newPlayer)
    }
    
    // Betting operations delegated to BettingPolicy
    fun placeBet(amount: Int): Game = betting.placeBet(this, amount)
    fun addToPendingBet(amount: Int): Game = betting.addToPendingBet(this, amount)
    fun clearBet(): Game = betting.clearBet(this)
    fun commitPendingBet(): Game = betting.commitPendingBet(this)
    fun tryAddChipToPendingBet(chipValue: ChipValue): AddChipResult = betting.tryAddChip(this, chipValue)
    
    /**
     * Initiates a new round by dealing cards to player and dealer.
     * Delegates to RoundManager for complex dealing logic.
     */
    fun dealRound(): Game = RoundManager().dealRound(this)
    
    /**
     * Processes a player action (hit, stand, double, split, surrender).
     * Delegates to RoundManager for action validation and execution.
     */
    fun playerAction(action: Action): Game = RoundManager().processPlayerAction(this, action)
    
    /**
     * Executes dealer's automated play according to house rules.
     * Delegates to RoundManager for dealer logic.
     */
    fun dealerPlayAutomated(): Game = RoundManager().processDealerTurn(this)
    
    /**
     * Settles the round by comparing hands and awarding chips.
     * Delegates to SettlementService for payout calculations.
     */
    fun settleRound(): Game = SettlementService().settleRound(this)
    
    fun addChipToBet(chipValue: ChipValue, count: Int = 1): Game = addToPendingBet(chipValue.value * count)

    /**
     * Resets game state for a new round while preserving the current player.
     * Clears hands, bets, and resets phase to betting.
     * 
     * Uses smart deck management: only reshuffles when penetration threshold is reached.
     * This maintains deck continuity for proper card counting and realistic casino simulation.
     * 
     * @return New Game ready for the next round
     */
    fun resetForNewRound(): Game {
        val newDeck = when {
            // Check if remaining cards fall below the penetration threshold
            deck.remainingCards <= rules.calculateReshuffleThreshold() -> deck.reset()
            // Otherwise maintain deck continuity
            else -> deck
        }
        
        return copy(
            playerHands = emptyList(),
            currentHandIndex = 0,
            betState = BetState(),
            dealer = Dealer(),
            deck = newDeck,
            phase = GamePhase.WAITING_FOR_BETS,
            isSettled = false
        )
    }
    
    /**
     * Determines available actions for the current hand.
     * Applies both hand-level and game-level constraints.
     * 
     * @return Set of actions the player can legally take
     */
    fun availableActions(): Set<Action> {
        if (!canAct) return emptySet()
        
        val baseActions = currentHand!!.availableActions(rules)
        val constrainedActions = baseActions.toMutableSet()
        
        if (baseActions.contains(Action.SPLIT) && 
            playerHands.size >= rules.maxSplits + 1) {
            constrainedActions.remove(Action.SPLIT)
        }
        
        if (baseActions.contains(Action.DOUBLE) && 
            (player?.chips ?: 0) < (currentHand?.bet ?: 0)) {
            constrainedActions.remove(Action.DOUBLE)
        }
        
        return constrainedActions
    }
    
    /**
     * Determines the overall outcome of the current round.
     * Based on the status of the primary (first) player hand.
     * 
     * @return Round outcome for game statistics
     * @throws IllegalArgumentException if not in settlement phase
     */
    fun getRoundOutcome(): RoundOutcome {
        require(phase == GamePhase.SETTLEMENT) { "Game must be in settlement phase" }
        
        return if (playerHands.isNotEmpty()) {
            val firstHand = playerHands[0]
            when (firstHand.status) {
                HandStatus.WIN -> RoundOutcome.WIN
                HandStatus.LOSS, HandStatus.BUSTED -> RoundOutcome.LOSS
                HandStatus.PUSH -> RoundOutcome.PUSH
                HandStatus.SURRENDERED -> RoundOutcome.LOSS
                else -> RoundOutcome.UNKNOWN
            }
        } else RoundOutcome.UNKNOWN
    }
    
    /**
     * Determines if the game should automatically advance to the next phase.
     * Used by UI to trigger automatic progression.
     * 
     * @return True if game can advance without player input
     */
    fun shouldAutoAdvance(): Boolean {
        return when (phase) {
            GamePhase.DEALER_TURN -> allHandsComplete
            GamePhase.SETTLEMENT -> !isSettled
            else -> false
        }
    }
}
