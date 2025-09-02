package org.ttpss930141011.bj.domain

data class DecisionFeedback(
    val playerAction: Action,
    val optimalAction: Action,
    val isCorrect: Boolean,
    val explanation: String
) {
    companion object {
        fun evaluate(
            playerHand: Hand,
            dealerUpCard: Card,
            playerAction: Action,
            strategyEngine: StrategyEngine,
            rules: GameRules
        ): DecisionFeedback {
            val optimalAction = strategyEngine.getOptimalAction(playerHand, dealerUpCard, rules)
            val isCorrect = playerAction == optimalAction
            
            val explanation = generateExplanation(
                playerHand = playerHand,
                dealerUpCard = dealerUpCard,
                playerAction = playerAction,
                optimalAction = optimalAction,
                isCorrect = isCorrect
            )
            
            return DecisionFeedback(
                playerAction = playerAction,
                optimalAction = optimalAction,
                isCorrect = isCorrect,
                explanation = explanation
            )
        }
        
        private fun generateExplanation(
            playerHand: Hand,
            dealerUpCard: Card,
            playerAction: Action,
            optimalAction: Action,
            isCorrect: Boolean
        ): String {
            val playerValue = playerHand.bestValue
            val dealerValue = dealerUpCard.blackjackValue
            val handType = when {
                playerHand.canSplit -> "pair"
                playerHand.isSoft -> "soft"
                else -> "hard"
            }
            
            return if (isCorrect) {
                buildString {
                    append("✅ Correct! ")
                    append("With $handType $playerValue vs dealer $dealerValue, ")
                    append("the optimal play is ${optimalAction.name}. ")
                    append(getStrategyReasoning(playerHand, dealerUpCard, optimalAction))
                }
            } else {
                buildString {
                    append("❌ Incorrect. ")
                    append("You chose ${playerAction.name}, but should ${optimalAction.name}. ")
                    append("With $handType $playerValue vs dealer $dealerValue, ")
                    append(getStrategyReasoning(playerHand, dealerUpCard, optimalAction))
                }
            }
        }
        
        private fun getStrategyReasoning(
            playerHand: Hand,
            dealerUpCard: Card,
            action: Action
        ): String {
            val dealerValue = dealerUpCard.blackjackValue
            
            return when {
                playerHand.canSplit -> when (action) {
                    Action.SPLIT -> "Always split this pair for better expected value."
                    Action.STAND -> "Never split this pair - treat as a strong hand."
                    Action.DOUBLE -> "Don't split - this pair should be doubled as a strong total."
                    else -> "This pair requires careful consideration based on dealer's upcard."
                }
                
                playerHand.isSoft -> when (action) {
                    Action.DOUBLE -> "Soft hands can double safely against weak dealer cards ($dealerValue)."
                    Action.HIT -> when {
                        dealerValue in 9..11 || dealerValue == 1 -> "Hit against strong dealer cards."
                        else -> "Hit to improve this soft total."
                    }
                    Action.STAND -> "This soft total is strong enough to stand."
                    else -> "Soft hands offer flexibility with the Ace."
                }
                
                else -> when (action) {
                    Action.HIT -> when {
                        playerHand.bestValue <= 11 -> "Always hit with 11 or less - cannot bust."
                        dealerValue >= 7 -> "Hit against strong dealer upcards (7-A)."
                        else -> "Hit to improve against this dealer upcard."
                    }
                    Action.STAND -> when {
                        playerHand.bestValue >= 17 -> "Always stand with 17 or higher."
                        dealerValue in 2..6 -> "Stand against weak dealer upcards (2-6)."
                        else -> "Stand with this total in this situation."
                    }
                    Action.DOUBLE -> "Double down for extra profit with this favorable situation."
                    else -> "Follow basic strategy for optimal play."
                }
            }
        }
    }
}