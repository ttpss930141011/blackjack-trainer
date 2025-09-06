package org.ttpss930141011.bj.presentation.components.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import org.ttpss930141011.bj.infrastructure.ScenarioStats
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.presentation.layout.ScreenWidth

enum class StatsSortBy {
    ERROR_RATE, TOTAL_ATTEMPTS, SCENARIO_NAME
}

@Composable
fun StatisticsSection(
    scenarioStats: Map<String, ScenarioStats>,
    screenWidth: ScreenWidth,
    modifier: Modifier = Modifier
) {
    var sortBy by remember { mutableStateOf(StatsSortBy.ERROR_RATE) }
    
    val sortedStats = remember(scenarioStats, sortBy) {
        when (sortBy) {
            StatsSortBy.ERROR_RATE -> scenarioStats.values.sortedByDescending { it.errorRate }
            StatsSortBy.TOTAL_ATTEMPTS -> scenarioStats.values.sortedByDescending { it.totalAttempts }
            StatsSortBy.SCENARIO_NAME -> scenarioStats.values.sortedBy { it.scenario }
        }
    }
    
    Column(
        modifier = modifier.padding(Tokens.spacing(screenWidth)),
        verticalArrangement = Arrangement.spacedBy(Tokens.spacing(screenWidth))
    ) {
        // Overall summary
        StatisticsSummary(
            scenarioStats = scenarioStats,
            screenWidth = screenWidth
        )
        
        // Sort controls
        SortControls(
            sortBy = sortBy,
            onSortChange = { sortBy = it },
            screenWidth = screenWidth
        )
        
        if (sortedStats.isEmpty()) {
            EmptyStatsState(screenWidth = screenWidth)
        } else {
            // Table header
            StatsTableHeader(screenWidth = screenWidth)
            
            sortedStats.forEach { stat ->
                StatsTableRow(
                    stat = stat,
                    screenWidth = screenWidth
                )
            }
        }
    }
}

@Composable
fun StatisticsSummary(
    scenarioStats: Map<String, ScenarioStats>,
    screenWidth: ScreenWidth
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(Tokens.spacing(screenWidth))
        ) {
            Text(
                text = "Learning Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = Tokens.spacing(screenWidth))
            )
            
            if (scenarioStats.isNotEmpty()) {
                val totalDecisions = scenarioStats.values.sumOf { it.totalAttempts }
                val totalCorrect = scenarioStats.values.sumOf { it.correctAttempts }
                val overallAccuracy = if (totalDecisions > 0) {
                    (totalCorrect * 100) / totalDecisions
                } else 0
                
                val needsPracticeCount = scenarioStats.values.count { it.needsPractice }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SummaryStat(
                        label = "Total Decisions",
                        value = totalDecisions.toString(),
                        screenWidth = screenWidth
                    )
                    SummaryStat(
                        label = "Overall Accuracy",
                        value = "$overallAccuracy%",
                        screenWidth = screenWidth
                    )
                    SummaryStat(
                        label = "Scenarios",
                        value = scenarioStats.size.toString(),
                        screenWidth = screenWidth
                    )
                    SummaryStat(
                        label = "Need Practice",
                        value = needsPracticeCount.toString(),
                        screenWidth = screenWidth
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryStat(
    label: String,
    value: String,
    screenWidth: ScreenWidth
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SortControls(
    sortBy: StatsSortBy,
    onSortChange: (StatsSortBy) -> Unit,
    screenWidth: ScreenWidth
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(Tokens.spacing(screenWidth))
        ) {
            Text(
                text = "Sort By",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = Tokens.spacing(screenWidth))
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Tokens.spacing(screenWidth))
            ) {
                SortChip(
                    text = "Error Rate",
                    selected = sortBy == StatsSortBy.ERROR_RATE,
                    onClick = { onSortChange(StatsSortBy.ERROR_RATE) },
                    modifier = Modifier.weight(1f)
                )
                
                SortChip(
                    text = "Attempts",
                    selected = sortBy == StatsSortBy.TOTAL_ATTEMPTS,
                    onClick = { onSortChange(StatsSortBy.TOTAL_ATTEMPTS) },
                    modifier = Modifier.weight(1f)
                )
                
                SortChip(
                    text = "Name",
                    selected = sortBy == StatsSortBy.SCENARIO_NAME,
                    onClick = { onSortChange(StatsSortBy.SCENARIO_NAME) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SortChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) },
        modifier = modifier
    )
}

@Composable
fun EmptyStatsState(screenWidth: ScreenWidth) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Tokens.padding(screenWidth)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No statistics available",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Tokens.spacing(screenWidth)))
            Text(
                text = "Make more decisions to see detailed analytics",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatsTableHeader(screenWidth: ScreenWidth) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(
            topStart = Tokens.cornerRadius(screenWidth),
            topEnd = Tokens.cornerRadius(screenWidth),
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Tokens.spacing(screenWidth)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Scenario",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(3f)
            )
            
            Text(
                text = "Attempts",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = "Wrong",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = "Error%",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatsTableRow(
    stat: ScenarioStats,
    screenWidth: ScreenWidth
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (stat.needsPractice) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Tokens.spacing(screenWidth)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Scenario name with warning indicator
            Row(
                modifier = Modifier.weight(3f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stat.scenario,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (stat.needsPractice) {
                    Text(
                        text = "⚠️",
                        fontSize = 14.sp
                    )
                }
            }
            
            // Attempts
            Text(
                text = stat.totalAttempts.toString(),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            
            // Wrong count
            val wrongCount = stat.totalAttempts - stat.correctAttempts
            Text(
                text = wrongCount.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = if (wrongCount > 0) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            
            // Error percentage
            val errorPercentage = (stat.errorRate * 100).toInt()
            Text(
                text = "$errorPercentage%",
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    errorPercentage >= 50 -> MaterialTheme.colorScheme.error
                    errorPercentage >= 30 -> Color(0xFFFF9800) // Orange
                    else -> MaterialTheme.colorScheme.onSurface
                },
                fontWeight = if (stat.needsPractice) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}