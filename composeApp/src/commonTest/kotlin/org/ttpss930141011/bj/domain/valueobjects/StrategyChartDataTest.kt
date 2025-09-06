package org.ttpss930141011.bj.domain.valueobjects

import org.ttpss930141011.bj.domain.enums.Action
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StrategyChartDataTest {

    @Test
    fun `given strategy chart when getting optimal action then should return correct action`() {
        // Given
        val strategies = mapOf(
            Pair(16, 10) to Action.HIT,
            Pair(20, 10) to Action.STAND
        )
        val chart = StrategyChart(ChartType.HARD, false, strategies)
        
        // When & Then
        assertEquals(Action.HIT, chart.getOptimalAction(16, 10))
        assertEquals(Action.STAND, chart.getOptimalAction(20, 10))
    }

    @Test
    fun `given strategy chart set when getting chart by type then should return correct chart`() {
        // Given
        val chartSet = StrategyChartFactory.createDealerStandsOnSoft17()
        
        // When & Then
        assertEquals(ChartType.HARD, chartSet.getChart(ChartType.HARD).chartType)
        assertEquals(ChartType.SOFT, chartSet.getChart(ChartType.SOFT).chartType)
        assertEquals(ChartType.SPLITS, chartSet.getChart(ChartType.SPLITS).chartType)
    }

    @Test
    fun `given dealer stands on soft 17 when creating chart set then should have correct rule`() {
        // Given & When
        val chartSet = StrategyChartFactory.createDealerStandsOnSoft17()
        
        // Then
        assertEquals(false, chartSet.dealerHitsOnSoft17)
        assertEquals(false, chartSet.hardTotals.dealerHitsOnSoft17)
        assertEquals(false, chartSet.softTotals.dealerHitsOnSoft17)
        assertEquals(false, chartSet.splits.dealerHitsOnSoft17)
    }

    @Test
    fun `given dealer hits on soft 17 when creating chart set then should have correct rule`() {
        // Given & When
        val chartSet = StrategyChartFactory.createDealerHitsOnSoft17()
        
        // Then
        assertEquals(true, chartSet.dealerHitsOnSoft17)
        assertEquals(true, chartSet.hardTotals.dealerHitsOnSoft17)
        assertEquals(true, chartSet.softTotals.dealerHitsOnSoft17)
        assertEquals(true, chartSet.splits.dealerHitsOnSoft17)
    }

    @Test
    fun `given hard totals chart when checking basic strategy rules then should be correct`() {
        // Given
        val chart = StrategyChartFactory.createDealerStandsOnSoft17().hardTotals
        
        // When & Then - Basic hard total rules
        assertEquals(Action.HIT, chart.getOptimalAction(8, 10)) // Always hit 8
        assertEquals(Action.DOUBLE, chart.getOptimalAction(11, 6)) // Double 11 vs 6
        assertEquals(Action.STAND, chart.getOptimalAction(17, 10)) // Always stand 17+
        assertEquals(Action.SURRENDER, chart.getOptimalAction(16, 10)) // Surrender 16 vs 10
        assertEquals(Action.STAND, chart.getOptimalAction(13, 6)) // Stand 13 vs 6
    }

    @Test
    fun `given soft totals chart when checking basic strategy rules then should be correct`() {
        // Given
        val chart = StrategyChartFactory.createDealerStandsOnSoft17().softTotals
        
        // When & Then - Basic soft total rules
        assertEquals(Action.HIT, chart.getOptimalAction(17, 2)) // Soft 17 vs 2 (stand on soft 17)
        assertEquals(Action.DOUBLE, chart.getOptimalAction(17, 6)) // Double soft 17 vs 6
        assertEquals(Action.STAND, chart.getOptimalAction(18, 8)) // Stand soft 18 vs 8
        assertEquals(Action.HIT, chart.getOptimalAction(18, 10)) // Hit soft 18 vs 10
        assertEquals(Action.STAND, chart.getOptimalAction(19, 10)) // Always stand soft 19+
    }

    @Test
    fun `given splits chart when checking basic strategy rules then should be correct`() {
        // Given
        val chart = StrategyChartFactory.createDealerStandsOnSoft17().splits
        
        // When & Then - Basic splitting rules
        assertEquals(Action.SPLIT, chart.getOptimalAction(11, 10)) // Always split A,A (11 = A,A)
        assertEquals(Action.SPLIT, chart.getOptimalAction(8, 10)) // Always split 8,8
        assertEquals(Action.STAND, chart.getOptimalAction(10, 10)) // Never split 10,10
        assertEquals(Action.HIT, chart.getOptimalAction(4, 10)) // Never split 4,4
        assertEquals(Action.SPLIT, chart.getOptimalAction(9, 6)) // Split 9,9 vs 6
        assertEquals(Action.STAND, chart.getOptimalAction(9, 7)) // Don't split 9,9 vs 7
    }

    @Test
    fun `given dealer hits soft 17 when comparing to stands soft 17 then should have differences`() {
        // Given
        val standsChart = StrategyChartFactory.createDealerStandsOnSoft17()
        val hitsChart = StrategyChartFactory.createDealerHitsOnSoft17()
        
        // When & Then - Key differences when dealer hits soft 17
        
        // Hard 11 vs A: Hit when dealer stands, Double when dealer hits
        assertEquals(Action.HIT, standsChart.hardTotals.getOptimalAction(11, 11))
        assertEquals(Action.DOUBLE, hitsChart.hardTotals.getOptimalAction(11, 11))
        
        // Soft 17 vs 2: Hit when dealer stands, Double when dealer hits
        assertEquals(Action.HIT, standsChart.softTotals.getOptimalAction(17, 2))
        assertEquals(Action.DOUBLE, hitsChart.softTotals.getOptimalAction(17, 2))
        
        // Soft 18 vs A: Stand when dealer stands, Hit when dealer hits
        assertEquals(Action.STAND, standsChart.softTotals.getOptimalAction(18, 11))
        assertEquals(Action.HIT, hitsChart.softTotals.getOptimalAction(18, 11))
    }

    @Test
    fun `given strategy charts when checking completeness then should cover all standard scenarios`() {
        // Given
        val chartSet = StrategyChartFactory.createDealerStandsOnSoft17()
        
        // When & Then - Verify charts have expected coverage
        
        // Hard totals: 4-21 vs 2-11 (dealer cards)
        val hardChart = chartSet.hardTotals
        for (player in 4..21) {
            for (dealer in 2..11) {
                assertNotNull(
                    hardChart.getOptimalAction(player, dealer),
                    "Missing hard total strategy for $player vs $dealer"
                )
            }
        }
        
        // Soft totals: 13-21 vs 2-11
        val softChart = chartSet.softTotals
        for (player in 13..21) {
            for (dealer in 2..11) {
                assertNotNull(
                    softChart.getOptimalAction(player, dealer),
                    "Missing soft total strategy for soft $player vs $dealer"
                )
            }
        }
        
        // Splits: All pair values vs 2-11
        val splitsChart = chartSet.splits
        val pairValues = listOf(2, 3, 4, 6, 7, 8, 9, 10, 11) // Standard pairs that can be split
        for (pair in pairValues) {
            for (dealer in 2..11) {
                assertNotNull(
                    splitsChart.getOptimalAction(pair, dealer),
                    "Missing split strategy for $pair,$pair vs $dealer"
                )
            }
        }
    }

    @Test
    fun `given strategy chart when checking data integrity then should be consistent`() {
        // Given
        val chartSet = StrategyChartFactory.createDealerStandsOnSoft17()
        
        // When & Then - Verify logical consistency
        
        // Hard 21 should always stand
        for (dealer in 2..11) {
            assertEquals(Action.STAND, chartSet.hardTotals.getOptimalAction(21, dealer))
        }
        
        // Soft 21 should always stand
        for (dealer in 2..11) {
            assertEquals(Action.STAND, chartSet.softTotals.getOptimalAction(21, dealer))
        }
        
        // A,A should always split
        for (dealer in 2..11) {
            assertEquals(Action.SPLIT, chartSet.splits.getOptimalAction(11, dealer))
        }
        
        // 8,8 should always split
        for (dealer in 2..11) {
            assertEquals(Action.SPLIT, chartSet.splits.getOptimalAction(8, dealer))
        }
    }
}