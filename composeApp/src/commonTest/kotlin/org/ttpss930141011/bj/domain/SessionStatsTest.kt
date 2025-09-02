package org.ttpss930141011.bj.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SessionStatsTest {
    
    @Test
    fun `given new session stats when recording perfect round then should update correctly`() {
        // Given - 新的統計
        val stats = SessionStats()
        
        // When - 記錄完美round (所有決策正確)
        val decisions = listOf(
            PlayerDecision(Action.HIT, true),
            PlayerDecision(Action.STAND, true)
        )
        val newStats = stats.recordRound(decisions)
        
        // Then - 統計應該正確更新
        assertEquals(1, newStats.totalRounds)
        assertEquals(1, newStats.perfectRounds)
        assertEquals(2, newStats.totalDecisions)
        assertEquals(1.0, newStats.perfectRoundRate)
        assertEquals(1.0, newStats.overallDecisionRate)
    }
    
    @Test
    fun `given session stats when recording imperfect round then should track mixed results`() {
        // Given - 現有統計
        val stats = SessionStats(
            totalRounds = 1,
            perfectRounds = 1,
            totalDecisions = 2,
            correctDecisions = 2
        )
        
        // When - 記錄不完美round (部分錯誤)
        val decisions = listOf(
            PlayerDecision(Action.HIT, true),   // 正確
            PlayerDecision(Action.STAND, false) // 錯誤
        )
        val newStats = stats.recordRound(decisions)
        
        // Then - 統計應該反映混合結果
        assertEquals(2, newStats.totalRounds)
        assertEquals(1, newStats.perfectRounds) // 還是1個完美round
        assertEquals(4, newStats.totalDecisions)
        assertEquals(0.5, newStats.perfectRoundRate) // 50% perfect rounds
        assertEquals(0.75, newStats.overallDecisionRate) // 3/4 correct decisions
    }
    
    @Test
    fun `given empty session when calculating rates then should handle zero division`() {
        // Given - 空的session
        val stats = SessionStats()
        
        // When & Then - 應該安全處理除零
        assertEquals(0.0, stats.perfectRoundRate)
        assertEquals(0.0, stats.overallDecisionRate)
    }
    
    @Test
    fun `given session stats when recording multiple rounds then should maintain accuracy`() {
        // Given - 進行多個rounds
        var stats = SessionStats()
        
        // Round 1: 完美 (2/2 正確)
        stats = stats.recordRound(listOf(
            PlayerDecision(Action.HIT, true),
            PlayerDecision(Action.STAND, true)
        ))
        
        // Round 2: 不完美 (1/2 正確)
        stats = stats.recordRound(listOf(
            PlayerDecision(Action.HIT, false),
            PlayerDecision(Action.STAND, true)
        ))
        
        // Round 3: 完美 (1/1 正確)
        stats = stats.recordRound(listOf(
            PlayerDecision(Action.DOUBLE, true)
        ))
        
        // Then - 總計應該正確
        assertEquals(3, stats.totalRounds)
        assertEquals(2, stats.perfectRounds)
        assertEquals(5, stats.totalDecisions)
        assertEquals(2.0/3.0, stats.perfectRoundRate, 0.01) // 66.7% perfect rounds
        assertEquals(4.0/5.0, stats.overallDecisionRate, 0.01) // 80% correct decisions
    }
    
    @Test
    fun `given session stats when recording round with history then should preserve all information`() {
        // Given - 新的統計
        val stats = SessionStats()
        
        // When - 記錄round含歷史和結果
        val decisions = listOf(
            PlayerDecision(Action.HIT, true),
            PlayerDecision(Action.STAND, false)
        )
        val newStats = stats.recordRoundWithHistory(decisions, "WIN")
        
        // Then - 統計和歷史都應正確更新
        assertEquals(1, newStats.totalRounds)
        assertEquals(0, newStats.perfectRounds) // 不完美
        assertEquals(2, newStats.totalDecisions)
        assertEquals(1, newStats.correctDecisions)
        assertEquals(0.5, newStats.overallDecisionRate)
        
        // 歷史記錄應該保存
        assertEquals(1, newStats.roundHistory.size)
        val record = newStats.roundHistory[0]
        assertEquals(1, record.roundNumber)
        assertEquals(2, record.decisions.size)
        assertEquals("WIN", record.outcome)
        assertEquals(Action.HIT, record.decisions[0].action)
        assertEquals(true, record.decisions[0].isCorrect)
        assertEquals(Action.STAND, record.decisions[1].action)
        assertEquals(false, record.decisions[1].isCorrect)
    }
    
    @Test
    fun `given session stats when recording multiple rounds with history then should accumulate correctly`() {
        // Given - 空統計
        var stats = SessionStats()
        
        // When - 記錄多個rounds
        stats = stats.recordRoundWithHistory(
            listOf(PlayerDecision(Action.HIT, true), PlayerDecision(Action.STAND, true)),
            "WIN"
        )
        stats = stats.recordRoundWithHistory(
            listOf(PlayerDecision(Action.DOUBLE, false)),
            "BUSTED"
        )
        
        // Then - 統計和歷史都正確
        assertEquals(2, stats.totalRounds)
        assertEquals(1, stats.perfectRounds)
        assertEquals(3, stats.totalDecisions)
        assertEquals(2, stats.correctDecisions)
        
        // 歷史記錄完整
        assertEquals(2, stats.roundHistory.size)
        assertEquals("WIN", stats.roundHistory[0].outcome)
        assertEquals("BUSTED", stats.roundHistory[1].outcome)
        assertEquals(1, stats.roundHistory[0].roundNumber)
        assertEquals(2, stats.roundHistory[1].roundNumber)
    }
}