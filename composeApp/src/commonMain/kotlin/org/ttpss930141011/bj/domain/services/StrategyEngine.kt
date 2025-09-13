package org.ttpss930141011.bj.domain.services

import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.domain.DomainConstants

/**
 * Core strategy engine implementing basic blackjack strategy.
 * Determines optimal player actions based on hand composition and dealer up card.
 */
class StrategyEngine(private val rules: GameRules = GameRules()) {
    
    /**
     * Recommends the optimal action for the given game state.
     * 
     * @param playerHand The player's current hand
     * @param dealerUpCard The dealer's visible card
     * @return The optimal action according to basic strategy
     */
    fun recommend(playerHand: Hand, dealerUpCard: Card): Action {
        return getOptimalAction(playerHand, dealerUpCard, rules)
    }
    
    /**
     * Gets the optimal action for a given hand and dealer card using basic strategy.
     * 
     * Strategy priority order:
     * 1. Surrender (if allowed and applicable)
     * 2. Split (for pairs)
     * 3. Soft hand strategy (hands with Ace counted as 11)
     * 4. Hard hand strategy (all other hands)
     * 
     * @param playerHand The player's current hand
     * @param dealerUpCard The dealer's visible card
     * @param rules The game rules in effect
     * @return The optimal action according to basic strategy
     */
    fun getOptimalAction(
        playerHand: Hand, 
        dealerUpCard: Card, 
        rules: GameRules
    ): Action {
        if (rules.surrenderAllowed && playerHand.cards.size == DomainConstants.HandLimits.INITIAL_HAND_SIZE) {
            val surrenderAction = getSurrenderAction(playerHand, dealerUpCard)
            if (surrenderAction != null) return surrenderAction
        }
        
        if (playerHand.canSplit && shouldSplit(playerHand, dealerUpCard)) {
            return Action.SPLIT
        }
        
        if (playerHand.isSoft) {
            return getSoftHandAction(playerHand, dealerUpCard, rules)
        }
        
        return getHardHandAction(playerHand, dealerUpCard, rules)
    }
    
    /**
     * Determines if a pair should be split (pure boolean decision).
     * No mixed return types - just YES or NO to splitting.
     * 
     * @param playerHand The player's hand (must be a valid pair)
     * @param dealerUpCard The dealer's visible card
     * @return True if pair should be split, false if use normal strategy
     */
    private fun shouldSplit(playerHand: Hand, dealerUpCard: Card): Boolean {
        val cards = playerHand.cards
        val firstCardRank = cards[0].rank
        val dealerValue = dealerUpCard.blackjackValue
        
        return when (firstCardRank) {
            Rank.ACE -> true
            Rank.EIGHT -> true
            Rank.TWO, Rank.THREE -> dealerValue in 2..7
            Rank.SIX -> dealerValue in 2..6
            Rank.SEVEN -> dealerValue in 2..7
            Rank.NINE -> when (dealerValue) {
                7, DomainConstants.BlackjackValues.FACE_CARD_VALUE -> false
                DomainConstants.BlackjackValues.ACE_LOW_VALUE -> false
                in 2..6, 8, 9 -> true
                else -> false
            }
            // All other pairs (TEN, JACK, QUEEN, KING, FIVE, FOUR) -> use normal strategy
            else -> false
        }
    }
    
    /**
     * Determines the optimal action for soft hands (hands containing an Ace counted as 11).
     * 
     * @param playerHand The player's soft hand
     * @param dealerUpCard The dealer's visible card
     * @param rules The game rules in effect
     * @return The optimal action for the soft hand
     */
    private fun getSoftHandAction(playerHand: Hand, dealerUpCard: Card, rules: GameRules): Action {
        val value = playerHand.bestValue
        val dealerValue = dealerUpCard.blackjackValue
        val canDouble = playerHand.canDouble
        
        return when (value) {
            13, 14 -> {
                if (canDouble && dealerValue in 5..6) Action.DOUBLE else Action.HIT
            }
            15, 16 -> {
                if (canDouble && dealerValue in 4..6) Action.DOUBLE else Action.HIT
            }
            DomainConstants.StrategyValues.SOFT_17_VALUE -> {
                if (canDouble && dealerValue in 3..6) Action.DOUBLE else Action.HIT
            }
            18 -> {
                when (dealerValue) {
                    in 3..6 -> if (canDouble) Action.DOUBLE else Action.STAND
                    2, 7, 8 -> Action.STAND
                    else -> Action.HIT
                }
            }
            19, 20, DomainConstants.BlackjackValues.BLACKJACK_TOTAL -> Action.STAND
            else -> Action.HIT
        }
    }
    
    /**
     * Determines the optimal action for hard hands (hands without an Ace counted as 11).
     * 
     * @param playerHand The player's hard hand
     * @param dealerUpCard The dealer's visible card
     * @param rules The game rules in effect
     * @return The optimal action for the hard hand
     */
    private fun getHardHandAction(playerHand: Hand, dealerUpCard: Card, rules: GameRules): Action {
        val value = playerHand.bestValue
        val dealerValue = dealerUpCard.blackjackValue
        val canDouble = playerHand.canDouble
        
        return when (value) {
            in 5..8 -> Action.HIT
            9 -> {
                if (canDouble && dealerValue in 3..6) Action.DOUBLE else Action.HIT
            }
            DomainConstants.BlackjackValues.FACE_CARD_VALUE -> {
                if (canDouble && dealerValue in 2..9) Action.DOUBLE else Action.HIT
            }
            11 -> {
                if (canDouble && dealerValue != DomainConstants.BlackjackValues.ACE_LOW_VALUE) Action.DOUBLE else Action.HIT
            }
            12 -> {
                if (dealerValue in 4..6) Action.STAND else Action.HIT
            }
            in 13..16 -> {
                if (dealerValue in 2..6) Action.STAND else Action.HIT
            }
            in DomainConstants.StrategyValues.SOFT_17_VALUE..DomainConstants.BlackjackValues.BLACKJACK_TOTAL -> Action.STAND
            else -> Action.HIT
        }
    }
    
    /**
     * Determines if surrender is the optimal action for the current hand.
     * Only applies to specific hard hand situations against strong dealer cards.
     * 
     * @param playerHand The player's current hand
     * @param dealerUpCard The dealer's visible card
     * @return Surrender action if optimal, null to defer to other strategies
     */
    private fun getSurrenderAction(playerHand: Hand, dealerUpCard: Card): Action? {
        val playerValue = playerHand.bestValue
        val dealerValue = dealerUpCard.blackjackValue
        val isHardHand = !playerHand.isSoft
        
        if (playerHand.canSplit) return null
        
        return when {
            isHardHand && playerValue == DomainConstants.StrategyValues.HARD_16_VALUE && 
            dealerValue in listOf(DomainConstants.StrategyValues.DEALER_NINE_VALUE, DomainConstants.StrategyValues.DEALER_TEN_VALUE, DomainConstants.StrategyValues.DEALER_ACE_VALUE) -> Action.SURRENDER
            isHardHand && playerValue == DomainConstants.StrategyValues.HARD_15_VALUE && 
            dealerValue == DomainConstants.StrategyValues.DEALER_TEN_VALUE -> Action.SURRENDER
            else -> null
        }
    }
    
}