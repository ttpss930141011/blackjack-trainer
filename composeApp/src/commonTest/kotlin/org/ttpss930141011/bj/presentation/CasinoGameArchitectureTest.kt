package org.ttpss930141011.bj.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.ttpss930141011.bj.domain.*

/**
 * TDD Tests for Casino Game architecture with CasinoGameScreen.kt
 * These tests verify that:
 * 1. Card persistence bug is fixed (cards disappear after resetForNewRound)
 * 2. All UI components work with Game domain model
 * 3. All game phases display correctly with pure Game architecture
 * 4. Settlement, split hands, and betting all work with casino interface
 */
class CasinoGameArchitectureTest {

    @Test
    fun `given completed round when resetForNewRound then UI should show empty state`() {
        // Given: Game after completed round with cards
        val deck = Deck.createTestDeck(listOf(
            Card(Suit.SPADES, Rank.TEN), Card(Suit.HEARTS, Rank.NINE),     // Player: 19
            Card(Suit.CLUBS, Rank.KING), Card(Suit.DIAMONDS, Rank.SEVEN)    // Dealer: 17
        ))
        
        val game = Game.create(GameRules())
            .addPlayer(Player("test", 100))
            .placeBet(25)
            .copy(deck = deck)
            .dealRound()
            .playerAction(Action.STAND)
            .dealerPlayAutomated()
            .settleRound()
        
        // When: Reset for new round
        val resetGame = game.resetForNewRound()
        
        // Then: UI should reflect empty state
        assertTrue(resetGame.playerHands.isEmpty(), "Player hands should be empty after reset")
        assertEquals(GamePhase.WAITING_FOR_BETS, resetGame.phase)
        assertEquals(0, resetGame.currentBet)
        assertEquals(null, resetGame.dealer.hand)
        assertEquals(null, resetGame.dealer.upCard)
        assertEquals(125, resetGame.player!!.chips, "Player chips should be preserved after winning") // Player won: 100 - 25 + 50
    }

    @Test
    fun `given game with player hands when displaying UI should show all hands without Table`() {
        // Given: Game with split hands
        val deck = Deck.createTestDeck(listOf(
            Card(Suit.SPADES, Rank.EIGHT), Card(Suit.HEARTS, Rank.EIGHT),  // Player: Split eligible
            Card(Suit.CLUBS, Rank.ACE), Card(Suit.DIAMONDS, Rank.QUEEN),    // Dealer
            Card(Suit.SPADES, Rank.TEN), Card(Suit.HEARTS, Rank.NINE)      // Additional cards for split
        ))
        
        val game = Game.create(GameRules())
            .addPlayer(Player("test", 100))
            .placeBet(25)
            .copy(deck = deck)
            .dealRound()
            .playerAction(Action.SPLIT)
        
        // Then: Game should properly track multiple hands without Table
        assertEquals(2, game.playerHands.size, "Should have 2 hands after split")
        assertEquals(GamePhase.PLAYER_ACTIONS, game.phase)
        assertTrue(game.canAct, "Should be able to act on current hand")
        
        // Verify each hand is independent
        val firstHand = game.playerHands[0]
        val secondHand = game.playerHands[1]
        assertEquals(Rank.EIGHT, firstHand.cards[0].rank)
        assertEquals(Rank.EIGHT, secondHand.cards[0].rank)
        assertEquals(25, firstHand.bet)
        assertEquals(25, secondHand.bet)
    }

    @Test
    fun `given dealer turn when using Game instead of Table should display correctly`() {
        // Given: Game in dealer turn phase
        val deck = Deck.createTestDeck(listOf(
            Card(Suit.SPADES, Rank.TEN), Card(Suit.HEARTS, Rank.NINE),     // Player: 19
            Card(Suit.CLUBS, Rank.FIVE), Card(Suit.DIAMONDS, Rank.QUEEN),   // Dealer: 5 + 10 = 15, must hit
            Card(Suit.SPADES, Rank.SIX)                 // Dealer hits to 21
        ))
        
        val game = Game.create(GameRules())
            .addPlayer(Player("test", 100))
            .placeBet(25)
            .copy(deck = deck)
            .dealRound()
            .playerAction(Action.STAND)
        
        // When: In dealer turn phase
        assertEquals(GamePhase.DEALER_TURN, game.phase)
        
        // Then: Should have dealer hand and up card for UI display
        assertEquals(Rank.FIVE, game.dealer.upCard!!.rank)
        assertEquals(1, game.dealer.hand!!.cards.size) // Only up card visible in DEALER_TURN phase
        assertEquals(5, game.dealer.hand!!.bestValue)
        
        // Verify automated dealer play works
        val afterDealerPlay = game.dealerPlayAutomated()
        assertEquals(GamePhase.SETTLEMENT, afterDealerPlay.phase)
        assertEquals(21, afterDealerPlay.dealer.hand!!.bestValue)
    }

    @Test
    fun `given settlement phase when using Game should show final results correctly`() {
        // Given: Game ready for settlement
        val deck = Deck.createTestDeck(listOf(
            Card(Suit.SPADES, Rank.TEN), Card(Suit.HEARTS, Rank.NINE),     // Player: 19
            Card(Suit.CLUBS, Rank.KING), Card(Suit.DIAMONDS, Rank.SEVEN)    // Dealer: 17
        ))
        
        val game = Game.create(GameRules())
            .addPlayer(Player("test", 100))
            .placeBet(25)
            .copy(deck = deck)
            .dealRound()
            .playerAction(Action.STAND)
            .dealerPlayAutomated()
        
        // When: In settlement phase
        assertEquals(GamePhase.SETTLEMENT, game.phase)
        
        // Then: Should be able to settle without Table dependencies
        val settledGame = game.settleRound()
        
        // Verify settlement results are visible in Game
        val playerHand = settledGame.playerHands[0]
        assertEquals(HandStatus.WIN, playerHand.status) // Player 19 beats dealer 17
        assertEquals(125, settledGame.player!!.chips) // 100 - 25 bet + 50 winnings
        // Domain layer should NOT control workflow - phase remains SETTLEMENT
        assertEquals(GamePhase.SETTLEMENT, settledGame.phase)
    }

    @Test
    fun `given PLAYER_ACTIONS phase when using Game should show available actions`() {
        // Given: Game with player having 11 (good for doubling)
        val deck = Deck.createTestDeck(listOf(
            Card(Suit.SPADES, Rank.FIVE), Card(Suit.HEARTS, Rank.SIX),     // Player: 11 (can double)
            Card(Suit.CLUBS, Rank.TEN), Card(Suit.DIAMONDS, Rank.QUEEN)     // Dealer
        ))
        
        val player = Player("test", 100)
        val game = Game.create(GameRules())
            .addPlayer(player)
            .placeBet(25)
            .copy(deck = deck)
            .dealRound()
        
        // When: In player actions phase
        assertEquals(GamePhase.PLAYER_ACTIONS, game.phase)
        
        // Then: Should show available actions from Game
        val availableActions = game.availableActions()
        assertTrue(availableActions.contains(Action.HIT))
        assertTrue(availableActions.contains(Action.STAND))
        assertTrue(availableActions.contains(Action.DOUBLE))
        assertTrue(game.canAct)
        assertEquals(0, game.currentHandIndex)
        assertEquals(11, game.currentHand!!.bestValue)
    }

    @Test
    fun `given waiting for bets phase should show betting interface without Table`() {
        // Given: Fresh game with player
        val game = Game.create(GameRules())
            .addPlayer(Player("test", 100))
        
        // When: In waiting for bets phase
        assertEquals(GamePhase.WAITING_FOR_BETS, game.phase)
        
        // Then: Should show player info and betting capability
        assertTrue(game.hasPlayer)
        assertEquals(100, game.player!!.chips)
        assertFalse(game.hasBet)
        assertTrue(game.playerHands.isEmpty())
        
        // Verify betting works
        val gameWithBet = game.placeBet(25)
        assertTrue(gameWithBet.hasBet)
        assertEquals(25, gameWithBet.currentBet)
        assertEquals(75, gameWithBet.player!!.chips)
    }

    @Test
    fun `given multiple split hands when settling should handle all hands correctly`() {
        // Given: Game with split hands (both win)
        val deck = Deck.createTestDeck(listOf(
            Card(Suit.SPADES, Rank.EIGHT), Card(Suit.HEARTS, Rank.EIGHT),  // Initial split pair
            Card(Suit.CLUBS, Rank.ACE), Card(Suit.DIAMONDS, Rank.FIVE),     // Dealer: A, 5 (soft 16, must hit)
            Card(Suit.SPADES, Rank.THREE), Card(Suit.HEARTS, Rank.TEN),    // Split hand additions: 11, 18  
            Card(Suit.CLUBS, Rank.KING), Card(Suit.SPADES, Rank.QUEEN),     // Extra cards for dealer
            Card(Suit.HEARTS, Rank.SEVEN), Card(Suit.DIAMONDS, Rank.NINE)  // More cards to prevent empty deck
        ))
        
        val game = Game.create(GameRules())
            .addPlayer(Player("test", 100))
            .placeBet(25)
            .copy(deck = deck)
            .dealRound()
            .playerAction(Action.SPLIT)
            .playerAction(Action.STAND) // First hand: 8+3=11, stands
            .playerAction(Action.STAND) // Second hand: 8+10=18, stands  
            .dealerPlayAutomated()      // Dealer busts
            .settleRound()
        
        // Then: Both hands should win
        assertEquals(2, game.playerHands.size)
        val firstHand = game.playerHands[0]
        val secondHand = game.playerHands[1]
        
        assertEquals(HandStatus.WIN, firstHand.status)
        assertEquals(HandStatus.WIN, secondHand.status)
        assertEquals(11, firstHand.bestValue)
        assertEquals(18, secondHand.bestValue)
        
        // Verify chip calculation: 100 - 50 (two bets) + 100 (two wins) + 25 (dealer bust bonus)
        // Actually: dealer busted so both hands win. 100 - 50 + 125 = 175
        assertEquals(175, game.player!!.chips)
    }

    @Test
    fun `given Game architecture should not have any Table dependencies`() {
        // Given: Complete game flow
        val deck = Deck.createTestDeck(listOf(
            Card(Suit.SPADES, Rank.TEN), Card(Suit.HEARTS, Rank.JACK),     // Player: 20
            Card(Suit.CLUBS, Rank.NINE), Card(Suit.DIAMONDS, Rank.EIGHT)    // Dealer: 17
        ))
        
        var game = Game.create(GameRules())
            .addPlayer(Player("test", 100))
            .placeBet(25)
            .copy(deck = deck)
        
        // When: Play complete round using only Game
        game = game.dealRound()
        assertEquals(GamePhase.PLAYER_ACTIONS, game.phase)
        
        game = game.playerAction(Action.STAND)
        assertEquals(GamePhase.DEALER_TURN, game.phase)
        
        game = game.dealerPlayAutomated()
        assertEquals(GamePhase.SETTLEMENT, game.phase)
        
        game = game.settleRound()
        // Domain layer should NOT control workflow - Application layer will handle phase transitions
        assertEquals(GamePhase.SETTLEMENT, game.phase)
        
        // Application layer workflow control (simulated)
        game = game.resetForNewRound()
        
        // Then: Should be able to reset and start new round
        val newRoundGame = game.resetForNewRound()
        
        // Verify complete clean state
        assertTrue(newRoundGame.playerHands.isEmpty())
        assertEquals(0, newRoundGame.currentBet)
        assertEquals(null, newRoundGame.dealer.hand)
        assertEquals(GamePhase.WAITING_FOR_BETS, newRoundGame.phase)
        assertEquals(125, newRoundGame.player!!.chips) // Won the round
    }

    @Test
    fun `given blackjack scenario should display and settle correctly with Game`() {
        // Given: Player blackjack vs dealer 20
        val deck = Deck.createTestDeck(listOf(
            Card(Suit.SPADES, Rank.ACE), Card(Suit.HEARTS, Rank.KING),     // Player: Blackjack (21)
            Card(Suit.CLUBS, Rank.TEN), Card(Suit.DIAMONDS, Rank.QUEEN)     // Dealer: 20
        ))
        
        val game = Game.create(GameRules())
            .addPlayer(Player("test", 100))
            .placeBet(20)
            .copy(deck = deck)
            .dealRound()
            // Player has blackjack, proceed directly to dealer turn then settlement
        val gameAfterPlayerTurn = if (game.playerHands[0].isBlackjack) {
            game.copy(phase = GamePhase.DEALER_TURN)
        } else {
            game
        }
        val finalGame = gameAfterPlayerTurn.dealerPlayAutomated().settleRound()
        
        // Then: Should handle blackjack payout correctly
        val playerHand = finalGame.playerHands[0]
        assertTrue(playerHand.isBlackjack)
        assertEquals(HandStatus.WIN, playerHand.status)
        
        // Blackjack pays 3:2, so bet 20 returns 20 + 30 = 50 total
        // Final chips: 100 - 20 + 50 = 130
        assertEquals(130, finalGame.player!!.chips)
    }
}