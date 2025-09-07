package org.ttpss930141011.bj.application

import androidx.compose.runtime.*
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.infrastructure.ScenarioStats

/**
 * Application service focused exclusively on learning analytics and statistics.
 * Manages session statistics and provides access to learning data analysis.
 */
internal class AnalyticsManager(
    private val learningRecorder: LearningRecorder
) {
    private var _sessionStats by mutableStateOf(SessionStats())
    val sessionStats: SessionStats get() = _sessionStats
    
    /**
     * Records a player action for learning analysis.
     * 
     * @param handBeforeAction The player's hand state when decision was made
     * @param dealerUpCard The dealer's visible card
     * @param playerAction The action the player took
     * @param isCorrect Whether the action was optimal
     * @param gameRules The game rules in effect
     */
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
    
    /**
     * Records the results of a completed round for session statistics.
     * 
     * @param roundDecisions List of player decisions made during the round
     */
    fun recordRound(roundDecisions: List<PlayerDecision>) {
        _sessionStats = _sessionStats.recordRound(roundDecisions)
    }
    
    /**
     * Resets session statistics to start fresh tracking.
     */
    fun resetSession() {
        _sessionStats = SessionStats()
    }
    
    /**
     * Gets the worst performing scenarios across all rules.
     * 
     * @param minSamples Minimum number of attempts required for meaningful statistics
     * @return List of worst performing scenarios with error statistics
     */
    fun getWorstScenarios(minSamples: Int = 3): List<ScenarioErrorStat> {
        return learningRecorder.getWorstScenarios(minSamples)
    }
    
    /**
     * Gets recent decision records for analysis.
     * 
     * @param limit Maximum number of recent decisions to retrieve
     * @return List of recent DecisionRecords
     */
    fun getRecentDecisions(limit: Int = 50): List<DecisionRecord> {
        return learningRecorder.getRecentDecisions(limit)
    }
    
    /**
     * Clears all learning data for reset purposes.
     */
    fun clearAllLearningData() {
        learningRecorder.clearAllData()
    }
    
    /**
     * Gets recent decisions filtered by specific game rules.
     * 
     * @param currentRules The game rules to filter by (null returns empty list)
     * @param limit Maximum number of filtered decisions to return
     * @return List of recent DecisionRecords matching the specified rules
     */
    fun getRecentDecisionsForCurrentRule(currentRules: GameRules?, limit: Int = 50): List<DecisionRecord> {
        val rules = currentRules ?: return emptyList()
        return learningRecorder.getRecentDecisions(limit * 2)
            .filter { it.gameRules == rules }
            .take(limit)
    }
    
    /**
     * Gets detailed scenario statistics for analytics.
     * 
     * @return Map of scenario keys to ScenarioStats
     */
    fun getScenarioStats(): Map<String, ScenarioStats> {
        return learningRecorder.getScenarioStats()
    }
    
    /**
     * Gets worst performing scenarios for specific game rules.
     * 
     * @param currentRules The game rules to analyze (null returns empty list)
     * @param minSamples Minimum attempts required for meaningful statistics
     * @return List of worst performing scenarios for the specified rules
     */
    fun getCurrentRuleWorstScenarios(currentRules: GameRules?, minSamples: Int = 3): List<ScenarioErrorStat> {
        return learningRecorder.getWorstScenariosForRule(currentRules, minSamples)
    }
    
    /**
     * Gets statistics for the current rule segment in the session.
     * 
     * @return Current rule segment statistics, or null if no current segment
     */
    fun getCurrentRuleStats(): RuleSegmentStats? {
        return _sessionStats.getCurrentRuleStats()
    }
}