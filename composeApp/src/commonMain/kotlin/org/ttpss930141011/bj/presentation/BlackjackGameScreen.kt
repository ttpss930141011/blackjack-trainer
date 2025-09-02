package org.ttpss930141011.bj.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.domain.*
import org.ttpss930141011.bj.presentation.components.*

@Composable
fun BlackjackGameScreen(
    gameRules: GameRules = GameRules(),
    onBackToMenu: () -> Unit = {}
) {
    val viewModel = remember { GameViewModel() }
    
    LaunchedEffect(gameRules) {
        viewModel.initializeGame(gameRules, Player(id = "player1", chips = 500))
    }
    
    val game = viewModel.game
    val currentPlayer = game?.player ?: Player(id = "player1", chips = 0)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameHeader(
            onBackToMenu = onBackToMenu,
            onShowSummary = { viewModel.showGameSummary() },
            hasStats = viewModel.sessionStats.totalRounds > 0
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        GameStatusDisplay(player = currentPlayer, stats = viewModel.sessionStats)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        game?.let { currentGame ->
            GameTableDisplay(
                game = currentGame,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            GamePhaseControls(
                game = currentGame,
                viewModel = viewModel
            )
        }
        
        if (game?.phase == GamePhase.WAITING_FOR_BETS && currentPlayer.chips <= 0) {
            GameOverDisplay(totalChips = currentPlayer.chips)
        }
        
        viewModel.errorMessage?.let { error ->
            LaunchedEffect(error) {
                viewModel.clearError()
            }
        }
    }
    
    if (viewModel.showGameSummary) {
        GameSummaryDialog(
            stats = viewModel.sessionStats,
            onDismiss = { viewModel.hideGameSummary() },
            onBackToMenu = {
                viewModel.hideGameSummary()
                onBackToMenu()
            }
        )
    }
}

@Composable
private fun GameHeader(
    onBackToMenu: () -> Unit,
    onShowSummary: () -> Unit,
    hasStats: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onBackToMenu) {
            Text("← Menu")
        }
        
        Text(
            text = "Strategy Trainer",
            style = MaterialTheme.typography.headlineSmall
        )
        
        TextButton(
            onClick = onShowSummary,
            enabled = hasStats
        ) {
            Text("Exit Game")
        }
    }
}

@Composable
private fun GamePhaseControls(
    game: Game,
    viewModel: GameViewModel
) {
    when (game.phase) {
        GamePhase.WAITING_FOR_BETS -> {
            BettingControls(
                currentChips = game.player?.chips ?: 0,
                onStartRound = { betAmount ->
                    viewModel.startRound(betAmount)
                }
            )
        }
        
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
            Text("Preparing...")
        }
    }
}

@Composable
private fun PlayerActionControls(
    game: Game,
    feedback: DecisionFeedback?,
    onAction: (Action) -> Unit
) {
    Column {
        feedback?.let { fb ->
            Card {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = fb.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (fb.isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        game.currentHand?.let {
            ActionButtons(
                availableActions = game.availableActions().toList(),
                currentChips = game.player?.chips,
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
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = "Play Dealer Turn",
            style = MaterialTheme.typography.labelLarge
        )
    }
}