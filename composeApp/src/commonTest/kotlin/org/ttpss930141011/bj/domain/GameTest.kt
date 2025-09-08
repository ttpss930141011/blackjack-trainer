package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*

class GameTest {
    
    private val rules = GameRules()
    private val testPlayer = Player(id = "test-player", chips = 1000)
    
    @Test
    fun `given new game when created then should have correct initial state`() {
        // When
        val game = Game.create(rules)
        
        // Then - Using BetState
        assertEquals(0, game.betState.amount)
        assertFalse(game.betState.hasCommittedBet)
        
        assertEquals(GamePhase.WAITING_FOR_BETS, game.phase)
        assertFalse(game.hasPlayer)
        assertFalse(game.hasPendingBet)
        assertFalse(game.hasCommittedBet)
        assertFalse(game.canDealCards)
    }
    
    @Test
    fun `given game when adding player then should have player`() {
        // Given
        val game = Game.create(rules)
        
        // When
        val gameWithPlayer = game.addPlayer(testPlayer)
        
        // Then
        assertTrue(gameWithPlayer.hasPlayer)
        assertEquals(1000, gameWithPlayer.player?.chips)
    }
    
    @Test
    fun `given game with pending bet when adding more chips then should increase pending bet`() {
        // Given
        val game = Game.create(rules)
            .addPlayer(testPlayer)
            .addToPendingBet(100)
        
        // When
        val updatedGame = game.addToPendingBet(50)
        
        // Then - Using BetState
        assertEquals(150, updatedGame.betState.amount)
        assertTrue(updatedGame.betState.isPending)
        assertFalse(updatedGame.betState.hasCommittedBet)
        
        assertEquals(1000, updatedGame.player?.chips) // Chips not deducted yet
        assertTrue(updatedGame.hasPendingBet)
        assertTrue(updatedGame.canDealCards)
    }
    
    @Test
    fun `given game when adding chips exceeding player balance then should throw exception`() {
        // Given
        val game = Game.create(rules).addPlayer(testPlayer)
        
        // When/Then
        assertFailsWith<IllegalArgumentException> {
            game.addToPendingBet(1500) // More than 1000 chips
        }
    }
    
    @Test
    fun `given game with pending bet when clearing then should reset to zero`() {
        // Given
        val game = Game.create(rules)
            .addPlayer(testPlayer)
            .addToPendingBet(200)
        
        // When
        val clearedGame = game.clearPendingBet()
        
        // Then - Using BetState
        assertEquals(0, clearedGame.betState.amount)
        assertFalse(clearedGame.betState.isPending)
        
        assertFalse(clearedGame.hasPendingBet)
        assertFalse(clearedGame.canDealCards)
    }
    
    @Test
    fun `given game with pending bet when committing then should deduct chips and set current bet`() {
        // Given
        val game = Game.create(rules)
            .addPlayer(testPlayer)
            .addToPendingBet(300)
        
        // When
        val committedGame = game.commitPendingBet()
        
        // Then - Using BetState
        assertEquals(300, committedGame.betState.amount)
        assertTrue(committedGame.betState.hasCommittedBet)
        assertFalse(committedGame.betState.isPending)
        
        assertEquals(700, committedGame.player?.chips) // 1000 - 300 = 700
        assertFalse(committedGame.hasPendingBet)
        assertTrue(committedGame.hasCommittedBet)
    }
    
    @Test
    fun `given game when committing without pending bet then should throw exception`() {
        // Given
        val game = Game.create(rules).addPlayer(testPlayer)
        
        // When/Then
        assertFailsWith<IllegalArgumentException> {
            game.commitPendingBet()
        }
    }
    
    @Test
    fun `given game when adding to pending bet without player then should throw exception`() {
        // Given
        val game = Game.create(rules)
        
        // When/Then
        assertFailsWith<IllegalArgumentException> {
            game.addToPendingBet(100)
        }
    }
    
    @Test
    fun `given game when adding negative amount then should throw exception`() {
        // Given
        val game = Game.create(rules).addPlayer(testPlayer)
        
        // When/Then
        assertFailsWith<IllegalArgumentException> {
            game.addToPendingBet(-50)
        }
    }
    
    @Test
    fun `given game when adding zero amount then should throw exception`() {
        // Given
        val game = Game.create(rules).addPlayer(testPlayer)
        
        // When/Then
        assertFailsWith<IllegalArgumentException> {
            game.addToPendingBet(0)
        }
    }
    
    @Test
    fun `given game with committed bet when resetting for new round then should clear all bets`() {
        // Given
        val game = Game.create(rules)
            .addPlayer(testPlayer)
            .addToPendingBet(200)
            .commitPendingBet()
        
        // When
        val newRoundGame = game.resetForNewRound()
        
        // Then - Using BetState
        assertEquals(0, newRoundGame.betState.amount)
        assertFalse(newRoundGame.betState.isPending)
        assertFalse(newRoundGame.betState.hasCommittedBet)
        
        assertEquals(GamePhase.WAITING_FOR_BETS, newRoundGame.phase)
        assertFalse(newRoundGame.hasPendingBet)
        assertFalse(newRoundGame.hasCommittedBet)
        assertTrue(newRoundGame.hasPlayer) // Player should be preserved
        assertEquals(800, newRoundGame.player?.chips) // Chips remain as deducted
    }
    
    @Test
    fun `given game with insufficient chips when adding to pending bet then should throw exception`() {
        // Given
        val poorPlayer = Player(id = "poor-player", chips = 50)
        val game = Game.create(rules)
            .addPlayer(poorPlayer)
        
        // When/Then
        assertFailsWith<IllegalArgumentException> {
            game.addToPendingBet(100) // More than available
        }
    }
    
    @Test
    fun `given game when domain queries should return correct values`() {
        // Given
        val game = Game.create(rules)
            .addPlayer(testPlayer)
            .addToPendingBet(150)
        
        // Then - Test all domain queries
        assertTrue(game.hasPlayer)
        assertTrue(game.hasPendingBet)
        assertFalse(game.hasCommittedBet)
        assertTrue(game.canDealCards)
        assertEquals(GamePhase.WAITING_FOR_BETS, game.phase)
    }
    
    @Test
    fun `given game with multiple pending bet additions then should accumulate correctly`() {
        // Given
        val game = Game.create(rules).addPlayer(testPlayer)
        
        // When - Add chips in multiple steps
        val finalGame = game
            .addToPendingBet(100)
            .addToPendingBet(75)
            .addToPendingBet(25)
        
        // Then
        assertEquals(200, finalGame.betState.amount)
        assertEquals(1000, finalGame.player?.chips) // Still not deducted
    }
    
    @Test
    fun `given game when clearing pending bet after multiple additions then should reset correctly`() {
        // Given
        val game = Game.create(rules)
            .addPlayer(testPlayer)
            .addToPendingBet(100)
            .addToPendingBet(200)
        
        // When
        val clearedGame = game.clearPendingBet()
        
        // Then
        assertEquals(0, clearedGame.betState.amount)
        assertEquals(1000, clearedGame.player?.chips) // No chips should be affected
    }
    
    @Test
    fun `given valid game when trying to add chip then should succeed`() {
        // Given
        val game = Game.create(rules).addPlayer(testPlayer)
        
        // When
        val result = game.tryAddChipToPendingBet(ChipValue.ONE_HUNDRED)
        
        // Then
        assertTrue(result.success)
        assertEquals(null, result.errorMessage)
        assertEquals(100, result.updatedGame.betState.amount)
        assertEquals(1000, result.updatedGame.player?.chips) // Chips not deducted yet
    }
    
    @Test
    fun `given game without player when trying to add chip then should fail`() {
        // Given
        val game = Game.create(rules)
        
        // When
        val result = game.tryAddChipToPendingBet(ChipValue.FIFTY)
        
        // Then
        assertFalse(result.success)
        assertEquals("No player in game", result.errorMessage)
        assertEquals(game, result.updatedGame) // Original game unchanged
    }
    
    @Test
    fun `given insufficient chips when trying to add chip then should fail`() {
        // Given
        val poorPlayer = Player(id = "poor-player", chips = 30)
        val game = Game.create(rules).addPlayer(poorPlayer)
        
        // When
        val result = game.tryAddChipToPendingBet(ChipValue.FIFTY)
        
        // Then
        assertFalse(result.success)
        assertEquals("Insufficient chips", result.errorMessage)
        assertEquals(0, result.updatedGame.betState.amount) // No change in pending bet
    }
    
    @Test
    fun `given game with existing pending bet when trying to add chip then should accumulate`() {
        // Given
        val game = Game.create(rules)
            .addPlayer(testPlayer)
            .addToPendingBet(100)
        
        // When
        val result = game.tryAddChipToPendingBet(ChipValue.FIFTY)
        
        // Then
        assertTrue(result.success)
        assertEquals(null, result.errorMessage)
        assertEquals(150, result.updatedGame.betState.amount)
    }
    
    @Test
    fun `given game with pending bet exceeding balance when trying to add chip then should fail`() {
        // Given
        val game = Game.create(rules)
            .addPlayer(testPlayer)
            .addToPendingBet(950) // Almost all chips used
        
        // When
        val result = game.tryAddChipToPendingBet(ChipValue.ONE_HUNDRED)
        
        // Then
        assertFalse(result.success)
        assertEquals("Insufficient chips", result.errorMessage)
        assertEquals(950, result.updatedGame.betState.amount) // Bet unchanged
    }
}