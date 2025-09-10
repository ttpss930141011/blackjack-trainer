package org.ttpss930141011.bj.application

import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.*

/**
 * Application service for recording and managing learning decisions.
 * 
 * This service handles the creation of DecisionRecords and coordinates with the
 * LearningRepository for persistence. Follows DDD Application layer patterns.
 */
class LearningRecorder(
    private val repository: LearningRepository
) {
    
    /**
     * Records a player decision with full context for learning analysis.
     * 
     * @param handBeforeAction The player's hand state when the decision was made
     * @param dealerUpCard The dealer's visible card
     * @param playerAction The action the player chose
     * @param isCorrect Whether the player's action was optimal
     * @param gameRules The game rules in effect
     * @return The created DecisionRecord
     */
    fun recordDecision(
        handBeforeAction: PlayerHand,
        dealerUpCard: Card,
        playerAction: Action,
        isCorrect: Boolean,
        gameRules: GameRules
    ): DecisionRecord {
        val decisionRecord = DecisionRecord(
            beforeAction = HandSnapshot(
                cards = handBeforeAction.cards,
                dealerUpCard = dealerUpCard,
                gameRules = gameRules,
                handIndex = 0,
                isFromSplit = handBeforeAction.isFromSplit
            ),
            action = playerAction,
            afterAction = ActionResult.Stand(handBeforeAction.cards), // Placeholder
            isCorrect = isCorrect,
            timestamp = TimeProvider.currentTimeMillis()
        )
        
        repository.save(decisionRecord)
        return decisionRecord
    }
    
    /**
     * Records a decision using Game state directly.
     * 
     * @param game The current game state
     * @param playerAction The action taken by the player
     * @param isCorrect Whether the action was optimal
     * @param gameRules The game rules in effect
     * @return The created DecisionRecord
     * @throws IllegalArgumentException if game state is invalid
     */
    fun recordDecision(
        game: Game,
        playerAction: Action,
        isCorrect: Boolean,
        gameRules: GameRules
    ): DecisionRecord {
        require(game.currentHand != null) { "No current hand to record decision for" }
        require(game.dealer.upCard != null) { "Dealer up card not available" }
        
        return recordDecision(
            handBeforeAction = game.currentHand,
            dealerUpCard = game.dealer.upCard,
            playerAction = playerAction,
            isCorrect = isCorrect,
            gameRules = gameRules
        )
    }
    
    /**
     * Gets cross-game learning analytics.
     * 
     * @param minSamples Minimum number of attempts required for meaningful statistics
     * @return List of worst performing scenarios with error statistics
     */
    fun getWorstScenarios(minSamples: Int = 3): List<ScenarioErrorStat> {
        return repository.getErrorStatsAcrossRules(minSamples)
    }
    
    /**
     * Gets worst scenarios for a specific rule set.
     * Provides clean rule-specific analytics without cross-rule contamination.
     * 
     * @param gameRules The specific rule set to analyze (null returns empty list)
     * @param minSamples Minimum attempts required for meaningful statistics
     * @return List of worst performing scenarios for the specified rules
     */
    fun getWorstScenariosForRule(gameRules: GameRules?, minSamples: Int = 3): List<ScenarioErrorStat> {
        return gameRules?.let { rules ->
            val ruleHash = DomainConstants.generateRuleHash(rules)
            repository.getErrorStatsByRule(ruleHash, minSamples)
        } ?: emptyList()
    }
    
    /**
     * Gets recent decision records for session analysis.
     * 
     * @param limit Maximum number of recent decisions to retrieve
     * @return List of recent DecisionRecords
     */
    fun getRecentDecisions(limit: Int = 50): List<DecisionRecord> {
        return repository.getRecent(limit)
    }
    
    /**
     * Gets all recorded decisions for comprehensive analysis.
     * 
     * @return All stored DecisionRecords
     */
    fun getAllDecisions(): List<DecisionRecord> {
        return repository.getAll()
    }
    
    /**
     * Clears all learning data for testing or reset purposes.
     */
    fun clearAllData() {
        repository.clear()
    }
    
    /**
     * Gets detailed scenario statistics for analytics.
     * 
     * @return Map of scenario keys to ScenarioStats
     */
    fun getScenarioStats(): Map<String, org.ttpss930141011.bj.infrastructure.ScenarioStats> {
        val repositoryInstance = repository as? org.ttpss930141011.bj.infrastructure.InMemoryLearningRepository
        return repositoryInstance?.getScenarioStats() ?: emptyMap()
    }
}