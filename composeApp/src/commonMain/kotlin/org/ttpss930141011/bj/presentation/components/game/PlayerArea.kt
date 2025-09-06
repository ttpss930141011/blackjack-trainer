package org.ttpss930141011.bj.presentation.components.game

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.*

/**
 * Player area component that handles player hand display
 * Shows betting circle during betting phase, player hands during game
 */

@Composable
fun PlayerArea(
    game: Game,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val chipCompositionService = remember { ChipCompositionService() }
    
    org.ttpss930141011.bj.presentation.layout.Layout { screenWidth ->
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            when (game.phase) {
                GamePhase.WAITING_FOR_BETS -> {
                    BettingCircle(
                        currentBet = viewModel.currentBetAmount,
                        chipComposition = viewModel.chipComposition,
                        onClearBet = { viewModel.clearBet() }
                    )
                }
                else -> {
                    SmartHandCarousel(
                        playerHands = game.playerHands,
                        currentHandIndex = game.currentHandIndex,
                        phase = game.phase,
                        chipCompositionService = chipCompositionService,
                        screenWidth = screenWidth
                    )
                }
            }
        }
    }
}

