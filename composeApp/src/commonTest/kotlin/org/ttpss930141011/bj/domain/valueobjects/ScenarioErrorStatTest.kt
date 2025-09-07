package org.ttpss930141011.bj.domain.valueobjects

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class ScenarioErrorStatTest {

    @Test
    fun `given valid scenario data when creating stat then should calculate properties correctly`() {
        // Given & When
        val stat = ScenarioErrorStat(
            baseScenarioKey = "H16 vs 10",
            totalAttempts = 10,
            errorCount = 3
        )
        
        // Then
        assertEquals("H16 vs 10", stat.baseScenarioKey)
        assertEquals(10, stat.totalAttempts)
        assertEquals(3, stat.errorCount)
        assertEquals(0.3, stat.errorRate, 0.001)
        assertEquals(0.7, stat.accuracyRate, 0.001)
        assertEquals(7, stat.correctCount)
    }

    @Test
    fun `given perfect performance when creating stat then error rate should be zero`() {
        // Given & When
        val stat = ScenarioErrorStat(
            baseScenarioKey = "S17 vs 6",
            totalAttempts = 5,
            errorCount = 0
        )
        
        // Then
        assertEquals(0.0, stat.errorRate)
        assertEquals(1.0, stat.accuracyRate)
        assertEquals(5, stat.correctCount)
        assertFalse(stat.needsPractice)
    }

    @Test
    fun `given all errors when creating stat then error rate should be one`() {
        // Given & When
        val stat = ScenarioErrorStat(
            baseScenarioKey = "H12 vs A",
            totalAttempts = 4,
            errorCount = 4
        )
        
        // Then
        assertEquals(1.0, stat.errorRate)
        assertEquals(0.0, stat.accuracyRate)
        assertEquals(0, stat.correctCount)
        assertTrue(stat.needsPractice)
    }

    @Test
    fun `given zero attempts when creating stat then error rate should be zero`() {
        // Given & When
        val stat = ScenarioErrorStat(
            baseScenarioKey = "Pair 8s vs 9",
            totalAttempts = 0,
            errorCount = 0
        )
        
        // Then
        assertEquals(0.0, stat.errorRate)
        assertEquals(1.0, stat.accuracyRate)
        assertEquals(0, stat.correctCount)
        assertFalse(stat.needsPractice) // No practice needed with no data
    }

    @Test
    fun `given high error rate with sufficient samples when creating stat then should need practice`() {
        // Given & When
        val stat = ScenarioErrorStat(
            baseScenarioKey = "H15 vs 10",
            totalAttempts = 10,
            errorCount = 4 // 40% error rate
        )
        
        // Then
        assertTrue(stat.needsPractice)
    }

    @Test
    fun `given high error rate with insufficient samples when creating stat then should not need practice`() {
        // Given & When
        val stat = ScenarioErrorStat(
            baseScenarioKey = "H14 vs A",
            totalAttempts = 2, // Below MIN_SCENARIO_SAMPLES threshold
            errorCount = 2 // 100% error rate
        )
        
        // Then
        assertFalse(stat.needsPractice) // Need more data before recommending practice
    }

    @Test
    fun `given low error rate when creating stat then should not need practice`() {
        // Given & When
        val stat = ScenarioErrorStat(
            baseScenarioKey = "BJ vs A",
            totalAttempts = 10,
            errorCount = 2 // 20% error rate
        )
        
        // Then
        assertFalse(stat.needsPractice)
    }

    @Test
    fun `given different sample sizes when checking confidence then should return appropriate levels`() {
        assertEquals("No Data", ScenarioErrorStat("test", 0, 0).confidenceLevel)
        assertEquals("Very Low", ScenarioErrorStat("test", 1, 0).confidenceLevel)
        assertEquals("Very Low", ScenarioErrorStat("test", 2, 0).confidenceLevel)
        assertEquals("Low", ScenarioErrorStat("test", 3, 0).confidenceLevel)
        assertEquals("Low", ScenarioErrorStat("test", 9, 0).confidenceLevel)
        assertEquals("Medium", ScenarioErrorStat("test", 10, 0).confidenceLevel)
        assertEquals("Medium", ScenarioErrorStat("test", 29, 0).confidenceLevel)
        assertEquals("High", ScenarioErrorStat("test", 30, 0).confidenceLevel)
        assertEquals("High", ScenarioErrorStat("test", 99, 0).confidenceLevel)
        assertEquals("Very High", ScenarioErrorStat("test", 100, 0).confidenceLevel)
    }

    @Test
    fun `given sufficient data when checking has sufficient data then should return true`() {
        // Given
        val stat = ScenarioErrorStat("H16 vs 10", 5, 2)
        
        // When & Then
        assertTrue(stat.hasSufficientData)
    }

    @Test
    fun `given insufficient data when checking has sufficient data then should return false`() {
        // Given
        val stat = ScenarioErrorStat("H16 vs 10", 2, 1)
        
        // When & Then
        assertFalse(stat.hasSufficientData)
    }

    @Test
    fun `given stats with different error rates when comparing then should identify worse performance`() {
        // Given
        val betterStat = ScenarioErrorStat("S17 vs 6", 10, 2) // 20% error
        val worseStat = ScenarioErrorStat("H16 vs 10", 10, 5) // 50% error
        
        // When & Then
        assertTrue(worseStat.hasWorseErrorRateThan(betterStat))
        assertFalse(betterStat.hasWorseErrorRateThan(worseStat))
    }

    @Test
    fun `given blank scenario key when creating stat then should throw exception`() {
        assertFailsWith<IllegalArgumentException> {
            ScenarioErrorStat("", 5, 2)
        }
    }

    @Test
    fun `given negative total attempts when creating stat then should throw exception`() {
        assertFailsWith<IllegalArgumentException> {
            ScenarioErrorStat("H16 vs 10", -1, 0)
        }
    }

    @Test
    fun `given negative error count when creating stat then should throw exception`() {
        assertFailsWith<IllegalArgumentException> {
            ScenarioErrorStat("H16 vs 10", 5, -1)
        }
    }

    @Test
    fun `given error count exceeding total attempts when creating stat then should throw exception`() {
        assertFailsWith<IllegalArgumentException> {
            ScenarioErrorStat("H16 vs 10", 5, 6)
        }
    }
}