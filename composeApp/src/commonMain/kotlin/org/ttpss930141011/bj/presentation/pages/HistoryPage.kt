package org.ttpss930141011.bj.presentation.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.valueobjects.DecisionRecord
import org.ttpss930141011.bj.presentation.components.history.HistorySection
import org.ttpss930141011.bj.presentation.layout.ScreenWidth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryPage(
    decisionHistory: List<DecisionRecord>,
    onClearHistory: () -> Unit,
    screenWidth: ScreenWidth,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Decision History") }
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
                HistorySection(
                    decisionHistory = decisionHistory,
                    onClearHistory = onClearHistory,
                    screenWidth = screenWidth
                )
            }
        }
    }
}