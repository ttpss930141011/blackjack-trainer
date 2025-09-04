package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNull

class GameTest {
    
    @Test
    fun `given new game when created then should have initial state`() {
        // Given - game rules
        val rules = GameRules()
        
        // When - creating game
        val game = Game.create(rules)
        
        // Then - should have proper initial state
        assertEquals(rules, game.rules)
        assertEquals(GamePhase.WAITING_FOR_BETS, game.phase)
        assertTrue(game.playerHands.isEmpty())
        assertNull(game.dealer.hand)
        assertEquals(0, game.currentHandIndex)
    }
    
    @Test
    fun `given game when player joins then should create player`() {
        // Given - new game
        val game = Game.create(GameRules())
        val player = Player(id = "player1", chips = 500)
        
        // When - player joins
        val gameWithPlayer = game.addPlayer(player)
        
        // Then - should have player
        assertEquals(player, gameWithPlayer.player)
        assertTrue(gameWithPlayer.playerHands.isEmpty()) // No hands until bet is placed
    }
    
    @Test
    fun `given game with player when placing bet then should deduct chips and record bet`() {
        // Given - game with player
        val player = Player(id = "player1", chips = 500)
        val game = Game.create(GameRules()).addPlayer(player)
        
        // When - placing bet
        val gameWithBet = game.placeBet(25)
        
        // Then - should deduct chips and record bet
        assertEquals(475, gameWithBet.player?.chips)
        assertEquals(25, gameWithBet.currentBet)
    }
    
    @Test
    fun `given game with bet when dealing then should create initial hands`() {
        // Given - game with bet placed
        val player = Player(id = "player1", chips = 500)
        val testDeck = Deck.createTestDeck(listOf(
            Card(Suit.HEARTS, Rank.KING),   // Player card 1
            Card(Suit.SPADES, Rank.SEVEN),  // Player card 2
            Card(Suit.CLUBS, Rank.ACE),     // Dealer up card
            Card(Suit.DIAMONDS, Rank.TEN)   // Dealer hole card
        ))
        val game = Game.createForTest(GameRules(), testDeck)
            .addPlayer(player)
            .placeBet(25)
        
        // When - dealing cards
        val dealtGame = game.dealRound()
        
        // Then - should create hands
        assertEquals(1, dealtGame.playerHands.size)
        assertEquals(2, dealtGame.playerHands[0].cards.size)
        assertEquals(25, dealtGame.playerHands[0].bet)
        assertEquals(Rank.KING, dealtGame.playerHands[0].cards[0].rank)
        assertEquals(Rank.SEVEN, dealtGame.playerHands[0].cards[1].rank)
        assertEquals(GamePhase.PLAYER_ACTIONS, dealtGame.phase)
        
        // Dealer should have up card
        assertEquals(Rank.ACE, dealtGame.dealer.upCard?.rank)
    }
    
    @Test
    fun `given game with active hand when player hits then should add card`() {
        // Given - game with dealt hand (K-7 = 17)
        val player = Player(id = "player1", chips = 500)
        val testDeck = Deck.createTestDeck(listOf(
            Card(Suit.HEARTS, Rank.KING),   // Player card 1
            Card(Suit.SPADES, Rank.SEVEN),  // Player card 2
            Card(Suit.CLUBS, Rank.ACE),     // Dealer up card
            Card(Suit.DIAMONDS, Rank.TEN),  // Dealer hole card
            Card(Suit.HEARTS, Rank.THREE)   // Hit card
        ))
        val game = Game.createForTest(GameRules(), testDeck)
            .addPlayer(player)
            .placeBet(25)
            .dealRound()
        
        // When - player hits
        val hitGame = game.playerAction(Action.HIT)
        
        // Then - should add card to current hand
        val currentHand = hitGame.playerHands[0]
        assertEquals(3, currentHand.cards.size)
        assertEquals(Rank.THREE, currentHand.cards[2].rank)
        assertEquals(20, currentHand.bestValue) // K + 7 + 3 = 20
    }
    
    @Test
    fun `given game with splittable hand when splitting then should create two hands`() {
        // Given - game with JJ hand
        val player = Player(id = "player1", chips = 500)
        val testDeck = Deck.createTestDeck(listOf(
            Card(Suit.HEARTS, Rank.JACK),   // Player card 1
            Card(Suit.SPADES, Rank.JACK),   // Player card 2
            Card(Suit.CLUBS, Rank.ACE),     // Dealer up card
            Card(Suit.DIAMONDS, Rank.TEN),  // Dealer hole card
            Card(Suit.HEARTS, Rank.FIVE),   // Split card 1
            Card(Suit.SPADES, Rank.SEVEN)   // Split card 2
        ))
        val game = Game.createForTest(GameRules(), testDeck)
            .addPlayer(player)
            .placeBet(25)
            .dealRound()
        
        // When - player splits
        val splitGame = game.playerAction(Action.SPLIT)
        
        // Then - should have two hands
        assertEquals(2, splitGame.playerHands.size)
        assertEquals(Rank.JACK, splitGame.playerHands[0].cards[0].rank)
        assertEquals(Rank.JACK, splitGame.playerHands[1].cards[0].rank)
        assertEquals(25, splitGame.playerHands[0].bet)
        assertEquals(25, splitGame.playerHands[1].bet)
        assertEquals(0, splitGame.currentHandIndex) // Still playing first hand
    }
    
    @Test
    fun `given game with split hands when first hand completes then should move to second`() {
        // Given - game with split hands where first hand will stand
        val player = Player(id = "player1", chips = 500)  
        val testDeck = Deck.createTestDeck(listOf(
            Card(Suit.HEARTS, Rank.JACK),   
            Card(Suit.SPADES, Rank.JACK),   
            Card(Suit.CLUBS, Rank.ACE),     
            Card(Suit.DIAMONDS, Rank.TEN),  
            Card(Suit.HEARTS, Rank.FIVE),   
            Card(Suit.SPADES, Rank.SEVEN)   
        ))
        val splitGame = Game.createForTest(GameRules(), testDeck)
            .addPlayer(player)
            .placeBet(25)
            .dealRound()
            .playerAction(Action.SPLIT)
        
        // When - first hand stands (J+5=15, player chooses to stand)
        val standGame = splitGame.playerAction(Action.STAND)
        
        // Then - should move to second hand
        assertEquals(1, standGame.currentHandIndex) // Now playing second hand
        assertEquals(HandStatus.STANDING, standGame.playerHands[0].status)
        assertEquals(HandStatus.ACTIVE, standGame.playerHands[1].status)
    }
    
    @Test
    fun `given game when all hands complete then should proceed to dealer turn`() {
        // Given - game with single hand that will stand
        val player = Player(id = "player1", chips = 500)
        val testDeck = Deck.createTestDeck(listOf(
            Card(Suit.HEARTS, Rank.KING),   
            Card(Suit.SPADES, Rank.SEVEN),  
            Card(Suit.CLUBS, Rank.ACE),     
            Card(Suit.DIAMONDS, Rank.TEN)
        ))
        val game = Game.createForTest(GameRules(), testDeck)
            .addPlayer(player)
            .placeBet(25)
            .dealRound()
        
        // When - player stands (completing all hands)
        val standGame = game.playerAction(Action.STAND)
        
        // Then - should proceed to dealer turn
        assertEquals(GamePhase.DEALER_TURN, standGame.phase)
    }
    
    @Test
    fun `given dealer turn when dealer plays then should follow dealer rules`() {
        // Given - game in dealer turn phase with dealer showing Ace+6 (soft 17)
        val player = Player(id = "player1", chips = 500)
        val testDeck = Deck.createTestDeck(listOf(
            Card(Suit.HEARTS, Rank.KING),   // Player
            Card(Suit.SPADES, Rank.SEVEN),  // Player  
            Card(Suit.CLUBS, Rank.ACE),     // Dealer up
            Card(Suit.DIAMONDS, Rank.SIX),  // Dealer hole  
            Card(Suit.HEARTS, Rank.FOUR)    // Dealer hit card
        ))
        val game = Game.createForTest(GameRules(), testDeck)
            .addPlayer(player)
            .placeBet(25)
            .dealRound()
            .playerAction(Action.STAND)
        
        // When - dealer plays automatically
        val finalGame = game.dealerPlayAutomated()
        
        // Then - dealer should have hit (soft 17) and final hand should be A+6+4=21
        assertEquals(3, finalGame.dealer.hand!!.cards.size)
        assertEquals(21, finalGame.dealer.hand!!.bestValue)
        assertEquals(GamePhase.SETTLEMENT, finalGame.phase)
    }
    
    @Test
    fun `given split JJ hands when first hand gets another J then should allow re-split`() {
        // Given - split JJ hands where first hand gets another J
        val player = Player(id = "player1", chips = 500)
        val testDeck = Deck.createTestDeck(listOf(
            Card(Suit.HEARTS, Rank.JACK),   // Initial hand 1
            Card(Suit.SPADES, Rank.JACK),   // Initial hand 2
            Card(Suit.CLUBS, Rank.ACE),     // Dealer up
            Card(Suit.DIAMONDS, Rank.TEN),  // Dealer hole
            Card(Suit.CLUBS, Rank.JACK),    // Split card 1 (for first J)
            Card(Suit.DIAMONDS, Rank.SEVEN), // Split card 2 (for second J)
            Card(Suit.HEARTS, Rank.FIVE),   // Re-split card 1 
            Card(Suit.SPADES, Rank.EIGHT)   // Re-split card 2
        ))
        val game = Game.createForTest(GameRules(), testDeck)
            .addPlayer(player)
            .placeBet(25)
            .dealRound()
            .playerAction(Action.SPLIT) // First split: JJ -> J-J, J-7
        
        // When - first hand (J-J) splits again
        val reSplitGame = game.playerAction(Action.SPLIT)
        
        // Then - should have 3 hands total
        assertEquals(3, reSplitGame.playerHands.size)
        assertEquals(Rank.JACK, reSplitGame.playerHands[0].cards[0].rank) // J-5
        assertEquals(Rank.JACK, reSplitGame.playerHands[1].cards[0].rank) // J-8  
        assertEquals(Rank.JACK, reSplitGame.playerHands[2].cards[0].rank) // J-7
        assertEquals(0, reSplitGame.currentHandIndex) // Still playing first hand
    }
    
    @Test
    fun `given split hands when max splits reached then should not allow more splits`() {
        // Given - game with rules that only allow 1 split (2 hands max)
        val rules = GameRules(maxSplits = 1)
        val player = Player(id = "player1", chips = 500)
        val testDeck = Deck.createTestDeck(listOf(
            Card(Suit.HEARTS, Rank.JACK),   
            Card(Suit.SPADES, Rank.JACK),   
            Card(Suit.CLUBS, Rank.ACE),     
            Card(Suit.DIAMONDS, Rank.TEN),  
            Card(Suit.CLUBS, Rank.JACK),    // Makes first hand J-J (splittable)
            Card(Suit.DIAMONDS, Rank.SEVEN)  
        ))
        val splitGame = Game.createForTest(rules, testDeck)
            .addPlayer(player)
            .placeBet(25)
            .dealRound()
            .playerAction(Action.SPLIT) // Now have 2 hands (max reached)
        
        // When - checking available actions for first hand (J-J)
        val availableActions = splitGame.availableActions()
        
        // Then - should not include SPLIT (max splits reached)
        assertFalse(availableActions.contains(Action.SPLIT), "Should not allow split when max splits reached")
        assertTrue(availableActions.contains(Action.HIT), "Should still allow other actions")
        assertTrue(availableActions.contains(Action.STAND), "Should still allow other actions")
    }
    
    @Test
    fun `given game when settlement occurs then should calculate winnings correctly`() {
        // Given - game where player wins with 20 vs dealer 19
        val player = Player(id = "player1", chips = 500)
        val testDeck = Deck.createTestDeck(listOf(
            Card(Suit.HEARTS, Rank.KING),   // Player K
            Card(Suit.SPADES, Rank.TEN),    // Player 10 (total 20)  
            Card(Suit.CLUBS, Rank.NINE),    // Dealer 9
            Card(Suit.DIAMONDS, Rank.TEN)   // Dealer hole 10 (total 19)
        ))
        val game = Game.createForTest(GameRules(), testDeck)
            .addPlayer(player)
            .placeBet(25)
            .dealRound()
            .playerAction(Action.STAND)
            .dealerPlayAutomated()
        
        // When - dealer finishes (auto-settles)
        // No additional settlement needed - dealerPlayAutomated() already settled
        
        // Then - player should win and get bet back + winnings (25 + 25 = 50 total)
        assertEquals(525, game.player!!.chips) // 500 - 25 (bet) + 50 (return + win) = 525
        assertEquals(HandStatus.WIN, game.playerHands[0].status)
        // Domain layer should NOT control workflow - phase remains SETTLEMENT
        assertEquals(GamePhase.SETTLEMENT, game.phase)
    }
    
    @Test
    fun `given insufficient balance when attempting double then should not include double action`() {
        // Given - Player with only 10 chips tries to double a 20 bet
        val player = Player("test", 30) // Only 30 total
        val testDeck = Deck.createTestDeck(listOf(
            Card(Suit.HEARTS, Rank.FIVE), Card(Suit.SPADES, Rank.SIX),     // Player: 11 (good for double)
            Card(Suit.DIAMONDS, Rank.TEN), Card(Suit.CLUBS, Rank.EIGHT)    // Dealer
        ))
        
        val game = Game.createForTest(GameRules(), testDeck)
            .addPlayer(player)
            .placeBet(20)  // Player now has 10 chips remaining
            .dealRound()
        
        // When - Check available actions
        val availableActions = game.availableActions()
        
        // Then - Double should not be available due to insufficient balance
        assertFalse(availableActions.contains(Action.DOUBLE))
        assertTrue(availableActions.contains(Action.HIT))
        assertTrue(availableActions.contains(Action.STAND))
    }
    
    @Test 
    fun `given exact balance when player doubles then should deduct correct amount`() {
        // Given - Player with exactly enough for double
        val player = Player("test", 40)
        val testDeck = Deck.createTestDeck(listOf(
            Card(Suit.HEARTS, Rank.FIVE), Card(Suit.SPADES, Rank.SIX),     // Player: 11
            Card(Suit.DIAMONDS, Rank.SIX), Card(Suit.CLUBS, Rank.EIGHT),   // Dealer
            Card(Suit.HEARTS, Rank.TEN)  // Double card makes 21
        ))
        
        val game = Game.createForTest(GameRules(), testDeck)
            .addPlayer(player)
            .placeBet(20)  // Player now has 20 chips remaining (exactly enough for double)
            .dealRound()
        
        // When - Player doubles down
        val doubleGame = game.playerAction(Action.DOUBLE)
        
        // Then - Should double the bet and complete hand with one more card
        assertEquals(40, doubleGame.playerHands[0].bet) // 20 * 2 = 40
        assertEquals(21, doubleGame.playerHands[0].bestValue) // 11 + 10 = 21
        assertTrue(doubleGame.playerHands[0].isCompleted) // Double completes hand
    }
}