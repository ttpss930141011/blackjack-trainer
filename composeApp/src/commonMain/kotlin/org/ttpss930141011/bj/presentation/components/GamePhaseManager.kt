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
 * Manages the display and controls for different game phases.
 * Extracted from CasinoGameScreen to improve Single Responsibility Principle.
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
        when (game.phase) {
            GamePhase.WAITING_FOR_BETS -> {
                BettingPhaseContent(
                    game = game,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            else -> {
                GameInProgressContent(
                    game = game,
                    viewModel = viewModel
                )
            }
        }
        
        // Game over check
        if (game.phase == GamePhase.WAITING_FOR_BETS && (game.player?.chips ?: 0) <= 0) {
            GameOverDisplay(totalChips = game.player?.chips ?: 0)
        }
    }
}

@Composable
private fun BettingPhaseContent(
    game: Game,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    BettingTableView(
        game = game,
        onChipSelected = { chipValue ->
            viewModel.addChipToBet(chipValue)
        },
        onClearBet = {
            viewModel.clearBet()
        },
        onDealCards = {
            viewModel.dealCards()
        },
        modifier = modifier
    )
}

@Composable
private fun GameInProgressContent(
    game: Game,
    viewModel: GameViewModel
) {
    // Game in progress - show table
    ResponsiveGameTable(
        game = game,
        modifier = Modifier.fillMaxWidth()
    )
    
    Spacer(modifier = Modifier.height(24.dp))
    
    GamePhaseControls(
        game = game,
        viewModel = viewModel
    )
}

@Composable
private fun GamePhaseControls(
    game: Game,
    viewModel: GameViewModel
) {
    when (game.phase) {
        GamePhase.PLAYER_ACTIONS -> {
            PlayerActionControls(
                game = game,
                feedback = viewModel.feedback,
                onAction = { action ->
                    viewModel.playerAction(action)
                }
            )
        }
        
        GamePhase.DEALER_TURN -> {
            DealerTurnControls(
                onPlayDealerTurn = {
                    viewModel.dealerTurn()
                }
            )
        }
        
        GamePhase.SETTLEMENT -> {
            SettlementControls(
                game = game,
                feedback = viewModel.feedback,
                roundDecisions = viewModel.roundDecisions,
                onSettle = {
                    viewModel.settleRound()
                },
                onNextRound = {
                    viewModel.nextRound()
                }
            )
        }
        
        else -> {
            Text(
                text = "Preparing...",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun PlayerActionControls(
    game: Game,
    feedback: DecisionFeedback?,
    onAction: (Action) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        game.currentHand?.let {
            ResponsiveActionButtons(
                availableActions = game.availableActions().toList(),
                onAction = onAction
            )
        }
    }
}

@Composable
private fun DealerTurnControls(
    onPlayDealerTurn: () -> Unit
) {
    Button(
        onClick = onPlayDealerTurn,
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFC107), // Casino gold
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Text(
            text = "Play Dealer Turn",
            style = MaterialTheme.typography.titleMedium
        )
    }
}