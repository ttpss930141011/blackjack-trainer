package org.ttpss930141011.bj.domain

class StrategyEngine(private val rules: GameRules = GameRules()) {
    
    fun recommend(playerHand: Hand, dealerUpCard: Card): Action {
        return getOptimalAction(playerHand, dealerUpCard, rules)
    }
    
    fun getOptimalAction(
        playerHand: Hand, 
        dealerUpCard: Card, 
        rules: GameRules
    ): Action {
        // 0. 檢查投降機會 (最高優先級，在其他策略前)
        if (rules.surrenderAllowed && playerHand.cards.size == 2) {
            val surrenderAction = getSurrenderAction(playerHand, dealerUpCard)
            if (surrenderAction != null) return surrenderAction
        }
        
        // 1. 優先檢查分牌 (只有前兩張牌時)
        if (playerHand.canSplit) {
            val splitAction = getSplitAction(playerHand, dealerUpCard)
            if (splitAction != null) return splitAction
        }
        
        // 2. 軟手牌策略
        if (playerHand.isSoft) {
            return getSoftHandAction(playerHand, dealerUpCard, rules)
        }
        
        // 3. 硬手牌策略  
        return getHardHandAction(playerHand, dealerUpCard, rules)
    }
    
    private fun getSplitAction(playerHand: Hand, dealerUpCard: Card): Action? {
        val cards = playerHand.cards
        val firstCardRank = cards[0].rank
        val dealerValue = dealerUpCard.blackjackValue
        
        return when (firstCardRank) {
            Rank.ACE -> Action.SPLIT  // A,A 永遠分牌
            Rank.EIGHT -> Action.SPLIT  // 8,8 永遠分牌
            Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING -> Action.STAND  // 10,10 永遠停牌
            Rank.FIVE -> Action.DOUBLE  // 5,5 當作10處理，加倍
            Rank.FOUR -> null  // 4,4 不分牌，按一般策略
            
            Rank.TWO, Rank.THREE -> {
                if (dealerValue in 2..7) Action.SPLIT else null
            }
            Rank.SIX -> {
                if (dealerValue in 2..6) Action.SPLIT else null  
            }
            Rank.SEVEN -> {
                if (dealerValue in 2..7) Action.SPLIT else null
            }
            Rank.NINE -> {
                // 9,9 vs 2-9分牌 (除了7), vs 7,10,A停牌
                when (dealerValue) {
                    7, 10 -> Action.STAND
                    1 -> Action.STAND  // vs Ace
                    in 2..6, 8, 9 -> Action.SPLIT
                    else -> null
                }
            }
            else -> null
        }
    }
    
    private fun getSoftHandAction(playerHand: Hand, dealerUpCard: Card, rules: GameRules): Action {
        val value = playerHand.bestValue
        val dealerValue = dealerUpCard.blackjackValue
        val canDouble = playerHand.canDouble
        
        return when (value) {
            13, 14 -> { // A,2 或 A,3
                if (canDouble && dealerValue in 5..6) Action.DOUBLE else Action.HIT
            }
            15, 16 -> { // A,4 或 A,5  
                if (canDouble && dealerValue in 4..6) Action.DOUBLE else Action.HIT
            }
            17 -> { // A,6
                if (canDouble && dealerValue in 3..6) Action.DOUBLE else Action.HIT
            }
            18 -> { // A,7
                when (dealerValue) {
                    in 3..6 -> if (canDouble) Action.DOUBLE else Action.STAND
                    2, 7, 8 -> Action.STAND
                    else -> Action.HIT  // vs 9,10,A
                }
            }
            19, 20, 21 -> Action.STAND  // A,8 A,9 A,10
            else -> Action.HIT
        }
    }
    
    private fun getHardHandAction(playerHand: Hand, dealerUpCard: Card, rules: GameRules): Action {
        val value = playerHand.bestValue
        val dealerValue = dealerUpCard.blackjackValue
        val canDouble = playerHand.canDouble
        
        return when (value) {
            in 5..8 -> Action.HIT
            9 -> {
                if (canDouble && dealerValue in 3..6) Action.DOUBLE else Action.HIT
            }
            10 -> {
                if (canDouble && dealerValue in 2..9) Action.DOUBLE else Action.HIT
            }
            11 -> {
                // 11 vs A 只能要牌 (不能加倍對抗Ace)
                if (canDouble && dealerValue != 1) Action.DOUBLE else Action.HIT
            }
            12 -> {
                if (dealerValue in 4..6) Action.STAND else Action.HIT
            }
            in 13..16 -> {
                if (dealerValue in 2..6) Action.STAND else Action.HIT
            }
            in 17..21 -> Action.STAND
            else -> Action.HIT  // 超過21點仍然回傳HIT (雖然已經爆牌)
        }
    }
    
    private fun getSurrenderAction(playerHand: Hand, dealerUpCard: Card): Action? {
        val playerValue = playerHand.bestValue
        val dealerValue = dealerUpCard.blackjackValue
        val isHardHand = !playerHand.isSoft
        
        // 重要：對子手牌不投降，應該優先考慮分牌
        if (playerHand.canSplit) return null
        
        // 根據 docs/blackjack-rules.md 第98-100行
        return when {
            // 硬16 vs 莊家9,10,A (但不是對子)
            isHardHand && playerValue == 16 && dealerValue in listOf(9, 10, 1) -> Action.SURRENDER
            // 硬15 vs 莊家10  
            isHardHand && playerValue == 15 && dealerValue == 10 -> Action.SURRENDER
            // 其他情況不投降
            else -> null
        }
    }
    
}