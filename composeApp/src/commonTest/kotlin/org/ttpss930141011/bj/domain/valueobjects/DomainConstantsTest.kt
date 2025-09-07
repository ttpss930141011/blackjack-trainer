package org.ttpss930141011.bj.domain.valueobjects

import kotlin.test.Test
import kotlin.test.assertEquals

class DomainConstantsTest {

    @Test
    fun `given hard hand when generating hand type then should return H prefix with value`() {
        // Given
        val cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.SPADES, Rank.SIX)
        )
        val hand = Hand(cards)
        
        // When
        val handType = DomainConstants.generateHandType(hand)
        
        // Then
        assertEquals("H16", handType)
    }

    @Test
    fun `given soft hand when generating hand type then should return S prefix with best value`() {
        // Given
        val cards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.SIX)
        )
        val hand = Hand(cards)
        
        // When
        val handType = DomainConstants.generateHandType(hand)
        
        // Then
        assertEquals("S17", handType)
    }

    @Test
    fun `given pair when generating hand type then should return Pair format with rank`() {
        // Given
        val cards = listOf(
            Card(Suit.HEARTS, Rank.EIGHT),
            Card(Suit.SPADES, Rank.EIGHT)
        )
        val hand = Hand(cards)
        
        // When
        val handType = DomainConstants.generateHandType(hand)
        
        // Then
        assertEquals("Pair 8s", handType)
    }

    @Test
    fun `given ace pair when generating hand type then should return Pair As`() {
        // Given
        val cards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.ACE)
        )
        val hand = Hand(cards)
        
        // When
        val handType = DomainConstants.generateHandType(hand)
        
        // Then
        assertEquals("Pair As", handType)
    }

    @Test
    fun `given blackjack when generating hand type then should return BJ`() {
        // Given
        val cards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.KING)
        )
        val hand = Hand(cards)
        
        // When
        val handType = DomainConstants.generateHandType(hand)
        
        // Then
        assertEquals("BJ", handType)
    }

    @Test
    fun `given hand type and dealer rank when generating scenario key then should format correctly`() {
        // Given
        val handType = "H16"
        val dealerRank = "10"
        
        // When
        val scenarioKey = DomainConstants.generateScenarioKey(handType, dealerRank)
        
        // Then
        assertEquals("H16 vs 10", scenarioKey)
    }

    @Test
    fun `given all ranks when getting short symbols then should return correct mappings`() {
        // Test number cards
        assertEquals("2", DomainConstants.getShortRankSymbol(Rank.TWO))
        assertEquals("3", DomainConstants.getShortRankSymbol(Rank.THREE))
        assertEquals("9", DomainConstants.getShortRankSymbol(Rank.NINE))
        assertEquals("10", DomainConstants.getShortRankSymbol(Rank.TEN))
        
        // Test face cards
        assertEquals("J", DomainConstants.getShortRankSymbol(Rank.JACK))
        assertEquals("Q", DomainConstants.getShortRankSymbol(Rank.QUEEN))
        assertEquals("K", DomainConstants.getShortRankSymbol(Rank.KING))
        assertEquals("A", DomainConstants.getShortRankSymbol(Rank.ACE))
    }

    @Test
    fun `given game rules when generating rule hash then should produce 6 character hex`() {
        // Given
        val gameRules = GameRules(
            dealerHitsOnSoft17 = true,
            doubleAfterSplitAllowed = true,
            surrenderAllowed = false
        )
        
        // When
        val ruleHash = DomainConstants.generateRuleHash(gameRules)
        
        // Then
        assertEquals(6, ruleHash.length)
        // Should be valid hex characters
        val validHexPattern = Regex("[0-9a-f]{6}")
        kotlin.test.assertTrue(validHexPattern.matches(ruleHash))
    }

    @Test
    fun `given different game rules when generating rule hash then should produce different hashes`() {
        // Given
        val rules1 = GameRules(dealerHitsOnSoft17 = true)
        val rules2 = GameRules(dealerHitsOnSoft17 = false)
        
        // When
        val hash1 = DomainConstants.generateRuleHash(rules1)
        val hash2 = DomainConstants.generateRuleHash(rules2)
        
        // Then
        kotlin.test.assertNotEquals(hash1, hash2)
    }

    @Test
    fun `given same game rules when generating rule hash then should produce identical hashes`() {
        // Given
        val rules1 = GameRules(dealerHitsOnSoft17 = true, doubleAfterSplitAllowed = false)
        val rules2 = GameRules(dealerHitsOnSoft17 = true, doubleAfterSplitAllowed = false)
        
        // When
        val hash1 = DomainConstants.generateRuleHash(rules1)
        val hash2 = DomainConstants.generateRuleHash(rules2)
        
        // Then
        assertEquals(hash1, hash2)
    }

    @Test
    fun `domain constraints should have expected values`() {
        assertEquals(3, DomainConstants.Constraints.MIN_SCENARIO_SAMPLES)
        assertEquals(6, DomainConstants.Constraints.RULE_HASH_LENGTH)
        assertEquals(1000, DomainConstants.Constraints.MAX_DECISION_HISTORY)
    }
}