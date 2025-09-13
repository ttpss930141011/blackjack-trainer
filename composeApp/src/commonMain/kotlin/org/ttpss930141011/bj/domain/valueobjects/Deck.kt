package org.ttpss930141011.bj.domain.valueobjects

import kotlin.ConsistentCopyVisibility

/**
 * Immutable deck aggregate for card dealing operations
 * Tracks both remaining cards and dealt cards for complete state management
 * 
 * @param cards Remaining cards in the deck
 * @param dealtCards Cards that have been dealt from this deck
 */
@ConsistentCopyVisibility
data class Deck private constructor(
    private val cards: List<Card>,
    private val dealtCards: List<Card> = emptyList()
) {
    
    companion object {
        /**
         * Creates a standard blackjack deck with specified number of deck sets
         * 
         * @param numberOfDecks Number of 52-card deck sets to include (1-8)
         * @return Shuffled deck ready for dealing
         */
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
        
        /**
         * Creates a test deck with specific card arrangement
         * 
         * @param cards Specific cards to include in deck
         * @return Deck with specified cards for testing
         */
        fun createTestDeck(cards: List<Card>): Deck = Deck(cards)
        
        /**
         * Creates a shuffled standard deck
         * 
         * @param numberOfDecks Number of deck sets to include
         * @return Pre-shuffled standard deck
         */
        fun shuffled(numberOfDecks: Int = 6): Deck = createStandardDeck(numberOfDecks)
    }
    
    /**
     * Deals a single card from the deck
     * 
     * @return Pair of dealt card and new deck state
     * @throws IllegalArgumentException if deck is empty
     */
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
    
    /**
     * Deals multiple cards at once
     * 
     * @param count Number of cards to deal
     * @return Pair of dealt cards list and new deck state
     * @throws IllegalArgumentException if count is invalid or insufficient cards
     */
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
    
    /** Number of cards remaining in deck */
    val remainingCards: Int = cards.size
    
    /** Number of cards dealt from this deck */
    val cardsDealt: Int = dealtCards.size
    
    /** True if deck should be reshuffled (less than half deck remaining) */
    val needsShuffle: Boolean = remainingCards < 26
    
    /**
     * Shuffles remaining cards in deck
     * 
     * @return New deck with shuffled remaining cards
     */
    fun shuffle(): Deck = copy(cards = cards.shuffled())
    
    /**
     * Resets deck to full state with all cards shuffled
     * 
     * @return New deck with all cards returned and shuffled
     */
    fun reset(): Deck {
        val allCards = cards + dealtCards
        return copy(
            cards = allCards.shuffled(),
            dealtCards = emptyList()
        )
    }
}