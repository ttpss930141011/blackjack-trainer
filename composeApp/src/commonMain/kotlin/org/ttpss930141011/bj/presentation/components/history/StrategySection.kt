package org.ttpss930141011.bj.presentation.components.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.valueobjects.GameRules
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.presentation.layout.ScreenWidth

@Composable
fun StrategySection(
    gameRules: GameRules,
    screenWidth: ScreenWidth,
    modifier: Modifier = Modifier
) {
    var selectedRule by remember { mutableStateOf(!gameRules.dealerHitsOnSoft17) }
    var selectedChartType by remember { mutableStateOf(ChartType.HARD) }
    
    val chartSet = if (selectedRule) {
        StrategyChartFactory.createDealerStandsOnSoft17()
    } else {
        StrategyChartFactory.createDealerHitsOnSoft17()
    }
    
    Column(
        modifier = modifier.padding(Tokens.spacing(screenWidth)),
        verticalArrangement = Arrangement.spacedBy(Tokens.spacing(screenWidth))
    ) {
        // Rule selector tabs
        RuleSelector(
            dealerStandsOnSoft17 = selectedRule,
            onRuleChange = { selectedRule = it },
            screenWidth = screenWidth
        )
        
        // Chart type selector
        ChartTypeSelector(
            selectedChartType = selectedChartType,
            onChartTypeChange = { selectedChartType = it },
            screenWidth = screenWidth
        )
        // Strategy chart display
        StrategyChart(
            chart = chartSet.getChart(selectedChartType),
            screenWidth = screenWidth
        )
        
        // Legend
        StrategyLegend(screenWidth = screenWidth)
    }
}

@Composable
fun RuleSelector(
    dealerStandsOnSoft17: Boolean,
    onRuleChange: (Boolean) -> Unit,
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
                text = "Dealer Rules",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = Tokens.spacing(screenWidth))
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Tokens.spacing(screenWidth))
            ) {
                RuleTab(
                    text = "Dealer Stands on Soft 17",
                    selected = dealerStandsOnSoft17,
                    onClick = { onRuleChange(true) },
                    modifier = Modifier.weight(1f)
                )
                
                RuleTab(
                    text = "Dealer Hits on Soft 17",
                    selected = !dealerStandsOnSoft17,
                    onClick = { onRuleChange(false) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun RuleTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (!selected) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        } else null
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )
    }
}

@Composable
fun ChartTypeSelector(
    selectedChartType: ChartType,
    onChartTypeChange: (ChartType) -> Unit,
    screenWidth: ScreenWidth
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Tokens.spacing(screenWidth))
    ) {
        ChartType.entries.forEach { chartType ->
            FilterChip(
                selected = selectedChartType == chartType,
                onClick = { onChartTypeChange(chartType) },
                label = {
                    Text(
                        text = when (chartType) {
                            ChartType.HARD -> "Hard Totals"
                            ChartType.SOFT -> "Soft Totals"  
                            ChartType.SPLITS -> "Pairs"
                        }
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StrategyChart(
    chart: StrategyChart,
    screenWidth: ScreenWidth
) {
    val cellSize = when (screenWidth) {
        ScreenWidth.COMPACT -> 28.dp
        ScreenWidth.MEDIUM -> 32.dp
        ScreenWidth.EXPANDED -> 36.dp
    }
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Tokens.spacing(screenWidth))
        ) {
            // Chart title
            Text(
                text = when (chart.chartType) {
                    ChartType.HARD -> "Hard Totals"
                    ChartType.SOFT -> "Soft Totals"
                    ChartType.SPLITS -> "Pair Splitting"
                } + if (chart.dealerHitsOnSoft17) " (Dealer Hits Soft 17)" else " (Dealer Stands Soft 17)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = Tokens.spacing(screenWidth))
            )
            
            // Create the chart grid
            when (chart.chartType) {
                ChartType.HARD -> HardTotalsChart(chart, cellSize)
                ChartType.SOFT -> SoftTotalsChart(chart, cellSize) 
                ChartType.SPLITS -> SplitsChart(chart, cellSize)
            }
        }
    }
}

@Composable
fun HardTotalsChart(chart: StrategyChart, cellSize: Dp) {
    val playerHands = (4..17).toList() + listOf(18, 19, 20, 21)
    val dealerCards = (2..10).toList() + listOf(11) // 11 = Ace
    
    Column {
        // Header row
        Row {
            // Corner cell
            ChartCell("P\\D", null, cellSize, isHeader = true)
            // Dealer cards
            dealerCards.forEach { dealer ->
                ChartCell(
                    if (dealer == 11) "A" else dealer.toString(),
                    null,
                    cellSize,
                    isHeader = true
                )
            }
        }
        
        // Data rows
        playerHands.forEach { player ->
            Row {
                // Player hand label
                ChartCell(player.toString(), null, cellSize, isHeader = true)
                // Strategy cells
                dealerCards.forEach { dealer ->
                    val action = chart.getOptimalAction(player, dealer)
                    ChartCell(
                        getActionSymbol(action),
                        action,
                        cellSize
                    )
                }
            }
        }
    }
}

@Composable
fun SoftTotalsChart(chart: StrategyChart, cellSize: Dp) {
    val playerHands = (13..19).toList() + listOf(20, 21)
    val dealerCards = (2..10).toList() + listOf(11) // 11 = Ace
    
    Column {
        // Header row
        Row {
            ChartCell("P\\D", null, cellSize, isHeader = true)
            dealerCards.forEach { dealer ->
                ChartCell(
                    if (dealer == 11) "A" else dealer.toString(),
                    null,
                    cellSize,
                    isHeader = true
                )
            }
        }
        
        // Data rows
        playerHands.forEach { player ->
            Row {
                ChartCell("A,${player-11}", null, cellSize, isHeader = true)
                dealerCards.forEach { dealer ->
                    val action = chart.getOptimalAction(player, dealer)
                    ChartCell(
                        getActionSymbol(action),
                        action,
                        cellSize
                    )
                }
            }
        }
    }
}

@Composable
fun SplitsChart(chart: StrategyChart, cellSize: Dp) {
    val pairValues = listOf(2, 3, 4, 6, 7, 8, 9, 10, 11) // 11 = A,A
    val dealerCards = (2..10).toList() + listOf(11) // 11 = Ace
    
    Column {
        // Header row
        Row {
            ChartCell("P\\D", null, cellSize, isHeader = true)
            dealerCards.forEach { dealer ->
                ChartCell(
                    if (dealer == 11) "A" else dealer.toString(),
                    null,
                    cellSize,
                    isHeader = true
                )
            }
        }
        
        // Data rows
        pairValues.forEach { pair ->
            Row {
                val pairLabel = if (pair == 11) "A,A" else "$pair,$pair"
                ChartCell(pairLabel, null, cellSize, isHeader = true)
                dealerCards.forEach { dealer ->
                    val action = chart.getOptimalAction(pair, dealer)
                    ChartCell(
                        getActionSymbol(action),
                        action,
                        cellSize
                    )
                }
            }
        }
    }
}

@Composable
fun ChartCell(
    text: String,
    action: Action?,
    size: Dp,
    isHeader: Boolean = false
) {
    val backgroundColor = if (isHeader) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        getActionColor(action)
    }
    
    val textColor = if (isHeader) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        Color.White
    }
    
    Box(
        modifier = Modifier
            .size(size)
            .border(0.5.dp, MaterialTheme.colorScheme.outline)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = (size.value * 0.3).sp,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun getActionColor(action: Action?): Color {
    return when (action) {
        Action.HIT -> Color(0xFFE53935)        // Red
        Action.STAND -> Color(0xFFFFD600)      // Yellow  
        Action.DOUBLE -> Color(0xFF1976D2)     // Blue
        Action.SPLIT -> Color(0xFF388E3C)      // Green
        Action.SURRENDER -> Color(0xFF7B1FA2)  // Purple
        null -> Color.Gray
    }
}

fun getActionSymbol(action: Action?): String {
    return when (action) {
        Action.HIT -> "H"
        Action.STAND -> "S"
        Action.DOUBLE -> "D"
        Action.SPLIT -> "P"
        Action.SURRENDER -> "R"
        null -> "?"
    }
}

@Composable
fun StrategyLegend(screenWidth: ScreenWidth) {
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
                text = "Legend",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = Tokens.spacing(screenWidth))
            )
            
            val legends = listOf(
                "H" to "Hit" to Color(0xFFE53935),
                "S" to "Stand" to Color(0xFFFFD600),
                "D" to "Double if allowed, otherwise hit" to Color(0xFF1976D2),
                "P" to "Split" to Color(0xFF388E3C),
                "R" to "Surrender if allowed, otherwise hit" to Color(0xFF7B1FA2)
            )
            
            legends.forEach { (symbolText, color) ->
                val (symbol, text) = symbolText
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(color, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = symbol,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}