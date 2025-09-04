package org.ttpss930141011.bj.domain

/**
 * Domain service responsible for round settlement and winnings calculation.
 * Extracted from Game class to improve Single Responsibility Principle.
 */
class SettlementService {
    
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
                RoundResult.SURRENDER -> HandStatus.SURRENDERED // Keep surrendered status for UI
                RoundResult.DEALER_WIN -> HandStatus.LOSS
                RoundResult.PUSH -> HandStatus.PUSH
            }
            
            hand.copy(status = newStatus)
        }
        
        val updatedPlayer = game.player!!.addChips(totalWinnings)
        
        // Domain layer calculates settlement results only - UI/Application layer controls workflow
        return game.copy(
            player = updatedPlayer,
            playerHands = settledHands,
            isSettled = true
            // phase remains SETTLEMENT - let Application layer control transitions
        )
    }
    
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
    
    fun calculateWinnings(bet: Int, result: RoundResult, rules: GameRules): Int {
        return when (result) {
            RoundResult.PLAYER_WIN -> bet * 2 // Return bet + win equal amount
            RoundResult.PLAYER_BLACKJACK -> (bet * (1 + rules.blackjackPayout)).toInt() // Return bet + win at payout rate
            RoundResult.SURRENDER -> bet / 2 // Return half bet (surrender rule)
            RoundResult.PUSH -> bet // Return bet only
            RoundResult.DEALER_WIN -> 0 // Lose bet (already deducted)
        }
    }
}

