package org.ttpss930141011.bj.domain.services

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.ttpss930141011.bj.domain.entities.Game
import org.ttpss930141011.bj.domain.entities.Player
import org.ttpss930141011.bj.domain.entities.Dealer
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*

class SettlementServiceTest {
    
    private val settlementService = SettlementService()
    
    private fun createTestGame(
        phase: GamePhase = GamePhase.SETTLEMENT,
        playerChips: Int = 1000,
        dealerHand: Hand? = Hand(listOf(Card(Suit.HEARTS, Rank.KING), Card(Suit.CLUBS, Rank.SEVEN))), // 17
        playerHands: List<PlayerHand> = listOf(
            PlayerHand.initial(listOf(Card(Suit.SPADES, Rank.QUEEN), Card(Suit.DIAMONDS, Rank.NINE)), 10) // 19
        ),
        isSettled: Boolean = false,
        rules: GameRules = GameRules()
    ): Game {
        // Create committed BetState from first hand's bet
        val betAmount = if (playerHands.isNotEmpty()) playerHands.first().bet else 0
        val betState = if (betAmount > 0) {
            BetState(amount = betAmount, isCommitted = true)
        } else {
            BetState()
        }
        
        return Game(
            player = Player(id = "test-player", chips = playerChips),
            playerHands = playerHands,
            currentHandIndex = 0,
            betState = betState,
            dealer = Dealer(hand = dealerHand),
            deck = Deck.createTestDeck(emptyList()),
            rules = rules,
            phase = phase,
            isSettled = isSettled
        )
    }
    
    @Test
    fun `given game not in settlement phase when settling then should throw exception`() {
        // Given
        val game = createTestGame(phase = GamePhase.PLAYER_TURN)
        
        // When & Then
        assertFailsWith<IllegalArgumentException> {
            settlementService.settleRound(game)
        }
    }
    
    @Test
    fun `given game with no dealer hand when settling then should throw exception`() {
        // Given
        val game = createTestGame(dealerHand = null)
        
        // When & Then
        assertFailsWith<IllegalArgumentException> {
            settlementService.settleRound(game)
        }
    }
    
    @Test
    fun `given game with no player when settling then should throw exception`() {
        // Given
        val game = createTestGame().copy(player = null)
        
        // When & Then
        assertFailsWith<IllegalArgumentException> {
            settlementService.settleRound(game)
        }
    }
    
    @Test
    fun `given already settled game when settling then should throw exception`() {
        // Given
        val game = createTestGame(isSettled = true)
        
        // When & Then
        assertFailsWith<IllegalArgumentException> {
            settlementService.settleRound(game)
        }
    }
    
    @Test
    fun `given player wins when settling then should award winnings correctly`() {
        // Given - Player 19, Dealer 17
        val game = createTestGame()
        val originalChips = game.player!!.chips
        val bet = game.playerHands[0].bet
        
        // When
        val result = settlementService.settleRound(game)
        
        // Then
        assertEquals(originalChips + (bet * 2), result.player!!.chips) // Return bet + win
        assertEquals(HandStatus.WIN, result.playerHands[0].status)
        assertEquals(true, result.isSettled)
    }
    
    @Test
    fun `given dealer wins when settling then should award no winnings`() {
        // Given - Player 16, Dealer 17
        val playerHand = PlayerHand.initial(
            listOf(Card(Suit.SPADES, Rank.KING), Card(Suit.DIAMONDS, Rank.SIX)), // 16
            10
        )
        val game = createTestGame(playerHands = listOf(playerHand))
        val originalChips = game.player!!.chips
        
        // When
        val result = settlementService.settleRound(game)
        
        // Then
        assertEquals(originalChips, result.player!!.chips) // No winnings
        assertEquals(HandStatus.LOSS, result.playerHands[0].status)
    }
    
    @Test
    fun `given push when settling then should return bet only`() {
        // Given - Both 17
        val playerHand = PlayerHand.initial(
            listOf(Card(Suit.SPADES, Rank.KING), Card(Suit.DIAMONDS, Rank.SEVEN)), // 17
            10
        )
        val game = createTestGame(playerHands = listOf(playerHand))
        val originalChips = game.player!!.chips
        val bet = playerHand.bet
        
        // When
        val result = settlementService.settleRound(game)
        
        // Then
        assertEquals(originalChips + bet, result.player!!.chips) // Return bet only
        assertEquals(HandStatus.PUSH, result.playerHands[0].status)
    }
    
    @Test
    fun `given player blackjack when settling then should award blackjack payout`() {
        // Given - Player blackjack vs dealer 20
        val playerHand = PlayerHand.initial(
            listOf(Card(Suit.SPADES, Rank.ACE), Card(Suit.DIAMONDS, Rank.KING)), // Blackjack
            10
        )
        val dealerHand = Hand(listOf(Card(Suit.HEARTS, Rank.KING), Card(Suit.CLUBS, Rank.QUEEN))) // 20
        val game = createTestGame(playerHands = listOf(playerHand), dealerHand = dealerHand)
        val originalChips = game.player!!.chips
        val bet = playerHand.bet
        
        // When
        val result = settlementService.settleRound(game)
        
        // Then
        val expectedWinnings = (bet * (1 + game.rules.blackjackPayout)).toInt()
        assertEquals(originalChips + expectedWinnings, result.player!!.chips)
        assertEquals(HandStatus.WIN, result.playerHands[0].status)
    }
    
    @Test
    fun `given both blackjack when settling then should be push`() {
        // Given - Both have blackjack
        val playerHand = PlayerHand.initial(
            listOf(Card(Suit.SPADES, Rank.ACE), Card(Suit.DIAMONDS, Rank.KING)), // Blackjack
            10
        )
        val dealerHand = Hand(listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.CLUBS, Rank.QUEEN))) // Blackjack
        val game = createTestGame(playerHands = listOf(playerHand), dealerHand = dealerHand)
        val originalChips = game.player!!.chips
        val bet = playerHand.bet
        
        // When
        val result = settlementService.settleRound(game)
        
        // Then
        assertEquals(originalChips + bet, result.player!!.chips) // Return bet only
        assertEquals(HandStatus.PUSH, result.playerHands[0].status)
    }
    
    @Test
    fun `given player busted when settling then should lose bet`() {
        // Given - Player busted
        val playerHand = PlayerHand.initial(
            listOf(Card(Suit.SPADES, Rank.KING), Card(Suit.DIAMONDS, Rank.QUEEN)), // 20
            10
        ).hit(Card(Suit.HEARTS, Rank.FIVE)) // 25 (busted)
        val game = createTestGame(playerHands = listOf(playerHand))
        val originalChips = game.player!!.chips
        
        // When
        val result = settlementService.settleRound(game)
        
        // Then
        assertEquals(originalChips, result.player!!.chips) // No winnings
        assertEquals(HandStatus.LOSS, result.playerHands[0].status)
    }
    
    @Test
    fun `given dealer busted when settling then should award winnings`() {
        // Given - Dealer busted
        val dealerHand = Hand(listOf(
            Card(Suit.HEARTS, Rank.KING), 
            Card(Suit.CLUBS, Rank.SEVEN),
            Card(Suit.SPADES, Rank.EIGHT) // 25 (busted)
        ))
        val game = createTestGame(dealerHand = dealerHand)
        val originalChips = game.player!!.chips
        val bet = game.playerHands[0].bet
        
        // When
        val result = settlementService.settleRound(game)
        
        // Then
        assertEquals(originalChips + (bet * 2), result.player!!.chips) // Return bet + win
        assertEquals(HandStatus.WIN, result.playerHands[0].status)
    }
    
    @Test
    fun `given surrendered hand when settling then should return half bet`() {
        // Given - Player surrendered
        val playerHand = PlayerHand.initial(
            listOf(Card(Suit.SPADES, Rank.KING), Card(Suit.DIAMONDS, Rank.SIX)), // 16
            10
        ).surrender()
        val game = createTestGame(playerHands = listOf(playerHand))
        val originalChips = game.player!!.chips
        val bet = playerHand.bet
        
        // When
        val result = settlementService.settleRound(game)
        
        // Then
        assertEquals(originalChips + (bet / 2), result.player!!.chips) // Half bet returned
        assertEquals(HandStatus.SURRENDERED, result.playerHands[0].status) // Keep surrendered status
    }
    
    @Test
    fun `given multiple hands when settling then should calculate total winnings`() {
        // Given - Multiple hands with different outcomes
        val hand1 = PlayerHand.initial(
            listOf(Card(Suit.SPADES, Rank.QUEEN), Card(Suit.DIAMONDS, Rank.NINE)), // 19 (wins)
            10
        )
        val hand2 = PlayerHand.initial(
            listOf(Card(Suit.HEARTS, Rank.KING), Card(Suit.CLUBS, Rank.SIX)), // 16 (loses)
            15
        )
        val hand3 = PlayerHand.initial(
            listOf(Card(Suit.SPADES, Rank.ACE), Card(Suit.DIAMONDS, Rank.KING)), // Blackjack
            20
        )
        val dealerHand = Hand(listOf(Card(Suit.HEARTS, Rank.EIGHT), Card(Suit.CLUBS, Rank.NINE))) // 17
        val game = createTestGame(
            playerHands = listOf(hand1, hand2, hand3),
            dealerHand = dealerHand
        )
        val originalChips = game.player!!.chips
        
        // When
        val result = settlementService.settleRound(game)
        
        // Then
        // Hand 1: Win 10 bet -> 20 winnings
        // Hand 2: Lose 15 bet -> 0 winnings
        // Hand 3: Blackjack 20 bet -> 50 winnings (20 + 30 bonus)
        val expectedWinnings = 20 + 0 + 50
        assertEquals(originalChips + expectedWinnings, result.player!!.chips)
        assertEquals(HandStatus.WIN, result.playerHands[0].status)
        assertEquals(HandStatus.LOSS, result.playerHands[1].status)
        assertEquals(HandStatus.WIN, result.playerHands[2].status)
    }
    
    // Test determine result logic directly
    @Test
    fun `given various scenarios when determining result then should return correct result`() {
        val dealerHand = Hand(listOf(Card(Suit.HEARTS, Rank.KING), Card(Suit.CLUBS, Rank.SEVEN))) // 17
        
        // Player wins
        val winningHand = PlayerHand.initial(listOf(Card(Suit.SPADES, Rank.QUEEN), Card(Suit.DIAMONDS, Rank.NINE)), 10) // 19
        assertEquals(RoundResult.PLAYER_WIN, settlementService.determineResult(winningHand, dealerHand))
        
        // Dealer wins
        val losingHand = PlayerHand.initial(listOf(Card(Suit.SPADES, Rank.KING), Card(Suit.DIAMONDS, Rank.SIX)), 10) // 16
        assertEquals(RoundResult.DEALER_WIN, settlementService.determineResult(losingHand, dealerHand))
        
        // Push
        val tiedHand = PlayerHand.initial(listOf(Card(Suit.SPADES, Rank.KING), Card(Suit.DIAMONDS, Rank.SEVEN)), 10) // 17
        assertEquals(RoundResult.PUSH, settlementService.determineResult(tiedHand, dealerHand))
    }
    
    // Test calculate winnings logic directly
    @Test
    fun `given various results when calculating winnings then should return correct amounts`() {
        val bet = 20
        val rules = GameRules()
        
        // Regular win
        assertEquals(40, settlementService.calculateWinnings(bet, RoundResult.PLAYER_WIN, rules))
        
        // Blackjack win
        assertEquals(50, settlementService.calculateWinnings(bet, RoundResult.PLAYER_BLACKJACK, rules)) // 20 + 30 bonus
        
        // Surrender
        assertEquals(10, settlementService.calculateWinnings(bet, RoundResult.SURRENDER, rules))
        
        // Push
        assertEquals(20, settlementService.calculateWinnings(bet, RoundResult.PUSH, rules))
        
        // Dealer win
        assertEquals(0, settlementService.calculateWinnings(bet, RoundResult.DEALER_WIN, rules))
    }
    
    @Test
    fun `given custom blackjack payout when calculating blackjack winnings then should use correct multiplier`() {
        // Given - Custom 2:1 blackjack payout instead of 3:2
        val customRules = GameRules(blackjackPayout = 2.0)
        val bet = 10
        
        // When
        val winnings = settlementService.calculateWinnings(bet, RoundResult.PLAYER_BLACKJACK, customRules)
        
        // Then
        assertEquals(30, winnings) // 10 bet + 20 bonus (2:1)
    }
}