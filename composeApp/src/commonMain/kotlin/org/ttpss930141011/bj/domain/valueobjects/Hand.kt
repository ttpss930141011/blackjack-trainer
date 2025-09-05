package org.ttpss930141011.bj.domain.valueobjects

import org.ttpss930141011.bj.domain.DomainConstants

data class Hand(internal val cards: List<Card>) {
    
    init {
        require(cards.isNotEmpty()) { "Hand cannot be empty" }
    }
    
    val hardValue: Int = cards.sumOf { it.blackjackValue }
    
    val softValue: Int = run {
        val aces = cards.count { it.rank == Rank.ACE }
        // 最多只有一個Ace可以算11點 (11 + 其他Ace算1 + 其他牌)
        if (aces > 0 && hardValue + DomainConstants.BlackjackValues.ACE_HIGH_VALUE - DomainConstants.BlackjackValues.ACE_LOW_VALUE <= DomainConstants.BlackjackValues.BLACKJACK_TOTAL) {
            hardValue + DomainConstants.BlackjackValues.ACE_HIGH_VALUE - DomainConstants.BlackjackValues.ACE_LOW_VALUE
        } else {
            hardValue
        }
    }
    
    val isSoft: Boolean = softValue != hardValue
    
    val bestValue: Int = if (isSoft) softValue else hardValue
    
    val isBusted: Boolean = hardValue > DomainConstants.BlackjackValues.BUST_THRESHOLD
    
    val canSplit: Boolean = run {
        if (cards.size != DomainConstants.HandLimits.SPLIT_REQUIRED_HAND_SIZE) return@run false
        // 只有同rank才能分牌 (根據 docs/blackjack-rules.md 第78行: 10,10永遠Stand)
        cards[0].rank == cards[1].rank
    }
    
    val canDouble: Boolean = cards.size == DomainConstants.HandLimits.DOUBLE_DOWN_HAND_SIZE
    
    val isBlackjack: Boolean = cards.size == DomainConstants.HandLimits.BLACKJACK_HAND_SIZE && bestValue == DomainConstants.BlackjackValues.BLACKJACK_TOTAL
    
    // 添加卡牌 (分牌、要牌時使用)
    fun addCard(card: Card): Hand = Hand(cards + card)
}