package org.ttpss930141011.bj.presentation.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.ttpss930141011.bj.domain.valueobjects.ScenarioErrorStat
import org.ttpss930141011.bj.domain.valueobjects.HandDecision
import org.ttpss930141011.bj.presentation.components.history.StatisticsSection
import org.ttpss930141011.bj.presentation.components.history.HandDecisionStatistics
import org.ttpss930141011.bj.presentation.layout.ScreenWidth

/**
 * StatisticsPage - Enhanced learning analytics based on HandDecision data
 * 
 * NEW PHILOSOPHY:
 * - Show actionable learning insights, not just error counts
 * - Focus on what users need to improve their strategy
 * - Provide positive reinforcement alongside areas for improvement
 * - Decision-level analysis for deeper insights
 * 
 * ENHANCED FEATURES:
 * - Performance overview with mastery levels
 * - Action-specific accuracy analysis
 * - Scenario mastery tracking
 * - Recent mistake patterns for focused practice
 */
@Composable
fun StatisticsPage(
    // Legacy aggregated scenario statistics (for backward compatibility)
    scenarioStats: List<ScenarioErrorStat>,
    
    // NEW: HandDecision data for comprehensive analysis
    decisionHistory: List<HandDecision> = emptyList(),
    
    // Layout
    screenWidth: ScreenWidth,
    modifier: Modifier = Modifier
) {
    // Use new HandDecision-based analysis if available, fallback to legacy
    if (decisionHistory.isNotEmpty()) {
        HandDecisionStatistics(
            decisions = decisionHistory,
            screenWidth = screenWidth,
            modifier = modifier.fillMaxSize()
        )
    } else {
        // Fallback to legacy view when no decision data is available
        StatisticsSection(
            scenarioStats = scenarioStats,
            screenWidth = screenWidth,
            modifier = modifier.fillMaxSize()
        )
    }
}

