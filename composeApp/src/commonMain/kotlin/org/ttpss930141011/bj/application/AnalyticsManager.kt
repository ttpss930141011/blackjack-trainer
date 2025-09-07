package org.ttpss930141011.bj.application

import androidx.compose.runtime.*
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.infrastructure.ScenarioStats

/**
 * AnalyticsManager - Focuses ONLY on learning analytics and statistics
 * 
 * Linus: "Handle stats and learning data. No UI shit, no game state shit."
 */
internal class AnalyticsManager(
    private val learningRecorder: LearningRecorder
) {
    private var _sessionStats by mutableStateOf(SessionStats())
    val sessionStats: SessionStats get() = _sessionStats
    
    fun recordPlayerAction(
        handBeforeAction: PlayerHand,
        dealerUpCard: Card,
        playerAction: Action,
        isCorrect: Boolean,
        gameRules: GameRules
    ) {
        learningRecorder.recordDecision(
            handBeforeAction = handBeforeAction,
            dealerUpCard = dealerUpCard,
            playerAction = playerAction,
            isCorrect = isCorrect,
            gameRules = gameRules
        )
    }
    
    fun recordRound(roundDecisions: List<PlayerDecision>) {
        _sessionStats = _sessionStats.recordRound(roundDecisions)
    }
    
    fun resetSession() {
        _sessionStats = SessionStats()
    }
    
    // Learning analytics access
    fun getWorstScenarios(minSamples: Int = 3): List<ScenarioErrorStat> {
        return learningRecorder.getWorstScenarios(minSamples)
    }
    
    fun getRecentDecisions(limit: Int = 50): List<DecisionRecord> {
        return learningRecorder.getRecentDecisions(limit)
    }
    
    fun clearAllLearningData() {
        learningRecorder.clearAllData()
    }
    
    fun getRecentDecisionsForCurrentRule(currentRules: GameRules?, limit: Int = 50): List<DecisionRecord> {
        val rules = currentRules ?: return emptyList()
        return learningRecorder.getRecentDecisions(limit * 2) // Get more to filter
            .filter { it.gameRules == rules }
            .take(limit)
    }
    
    fun getScenarioStats(): Map<String, ScenarioStats> {
        return learningRecorder.getScenarioStats()
    }
    
    fun getCurrentRuleWorstScenarios(currentRules: GameRules?, minSamples: Int = 3): List<ScenarioErrorStat> {
        return learningRecorder.getWorstScenariosForRule(currentRules, minSamples)
    }
    
    fun getCurrentRuleStats(): RuleSegmentStats? {
        return _sessionStats.getCurrentRuleStats()
    }
}