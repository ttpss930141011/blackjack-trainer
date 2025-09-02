package org.ttpss930141011.bj.domain

// Dealer entity
data class Dealer(
    val hand: Hand? = null,
    val holeCard: Card? = null // 暗牌
) {
    
    fun dealInitialCard(upCard: Card): Dealer {
        return copy(hand = Hand(listOf(upCard)))
    }
    
    fun dealInitialCards(upCard: Card, holeCard: Card): Dealer {
        return copy(
            hand = Hand(listOf(upCard)), // 初始只顯示明牌
            holeCard = holeCard
        )
    }
    
    fun revealHoleCard(): Dealer {
        require(hand != null) { "No cards dealt to dealer yet" }
        require(holeCard != null) { "No hole card to reveal" }
        val allCards = hand.cards + holeCard
        return copy(hand = Hand(allCards))
    }
    
    fun hit(card: Card): Dealer {
        require(hand != null) { "No cards dealt to dealer yet" }
        val newCards = hand.cards + card
        return copy(hand = Hand(newCards))
    }
    
    val upCard: Card? = hand?.cards?.firstOrNull()
    val shouldHit: Boolean = hand?.let { it.bestValue < 17 || (it.bestValue == 17 && it.isSoft) } ?: false
}