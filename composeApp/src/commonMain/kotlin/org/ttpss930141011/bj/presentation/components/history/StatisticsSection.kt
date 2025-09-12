package org.ttpss930141011.bj.presentation.components.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.presentation.layout.ScreenWidth

/**
 * Enhanced Statistics Section - HandDecision analysis with beautiful color scheme
 * 
 * Beautiful, harmonious color palette:
 * - Soft gradients and subtle backgrounds
 * - Clear visual hierarchy with pleasing colors
 * - Consistent color meanings across all sections
 * - Professional casino theme with modern aesthetics
 */
@Composable
fun StatisticsSection(
    scenarioStats: List<ScenarioErrorStat>,
    screenWidth: ScreenWidth,
    modifier: Modifier = Modifier
) {
    // Fallback to legacy view when no HandDecision data available
    LegacyStatisticsView(
        scenarioStats = scenarioStats,
        screenWidth = screenWidth,
        modifier = modifier
    )
}

/**
 * Beautiful HandDecision statistics with carefully chosen colors
 */
@Composable
fun HandDecisionStatistics(
    decisions: List<HandDecision>,
    screenWidth: ScreenWidth,
    modifier: Modifier = Modifier
) {
    if (decisions.isEmpty()) {
        EmptyStatisticsState(screenWidth = screenWidth)
        return
    }

    val recentDecisions = decisions.takeLast(50) // Focus on recent performance
    
    LazyColumn(
        modifier = modifier.padding(Tokens.spacing(screenWidth)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Performance Overview
        item {
            PerformanceOverview(
                decisions = recentDecisions,
                screenWidth = screenWidth
            )
        }
        
        // Action Analysis
        item {
            ActionAnalysis(
                decisions = recentDecisions,
                screenWidth = screenWidth
            )
        }
        
        // Scenario Mastery
        item {
            ScenarioMastery(
                decisions = recentDecisions,
                screenWidth = screenWidth
            )
        }
        
        // Improvement Opportunities
        item {
            ImprovementOpportunities(
                decisions = recentDecisions,
                screenWidth = screenWidth
            )
        }
    }
}

@Composable
fun PerformanceOverview(
    decisions: List<HandDecision>,
    screenWidth: ScreenWidth
) {
    val correctDecisions = decisions.count { it.isCorrect }
    val accuracy = if (decisions.isNotEmpty()) {
        (correctDecisions.toDouble() / decisions.size * 100).toInt()
    } else 0
    
    val masteryLevel = when {
        accuracy >= 90 && decisions.size >= 20 -> "Expert" to Color(0xFF4F46E5)      // 深紫色 - 专业感
        accuracy >= 75 && decisions.size >= 15 -> "Proficient" to Color(0xFF059669)  // 绿色 - 熟练
        accuracy >= 60 && decisions.size >= 10 -> "Learning" to Color(0xFF0891B2)    // 蓝绿色 - 学习中
        else -> "Beginner" to Color(0xFF6B7280)                                      // 中性灰 - 初学者
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B).copy(alpha = 0.6f)  // 优雅的深色背景
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Your Performance",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Accuracy
                Column {
                    Text(
                        text = "$accuracy%",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            accuracy >= 85 -> Color(0xFF10B981)  // 鲜绿色 - 优秀
                            accuracy >= 70 -> Color(0xFF06B6D4)  // 青色 - 良好
                            accuracy >= 55 -> Color(0xFFF59E0B)  // 金色 - 一般
                            else -> Color(0xFFEF4444)            // 柔和红色 - 需要提升
                        }
                    )
                    Text(
                        text = "Accuracy",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFD1D5DB)
                    )
                }
                
                // Level Badge
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = masteryLevel.second.copy(alpha = 0.15f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, masteryLevel.second.copy(alpha = 0.3f))
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = masteryLevel.first,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = masteryLevel.second
                        )
                        Text(
                            text = "${decisions.size} decisions",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionAnalysis(
    decisions: List<HandDecision>,
    screenWidth: ScreenWidth
) {
    val actionStats = decisions.groupBy { it.action }.mapValues { (_, decisionList) ->
        val correct = decisionList.count { it.isCorrect }
        val total = decisionList.size
        val accuracy = if (total > 0) (correct.toDouble() / total * 100).toInt() else 0
        Triple(correct, total, accuracy)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A).copy(alpha = 0.7f)  // 深雅致背景
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Action Accuracy",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "How well you perform each decision type",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFD1D5DB),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Action.entries.forEach { action ->
                val stats = actionStats[action]
                if (stats != null && stats.second > 0) {
                    ActionStatRow(
                        action = action,
                        correct = stats.first,
                        total = stats.second,
                        accuracy = stats.third
                    )
                }
            }
        }
    }
}

@Composable
fun ActionStatRow(
    action: Action,
    correct: Int,
    total: Int,
    accuracy: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            ActionChip(action = action)
            Text(
                text = action.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "$correct/$total",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF9CA3AF)
            )
            
            // Accuracy badge with beautiful colors
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        accuracy >= 85 -> Color(0xFF065F46)  // 深绿背景
                        accuracy >= 70 -> Color(0xFF0C4A6E)  // 深蓝背景
                        accuracy >= 55 -> Color(0xFF92400E)  // 深橙背景
                        else -> Color(0xFF7F1D1D)            // 深红背景
                    }
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "$accuracy%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        accuracy >= 85 -> Color(0xFF10B981)  // 亮绿色
                        accuracy >= 70 -> Color(0xFF06B6D4)  // 亮青色
                        accuracy >= 55 -> Color(0xFFF59E0B)  // 亮橙色
                        else -> Color(0xFFEF4444)            // 亮红色
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun ActionChip(action: Action) {
    val (backgroundColor, symbol) = when (action) {
        Action.HIT -> Color(0xFF059669) to "+"
        Action.STAND -> Color(0xFFDC2626) to "−"
        Action.DOUBLE -> Color(0xFFF59E0B) to "×2"
        Action.SPLIT -> Color(0xFF7C3AED) to "⁝⁝"
        Action.SURRENDER -> Color(0xFF6B7280) to "↓"
    }
    
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.size(32.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = symbol,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ScenarioMastery(
    decisions: List<HandDecision>,
    screenWidth: ScreenWidth
) {
    val scenarioStats = decisions
        .groupBy { it.baseScenarioKey }
        .mapValues { (_, decisionList) ->
            val correct = decisionList.count { it.isCorrect }
            val total = decisionList.size
            val accuracy = if (total > 0) (correct.toDouble() / total * 100).toInt() else 0
            Triple(correct, total, accuracy)
        }
        .filter { it.value.second >= 3 } // Only show scenarios with 3+ decisions
        .toList()
        .sortedBy { it.second.third } // Sort by accuracy (worst first)
        .take(5) // Show top 5 scenarios needing work
    
    if (scenarioStats.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1F2937).copy(alpha = 0.8f)  // 温暖的深色背景
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Focus Areas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Scenarios where you can improve most",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD1D5DB),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                scenarioStats.forEach { (scenario, stats) ->
                    ScenarioStatRow(
                        scenario = scenario,
                        correct = stats.first,
                        total = stats.second,
                        accuracy = stats.third
                    )
                }
            }
        }
    }
}

@Composable
fun ScenarioStatRow(
    scenario: String,
    correct: Int,
    total: Int,
    accuracy: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF374151).copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = scenario,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "$correct/$total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF9CA3AF)
                )
                
                // Priority indicator
                val priorityColor = when {
                    accuracy < 50 -> Color(0xFFDC2626)  // 高优先级 - 红色
                    accuracy < 70 -> Color(0xFFF59E0B)  // 中优先级 - 橙色
                    else -> Color(0xFF059669)           // 低优先级 - 绿色
                }
                
                Surface(
                    color = priorityColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, priorityColor)
                ) {
                    Text(
                        text = "$accuracy%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = priorityColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ImprovementOpportunities(
    decisions: List<HandDecision>,
    screenWidth: ScreenWidth
) {
    val recentErrors = decisions
        .takeLast(20)
        .filter { !it.isCorrect }
        .groupBy { "${it.baseScenarioKey} (${it.action.name})" }
        .mapValues { it.value.size }
        .toList()
        .sortedByDescending { it.second }
        .take(3)
    
    if (recentErrors.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF7C2D12).copy(alpha = 0.4f)  // 温暖的橙红色背景
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🎯",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Recent Mistakes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "Practice these patterns to improve quickly",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFFED7AA),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                recentErrors.forEach { (pattern, count) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFEA580C).copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = pattern,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            
                            Surface(
                                color = Color(0xFFDC2626),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "$count recent",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStatisticsState(screenWidth: ScreenWidth) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E40AF).copy(alpha = 0.3f)  // 优雅的蓝色背景
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📊",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Start Your Journey",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Make some decisions to see your learning analytics",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFBFDBFE),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// Legacy view - keep for backward compatibility
@Composable
fun LegacyStatisticsView(
    scenarioStats: List<ScenarioErrorStat>,
    screenWidth: ScreenWidth,
    modifier: Modifier = Modifier
) {
    val mistakeStats = scenarioStats.filter { it.errorCount > 0 }
    
    LazyColumn(
        modifier = modifier.padding(Tokens.spacing(screenWidth)),
        verticalArrangement = Arrangement.spacedBy(Tokens.spacing(screenWidth))
    ) {
        item {
            Text(
                text = "Mistakes by Hand",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        if (mistakeStats.isEmpty()) {
            item {
                EmptyMistakesState(screenWidth = screenWidth)
            }
        } else {
            items(mistakeStats) { stat ->
                MistakeCard(
                    stat = stat,
                    screenWidth = screenWidth
                )
            }
        }
    }
}

@Composable
fun MistakeCard(
    stat: ScenarioErrorStat,
    screenWidth: ScreenWidth
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Tokens.spacing(screenWidth)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hand illustration
            HandIllustration(
                scenarioKey = stat.baseScenarioKey,
                modifier = Modifier.weight(2f)
            )
            
            // Error count
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stat.errorCount.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "mistakes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun HandIllustration(
    scenarioKey: String,
    modifier: Modifier = Modifier
) {
    val (playerHandDisplay, dealerCardDisplay) = DomainConstants.parseScenarioKey(scenarioKey)
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Player hand display
        Column {
            Text(
                text = "Your hand:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = playerHandDisplay,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        Text(
            text = "vs",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Dealer card display
        Column {
            Text(
                text = "Dealer:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = dealerCardDisplay,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EmptyMistakesState(screenWidth: ScreenWidth) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Tokens.padding(screenWidth)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎉",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No mistakes yet!",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Keep playing to see which hands give you trouble",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}