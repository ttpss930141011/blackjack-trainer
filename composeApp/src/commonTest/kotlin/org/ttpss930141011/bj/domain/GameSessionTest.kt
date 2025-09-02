package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameSessionTest {
    
    @Test
    fun `given new game session when created then should have 500 chips and no current round`() {
        // Given & When
        val session = GameSession()
        
        // Then
        assertEquals(500, session.chips)
        assertNull(session.currentRound)
        assertEquals(0, session.stats.totalRounds)
        assertEquals(0, session.stats.perfectRounds)
    }
    
    @Test
    fun `given game session when starting round with bet then should create new round`() {
        // Given
        val session = GameSession()
        val deck = Deck.shuffled()
        
        // When
        val result = session.startNewRound(bet = 25, deck = deck)
        val newSession = result.first
        val newDeck = result.second
        
        // Then
        assertEquals(475, newSession.chips) // 500 - 25
        assertTrue(newSession.currentRound != null)
        assertEquals(25, newSession.currentRound!!.bet)
        assertEquals(RoundPhase.PLAYER_TURN, newSession.currentRound!!.phase)
        assertTrue(newDeck.remainingCards < deck.remainingCards)
    }
    
    @Test
    fun `given session with insufficient chips when starting round then should fail`() {
        // Given
        val session = GameSession(chips = 10)
        val deck = Deck.shuffled()
        
        // When & Then
        try {
            session.startNewRound(
                bet = 25, // 需要25但只有10籌碼
                deck = deck
            )
            assertTrue(false, "Should throw exception for insufficient chips")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("Insufficient chips") == true)
        }
    }
    
    @Test
    fun `given invalid bet amount when starting round then should fail`() {
        // Given
        val session = GameSession()
        val deck = Deck.shuffled()
        
        // When & Then - 測試非5的倍數
        try {
            session.startNewRound(
                bet = 7, // 不是5的倍數
                deck = deck
            )
            assertTrue(false, "Should throw exception for invalid bet")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("must be multiple of 5") == true)
        }
    }
}