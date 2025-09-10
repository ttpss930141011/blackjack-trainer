package org.ttpss930141011.bj.presentation.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.ttpss930141011.bj.domain.valueobjects.ScenarioErrorStat
import org.ttpss930141011.bj.presentation.components.history.StatisticsSection
import org.ttpss930141011.bj.presentation.layout.ScreenWidth

/**
 * StatisticsPage - CLEANUP: Focused on scenario statistics only
 * 
 * SIMPLIFIED ARCHITECTURE:
 * - Single purpose: Scenario-based performance analysis
 * - Data focus: ScenarioErrorStat aggregated statistics
 * - Decision details available in HistoryPage (complete context)
 * 
 * REMOVED REDUNDANCY:
 * - Removed Decision Analytics tab (duplicated HistoryPage functionality)
 * - Simplified interface with single-purpose design
 * - Clear separation: Statistics = aggregated, History = detailed
 */
@Composable
fun StatisticsPage(
    // Aggregated scenario statistics
    scenarioStats: List<ScenarioErrorStat>,
    
    // Layout
    screenWidth: ScreenWidth,
    modifier: Modifier = Modifier
) {
    // Direct display - StatisticsSection handles its own scrolling
    StatisticsSection(
        scenarioStats = scenarioStats,
        screenWidth = screenWidth,
        modifier = modifier.fillMaxSize()
    )
}

/**
 * CLEANUP SUMMARY:
 * 
 * REMOVED:
 * - Decision Analytics tab (redundant with HistoryPage)
 * - Tab navigation interface
 * - decisionHistory parameter
 * - onClearDecisionHistory callback
 * 
 * SIMPLIFIED TO:
 * - Single-purpose scenario statistics display
 * - Clear separation: Statistics = aggregated analysis, History = detailed records
 * - Better user experience with focused functionality
 * 
 * RATIONALE:
 * - HistoryPage already provides complete decision context and details
 * - Eliminates duplicate functionality and user confusion
 * - Follows "good taste" principle of eliminating special cases
 */