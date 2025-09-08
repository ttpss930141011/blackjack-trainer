package org.ttpss930141011.bj.domain.valueobjects

import kotlinx.serialization.Serializable
import org.ttpss930141011.bj.domain.DomainConstants

/**
 * Game rules configuration for blackjack gameplay mechanics.
 * Defines the standard casino rules that affect game strategy and outcomes.
 * 
 * @property dealerHitsOnSoft17 Whether dealer must hit on soft 17 (A-6)
 * @property doubleAfterSplitAllowed Whether doubling down is allowed after splitting
 * @property surrenderAllowed Whether players can surrender their hand
 * @property blackjackPayout Multiplier for natural blackjack wins (default 1.5 = 3:2)
 * @property maxSplits Maximum number of times a hand can be split (3 splits = 4 hands)
 * @property deckCount Number of standard 52-card decks used
 * @property resplitAces Whether Aces can be split again after initial split
 * @property hitSplitAces Whether players can hit on split Aces
 * @property earlyVsLateSurrender Early surrender (before dealer check) vs late surrender
 * @property minimumBet Minimum bet amount required to play
 */
@Serializable
data class GameRules(
    val dealerHitsOnSoft17: Boolean = true,
    val doubleAfterSplitAllowed: Boolean = true,
    val surrenderAllowed: Boolean = true,
    val blackjackPayout: Double = DomainConstants.GameRules.BLACKJACK_PAYOUT_MULTIPLIER,
    val maxSplits: Int = DomainConstants.GameRules.MAX_SPLIT_HANDS - 1,
    val deckCount: Int = DomainConstants.DeckComposition.STANDARD_DECK_COUNT,
    val resplitAces: Boolean = false,
    val hitSplitAces: Boolean = false,
    val earlyVsLateSurrender: Boolean = false,
    val minimumBet: Int = 5
)