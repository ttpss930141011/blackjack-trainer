package org.ttpss930141011.bj.domain

// Rich Round aggregate with behavior
data class Round(
    val playerHand: Hand,
    val dealerHand: Hand,
    val bet: Int,
    val phase: RoundPhase,
    val decisions: List<PlayerDecision> = emptyList(),
    val playerHands: List<Hand> = emptyList(), // Split hands support
    val currentHandIndex: Int = 0 // Current hand being played in split
) {
    
    // 當前正在玩的手牌（支援split）
    val currentHand: Hand = if (playerHands.isNotEmpty()) playerHands[currentHandIndex] else playerHand
    val isSplitRound: Boolean = playerHands.isNotEmpty()
    
    // Rich domain behavior - state transitions
    fun playerAction(action: Action, deck: Deck, rules: GameRules = GameRules()): ActionResult {
        require(phase == RoundPhase.PLAYER_TURN) { "Not in player turn phase" }
        
        // 特殊處理：Split需要不同的邏輯流程
        if (action == Action.SPLIT) {
            require(currentHand.canSplit) { "Cannot split this hand" }
            require(currentHand.cards.size == 2) { "Can only split initial two cards" }
            
            val dealResult = deck.dealCards(2) // 為兩手各發一張新牌
            val newCards = dealResult.first
            val newDeck = dealResult.second
            
            // 創建兩個新手牌
            val hand1 = Hand(listOf(currentHand.cards[0], newCards[0]))
            val hand2 = Hand(listOf(currentHand.cards[1], newCards[1]))
            
            val newDecision = PlayerDecision(action, true) // Split總是正確的策略決定
            val newDecisions = decisions + newDecision
            
            return ActionResult(
                round = copy(
                    playerHands = listOf(hand1, hand2),
                    currentHandIndex = 0,
                    phase = RoundPhase.PLAYER_TURN,
                    decisions = newDecisions
                ),
                deck = newDeck
            )
        }
        
        // 一般行動邏輯
        val (newHand, newDeck) = when (action) {
            Action.HIT -> {
                val cardResult = deck.dealCard()
                Pair(currentHand.addCard(cardResult.first), cardResult.second)
            }
            Action.STAND -> Pair(currentHand, deck)
            Action.DOUBLE -> {
                require(currentHand.cards.size == 2) { "Can only double on first two cards" }
                val cardResult = deck.dealCard()
                Pair(currentHand.addCard(cardResult.first), cardResult.second)
            }
            Action.SURRENDER -> {
                require(rules.surrenderAllowed) { "Surrender not allowed by game rules" }
                require(currentHand.cards.size == 2) { "Can only surrender on first two cards" }
                require(!isSplitRound) { "Cannot surrender after split" }
                Pair(currentHand, deck)
            }
            Action.SPLIT -> error("Split should be handled above") // 不應該到這裡
        }
        
        // Calculate correct strategy decision using StrategyEngine
        val strategyEngine = StrategyEngine(rules)
        val recommendedAction = strategyEngine.recommend(
            playerHand = currentHand,
            dealerUpCard = dealerHand.cards.firstOrNull() ?: error("Dealer must have up card")
        )
        val isCorrect = action == recommendedAction
        val newDecision = PlayerDecision(action, isCorrect)
        val newDecisions = decisions + newDecision
        
        // 更新手牌（split或一般情況）
        val (updatedRound, nextPhase) = if (isSplitRound) {
            // Split情況：更新當前手牌
            val updatedHands = playerHands.toMutableList()
            updatedHands[currentHandIndex] = newHand
            
            // 決定下一步：下一手或dealer turn
            val shouldContinueToNextHand = when {
                action == Action.SURRENDER -> false
                action == Action.DOUBLE -> false // Double後自動切換
                newHand.isBusted -> false // 爆牌後自動切換
                newHand.bestValue == 21 -> false // 21點後自動切換
                action == Action.STAND -> false // Stand後切換
                else -> true
            }
            
            val (nextIndex, nextPhase) = if (shouldContinueToNextHand && currentHandIndex == 0) {
                // 繼續第二手
                Pair(1, RoundPhase.PLAYER_TURN)
            } else {
                // 所有手牌完成，進入dealer turn
                Pair(currentHandIndex, RoundPhase.DEALER_TURN)
            }
            
            Pair(
                copy(
                    playerHands = updatedHands,
                    currentHandIndex = nextIndex,
                    decisions = newDecisions
                ),
                nextPhase
            )
        } else {
            // 一般情況：更新主手牌
            val nextPhase = when {
                action == Action.SURRENDER -> RoundPhase.COMPLETED
                action == Action.STAND || action == Action.DOUBLE -> RoundPhase.DEALER_TURN
                newHand.isBusted -> RoundPhase.COMPLETED
                newHand.bestValue == 21 -> RoundPhase.DEALER_TURN
                else -> RoundPhase.PLAYER_TURN
            }
            
            Pair(
                copy(
                    playerHand = newHand,
                    decisions = newDecisions
                ),
                nextPhase
            )
        }
        
        val finalRound = updatedRound.copy(phase = nextPhase)
        return ActionResult(finalRound, newDeck)
    }
    
    // Dealer play according to rules
    fun dealerPlay(rules: GameRules, deck: Deck): Pair<Round, Deck> {
        require(phase == RoundPhase.DEALER_TURN) { "Not in dealer turn phase" }
        
        var currentDealerHand = dealerHand
        
        var currentDeck = deck
        
        // Dealer抽牌到17+ (軟17規則)
        while (shouldDealerHit(currentDealerHand, rules)) {
            val (newCard, newDeck) = currentDeck.dealCard()
            currentDeck = newDeck
            currentDealerHand = currentDealerHand.addCard(newCard)
        }
        
        val newRound = copy(
            dealerHand = currentDealerHand,
            phase = RoundPhase.COMPLETED
        )
        
        return newRound to currentDeck
    }
    
    // 可用行動計算
    fun availableActions(rules: GameRules = GameRules()): Set<Action> {
        if (phase != RoundPhase.PLAYER_TURN) return emptySet()
        
        val actions = mutableSetOf<Action>()
        
        val hand = currentHand // 使用currentHand支援split
        
        // 21點只能站牌
        if (hand.bestValue == 21) {
            actions.add(Action.STAND)
            return actions
        }
        
        // 基本行動 (未滿21)
        if (hand.bestValue < 21) {
            actions.add(Action.HIT)
        }
        actions.add(Action.STAND)
        
        // 加倍條件 (前兩張牌且未滿21)
        if (hand.cards.size == 2 && !hand.isBusted && hand.bestValue < 21) {
            actions.add(Action.DOUBLE)
        }
        
        // 分牌條件 (前兩張牌且可分牌，且還沒分牌)
        if (hand.cards.size == 2 && hand.canSplit && !isSplitRound) {
            actions.add(Action.SPLIT)
        }
        
        // 投降條件 (根據 docs/blackjack-rules.md)
        if (rules.surrenderAllowed && 
            hand.cards.size == 2 && 
            !isSplitRound &&
            hand.bestValue < 21) {
            actions.add(Action.SURRENDER)
        }
        
        return actions
    }
    
    // Round整體正確性計算
    fun calculateOverallCorrectness(allDecisions: List<PlayerDecision>): RoundCorrectness {
        val totalDecisions = allDecisions.size
        val correctCount = allDecisions.count { it.isCorrect }
        
        return RoundCorrectness(
            isCorrect = correctCount == totalDecisions,
            totalDecisions = totalDecisions
        )
    }
    
    companion object {
        fun dealInitialCards(bet: Int, deck: Deck): ActionResult {
            val dealResult1 = deck.dealCards(4) // 2張給玩家，2張給莊家（1張暗牌）
            val cards = dealResult1.first
            val newDeck = dealResult1.second
            
            val playerCards = listOf(cards[0], cards[1])
            val dealerCards = listOf(cards[2]) // 只有明牌
            
            val round = Round(
                playerHand = Hand(playerCards),
                dealerHand = Hand(dealerCards),
                bet = bet,
                phase = RoundPhase.PLAYER_TURN
            )
            
            return ActionResult(round, newDeck)
        }
    }
    
    // Domain service methods
    private fun shouldDealerHit(hand: Hand, rules: GameRules): Boolean {
        val value = hand.bestValue
        return when {
            value < 17 -> true
            value > 17 -> false
            value == 17 && hand.isSoft && rules.dealerHitsOnSoft17 -> true
            else -> false
        }
    }
    
}

enum class RoundPhase {
    DEALING,         // 發牌階段
    PLAYER_TURN,     // 玩家決策階段
    DEALER_TURN,     // 莊家行動階段
    COMPLETED        // 完成階段
}

enum class RoundResult {
    PLAYER_WIN,       // 玩家勝利
    PLAYER_BLACKJACK, // 玩家21點
    DEALER_WIN,       // 莊家勝利
    SURRENDER,        // 投降 - 退回一半賭注
    PUSH              // 平手
}

// Rich domain value objects
data class PlayerDecision(
    val action: Action,
    val isCorrect: Boolean
)

data class RoundCorrectness(
    val isCorrect: Boolean,
    val totalDecisions: Int
)

data class ActionResult(
    val round: Round,
    val deck: Deck
)