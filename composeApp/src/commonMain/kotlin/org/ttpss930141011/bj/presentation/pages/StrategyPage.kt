package org.ttpss930141011.bj.presentation.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.presentation.components.history.StrategySection
import org.ttpss930141011.bj.presentation.layout.ScreenWidth

@Composable
fun StrategyPage(
    gameRules: GameRules,
    screenWidth: ScreenWidth,
    modifier: Modifier = Modifier
) {
    // Use consistent layout without TopAppBar - header is handled by main screen
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            StrategySection(
                gameRules = gameRules,
                screenWidth = screenWidth
            )
        }
    }
}