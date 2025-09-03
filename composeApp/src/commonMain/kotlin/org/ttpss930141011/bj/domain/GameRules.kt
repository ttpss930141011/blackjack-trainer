package org.ttpss930141011.bj.domain

data class GameRules(
    val dealerHitsOnSoft17: Boolean = true,           // 莊家軟17要牌
    val doubleAfterSplit: Boolean = true,             // 分牌後可加倍
    val surrenderAllowed: Boolean = true,             // 允許投降
    val blackjackPayout: Double = 1.5,                // 21點賠率 (3:2)
    val maxSplits: Int = 3,                           // 最多分牌次數
    val deckCount: Int = 6,                           // 副牌數量
    val resplitAces: Boolean = false,                 // Ace可否再分牌
    val hitSplitAces: Boolean = false,                // 分牌Ace可否要牌
    val earlyVsLateSurrender: Boolean = false         // 早期投降(true) vs 晚期投降(false)
)