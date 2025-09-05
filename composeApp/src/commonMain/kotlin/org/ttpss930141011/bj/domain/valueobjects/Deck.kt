package org.ttpss930141011.bj.domain.valueobjects

import kotlin.ConsistentCopyVisibility

// Rich Deck aggregate for card dealing
@ConsistentCopyVisibility
data class Deck private constructor(
    private val cards: List<Card>,
    private val dealtCards: List<Card> = emptyList()
) {
    
    companion object {
        fun createStandardDeck(numberOfDecks: Int = 6): Deck {
            require(numberOfDecks in 1..8) { "Number of decks must be between 1 and 8" }
            
            val singleDeck = buildList {
                Suit.values().forEach { suit ->
                    Rank.values().forEach { rank ->
                        add(Card(suit, rank))
                    }
                }
            }
            
            val allCards = (1..numberOfDecks).flatMap { singleDeck }
            return Deck(allCards.shuffled())
        }
        
        // 測試用，可控制的牌組
        fun createTestDeck(cards: List<Card>): Deck = Deck(cards)
        
        // 便利方法，創建已洗牌的標準牌組
        fun shuffled(numberOfDecks: Int = 6): Deck = createStandardDeck(numberOfDecks)
    }
    
    // Rich domain behavior - dealing cards
    fun dealCard(): Pair<Card, Deck> {
        require(cards.isNotEmpty()) { "Deck is empty" }
        
        val nextCard = cards.first()
        val remainingCards = cards.drop(1)
        val newDealtCards = dealtCards + nextCard
        
        return nextCard to copy(
            cards = remainingCards,
            dealtCards = newDealtCards
        )
    }
    
    // Deal multiple cards at once
    fun dealCards(count: Int): Pair<List<Card>, Deck> {
        require(count > 0) { "Must deal at least one card" }
        require(cards.size >= count) { "Not enough cards in deck. Have ${cards.size}, need $count" }
        
        val cardsToDeal = cards.take(count)
        val remainingCards = cards.drop(count)
        val newDealtCards = dealtCards + cardsToDeal
        
        return cardsToDeal to copy(
            cards = remainingCards,
            dealtCards = newDealtCards
        )
    }
    
    // Domain queries
    val remainingCards: Int = cards.size
    val cardsDealt: Int = dealtCards.size
    val needsShuffle: Boolean = remainingCards < 26 // 當少於半副牌時建議重洗
    
    // Shuffle deck (保留已發出的牌)
    fun shuffle(): Deck = copy(cards = cards.shuffled())
    
    // Reset deck (重新開始，所有牌回到牌組)
    fun reset(): Deck {
        val allCards = cards + dealtCards
        return copy(
            cards = allCards.shuffled(),
            dealtCards = emptyList()
        )
    }
}