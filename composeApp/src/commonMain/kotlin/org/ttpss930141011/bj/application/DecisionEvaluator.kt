package org.ttpss930141011.bj.application

import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.*

/**
 * Application service for evaluating player decisions against optimal strategy.
 * 
 * This service encapsulates the strategy evaluation logic, separating decision assessment
 * from learning record management. Part of the DDD Application layer.
 */
class DecisionEvaluator {
    
    private val strategyEngine = StrategyEngine()
    
    /**
     * Evaluates a player's decision and provides feedback.
     * 
     * @param handBeforeAction The player's hand state before taking action
     * @param dealerUpCard The dealer's visible card
     * @param playerAction The action the player chose to take
     * @param rules The game rules in effect
     * @return DecisionFeedback containing correctness assessment and explanation
     */
    fun evaluateDecision(
        handBeforeAction: PlayerHand,
        dealerUpCard: Card,
        playerAction: Action,
        rules: GameRules
    ): DecisionFeedback {
        return DecisionFeedback.evaluate(
            playerHand = Hand(handBeforeAction.cards),
            dealerUpCard = dealerUpCard,
            playerAction = playerAction,
            strategyEngine = strategyEngine,
            rules = rules
        )
    }
    
    /**
     * Gets the optimal action for a given game state.
     * 
     * @param playerHand The current player hand
     * @param dealerUpCard The dealer's visible card
     * @param rules The game rules in effect
     * @return The optimal action according to basic strategy
     */
    fun getOptimalAction(
        playerHand: Hand,
        dealerUpCard: Card,
        rules: GameRules
    ): Action {
        return strategyEngine.getOptimalAction(playerHand, dealerUpCard, rules)
    }
    
    /**
     * Gets the optimal action for a PlayerHand (convenience method).
     * 
     * @param playerHand The player hand to evaluate
     * @param dealerUpCard The dealer's visible card  
     * @param rules The game rules in effect
     * @return The optimal action according to basic strategy
     */
    fun getOptimalAction(
        playerHand: PlayerHand,
        dealerUpCard: Card,
        rules: GameRules
    ): Action {
        return getOptimalAction(Hand(playerHand.cards), dealerUpCard, rules)
    }
}