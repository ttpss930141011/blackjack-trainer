package org.ttpss930141011.bj.domain

data class GameSession(
    val chips: Int = 500,
    val currentRound: Round? = null,
    val settings: GameRules = GameRules(),
    val stats: SessionStats = SessionStats()
) {
    
    fun startNewRound(
        bet: Int,
        deck: Deck
    ): Pair<GameSession, Deck> {
        require(bet >= 5) { "Minimum bet is 5 chips" }
        require(bet % 5 == 0) { "Bet must be multiple of 5" }
        require(chips >= bet) { "Insufficient chips. Have $chips, need $bet" }
        require(currentRound == null) { "Round already in progress" }
        
        val dealResult = Round.dealInitialCards(bet, deck)
        val newRound = dealResult.round
        val newDeck = dealResult.deck
        
        val newSession = copy(
            chips = chips - bet,
            currentRound = newRound
        )
        
        return Pair(newSession, newDeck)
    }
    
    fun makePlayerAction(action: Action, deck: Deck): Pair<GameSession, Deck> {
        require(currentRound != null) { "No round in progress" }
        require(currentRound.phase == RoundPhase.PLAYER_TURN) { "Not in player turn phase" }
        
        val actionResult = currentRound.playerAction(action, deck)
        val newRound = actionResult.round
        var newDeck = actionResult.deck
        
        val updatedSession = copy(currentRound = newRound)
        
        // 如果player完成，自動進行dealer play
        return if (newRound.phase == RoundPhase.DEALER_TURN) {
            val dealerResult = newRound.dealerPlay(settings, newDeck)
            newDeck = dealerResult.second
            val finalRound = dealerResult.first
            
            val updatedSessionWithDealer = updatedSession.copy(currentRound = finalRound)
            Pair(updatedSessionWithDealer, newDeck)
        } else {
            Pair(updatedSession, newDeck)
        }
    }
    
    fun completeRound(result: RoundResult): GameSession {
        require(currentRound != null) { "No round in progress" }
        
        val winnings = calculateWinnings(result, currentRound.bet, settings)
        val newStats = stats.recordRound(currentRound.decisions)
        
        return copy(
            chips = chips + winnings,
            currentRound = null,
            stats = newStats
        )
    }
    
    private fun calculateWinnings(result: RoundResult, bet: Int, rules: GameRules): Int {
        return when (result) {
            RoundResult.PLAYER_WIN -> bet * 2
            RoundResult.PLAYER_BLACKJACK -> (bet * (1 + rules.blackjackPayout)).toInt()
            RoundResult.PUSH -> bet  // 退還賭注
            RoundResult.DEALER_WIN -> 0  // 輸掉賭注
        }
    }
    
    val isGameOver: Boolean = chips < 5 // 無法下最小注額
}