package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class DealerPlayTest {
    
    @Test
    fun `given dealer soft 17 and dealer hits soft 17 rule when dealer plays then should hit`() {
        // Given - dealer軟17 (A-6) 且規則是hit soft 17
        val dealerSoft17 = Hand(listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.SIX)))
        val rules = GameRules(dealerHitsOnSoft17 = true)
        val round = Round(
            playerHand = Hand(listOf(Card(Suit.CLUBS, Rank.KING), Card(Suit.DIAMONDS, Rank.SEVEN))),
            dealerHand = dealerSoft17,
            bet = 10,
            phase = RoundPhase.DEALER_TURN
        )
        
        // When - dealer行動
        val testDeck = Deck.createStandardDeck(1)
        val (result, _) = round.dealerPlay(rules, testDeck)
        
        // Then - dealer應該抽牌 (手牌數量增加)
        assertTrue(result.dealerHand.cards.size > dealerSoft17.cards.size)
        assertEquals(RoundPhase.COMPLETED, result.phase)
    }
    
    @Test
    fun `given dealer soft 17 and dealer stands on soft 17 rule when dealer plays then should stand`() {
        // Given - dealer軟17 (A-6) 且規則是stand on soft 17
        val dealerSoft17 = Hand(listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.SIX)))
        val rules = GameRules(dealerHitsOnSoft17 = false)
        val round = Round(
            playerHand = Hand(listOf(Card(Suit.CLUBS, Rank.KING), Card(Suit.DIAMONDS, Rank.SEVEN))),
            dealerHand = dealerSoft17,
            bet = 10,
            phase = RoundPhase.DEALER_TURN
        )
        
        // When - dealer行動
        val testDeck = Deck.createStandardDeck(1)
        val (result, _) = round.dealerPlay(rules, testDeck)
        
        // Then - dealer應該站牌 (手牌數量不變)
        assertEquals(dealerSoft17.cards.size, result.dealerHand.cards.size)
        assertEquals(RoundPhase.COMPLETED, result.phase)
    }
    
    @Test
    fun `given dealer hard 16 when dealer plays then should hit until 17 or bust`() {
        // Given - dealer硬16
        val dealerHard16 = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.SIX)))
        val rules = GameRules()
        val round = Round(
            playerHand = Hand(listOf(Card(Suit.CLUBS, Rank.KING), Card(Suit.DIAMONDS, Rank.SEVEN))),
            dealerHand = dealerHard16,
            bet = 10,
            phase = RoundPhase.DEALER_TURN
        )
        
        // When - dealer行動
        val testDeck = Deck.createStandardDeck(1)
        val (result, _) = round.dealerPlay(rules, testDeck)
        
        // Then - dealer應該抽牌到17+或爆牌
        assertTrue(result.dealerHand.bestValue >= 17 || result.dealerHand.isBusted)
        assertEquals(RoundPhase.COMPLETED, result.phase)
    }
    
    @Test
    fun `given dealer hard 18 when dealer plays then should stand`() {
        // Given - dealer硬18
        val dealerHard18 = Hand(listOf(Card(Suit.HEARTS, Rank.TEN), Card(Suit.SPADES, Rank.EIGHT)))
        val rules = GameRules()
        val round = Round(
            playerHand = Hand(listOf(Card(Suit.CLUBS, Rank.KING), Card(Suit.DIAMONDS, Rank.SEVEN))),
            dealerHand = dealerHard18,
            bet = 10,
            phase = RoundPhase.DEALER_TURN
        )
        
        // When - dealer行動
        val testDeck = Deck.createStandardDeck(1)
        val (result, _) = round.dealerPlay(rules, testDeck)
        
        // Then - dealer應該站牌 (手牌不變)
        assertEquals(dealerHard18.cards.size, result.dealerHand.cards.size)
        assertEquals(18, result.dealerHand.bestValue)
        assertEquals(RoundPhase.COMPLETED, result.phase)
    }
}