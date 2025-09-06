package org.ttpss930141011.bj.presentation.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.infrastructure.ScenarioStats
import org.ttpss930141011.bj.presentation.components.history.StatisticsSection
import org.ttpss930141011.bj.presentation.layout.ScreenWidth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsPage(
    scenarioStats: Map<String, ScenarioStats>,
    screenWidth: ScreenWidth,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                StatisticsSection(
                    scenarioStats = scenarioStats,
                    screenWidth = screenWidth
                )
            }
        }
    }
}