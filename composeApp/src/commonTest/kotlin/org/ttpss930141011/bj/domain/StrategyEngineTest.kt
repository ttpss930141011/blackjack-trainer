package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class StrategyEngineTest {
    
    private val strategyEngine = StrategyEngine()
    private val standardRules = GameRules()
    private val noSurrenderRules = GameRules(surrenderAllowed = false)
    
    @Test
    fun `given player hard 16 vs dealer 10 when surrender allowed then should surrender`() {
        // Given - 硬16 vs 10 with surrender allowed (optimal surrender scenario)
        val playerHand = Hand(listOf(
            Card(Suit.HEARTS, Rank.TEN), 
            Card(Suit.SPADES, Rank.SIX)
        ))
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.TEN)
        
        // When
        val action = strategyEngine.getOptimalAction(playerHand, dealerUpCard, standardRules)
        
        // Then
        assertEquals(Action.SURRENDER, action)
    }
    
    @Test
    fun `given player hard 16 vs dealer 10 when surrender disabled then should hit`() {
        // Given - 硬16 vs 10 without surrender (fallback to hit)
        val playerHand = Hand(listOf(
            Card(Suit.HEARTS, Rank.TEN), 
            Card(Suit.SPADES, Rank.SIX)
        ))
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.TEN)
        
        // When
        val action = strategyEngine.getOptimalAction(playerHand, dealerUpCard, noSurrenderRules)
        
        // Then
        assertEquals(Action.HIT, action)
    }
    
    @Test
    fun `given player 17 vs dealer 10 when getting optimal action then should stand`() {
        // Given - 硬17永遠停牌
        val playerHand = Hand(listOf(
            Card(Suit.HEARTS, Rank.TEN), 
            Card(Suit.SPADES, Rank.SEVEN)
        ))
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.TEN)
        
        // When
        val action = strategyEngine.getOptimalAction(playerHand, dealerUpCard, standardRules)
        
        // Then
        assertEquals(Action.STAND, action)
    }
    
    @Test
    fun `given player soft 17 vs dealer 6 when getting optimal action then should double`() {
        // Given - A,6 vs 6應該加倍 (參考軟手牌策略表)
        val playerHand = Hand(listOf(
            Card(Suit.HEARTS, Rank.ACE), 
            Card(Suit.SPADES, Rank.SIX)
        ))
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.SIX)
        
        // When
        val action = strategyEngine.getOptimalAction(playerHand, dealerUpCard, standardRules)
        
        // Then
        assertEquals(Action.DOUBLE, action)
    }
    
    @Test
    fun `given player pair of 8s vs dealer 10 when getting optimal action then should split`() {
        // Given - 8,8永遠分牌
        val playerHand = Hand(listOf(
            Card(Suit.HEARTS, Rank.EIGHT), 
            Card(Suit.SPADES, Rank.EIGHT)
        ))
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.TEN)
        
        // When
        val action = strategyEngine.getOptimalAction(playerHand, dealerUpCard, standardRules)
        
        // Then
        assertEquals(Action.SPLIT, action)
    }
    
    @Test
    fun `given player 11 vs dealer ace when getting optimal action then should hit`() {
        // Given - 11 vs A 只能要牌(不能加倍)
        val playerHand = Hand(listOf(
            Card(Suit.HEARTS, Rank.FIVE), 
            Card(Suit.SPADES, Rank.SIX)
        ))
        val dealerUpCard = Card(Suit.DIAMONDS, Rank.ACE)
        
        // When
        val action = strategyEngine.getOptimalAction(playerHand, dealerUpCard, standardRules)
        
        // Then
        assertEquals(Action.HIT, action)
    }
}