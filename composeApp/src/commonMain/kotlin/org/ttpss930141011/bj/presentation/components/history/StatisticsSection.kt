package org.ttpss930141011.bj.presentation.components.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.valueobjects.ScenarioErrorStat
import org.ttpss930141011.bj.domain.valueobjects.DomainConstants
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.presentation.layout.ScreenWidth

/**
 * Statistics Section - Shows error count and hand illustration
 * Linus: "No sorting bullshit, no percentages, just what the user needs to know"
 */
@Composable
fun StatisticsSection(
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