package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

class SurrenderTest {
    
    private val standardRules = GameRules(surrenderAllowed = true)
    private val noSurrenderRules = GameRules(surrenderAllowed = false)
    
    // === Surrender Availability Tests ===
    
    @Test
    fun `given hard 16 vs dealer 9 when surrender allowed then should include surrender in available actions`() {
        // Given - Hard 16 vs 9 (optimal surrender scenario)
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)))
        val dealerHand = Hand(listOf(Card(Suit.DIAMONDS, Rank.NINE)))
        val round = Round(playerHand, dealerHand, 20, RoundPhase.PLAYER_TURN)
        
        // When
        val availableActions = round.availableActions(standardRules)
        
        // Then
        assertTrue(Action.SURRENDER in availableActions)
    }
    
    @Test
    fun `given hard 15 vs dealer 10 when surrender allowed then should include surrender in available actions`() {
        // Given - Hard 15 vs 10 (optimal surrender scenario)
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.NINE), Card(Suit.SPADES, Rank.SIX)))
        val dealerHand = Hand(listOf(Card(Suit.DIAMONDS, Rank.TEN)))
        val round = Round(playerHand, dealerHand, 20, RoundPhase.PLAYER_TURN)
        
        // When
        val availableActions = round.availableActions(standardRules)
        
        // Then
        assertTrue(Action.SURRENDER in availableActions)
    }
    
    @Test
    fun `given surrender disabled in rules when checking available actions then should not include surrender`() {
        // Given - Surrender disabled
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)))
        val dealerHand = Hand(listOf(Card(Suit.DIAMONDS, Rank.NINE)))
        val round = Round(playerHand, dealerHand, 20, RoundPhase.PLAYER_TURN)
        
        // When
        val availableActions = round.availableActions(noSurrenderRules)
        
        // Then
        assertFalse(Action.SURRENDER in availableActions)
    }
    
    @Test
    fun `given three cards when checking available actions then should not include surrender`() {
        // Given - Three cards (surrender only on first two)
        val playerHand = Hand(listOf(
            Card(Suit.HEARTS, Rank.FIVE),
            Card(Suit.SPADES, Rank.SIX),
            Card(Suit.DIAMONDS, Rank.FIVE)
        ))
        val dealerHand = Hand(listOf(Card(Suit.DIAMONDS, Rank.TEN)))
        val round = Round(playerHand, dealerHand, 20, RoundPhase.PLAYER_TURN)
        
        // When
        val availableActions = round.availableActions(standardRules)
        
        // Then
        assertFalse(Action.SURRENDER in availableActions)
    }
    
    @Test
    fun `given split round when checking available actions then should not include surrender`() {
        // Given - Split round (surrender not available after split)
        val playerHands = listOf(
            Hand(listOf(Card(Suit.HEARTS, Rank.EIGHT), Card(Suit.DIAMONDS, Rank.THREE))),
            Hand(listOf(Card(Suit.SPADES, Rank.EIGHT), Card(Suit.CLUBS, Rank.FOUR)))
        )
        val dealerHand = Hand(listOf(Card(Suit.DIAMONDS, Rank.TEN)))
        val round = Round(
            playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.EIGHT), Card(Suit.SPADES, Rank.EIGHT))),
            dealerHand = dealerHand,
            bet = 20,
            phase = RoundPhase.PLAYER_TURN,
            playerHands = playerHands,
            currentHandIndex = 0
        )
        
        // When
        val availableActions = round.availableActions(standardRules)
        
        // Then
        assertFalse(Action.SURRENDER in availableActions)
    }
    
    // === Surrender Execution Tests ===
    
    @Test
    fun `given valid surrender scenario when player surrenders then should complete round immediately`() {
        // Given - Hard 16 vs 9, valid surrender
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)))
        val dealerHand = Hand(listOf(Card(Suit.DIAMONDS, Rank.NINE)))
        val round = Round(playerHand, dealerHand, 20, RoundPhase.PLAYER_TURN)
        val deck = Deck.createStandardDeck(1)
        
        // When
        val result = round.playerAction(Action.SURRENDER, deck, standardRules)
        
        // Then
        assertEquals(RoundPhase.COMPLETED, result.round.phase)
        assertEquals(1, result.round.decisions.size)
        assertEquals(Action.SURRENDER, result.round.decisions[0].action)
    }
    
    @Test
    fun `given invalid surrender attempt when surrender not allowed then should throw exception`() {
        // Given - Surrender disabled in rules
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)))
        val dealerHand = Hand(listOf(Card(Suit.DIAMONDS, Rank.NINE)))
        val round = Round(playerHand, dealerHand, 20, RoundPhase.PLAYER_TURN)
        val deck = Deck.createStandardDeck(1)
        
        // When/Then
        assertFailsWith<IllegalArgumentException> {
            round.playerAction(Action.SURRENDER, deck, noSurrenderRules)
        }
    }
    
    @Test
    fun `given three cards when attempting surrender then should throw exception`() {
        // Given - Three cards (invalid surrender)
        val playerHand = Hand(listOf(
            Card(Suit.HEARTS, Rank.FIVE),
            Card(Suit.SPADES, Rank.SIX),
            Card(Suit.DIAMONDS, Rank.FIVE)
        ))
        val dealerHand = Hand(listOf(Card(Suit.DIAMONDS, Rank.NINE)))
        val round = Round(playerHand, dealerHand, 20, RoundPhase.PLAYER_TURN)
        val deck = Deck.createStandardDeck(1)
        
        // When/Then
        assertFailsWith<IllegalArgumentException> {
            round.playerAction(Action.SURRENDER, deck, standardRules)
        }
    }
    
    // === Strategy Engine Surrender Tests ===
    
    @Test
    fun `given hard 16 vs dealer 9 when surrender allowed then strategy should recommend surrender`() {
        // Given - Hard 16 vs 9 (optimal surrender per docs/blackjack-rules.md line 98)
        val strategyEngine = StrategyEngine()
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)))
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.NINE)
        
        // When
        val action = strategyEngine.getOptimalAction(playerHand, dealerUpCard, standardRules)
        
        // Then
        assertEquals(Action.SURRENDER, action)
    }
    
    @Test
    fun `given hard 16 vs dealer ace when surrender allowed then strategy should recommend surrender`() {
        // Given - Hard 16 vs A (optimal surrender per docs/blackjack-rules.md line 98)
        val strategyEngine = StrategyEngine()
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)))
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.ACE)
        
        // When
        val action = strategyEngine.getOptimalAction(playerHand, dealerUpCard, standardRules)
        
        // Then
        assertEquals(Action.SURRENDER, action)
    }
    
    @Test
    fun `given hard 15 vs dealer 10 when surrender allowed then strategy should recommend surrender`() {
        // Given - Hard 15 vs 10 (optimal surrender per docs/blackjack-rules.md line 99)
        val strategyEngine = StrategyEngine()
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.NINE), Card(Suit.SPADES, Rank.SIX)))
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.TEN)
        
        // When
        val action = strategyEngine.getOptimalAction(playerHand, dealerUpCard, standardRules)
        
        // Then
        assertEquals(Action.SURRENDER, action)
    }
    
    @Test
    fun `given hard 16 vs dealer 6 when surrender allowed then strategy should recommend stand not surrender`() {
        // Given - Hard 16 vs 6 (should stand, not surrender)
        val strategyEngine = StrategyEngine()
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)))
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.SIX)
        
        // When
        val action = strategyEngine.getOptimalAction(playerHand, dealerUpCard, standardRules)
        
        // Then
        assertEquals(Action.STAND, action)
    }
    
    @Test
    fun `given surrender disabled when hard 16 vs dealer 9 then strategy should recommend hit`() {
        // Given - Surrender disabled, fallback to hit
        val strategyEngine = StrategyEngine()
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)))
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.NINE)
        
        // When
        val action = strategyEngine.getOptimalAction(playerHand, dealerUpCard, noSurrenderRules)
        
        // Then
        assertEquals(Action.HIT, action)
    }
    
    // === Edge Cases ===
    
    @Test
    fun `given soft 17 vs dealer ace when surrender allowed then should hit not surrender`() {
        // Given - Soft hands generally don't surrender
        val strategyEngine = StrategyEngine()
        val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.SIX)))
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.ACE)
        
        // When
        val action = strategyEngine.getOptimalAction(playerHand, dealerUpCard, standardRules)
        
        // Then
        assertEquals(Action.HIT, action)  // Soft 17 vs A should hit, not surrender
    }
    
    @Test
    fun `given already hit once when hard 16 vs dealer 9 then should not include surrender`() {
        // Given - Three cards (already hit), cannot surrender
        val playerHand = Hand(listOf(
            Card(Suit.HEARTS, Rank.FIVE),
            Card(Suit.SPADES, Rank.SIX),
            Card(Suit.DIAMONDS, Rank.FIVE)  // 16 total, but three cards
        ))
        val dealerHand = Hand(listOf(Card(Suit.DIAMONDS, Rank.NINE)))
        val round = Round(playerHand, dealerHand, 20, RoundPhase.PLAYER_TURN)
        
        // When
        val availableActions = round.availableActions(standardRules)
        
        // Then
        assertFalse(Action.SURRENDER in availableActions)
    }
}