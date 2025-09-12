package org.ttpss930141011.bj.presentation.components.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.domain.entities.Game
import org.ttpss930141011.bj.domain.valueobjects.DecisionFeedback
import org.ttpss930141011.bj.presentation.components.GameTable
import org.ttpss930141011.bj.presentation.layout.ScreenWidth

/**
 * Simplified phase manager - GameTable now handles its own game over overlay.
 * Follows same pattern as StatusOverlay on DealerArea and PlayerHandCard.
 */
@Composable
fun GamePhaseManager(
    game: Game,
    viewModel: GameViewModel,
    feedback: DecisionFeedback? = null,
    screenWidth: ScreenWidth,
    modifier: Modifier = Modifier
) {
    // GameTable now handles game over overlay internally
    GameTable(
        game = game,
        viewModel = viewModel,
        feedback = feedback,
        screenWidth = screenWidth,
        modifier = modifier
    )
}