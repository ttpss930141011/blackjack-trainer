package org.ttpss930141011.bj.domain.entities

import org.ttpss930141011.bj.domain.valueobjects.Hand
import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.DomainConstants

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
    
    // 莊家要牌邏輯：必須檢查是否已爆牌！
    val shouldHit: Boolean = hand?.let { dealerHand ->
        // 已爆牌就不能再要牌
        if (dealerHand.isBusted) return@let false
        
        val value = dealerHand.bestValue
        when {
            value < DomainConstants.BlackjackValues.DEALER_STAND_HARD -> true
            value > DomainConstants.BlackjackValues.DEALER_STAND_HARD -> false
            // value == 17: 軟17根據規則決定，硬17停牌
            value == DomainConstants.BlackjackValues.DEALER_STAND_HARD && dealerHand.isSoft -> true // 默認軟17要牌
            else -> false
        }
    } ?: false
}