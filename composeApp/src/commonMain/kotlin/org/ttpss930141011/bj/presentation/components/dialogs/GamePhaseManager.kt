package org.ttpss930141011.bj.presentation.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.*
import org.ttpss930141011.bj.presentation.components.GameTable
import org.ttpss930141011.bj.presentation.components.feedback.GameOverDisplay
import org.ttpss930141011.bj.presentation.design.AppConstants

/**
 * Simple phase manager that delegates to unified GameTable component.
 * No more separate betting/game views - single consistent interface.
 */
@Composable
fun GamePhaseManager(
    game: Game,
    viewModel: GameViewModel,
    feedback: DecisionFeedback? = null,
    modifier: Modifier = Modifier
) {
    // Game over check - use ViewModel's domain-aligned logic
    if (viewModel.isGameOver) {
        GameOverDisplay(
            totalChips = game.player?.chips ?: 0,
            sessionStats = viewModel.sessionStats,
            onNewGame = {
                // Reset to new game with starting chips
                viewModel.initializeGame(
                    game.rules, 
                    Player(
                        id = game.player?.id ?: "player1",
                        chips = AppConstants.Defaults.PLAYER_STARTING_CHIPS
                    )
                )
            },
            onViewHistory = {
                // This would trigger opening the feedback drawer
                // Implementation depends on parent component structure
            },
            onViewSummary = {
                viewModel.showGameSummary()
            }
        )
    } else {
        // Single unified table for normal game phases
        GameTable(
            game = game,
            viewModel = viewModel,
            feedback = feedback,
            modifier = modifier
        )
    }
}