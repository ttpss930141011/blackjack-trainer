package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class BettingTableTest {
    
    @Test
    fun `given new game in waiting for bets phase when creating betting table state then should show empty betting spot`() {
        // Given - new game with player
        val player = Player(id = "player1", chips = 500)
        val game = Game.create(GameRules()).addPlayer(player)
        
        // When - creating betting table state
        val bettingTable = BettingTableState.fromGame(game)
        
        // Then - should show empty betting state
        assertEquals(0, bettingTable.currentBet)
        assertEquals(500, bettingTable.availableBalance)
        assertTrue(bettingTable.availableChips.isNotEmpty())
        assertFalse(bettingTable.canDeal)
        assertEquals("Waiting for betting", bettingTable.dealerMessage)
    }
    
    @Test
    fun `given game with bet placed when creating betting table state then should show betting spot with chips`() {
        // Given - game with bet placed
        val player = Player(id = "player1", chips = 500)
        val game = Game.create(GameRules())
            .addPlayer(player)
            .placeBet(75) // 75 = 50 + 25 chips
        
        // When - creating betting table state
        val bettingTable = BettingTableState.fromGame(game)
        
        // Then - should show betting state with chips
        assertEquals(75, bettingTable.currentBet)
        assertEquals(425, bettingTable.availableBalance) // 500 - 75
        assertTrue(bettingTable.canDeal)
        assertEquals("Waiting for betting", bettingTable.dealerMessage)
        
        // Should be able to determine chip composition
        val expectedChips = listOf(
            ChipInSpot(ChipValue.FIFTY, 1),
            ChipInSpot(ChipValue.TWENTY_FIVE, 1)
        )
        assertEquals(expectedChips, bettingTable.chipComposition)
    }
    
    @Test
    fun `given game when adding chip to bet then should update betting state correctly`() {
        // Given - game with player  
        val player = Player(id = "player1", chips = 500)
        val game = Game.create(GameRules()).addPlayer(player)
        val bettingTable = BettingTableState.fromGame(game)
        
        // When - adding chip to bet
        val updatedTable = bettingTable.addChip(ChipValue.TWENTY_FIVE)
        
        // Then - should update bet and composition
        assertEquals(25, updatedTable.currentBet)
        assertEquals(1, updatedTable.chipComposition.size)
        assertEquals(ChipValue.TWENTY_FIVE, updatedTable.chipComposition[0].value)
        assertEquals(1, updatedTable.chipComposition[0].count)
        assertTrue(updatedTable.canDeal)
    }
    
    @Test
    fun `given game when adding same chip multiple times then should stack chips correctly`() {
        // Given - betting table  
        val player = Player(id = "player1", chips = 500)
        val game = Game.create(GameRules()).addPlayer(player)
        val bettingTable = BettingTableState.fromGame(game)
        
        // When - adding same chip value multiple times
        val updatedTable = bettingTable
            .addChip(ChipValue.TEN)
            .addChip(ChipValue.TEN)
            .addChip(ChipValue.TEN)
        
        // Then - should consolidate to optimal composition (25+5)
        assertEquals(30, updatedTable.currentBet)
        assertEquals(2, updatedTable.chipComposition.size) // Optimal: one 25 + one 5
        assertEquals(ChipValue.TWENTY_FIVE, updatedTable.chipComposition[0].value)
        assertEquals(ChipValue.FIVE, updatedTable.chipComposition[1].value)
    }
    
    @Test
    fun `given betting table with chips when clearing bet then should reset to empty state`() {
        // Given - betting table with bet placed
        val player = Player(id = "player1", chips = 500) 
        val game = Game.create(GameRules()).addPlayer(player).placeBet(50)
        val bettingTable = BettingTableState.fromGame(game)
        
        // When - clearing bet
        val clearedTable = bettingTable.clearBet()
        
        // Then - should reset to empty betting state
        assertEquals(0, clearedTable.currentBet)
        assertTrue(clearedTable.chipComposition.isEmpty())
        assertFalse(clearedTable.canDeal)
        assertEquals(500, clearedTable.availableBalance) // Balance restored
    }
    
    @Test  
    fun `given betting table when adding chip exceeds balance then should prevent overbetting`() {
        // Given - game with limited chips
        val player = Player(id = "player1", chips = 50) 
        val game = Game.create(GameRules()).addPlayer(player)
        val bettingTable = BettingTableState.fromGame(game)
        
        // When - trying to add chip that exceeds balance
        val result = bettingTable.tryAddChip(ChipValue.ONE_HUNDRED)
        
        // Then - should prevent overbetting
        assertFalse(result.success)
        assertEquals("Insufficient balance", result.errorMessage)
        assertEquals(0, result.bettingTable.currentBet) // No change
    }
    
    @Test
    fun `given available chip values when getting standard chips then should return casino chip denominations`() {
        // When - getting available chips
        val availableChips = ChipValue.standardCasinoChips()
        
        // Then - should return standard denominations
        val expected = listOf(
            ChipValue.FIVE, ChipValue.TEN, ChipValue.TWENTY_FIVE,
            ChipValue.FIFTY, ChipValue.ONE_HUNDRED, ChipValue.FIVE_HUNDRED
        )
        assertEquals(expected, availableChips)
    }
}