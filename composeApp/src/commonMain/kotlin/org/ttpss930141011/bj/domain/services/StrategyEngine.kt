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
        
        if (playerHand.canSplit) {
            val splitAction = getSplitAction(playerHand, dealerUpCard)
            if (splitAction != null) return splitAction
        }
        
        if (playerHand.isSoft) {
            return getSoftHandAction(playerHand, dealerUpCard, rules)
        }
        
        return getHardHandAction(playerHand, dealerUpCard, rules)
    }
    
    /**
     * Determines the optimal split action for pair hands.
     * 
     * @param playerHand The player's hand (must be a valid pair)
     * @param dealerUpCard The dealer's visible card
     * @return Split action if splitting is optimal, null to defer to regular strategy
     */
    private fun getSplitAction(playerHand: Hand, dealerUpCard: Card): Action? {
        val cards = playerHand.cards
        val firstCardRank = cards[0].rank
        val dealerValue = dealerUpCard.blackjackValue
        
        return when (firstCardRank) {
            Rank.ACE -> Action.SPLIT
            Rank.EIGHT -> Action.SPLIT
            Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING -> Action.STAND
            Rank.FIVE -> Action.DOUBLE
            Rank.FOUR -> null
            
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
                when (dealerValue) {
                    7, DomainConstants.BlackjackValues.FACE_CARD_VALUE -> Action.STAND
                    DomainConstants.BlackjackValues.ACE_LOW_VALUE -> Action.STAND
                    in 2..6, 8, 9 -> Action.SPLIT
                    else -> null
                }
            }
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