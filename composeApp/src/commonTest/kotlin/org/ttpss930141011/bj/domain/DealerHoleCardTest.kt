package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DealerHoleCardTest {
    
    @Test
    fun `given dealer with up card and hole card when revealing hole card then should show both cards`() {
        // Given - Dealer with 5♦ up card and 7♥ hole card
        val upCard = Card(Suit.DIAMONDS, Rank.FIVE)
        val holeCard = Card(Suit.HEARTS, Rank.SEVEN)
        val dealer = Dealer().dealInitialCards(upCard, holeCard)
        
        // Verify initial state
        assertEquals(1, dealer.hand!!.cards.size)
        assertEquals(upCard, dealer.hand!!.cards[0])
        assertEquals(upCard, dealer.upCard)
        assertNotNull(dealer.holeCard)
        assertEquals(holeCard, dealer.holeCard)
        
        // When - Reveal hole card
        val revealedDealer = dealer.revealHoleCard()
        
        // Then - Should show both cards in correct order
        assertEquals(2, revealedDealer.hand!!.cards.size)
        assertEquals(upCard, revealedDealer.hand!!.cards[0])    // Up card first
        assertEquals(holeCard, revealedDealer.hand!!.cards[1])  // Hole card second
        assertEquals(12, revealedDealer.hand!!.bestValue)       // 5 + 7 = 12
    }
    
    @Test
    fun `given game after dealing when transitioning to dealer turn then should reveal hole card correctly`() {
        // Given - Game with dealt cards
        val rules = GameRules()
        val game = Game.create(rules)
            .addPlayer(Player(id = "test", chips = 100))
            .placeBet(25)
            .dealRound()
        
        // Verify initial dealer state (during PLAYER_ACTIONS)
        assertNotNull(game.dealer.upCard)
        assertNotNull(game.dealer.holeCard)
        assertEquals(1, game.dealer.hand!!.cards.size)
        
        val originalUpCard = game.dealer.upCard!!
        val originalHoleCard = game.dealer.holeCard!!
        
        // When - Transition to dealer turn (which should reveal hole card)
        val gameAfterPlayerStand = game.playerAction(Action.STAND)
        val gameAfterDealerPlay = gameAfterPlayerStand.dealerPlayAutomated()
        
        // Then - Dealer hand should contain original up card + hole card + any additional cards
        val dealerHand = gameAfterDealerPlay.dealer.hand!!
        assertTrue(dealerHand.cards.size >= 2)
        assertEquals(originalUpCard, dealerHand.cards[0])     // Original up card preserved
        assertEquals(originalHoleCard, dealerHand.cards[1])   // Original hole card revealed
    }
}