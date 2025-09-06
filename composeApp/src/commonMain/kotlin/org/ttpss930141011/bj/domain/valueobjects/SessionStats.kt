package org.ttpss930141011.bj.domain.valueobjects

/**
 * SessionStats - Session-level learning statistics and progress tracking.
 * 
 * Updated to work with DecisionRecord-based learning system while maintaining
 * backward compatibility for UI components. Focuses on current session data
 * rather than cross-game analytics.
 */
data class SessionStats(
    val totalDecisions: Int = 0,
    val correctDecisions: Int = 0,
    val recentDecisions: List<DecisionRecord> = emptyList()  // Recent session decisions
) {
    
    // Core learning metrics
    val overallDecisionRate: Double = if (totalDecisions > 0) {
        correctDecisions.toDouble() / totalDecisions
    } else 0.0
    
    // Backward compatibility - calculate round-based stats from decisions
    val totalRounds: Int = recentDecisions.size.coerceAtMost(50) // Approximate recent rounds
    
    val perfectRounds: Int = 0 // Simplified - could be enhanced if needed
    
    val perfectRoundRate: Double = if (totalRounds > 0) {
        perfectRounds.toDouble() / totalRounds  
    } else 0.0
    
    // Session mastery assessment
    val hasSignificantData: Boolean = totalDecisions >= 10
    val masteryLevel: MasteryLevel = when {
        overallDecisionRate >= 0.9 && hasSignificantData -> MasteryLevel.EXPERT
        overallDecisionRate >= 0.7 && hasSignificantData -> MasteryLevel.PROFICIENT
        overallDecisionRate >= 0.5 && hasSignificantData -> MasteryLevel.LEARNING
        else -> MasteryLevel.BEGINNER
    }
    
    // Backward compatibility - provide roundHistory for UI
    val roundHistory: List<RoundRecord> = recentDecisions.mapIndexed { index, decision ->
        RoundRecord(
            roundNumber = index + 1,
            decisions = listOf(PlayerDecision(decision.playerAction, decision.isCorrect)),
            outcome = if (decision.isCorrect) "CORRECT" else "INCORRECT"
        )
    }
    
    /**
     * Record a single decision (new primary method)
     */
    fun recordDecision(decision: DecisionRecord): SessionStats {
        val newRecentDecisions = (recentDecisions + decision).takeLast(50) // Keep recent 50
        
        return copy(
            totalDecisions = totalDecisions + 1,
            correctDecisions = if (decision.isCorrect) correctDecisions + 1 else correctDecisions,
            recentDecisions = newRecentDecisions
        )
    }
    
    /**
     * Backward compatibility - record multiple decisions as a "round"
     */
    fun recordRound(decisions: List<PlayerDecision>): SessionStats {
        return decisions.fold(this) { stats, playerDecision ->
            // Create a synthetic DecisionRecord for backward compatibility
            val decisionRecord = DecisionRecord(
                handCards = emptyList(), // Minimal data for compatibility
                dealerUpCard = Card(Suit.HEARTS, Rank.TWO), // Placeholder
                playerAction = playerDecision.action,
                isCorrect = playerDecision.isCorrect
            )
            stats.recordDecision(decisionRecord)
        }
    }
    
    /**
     * Backward compatibility - record round with history
     */
    fun recordRoundWithHistory(decisions: List<PlayerDecision>, outcome: String): SessionStats {
        return recordRound(decisions)
    }
    
    /**
     * Get session-specific analytics
     */
    fun getSessionWorstScenarios(minSamples: Int = 3): List<Pair<String, Double>> {
        return recentDecisions
            .filter { it.handCards.isNotEmpty() } // Filter out synthetic records
            .groupBy { it.scenarioKey }
            .filter { (_, decisionList) -> decisionList.size >= minSamples }
            .map { (scenario, decisionList) ->
                val errorRate = decisionList.count { !it.isCorrect }.toDouble() / decisionList.size
                scenario to errorRate
            }
            .sortedByDescending { it.second }
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