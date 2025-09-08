package org.ttpss930141011.bj.domain.services

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import org.ttpss930141011.bj.domain.entities.Game
import org.ttpss930141011.bj.domain.entities.Player
import org.ttpss930141011.bj.domain.valueobjects.BetState
import org.ttpss930141011.bj.domain.enums.ChipValue
import org.ttpss930141011.bj.domain.entities.Dealer
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*

class RoundManagerTest {
    
    private val roundManager = RoundManager()
    
    private fun createTestGame(
        phase: GamePhase = GamePhase.WAITING_FOR_BETS,
        hasCommittedBet: Boolean = true,
        currentBet: Int = 10,
        deckCards: List<Card> = createTestDeck(),
        rules: GameRules = GameRules()
    ): Game {
        // Create BetState if bet is committed
        val betState = if (hasCommittedBet) {
            BetState(amount = currentBet, isCommitted = true)
        } else {
            BetState()
        }
        
        return Game(
            player = Player(id = "test-player", chips = 1000),
            playerHands = emptyList(),
            currentHandIndex = 0,
            betState = betState,
            dealer = Dealer(),
            deck = Deck.createTestDeck(deckCards),
            rules = rules,
            phase = phase
        )
    }
    
    private fun createTestDeck(): List<Card> {
        // Create a predictable deck for testing
        return listOf(
            Card(Suit.HEARTS, Rank.KING),    // Player card 1
            Card(Suit.CLUBS, Rank.FIVE),     // Player card 2
            Card(Suit.SPADES, Rank.QUEEN),   // Dealer up card
            Card(Suit.DIAMONDS, Rank.ACE),   // Dealer hole card
            Card(Suit.HEARTS, Rank.SIX),     // Additional cards...
            Card(Suit.CLUBS, Rank.SEVEN),
            Card(Suit.SPADES, Rank.EIGHT),
            Card(Suit.DIAMONDS, Rank.NINE),
            Card(Suit.HEARTS, Rank.TWO),
            Card(Suit.CLUBS, Rank.THREE)
        )
    }
    
    @Test
    fun `given game with no bet when dealing round then should throw exception`() {
        // Given
        val game = createTestGame(hasCommittedBet = false)
        
        // When & Then
        assertFailsWith<IllegalArgumentException> {
            roundManager.dealRound(game)
        }
    }
    
    @Test
    fun `given game not in waiting phase when dealing round then should throw exception`() {
        // Given
        val game = createTestGame(phase = GamePhase.PLAYER_TURN)
        
        // When & Then
        assertFailsWith<IllegalArgumentException> {
            roundManager.dealRound(game)
        }
    }
    
    @Test
    fun `given valid game when dealing round then should deal cards correctly`() {
        // Given
        val game = createTestGame()
        
        // When
        val result = roundManager.dealRound(game)
        
        // Then
        assertEquals(GamePhase.PLAYER_TURN, result.phase)
        assertEquals(1, result.playerHands.size)
        assertEquals(2, result.playerHands[0].hand.cards.size)
        assertEquals(0, result.currentHandIndex)
        
        // Check dealer has up card and hole card
        assertNotNull(result.dealer.hand)
        assertEquals(1, result.dealer.hand!!.cards.size) // Only up card showing
        assertNotNull(result.dealer.holeCard)
        
        // Check cards are dealt from deck
        assertEquals(4, game.deck.remainingCards - result.deck.remainingCards) // 4 cards dealt
    }
    
    @Test
    fun `given valid game when dealing round then player hand should have correct bet`() {
        // Given
        val game = createTestGame(currentBet = 25)
        
        // When
        val result = roundManager.dealRound(game)
        
        // Then
        assertEquals(25, result.playerHands[0].bet)
    }
    
    @Test
    fun `given game with invalid current hand when processing action then should throw exception`() {
        // Given
        val game = createTestGame(phase = GamePhase.PLAYER_TURN)
            .copy(playerHands = emptyList()) // No hands
        
        // When & Then
        assertFailsWith<IllegalArgumentException> {
            roundManager.processPlayerAction(game, Action.HIT)
        }
    }
    
    @Test
    fun `given valid game when processing hit action then should add card to hand`() {
        // Given
        val game = createTestGame().let { roundManager.dealRound(it) }
        val originalHandSize = game.currentHand!!.hand.cards.size
        
        // When
        val result = roundManager.processPlayerAction(game, Action.HIT)
        
        // Then
        assertEquals(originalHandSize + 1, result.currentHand!!.hand.cards.size)
        assertEquals(1, game.deck.remainingCards - result.deck.remainingCards) // One card dealt
    }
    
    @Test
    fun `given valid game when processing stand action then should mark hand complete`() {
        // Given
        val game = createTestGame().let { roundManager.dealRound(it) }
        
        // When
        val result = roundManager.processPlayerAction(game, Action.STAND)
        
        // Then
        assertEquals(HandStatus.STANDING, result.currentHand!!.status)
        assertTrue(result.currentHand!!.isCompleted)
    }
    
    @Test
    fun `given valid game when processing double action then should add card and double bet`() {
        // Given
        val game = createTestGame().let { roundManager.dealRound(it) }
        val originalBet = game.currentHand!!.bet
        
        // When
        val result = roundManager.processPlayerAction(game, Action.DOUBLE)
        
        // Then
        assertEquals(originalBet * 2, result.currentHand!!.bet)
        assertTrue(result.currentHand!!.isCompleted)
    }
    
    @Test
    fun `given valid game when processing surrender action then should mark hand surrendered`() {
        // Given
        val game = createTestGame().let { roundManager.dealRound(it) }
        
        // When
        val result = roundManager.processPlayerAction(game, Action.SURRENDER)
        
        // Then
        assertEquals(HandStatus.SURRENDERED, result.currentHand!!.status)
        assertTrue(result.currentHand!!.isCompleted)
    }
    
    @Test
    fun `given splittable hand when processing split then should create two hands`() {
        // Given - Create game with pair for splitting
        val pairDeck = listOf(
            Card(Suit.HEARTS, Rank.EIGHT),   // Player card 1
            Card(Suit.CLUBS, Rank.EIGHT),    // Player card 2 - splittable pair
            Card(Suit.SPADES, Rank.QUEEN),   // Dealer up card
            Card(Suit.DIAMONDS, Rank.ACE),   // Dealer hole card
            Card(Suit.HEARTS, Rank.SIX),     // Split card 1
            Card(Suit.CLUBS, Rank.SEVEN)     // Split card 2
        )
        val game = createTestGame(deckCards = pairDeck).let { roundManager.dealRound(it) }
        
        // When
        val result = roundManager.processPlayerAction(game, Action.SPLIT)
        
        // Then
        assertEquals(2, result.playerHands.size)
        assertEquals(2, result.playerHands[0].hand.cards.size) // First split hand
        assertEquals(2, result.playerHands[1].hand.cards.size) // Second split hand
        assertEquals(0, result.currentHandIndex) // Still on first hand
    }
    
    @Test
    fun `given non-splittable hand when processing split then should throw exception`() {
        // Given
        val game = createTestGame().let { roundManager.dealRound(it) }
        // Default deck has K and 5 - not splittable
        
        // When & Then
        assertFailsWith<IllegalArgumentException> {
            roundManager.processPlayerAction(game, Action.SPLIT)
        }
    }
    
    @Test
    fun `given completed hand when processing action then should move to next hand`() {
        // Given - Create game with split hands
        val pairDeck = listOf(
            Card(Suit.HEARTS, Rank.EIGHT),   // Player cards
            Card(Suit.CLUBS, Rank.EIGHT),
            Card(Suit.SPADES, Rank.QUEEN),   // Dealer cards
            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.HEARTS, Rank.SIX),     // Split cards
            Card(Suit.CLUBS, Rank.SEVEN),
            Card(Suit.SPADES, Rank.FIVE)     // Hit card
        )
        val game = createTestGame(deckCards = pairDeck)
            .let { roundManager.dealRound(it) }
            .let { roundManager.processPlayerAction(it, Action.SPLIT) }
        
        // When - Stand on first hand
        val result = roundManager.processPlayerAction(game, Action.STAND)
        
        // Then - Should move to second hand
        assertEquals(1, result.currentHandIndex)
        assertEquals(HandStatus.STANDING, result.playerHands[0].status)
    }
    
    @Test
    fun `given incomplete player hands when processing dealer turn then should throw exception`() {
        // Given
        val game = createTestGame().let { roundManager.dealRound(it) }
        // Player hands are not complete yet
        
        // When & Then
        assertFailsWith<IllegalArgumentException> {
            roundManager.processDealerTurn(game)
        }
    }
    
    @Test
    fun `given all player hands busted when processing dealer turn then dealer should not act`() {
        // Given
        val game = createTestGame()
            .let { roundManager.dealRound(it) }
            .copy(playerHands = listOf(
                PlayerHand.initial(
                    listOf(Card(Suit.HEARTS, Rank.KING), Card(Suit.CLUBS, Rank.QUEEN)),
                    10
                ).hit(Card(Suit.SPADES, Rank.FIVE)) // Busted hand (25)
            ))
        
        // When
        val result = roundManager.processDealerTurn(game)
        
        // Then - Dealer hand should remain as is (just up card)
        assertEquals(1, result.dealer.hand!!.cards.size)
    }
    
    @Test
    fun `given player with winning hand when processing dealer turn then dealer should reveal and act`() {
        // Given
        val game = createTestGame()
            .let { roundManager.dealRound(it) }
            .let { roundManager.processPlayerAction(it, Action.STAND) }
        
        // When
        val result = roundManager.processDealerTurn(game)
        
        // Then
        // Dealer should reveal hole card (2 cards minimum)
        assertTrue(result.dealer.hand!!.cards.size >= 2)
        
        // Based on test deck: Queen + Ace = 21, dealer should stand
        assertFalse(result.dealer.shouldHit)
    }
    
    @Test
    fun `given dealer with low value when processing dealer turn then should hit until 17 or higher`() {
        // Given - Create scenario where dealer must hit
        val lowDealerDeck = listOf(
            Card(Suit.HEARTS, Rank.KING),    // Player cards
            Card(Suit.CLUBS, Rank.FIVE),
            Card(Suit.SPADES, Rank.FIVE),    // Dealer up card (low)
            Card(Suit.DIAMONDS, Rank.SEVEN), // Dealer hole card = 12 total
            Card(Suit.HEARTS, Rank.SIX)      // Hit card to make 18
        )
        val game = createTestGame(deckCards = lowDealerDeck)
            .let { roundManager.dealRound(it) }
            .let { roundManager.processPlayerAction(it, Action.STAND) }
        
        // When
        val result = roundManager.processDealerTurn(game)
        
        // Then - Dealer should have hit once
        assertEquals(3, result.dealer.hand!!.cards.size) // Up + hole + hit
        assertTrue(result.dealer.hand!!.bestValue >= 17)
    }
    
    @Test
    fun `given dealer with soft 17 and hit soft 17 rule when processing then should hit`() {
        // Given - Create soft 17 scenario
        val soft17Deck = listOf(
            Card(Suit.HEARTS, Rank.KING),    // Player cards
            Card(Suit.CLUBS, Rank.FIVE),
            Card(Suit.SPADES, Rank.ACE),     // Dealer up card
            Card(Suit.DIAMONDS, Rank.SIX),   // Dealer hole card = soft 17
            Card(Suit.HEARTS, Rank.TWO)      // Hit card
        )
        val rules = GameRules(dealerHitsOnSoft17 = true)
        val game = createTestGame(deckCards = soft17Deck, rules = rules)
            .let { roundManager.dealRound(it) }
            .let { roundManager.processPlayerAction(it, Action.STAND) }
        
        // When
        val result = roundManager.processDealerTurn(game)
        
        // Then - Dealer should have hit on soft 17
        assertEquals(3, result.dealer.hand!!.cards.size)
    }
    
    @Test
    fun `given max splits reached when processing split then should throw exception`() {
        // Given - Create game with max splits already used
        val game = createTestGame().let { roundManager.dealRound(it) }
        val maxSplitHands = mutableListOf<PlayerHand>()
        repeat(game.rules.maxSplits + 1) { // Exceed max splits
            maxSplitHands.add(PlayerHand.initial(
                listOf(Card(Suit.HEARTS, Rank.EIGHT), Card(Suit.CLUBS, Rank.EIGHT)),
                10
            ))
        }
        val gameWithMaxSplits = game.copy(playerHands = maxSplitHands)
        
        // When & Then
        assertFailsWith<IllegalArgumentException> {
            roundManager.processPlayerAction(gameWithMaxSplits, Action.SPLIT)
        }
    }
    
    @Test
    fun `given dealer busted when processing dealer turn then should stop hitting`() {
        // Given - Create scenario where dealer busts
        val bustDeck = listOf(
            Card(Suit.HEARTS, Rank.KING),    // Player cards
            Card(Suit.CLUBS, Rank.FIVE),
            Card(Suit.SPADES, Rank.KING),    // Dealer up card
            Card(Suit.DIAMONDS, Rank.FIVE),  // Dealer hole card = 15
            Card(Suit.HEARTS, Rank.TEN)      // Hit card = 25 (bust)
        )
        val game = createTestGame(deckCards = bustDeck)
            .let { roundManager.dealRound(it) }
            .let { roundManager.processPlayerAction(it, Action.STAND) }
        
        // When
        val result = roundManager.processDealerTurn(game)
        
        // Then
        assertTrue(result.dealer.hand!!.isBusted)
        assertFalse(result.dealer.shouldHit) // Should not continue hitting after bust
    }
    
    @Test
    fun `given dealer with progressive bust scenario then should not infinite loop`() {
        // Given - Test the exact scenario that caused infinite loop: T + 6 + 6 + 6...
        val progressiveBustDeck = listOf(
            Card(Suit.HEARTS, Rank.KING),    // Player cards
            Card(Suit.CLUBS, Rank.FIVE),
            Card(Suit.SPADES, Rank.TEN),     // Dealer up card: T
            Card(Suit.DIAMONDS, Rank.SIX),   // Dealer hole card: 6 (T+6=16, must hit)
            Card(Suit.CLUBS, Rank.SIX),      // First hit: T+6+6=22 (bust, must stop)
            Card(Suit.CLUBS, Rank.SIX),      // These should never be dealt
            Card(Suit.CLUBS, Rank.SIX),      // due to bust protection
            Card(Suit.CLUBS, Rank.SIX)
        )
        val game = createTestGame(deckCards = progressiveBustDeck)
            .let { roundManager.dealRound(it) }
            .let { roundManager.processPlayerAction(it, Action.STAND) }
        
        // When
        val result = roundManager.processDealerTurn(game)
        
        // Then
        assertEquals(3, result.dealer.hand!!.cards.size) // T + 6 + 6 (stopped after bust)
        assertEquals(22, result.dealer.hand!!.bestValue)
        assertTrue(result.dealer.hand!!.isBusted)
        assertEquals(GamePhase.SETTLEMENT, result.phase) // Should transition to settlement
        
        // Verify loop terminated by checking deck is not exhausted
        // Original deck had 8 cards, used 4 for dealing, 1 for dealer hit
        // Should still have cards remaining (proving infinite loop didn't occur)
    }
}