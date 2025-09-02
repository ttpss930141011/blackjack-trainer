package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

class RoundTest {
    
    private val defaultRules = GameRules()
    private val playerHand = Hand(listOf(Card(Suit.HEARTS, Rank.KING), Card(Suit.SPADES, Rank.SIX)))
    private val dealerUpCard = Card(Suit.DIAMONDS, Rank.NINE)
    private val dealerHand = Hand(listOf(dealerUpCard))
    
    @Test
    fun `given new round when player hits then should progress to dealer turn if player stands or busts`() {
        // Given - 新round在玩家決策階段
        val round = Round(
            playerHand = playerHand,
            dealerHand = dealerHand,
            bet = 10,
            phase = RoundPhase.PLAYER_TURN
        )
        
        // When - 玩家選擇站牌
        val testDeck = Deck.shuffled()
        val result = round.playerAction(Action.STAND, testDeck)
        
        // Then - 應該進入dealer階段
        assertEquals(RoundPhase.DEALER_TURN, result.round.phase)
    }
    
    @Test
    fun `given round in dealer turn when dealer plays then should complete round with result`() {
        // Given - dealer階段，dealer有16點
        val dealerHand = Hand(listOf(
            Card(Suit.HEARTS, Rank.KING),
            Card(Suit.SPADES, Rank.SIX)
        ))
        val round = Round(
            playerHand = playerHand, // 16點
            dealerHand = dealerHand, // 16點
            bet = 10,
            phase = RoundPhase.DEALER_TURN
        )
        
        // When - dealer按規則抽牌
        val testDeck = Deck.createStandardDeck(1)
        val (result, _) = round.dealerPlay(defaultRules, testDeck)
        
        // Then - round應該完成且有結果
        assertEquals(RoundPhase.COMPLETED, result.phase)
        assertTrue(result.dealerHand.bestValue >= 17 || result.dealerHand.isBusted)
    }
    
    @Test
    fun `given player 21 when checking available actions then should only allow stand`() {
        // Given - 玩家21點
        val blackjackHand = Hand(listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.KING)))
        val round = Round(
            playerHand = blackjackHand,
            dealerHand = dealerHand,
            bet = 10,
            phase = RoundPhase.PLAYER_TURN
        )
        
        // When - 檢查可用行動
        val availableActions = round.availableActions()
        
        // Then - 只能站牌
        assertEquals(setOf(Action.STAND), availableActions)
    }
    
    @Test
    fun `given pair hand when can split then should allow split action`() {
        // Given - 對子手牌
        val pairHand = Hand(listOf(Card(Suit.HEARTS, Rank.EIGHT), Card(Suit.SPADES, Rank.EIGHT)))
        val round = Round(
            playerHand = pairHand,
            dealerHand = dealerHand,
            bet = 10,
            phase = RoundPhase.PLAYER_TURN
        )
        
        // When - 檢查可用行動
        val availableActions = round.availableActions()
        
        // Then - 應該包含分牌
        assertTrue(Action.SPLIT in availableActions)
    }
    
    @Test
    fun `given round with decision history when calculating round correctness then should consider overall strategy`() {
        // Given - round有決策歷史
        val round = Round(
            playerHand = playerHand,
            dealerHand = dealerHand,
            bet = 10,
            phase = RoundPhase.COMPLETED
        )
        // 模擬決策：玩家Hit(正確) → Stand(正確)
        val decisions = listOf(
            PlayerDecision(Action.HIT, true),
            PlayerDecision(Action.STAND, true)
        )
        
        // When - 計算整round正確性
        val roundCorrectness = round.calculateOverallCorrectness(decisions)
        
        // Then - 整round應該算正確
        assertTrue(roundCorrectness.isCorrect)
        assertEquals(2, roundCorrectness.totalDecisions)
    }
    
    @Test
    fun `given dealer soft 17 rule when dealer has ace-6 then should hit`() {
        // Given - dealer軟17規則，dealer有A-6
        val dealerSoft17 = Hand(listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.SPADES, Rank.SIX)))
        val rules = GameRules(dealerHitsOnSoft17 = true)
        val round = Round(
            playerHand = playerHand,
            dealerHand = dealerSoft17,
            bet = 10,
            phase = RoundPhase.DEALER_TURN
        )
        
        // When - dealer行動
        val testDeck = Deck.createStandardDeck(1)
        val (result, _) = round.dealerPlay(rules, testDeck)
        
        // Then - dealer應該抽牌
        assertTrue(result.dealerHand.cards.size > dealerSoft17.cards.size)
    }
}