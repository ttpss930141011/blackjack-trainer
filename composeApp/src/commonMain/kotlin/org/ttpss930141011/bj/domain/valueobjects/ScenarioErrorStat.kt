package org.ttpss930141011.bj.domain.valueobjects

/**
 * ScenarioErrorStat - Pure domain value object for scenario error statistics
 * 
 * Represents error rate analysis for a specific blackjack scenario.
 * Replaces the complex ScenarioStats infrastructure object with a focused domain concept.
 * 
 * Domain-driven design principles:
 * - Immutable value object
 * - Self-contained business logic
 * - No external dependencies
 * - Clear domain vocabulary
 */
data class ScenarioErrorStat(
    val baseScenarioKey: String,
    val totalAttempts: Int,
    val errorCount: Int
) {
    
    init {
        require(baseScenarioKey.isNotBlank()) { "Scenario key cannot be blank" }
        require(totalAttempts >= 0) { "Total attempts cannot be negative" }
        require(errorCount >= 0) { "Error count cannot be negative" }
        require(errorCount <= totalAttempts) { "Error count cannot exceed total attempts" }
    }
    
    /**
     * Error rate as a proportion (0.0 to 1.0)
     */
    val errorRate: Double = if (totalAttempts > 0) {
        errorCount.toDouble() / totalAttempts
    } else 0.0
    
    /**
     * Accuracy rate as a proportion (0.0 to 1.0)
     */
    val accuracyRate: Double = 1.0 - errorRate
    
    /**
     * Number of correct attempts
     */
    val correctCount: Int = totalAttempts - errorCount
    
    /**
     * Whether this scenario needs practice based on domain rules.
     * Uses minimum samples threshold and error rate threshold.
     */
    val needsPractice: Boolean = 
        totalAttempts >= DomainConstants.Constraints.MIN_SCENARIO_SAMPLES && errorRate > 0.3
    
    /**
     * Confidence level based on sample size.
     * More samples = higher confidence in the error rate.
     */
    val confidenceLevel: String = when (totalAttempts) {
        0 -> "No Data"
        in 1..2 -> "Very Low"
        in 3..9 -> "Low"
        in 10..29 -> "Medium" 
        in 30..99 -> "High"
        else -> "Very High"
    }
    
    /**
     * Compare error rates with another scenario stat
     */
    fun hasWorseErrorRateThan(other: ScenarioErrorStat): Boolean {
        return this.errorRate > other.errorRate
    }
    
    /**
     * Check if this scenario has sufficient data for analysis
     */
    val hasSufficientData: Boolean = 
        totalAttempts >= DomainConstants.Constraints.MIN_SCENARIO_SAMPLES
}