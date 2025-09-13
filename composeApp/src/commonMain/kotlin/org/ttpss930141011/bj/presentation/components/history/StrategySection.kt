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

// Design constants - no magic numbers
private object StrategyDesign {
    val SECTION_SPACING = 20.dp
    val CARD_PADDING = 24.dp
    val RULE_CARD_ELEVATION = 6.dp
    val CHART_CARD_ELEVATION = 8.dp
    val LEGEND_CARD_ELEVATION = 6.dp
    val TIPS_CARD_ELEVATION = 4.dp
    val SELECTOR_CARD_ELEVATION = 4.dp
    
    val RULE_BUTTON_SPACING = 8.dp
    val RULE_BUTTON_PADDING = 12.dp
    val CHART_BUTTON_PADDING = 12.dp
    val CHART_BUTTON_SPACING = 8.dp
    val LEGEND_ITEM_SPACING = 2.dp
    val LEGEND_ICON_SIZE = 20.dp
    val LEGEND_TEXT_MARGIN = 8.dp
    val TIPS_ITEM_SPACING = 4.dp
    val TIPS_NUMBER_MARGIN = 8.dp
    
    val HEADER_CELL_MULTIPLIER = 1.2f
    val BORDER_WIDTH = 0.5.dp
    val FONT_SIZE_MULTIPLIER = 0.3f
    val RULE_DESCRIPTION_SIZE = 10.sp
    val LEGEND_FONT_SIZE = 12.sp
    
    val HEADER_ALPHA = 0.3f
    val BORDER_ALPHA = 0.2f
    val RULE_DESCRIPTION_ALPHA = 0.8f
}

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
        verticalArrangement = Arrangement.spacedBy(StrategyDesign.SECTION_SPACING)
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
        
        // Pro tips section
        ProTipsSection(screenWidth = screenWidth)
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
            containerColor = Color(0xFF0F172A).copy(alpha = 0.95f) // Dark slate
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = StrategyDesign.RULE_CARD_ELEVATION)
    ) {
        Column(
            modifier = Modifier.padding(Tokens.spacing(screenWidth))
        ) {
            Text(
                text = "⚙️ Dealer Rules",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = Tokens.spacing(screenWidth))
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(StrategyDesign.RULE_BUTTON_SPACING)
            ) {
                RuleTab(
                    text = "S17",
                    fullText = "Dealer Stands on Soft 17",
                    selected = dealerStandsOnSoft17,
                    onClick = { onRuleChange(true) },
                    modifier = Modifier.weight(1f)
                )
                
                RuleTab(
                    text = "H17",
                    fullText = "Dealer Hits on Soft 17",
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
    fullText: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                Color(0xFF10B981) // Emerald green for selected
            } else {
                Color(0xFF374151).copy(alpha = 0.8f) // Gray for unselected
            }
        ),
        border = if (!selected) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        } else null
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(StrategyDesign.RULE_BUTTON_PADDING)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = fullText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = StrategyDesign.RULE_DESCRIPTION_ALPHA),
                textAlign = TextAlign.Center,
                fontSize = StrategyDesign.RULE_DESCRIPTION_SIZE
            )
        }
    }
}

@Composable
fun ChartTypeSelector(
    selectedChartType: ChartType,
    onChartTypeChange: (ChartType) -> Unit,
    screenWidth: ScreenWidth
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937).copy(alpha = 0.95f) // Dark gray
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = StrategyDesign.SELECTOR_CARD_ELEVATION)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📊 Chart Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = StrategyDesign.CHART_BUTTON_SPACING + 4.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(StrategyDesign.RULE_BUTTON_SPACING)
            ) {
                ChartType.entries.forEach { chartType ->
                    val isSelected = selectedChartType == chartType
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onChartTypeChange(chartType) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                Color(0xFF3B82F6) // Blue for selected
                            } else {
                                Color(0xFF4B5563).copy(alpha = 0.7f) // Gray for unselected
                            }
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (isSelected) 6.dp else 2.dp
                        )
                    ) {
                        Text(
                            text = when (chartType) {
                                ChartType.HARD -> "💪 Hard"
                                ChartType.SOFT -> "🌊 Soft"  
                                ChartType.SPLITS -> "✂️ Pairs"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(StrategyDesign.RULE_BUTTON_PADDING)
                        )
                    }
                }
            }
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
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A).copy(alpha = 0.98f) // Very dark blue
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = StrategyDesign.CHART_CARD_ELEVATION)
    ) {
        Column(
            modifier = Modifier.padding(Tokens.spacing(screenWidth))
        ) {
            // Chart title
            Text(
                text = when (chart.chartType) {
                    ChartType.HARD -> "💪 Hard Totals Strategy"
                    ChartType.SOFT -> "🌊 Soft Totals Strategy"
                    ChartType.SPLITS -> "✂️ Pair Splitting Strategy"
                } + if (chart.dealerHitsOnSoft17) " (H17)" else " (S17)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
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
        Row(modifier = Modifier.fillMaxWidth()) {
            // Corner cell - equal width using weight
            ChartCellWithWeight("P\\D", null, cellSize, isHeader = true, weight = 1f)
            // Dealer cards - all equal width
            dealerCards.forEach { dealer ->
                ChartCellWithWeight(
                    if (dealer == 11) "A" else dealer.toString(),
                    null,
                    cellSize,
                    isHeader = true,
                    weight = 1f
                )
            }
        }
        
        // Data rows
        playerHands.forEach { player ->
            Row(modifier = Modifier.fillMaxWidth()) {
                // Player hand label - equal width
                ChartCellWithWeight(player.toString(), null, cellSize, isHeader = true, weight = 1f)
                // Strategy cells - all equal width
                dealerCards.forEach { dealer ->
                    val action = chart.getOptimalAction(player, dealer)
                    ChartCellWithWeight(
                        getActionSymbol(action),
                        action,
                        cellSize,
                        weight = 1f
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
        Row(modifier = Modifier.fillMaxWidth()) {
            ChartCellWithWeight("P\\D", null, cellSize, isHeader = true, weight = 1f)
            dealerCards.forEach { dealer ->
                ChartCellWithWeight(
                    if (dealer == 11) "A" else dealer.toString(),
                    null,
                    cellSize,
                    isHeader = true,
                    weight = 1f
                )
            }
        }
        
        // Data rows
        playerHands.forEach { player ->
            Row(modifier = Modifier.fillMaxWidth()) {
                ChartCellWithWeight("A,${player-11}", null, cellSize, isHeader = true, weight = 1f)
                dealerCards.forEach { dealer ->
                    val action = chart.getOptimalAction(player, dealer)
                    ChartCellWithWeight(
                        getActionSymbol(action),
                        action,
                        cellSize,
                        weight = 1f
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
        Row(modifier = Modifier.fillMaxWidth()) {
            ChartCellWithWeight("P\\D", null, cellSize, isHeader = true, weight = 1f)
            dealerCards.forEach { dealer ->
                ChartCellWithWeight(
                    if (dealer == 11) "A" else dealer.toString(),
                    null,
                    cellSize,
                    isHeader = true,
                    weight = 1f
                )
            }
        }
        
        // Data rows
        pairValues.forEach { pair ->
            Row(modifier = Modifier.fillMaxWidth()) {
                val pairLabel = if (pair == 11) "A,A" else "$pair,$pair"
                ChartCellWithWeight(pairLabel, null, cellSize, isHeader = true, weight = 1f)
                dealerCards.forEach { dealer ->
                    val action = chart.getOptimalAction(pair, dealer)
                    ChartCellWithWeight(
                        getActionSymbol(action),
                        action,
                        cellSize,
                        weight = 1f
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
        Color(0xFF374151).copy(alpha = 0.9f) // Dark gray for headers
    } else {
        getActionColor(action)
    }
    
    val textColor = Color.White
    
    Box(
        modifier = Modifier
            .size(width = size, height = size)
            .border(
                StrategyDesign.BORDER_WIDTH, 
                if (isHeader) Color.White.copy(alpha = StrategyDesign.HEADER_ALPHA) 
                else Color.White.copy(alpha = StrategyDesign.BORDER_ALPHA)
            )
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = (size.value * StrategyDesign.FONT_SIZE_MULTIPLIER).sp,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RowScope.ChartCellWithWeight(
    text: String,
    action: Action?,
    size: Dp,
    isHeader: Boolean = false,
    weight: Float = 1f
) {
    val backgroundColor = if (isHeader) {
        Color(0xFF374151).copy(alpha = 0.9f) // Dark gray for headers
    } else {
        getActionColor(action)
    }
    
    val textColor = Color.White
    
    Box(
        modifier = Modifier
            .weight(weight)
            .height(size)
            .border(
                StrategyDesign.BORDER_WIDTH, 
                if (isHeader) Color.White.copy(alpha = StrategyDesign.HEADER_ALPHA) 
                else Color.White.copy(alpha = StrategyDesign.BORDER_ALPHA)
            )
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = (size.value * StrategyDesign.FONT_SIZE_MULTIPLIER).sp,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun getActionColor(action: Action?): Color {
    return when (action) {
        Action.HIT -> Color(0xFFDC2626)        // Bright red
        Action.STAND -> Color(0xFFF59E0B)      // Amber yellow
        Action.DOUBLE -> Color(0xFF2563EB)     // Royal blue
        Action.SPLIT -> Color(0xFF16A34A)      // Emerald green
        Action.SURRENDER -> Color(0xFF9333EA)  // Purple
        null -> Color(0xFF6B7280)              // Neutral gray
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
fun ProTipsSection(screenWidth: ScreenWidth) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF059669).copy(alpha = 0.15f) // Light emerald background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = StrategyDesign.TIPS_CARD_ELEVATION)
    ) {
        Column(
            modifier = Modifier.padding(StrategyDesign.CARD_PADDING - 4.dp)
        ) {
            Text(
                text = "💡 Pro Tips",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF059669)
            )
            
            Spacer(modifier = Modifier.height(StrategyDesign.CHART_BUTTON_SPACING + 4.dp))
            
            val tips = listOf(
                "Always split Aces and 8s, never split 5s or 10s",
                "Double down on 11 against any dealer card except Ace",
                "Never take insurance - the house edge is too high",
                "Surrender hard 16 against dealer 9, 10, or Ace (if allowed)",
                "Learn basic strategy first, then consider card counting"
            )
            
            tips.forEachIndexed { index, tip ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(vertical = StrategyDesign.TIPS_ITEM_SPACING)
                ) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669),
                        modifier = Modifier.padding(end = StrategyDesign.TIPS_NUMBER_MARGIN)
                    )
                    
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun StrategyLegend(screenWidth: ScreenWidth) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937).copy(alpha = 0.95f) // Dark background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = StrategyDesign.LEGEND_CARD_ELEVATION)
    ) {
        Column(
            modifier = Modifier.padding(Tokens.spacing(screenWidth))
        ) {
            Text(
                text = "🎯 Action Legend",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = Tokens.spacing(screenWidth))
            )
            
            val legends = listOf(
                "H" to "Hit - Take another card" to Color(0xFFDC2626),
                "S" to "Stand - Keep your current total" to Color(0xFFF59E0B),
                "D" to "Double - Double bet, take one card only" to Color(0xFF2563EB),
                "P" to "Split - Split pair into two hands" to Color(0xFF16A34A),
                "R" to "Surrender - Give up half your bet" to Color(0xFF9333EA)
            )
            
            legends.forEach { (symbolText, color) ->
                val (symbol, text) = symbolText
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = StrategyDesign.LEGEND_ITEM_SPACING)
                ) {
                    Box(
                        modifier = Modifier
                            .size(StrategyDesign.LEGEND_ICON_SIZE)
                            .background(color, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = symbol,
                            color = Color.White,
                            fontSize = StrategyDesign.LEGEND_FONT_SIZE,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(StrategyDesign.LEGEND_TEXT_MARGIN))
                    
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}