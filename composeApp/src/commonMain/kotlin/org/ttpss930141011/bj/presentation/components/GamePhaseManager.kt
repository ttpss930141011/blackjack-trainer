package org.ttpss930141011.bj.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.domain.*

/**
 * Simple phase manager that delegates to unified GameTable component.
 * No more separate betting/game views - single consistent interface.
 */
@Composable
fun GamePhaseManager(
    game: Game,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Single unified table for all phases
        GameTable(
            game = game,
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )
        
        // Game over check
        if (game.phase == GamePhase.WAITING_FOR_BETS && (game.player?.chips ?: 0) <= 0) {
            GameOverDisplay(totalChips = game.player?.chips ?: 0)
        }
    }
}