package org.ttpss930141011.bj.domain.valueobjects

/**
 * Statistics for a specific rule set within a session.
 */
data class RuleSegmentStats(
    val ruleVersion: String,
    val gameRules: GameRules,
    val decisions: List<DecisionRecord>,
    val totalDecisions: Int = decisions.size,
    val correctDecisions: Int = decisions.count { it.isCorrect }
) {
    val decisionRate: Double = if (totalDecisions > 0) {
        correctDecisions.toDouble() / totalDecisions
    } else 0.0
    
    val masteryLevel: MasteryLevel = when {
        decisionRate >= 0.9 && totalDecisions >= 10 -> MasteryLevel.EXPERT
        decisionRate >= 0.7 && totalDecisions >= 10 -> MasteryLevel.PROFICIENT
        decisionRate >= 0.5 && totalDecisions >= 10 -> MasteryLevel.LEARNING
        else -> MasteryLevel.BEGINNER
    }
}

/**
 * SessionStats - Rule-aware session-level learning statistics and progress tracking.
 * 
 * Enhanced with rule-aware analytics to prevent contamination when game rules
 * change mid-session. Maintains separate statistics for each rule set while
 * providing backward compatibility for existing UI components.
 * 
 * Focuses on current session data rather than cross-game analytics.
 */
data class SessionStats(
    val totalDecisions: Int = 0,
    val correctDecisions: Int = 0,
    val recentDecisions: List<DecisionRecord> = emptyList(),  // All recent session decisions
    val currentRuleVersion: String? = null,  // Active rule set identifier
    val ruleSegments: Map<String, RuleSegmentStats> = emptyMap()  // Per-rule statistics
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
     * Record a single decision (enhanced rule-aware method)
     */
    fun recordDecision(decision: DecisionRecord): SessionStats {
        val newRecentDecisions = (recentDecisions + decision).takeLast(50) // Keep recent 50
        val ruleHash = decision.ruleHash
        
        // Update rule segments
        val currentSegment = ruleSegments[ruleHash] ?: RuleSegmentStats(
            ruleVersion = ruleHash,
            gameRules = decision.gameRules,
            decisions = emptyList()
        )
        val updatedSegment = currentSegment.copy(
            decisions = (currentSegment.decisions + decision).takeLast(50)
        )
        val newRuleSegments = ruleSegments + (ruleHash to updatedSegment)
        
        return copy(
            totalDecisions = totalDecisions + 1,
            correctDecisions = if (decision.isCorrect) correctDecisions + 1 else correctDecisions,
            recentDecisions = newRecentDecisions,
            currentRuleVersion = ruleHash,
            ruleSegments = newRuleSegments
        )
    }
    
    /**
     * Backward compatibility - record multiple decisions as a "round"
     */
    fun recordRound(decisions: List<PlayerDecision>): SessionStats {
        return decisions.fold(this) { stats, playerDecision ->
            // Create a synthetic DecisionRecord for backward compatibility
            val decisionRecord = DecisionRecord(
                handCards = listOf(Card(Suit.HEARTS, Rank.TWO)), // Minimal data for compatibility
                dealerUpCard = Card(Suit.HEARTS, Rank.TWO), // Placeholder
                playerAction = playerDecision.action,
                isCorrect = playerDecision.isCorrect,
                gameRules = GameRules() // Default rules for backward compatibility
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
    
    // === Rule-Aware Analytics Methods ===
    
    /**
     * Get statistics for the current active rule set.
     */
    fun getCurrentRuleStats(): RuleSegmentStats? {
        return currentRuleVersion?.let { ruleSegments[it] }
    }
    
    /**
     * Get statistics for a specific rule set.
     */
    fun getStatsForRules(gameRules: GameRules): RuleSegmentStats? {
        val targetRuleVersion = gameRules.hashCode().toString(16).takeLast(8)
        return ruleSegments[targetRuleVersion]
    }
    
    /**
     * Get worst scenarios for current rule set only (prevents contamination).
     */
    fun getCurrentRuleWorstScenarios(minSamples: Int = 3): List<Pair<String, Double>> {
        return getCurrentRuleStats()?.decisions
            ?.filter { it.handCards.isNotEmpty() } // Filter out synthetic records
            ?.groupBy { it.baseScenarioKey } // Use base scenario (without rule hash)
            ?.filter { (_, decisionList) -> decisionList.size >= minSamples }
            ?.map { (scenario, decisionList) ->
                val errorRate = decisionList.count { !it.isCorrect }.toDouble() / decisionList.size
                scenario to errorRate
            }
            ?.sortedByDescending { it.second } ?: emptyList()
    }
    
    /**
     * Check if rule change notification should be shown.
     */
    fun hasRuleChanged(newRules: GameRules): Boolean {
        val newRuleVersion = newRules.hashCode().toString(16).takeLast(8)
        return currentRuleVersion != null && currentRuleVersion != newRuleVersion
    }
    
    /**
     * Get rule comparison summary for user awareness.
     */
    fun getRuleComparisonSummary(): String? {
        if (ruleSegments.size <= 1) return null
        
        val segments = ruleSegments.values.toList()
        val current = getCurrentRuleStats()
        val previous = segments.filter { it != current }.maxByOrNull { it.totalDecisions }
        
        return if (current != null && previous != null) {
            val currentRate = (current.decisionRate * 100).toInt()
            val previousRate = (previous.decisionRate * 100).toInt()
            val trend = if (currentRate > previousRate) "↗" else if (currentRate < previousRate) "↘" else "→"
            
            "Rule changed: ${previousRate}% → ${currentRate}% $trend (${current.totalDecisions} decisions)"
        } else null
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