package org.ttpss930141011.bj.domain

data class Hand(internal val cards: List<Card>) {
    
    init {
        require(cards.isNotEmpty()) { "Hand cannot be empty" }
    }
    
    val hardValue: Int = cards.sumOf { it.blackjackValue }
    
    val softValue: Int = run {
        val aces = cards.count { it.rank == Rank.ACE }
        // 最多只有一個Ace可以算11點 (11 + 其他Ace算1 + 其他牌)
        if (aces > 0 && hardValue + 10 <= 21) hardValue + 10 else hardValue
    }
    
    val isSoft: Boolean = softValue != hardValue
    
    val bestValue: Int = if (isSoft) softValue else hardValue
    
    val isBusted: Boolean = hardValue > 21
    
    val canSplit: Boolean = run {
        if (cards.size != 2) return@run false
        // 只有同rank才能分牌 (根據 docs/blackjack-rules.md 第78行: 10,10永遠Stand)
        cards[0].rank == cards[1].rank
    }
    
    val canDouble: Boolean = cards.size == 2
    
    val isBlackjack: Boolean = cards.size == 2 && bestValue == 21
    
    // 添加卡牌 (分牌、要牌時使用)
    fun addCard(card: Card): Hand = Hand(cards + card)
}