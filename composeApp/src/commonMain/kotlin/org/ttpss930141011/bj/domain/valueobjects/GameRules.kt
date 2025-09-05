package org.ttpss930141011.bj.domain.valueobjects

import org.ttpss930141011.bj.domain.DomainConstants

data class GameRules(
    val dealerHitsOnSoft17: Boolean = true,           // 莊家軟17要牌
    val doubleAfterSplitAllowed: Boolean = true,      // 分牌後可加倍
    val surrenderAllowed: Boolean = true,             // 允許投降 (默認允許)
    val blackjackPayout: Double = DomainConstants.GameRules.BLACKJACK_PAYOUT_MULTIPLIER, // 21點賠率 (3:2)
    val maxSplits: Int = DomainConstants.GameRules.MAX_SPLIT_HANDS - 1,     // 最多分牌次數 (3次分牌=4手)
    val deckCount: Int = DomainConstants.DeckComposition.STANDARD_DECK_COUNT, // 副牌數量
    val resplitAces: Boolean = false,                 // Ace可否再分牌
    val hitSplitAces: Boolean = false,                // 分牌Ace可否要牌
    val earlyVsLateSurrender: Boolean = false,        // 早期投降(true) vs 晚期投降(false)
    val minimumBet: Int = 5                           // 最小下注額度
)