package org.ttpss930141011.bj.presentation.components

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.*
import org.ttpss930141011.bj.presentation.components.game.*
import org.ttpss930141011.bj.presentation.design.CasinoTheme
import org.ttpss930141011.bj.presentation.design.AppConstants
import org.ttpss930141011.bj.presentation.design.Strings
import org.ttpss930141011.bj.presentation.layout.ScreenWidth
import org.ttpss930141011.bj.presentation.layout.BreakpointLayout

/**
 * Unified game table that adapts to all game phases.
 * Main orchestrator that delegates to specialized area components.
 */
@Composable
fun GameTable(
    game: Game,
    viewModel: GameViewModel,
    feedback: DecisionFeedback? = null,
    screenWidth: ScreenWidth,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Main game table - responsive layout based on screen width
        BreakpointLayout(
            compact = {
                // Compact: buttons fixed in thumb zone
                // Upper spacer has fixed weight — settlement card inside won't push buttons
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CasinoTheme.CardTableBackground)
                        .padding(Tokens.Space.m),
                    verticalArrangement = Arrangement.spacedBy(Tokens.Space.m)
                ) {
                    PhaseHeader(game.phase)

                    DealerArea(
                        game = game,
                        screenWidth = screenWidth,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Upper flex zone — settlement card appears here (between dealer & buttons)
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = game.phase == GamePhase.SETTLEMENT,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            SettlementCard(
                                game = game,
                                roundDecisions = viewModel.roundDecisions,
                                modifier = Modifier.padding(horizontal = Tokens.Space.s)
                            )
                        }
                    }

                    // Action area — fixed position, only buttons
                    ActionArea(
                        game = game,
                        viewModel = viewModel,
                        feedback = feedback,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.weight(0.5f))

                    PlayerArea(
                        game = game,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            medium = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CasinoTheme.CardTableBackground)
                        .padding(Tokens.Space.m),
                    verticalArrangement = Arrangement.spacedBy(Tokens.Space.m)
                ) {
                    PhaseHeader(game.phase)

                    DealerArea(
                        game = game,
                        screenWidth = screenWidth,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    PlayerArea(
                        game = game,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Settlement card in flow but inside weighted spacer
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = game.phase == GamePhase.SETTLEMENT,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            SettlementCard(
                                game = game,
                                roundDecisions = viewModel.roundDecisions,
                                modifier = Modifier.padding(horizontal = Tokens.Space.s)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(0.5f))

                    ActionArea(
                        game = game,
                        viewModel = viewModel,
                        feedback = feedback,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
        
        // Game over overlay
        if (viewModel.isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Tokens.Space.m)
                ) {
                    Text(
                        text = "Out of Chips",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = {
                            viewModel.initializeGame(
                                game.rules,
                                Player(
                                    id = game.player?.id ?: "player1",
                                    chips = AppConstants.Defaults.PLAYER_STARTING_CHIPS
                                )
                            )
                        }
                    ) {
                        Text("New Game")
                    }
                }
            }
        }
    }
}

@Composable
private fun PhaseHeader(phase: GamePhase) {
    val title = when (phase) {
        GamePhase.WAITING_FOR_BETS -> Strings.Game.PLACE_YOUR_BET
        GamePhase.PLAYER_TURN -> Strings.Game.YOUR_TURN
        GamePhase.DEALER_TURN -> Strings.Game.DEALERS_TURN
        GamePhase.SETTLEMENT -> Strings.Game.ROUND_RESULTS
        else -> "Casino Table"
    }
    
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}