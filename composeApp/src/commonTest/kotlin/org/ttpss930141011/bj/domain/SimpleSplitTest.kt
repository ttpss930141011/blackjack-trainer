package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class SimpleSplitTest {
    
    @Test
    fun `given JQ hand when checking canSplit then should return false`() {
        // Given - JQ手牌（根據 docs/blackjack-rules.md 第78行: 10,10永遠Stand）
        val jqHand = Hand(listOf(Card(Suit.HEARTS, Rank.JACK), Card(Suit.SPADES, Rank.QUEEN)))
        
        // When & Then - 不應該可以分牌（只有同rank才能分）
        assertFalse(jqHand.canSplit, "JQ should not be splittable - only same rank can split")
    }
    
    @Test
    fun `given JJ pair when player splits then should not throw exception`() {
        // Given - 真正的對子JJ（較簡單的測試）
        val jackPair = Hand(listOf(Card(Suit.HEARTS, Rank.JACK), Card(Suit.SPADES, Rank.JACK)))
        val dealerHand = Hand(listOf(Card(Suit.DIAMONDS, Rank.SEVEN)))
        val round = Round(
            playerHand = jackPair,
            dealerHand = dealerHand,
            bet = 25,
            phase = RoundPhase.PLAYER_TURN
        )
        val deck = Deck.shuffled()
        
        // When - 玩家選擇分牌 (測試不會拋例外)
        val result = round.playerAction(Action.SPLIT, deck)
        
        // Then - 應該成功執行且有結果
        assertTrue(result.round.isSplitRound)
        assertEquals(2, result.round.playerHands.size)
    }
}