package org.ttpss930141011.bj.domain.services

import org.ttpss930141011.bj.domain.entities.Game
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.GamePhase
import org.ttpss930141011.bj.domain.enums.HandStatus
import org.ttpss930141011.bj.domain.enums.RoundResult

/**
 * Domain service responsible for round settlement and winnings calculation.
 * Handles result determination, winnings calculation, and final game state updates.
 */
class SettlementService {
    
    /**
     * Settles the current round by calculating results and updating player winnings.
     * 
     * @param game Current game state (must be in settlement phase)
     * @return Updated game state with settlement results applied
     * @throws IllegalArgumentException if preconditions are not met
     */
    fun settleRound(game: Game): Game {
        require(game.phase == GamePhase.SETTLEMENT) { "Not in settlement phase" }
        require(game.dealer.hand != null) { "Dealer hand required for settlement" }
        require(game.hasPlayer) { "No player to settle" }
        require(!game.isSettled) { "Round already settled - cannot settle again" }
        
        val dealerHand = game.dealer.hand!!
        var totalWinnings = 0
        val settledHands = game.playerHands.map { hand ->
            val result = determineResult(hand, dealerHand)
            val handWinnings = calculateWinnings(hand.bet, result, game.rules)
            totalWinnings += handWinnings
            
            val newStatus = when (result) {
                RoundResult.PLAYER_WIN, RoundResult.PLAYER_BLACKJACK -> HandStatus.WIN
                RoundResult.SURRENDER -> HandStatus.SURRENDERED
                RoundResult.DEALER_WIN -> HandStatus.LOSS
                RoundResult.PUSH -> HandStatus.PUSH
            }
            
            hand.copy(status = newStatus)
        }
        
        val updatedPlayer = game.player!!.addChips(totalWinnings)
        
        return game.copy(
            player = updatedPlayer,
            playerHands = settledHands,
            isSettled = true
        )
    }
    
    /**
     * Determines the result of a hand comparison between player and dealer.
     * 
     * @param playerHand The player's hand to evaluate
     * @param dealerHand The dealer's final hand
     * @return The round result for this specific hand
     */
    fun determineResult(playerHand: PlayerHand, dealerHand: Hand): RoundResult {
        val playerValue = playerHand.bestValue
        val dealerValue = dealerHand.bestValue
        val playerBlackjack = playerHand.isBlackjack
        val dealerBlackjack = dealerHand.isBlackjack
        
        return when {
            playerHand.status == HandStatus.SURRENDERED -> RoundResult.SURRENDER
            playerHand.status == HandStatus.BUSTED -> RoundResult.DEALER_WIN
            dealerHand.isBusted -> RoundResult.PLAYER_WIN
            playerBlackjack && dealerBlackjack -> RoundResult.PUSH
            playerBlackjack && !dealerBlackjack -> RoundResult.PLAYER_BLACKJACK
            !playerBlackjack && dealerBlackjack -> RoundResult.DEALER_WIN
            playerValue > dealerValue -> RoundResult.PLAYER_WIN
            playerValue < dealerValue -> RoundResult.DEALER_WIN
            else -> RoundResult.PUSH
        }
    }
    
    /**
     * Calculates the winnings for a given bet and result.
     * 
     * @param bet The original bet amount
     * @param result The round result
     * @param rules The game rules (for blackjack payout rate)
     * @return The total amount to return to player (including original bet if won)
     */
    fun calculateWinnings(bet: Int, result: RoundResult, rules: GameRules): Int {
        return when (result) {
            RoundResult.PLAYER_WIN -> bet * 2
            RoundResult.PLAYER_BLACKJACK -> (bet * (1 + rules.blackjackPayout)).toInt()
            RoundResult.SURRENDER -> bet / 2
            RoundResult.PUSH -> bet
            RoundResult.DEALER_WIN -> 0
        }
    }
}

