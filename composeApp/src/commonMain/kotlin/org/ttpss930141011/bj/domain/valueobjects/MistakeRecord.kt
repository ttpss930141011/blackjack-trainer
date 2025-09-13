package org.ttpss930141011.bj.domain.valueobjects

/**
 * MistakeRecord - Contains both error count and actual card data for display
 * 
 * Linus: "Don't parse strings to rebuild data you already had. Keep the original."
 */
data class MistakeRecord(
    val handCards: List<Card>,
    val dealerUpCard: Card,
    val errorCount: Int,
    val baseScenarioKey: String
) {
    init {
        require(handCards.isNotEmpty()) { "Hand cards cannot be empty" }
        require(errorCount > 0) { "Error count must be positive for mistake records" }
    }
}