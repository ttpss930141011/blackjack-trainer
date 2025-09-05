package org.ttpss930141011.bj.domain.valueobjects

// Rich SessionStats - Round基礎統計與歷史記錄
data class SessionStats(
    val totalRounds: Int = 0,
    val perfectRounds: Int = 0,  // 所有決策都正確的round
    val totalDecisions: Int = 0,
    val correctDecisions: Int = 0,  // 所有正確決策總數
    val roundHistory: List<RoundRecord> = emptyList()  // 每輪完整記錄
) {
    
    // 完美round比率 (策略掌握度指標)
    val perfectRoundRate: Double = if (totalRounds > 0) {
        perfectRounds.toDouble() / totalRounds
    } else 0.0
    
    // 整體決策正確率 (細節學習指標)
    val overallDecisionRate: Double = if (totalDecisions > 0) {
        correctDecisions.toDouble() / totalDecisions
    } else 0.0
    
    // Rich domain behavior - 記錄完整round結果
    fun recordRound(decisions: List<PlayerDecision>): SessionStats {
        val allCorrect = decisions.all { it.isCorrect }
        val decisionCount = decisions.size
        val correctCount = decisions.count { it.isCorrect }
        
        return copy(
            totalRounds = totalRounds + 1,
            perfectRounds = if (allCorrect) perfectRounds + 1 else perfectRounds,
            totalDecisions = totalDecisions + decisionCount,
            correctDecisions = correctDecisions + correctCount
        )
    }
    
    // 記錄完整round含結果
    fun recordRoundWithHistory(decisions: List<PlayerDecision>, outcome: String): SessionStats {
        val allCorrect = decisions.all { it.isCorrect }
        val decisionCount = decisions.size
        val correctCount = decisions.count { it.isCorrect }
        
        val roundRecord = RoundRecord(
            roundNumber = totalRounds + 1,
            decisions = decisions,
            outcome = outcome
        )
        
        return copy(
            totalRounds = totalRounds + 1,
            perfectRounds = if (allCorrect) perfectRounds + 1 else perfectRounds,
            totalDecisions = totalDecisions + decisionCount,
            correctDecisions = correctDecisions + correctCount,
            roundHistory = roundHistory + roundRecord
        )
    }
    
    // 學習進度查詢
    val hasSignificantData: Boolean = totalRounds >= 10
    val masteryLevel: MasteryLevel = when {
        perfectRoundRate >= 0.9 && hasSignificantData -> MasteryLevel.EXPERT
        perfectRoundRate >= 0.7 && hasSignificantData -> MasteryLevel.PROFICIENT
        perfectRoundRate >= 0.5 && hasSignificantData -> MasteryLevel.LEARNING
        else -> MasteryLevel.BEGINNER
    }
}

// Rich domain value objects
data class RoundRecord(
    val roundNumber: Int,
    val decisions: List<PlayerDecision>,
    val outcome: String  // "WIN", "LOSE", "PUSH", "BUSTED"
)

enum class MasteryLevel {
    BEGINNER,   // < 50% 或數據不足
    LEARNING,   // 50-70% 完美rounds
    PROFICIENT, // 70-90% 完美rounds
    EXPERT      // >90% 完美rounds
}