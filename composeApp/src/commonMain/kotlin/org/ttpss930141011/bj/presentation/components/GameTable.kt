package org.ttpss930141011.bj.presentation.components

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
                // Compact: Action buttons fixed in middle (thumb zone)
                // Settlement card floats in upper spacer, never pushes buttons
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CasinoTheme.CardTableBackground)
                        .padding(Tokens.Space.m),
                    verticalArrangement = Arrangement.spacedBy(Tokens.Space.m)
                ) {
                    // Phase title
                    PhaseHeader(game.phase)
                    
                    // Dealer area - consistent across all phases
                    DealerArea(
                        game = game,
                        screenWidth = screenWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Upper spacer — settlement card lives here
                    Box(
                        modifier = Modifier.weight(0.5f).fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        if (game.phase == GamePhase.SETTLEMENT) {
                            SettlementCard(
                                game = game,
                                roundDecisions = viewModel.roundDecisions
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
                    
                    // Player area - bottom for chip visibility
                    PlayerArea(
                        game = game,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            medium = {
                // Medium/Expanded: Action buttons at bottom
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CasinoTheme.CardTableBackground)
                        .padding(Tokens.Space.m),
                    verticalArrangement = Arrangement.spacedBy(Tokens.Space.m)
                ) {
                    // Phase title
                    PhaseHeader(game.phase)
                    
                    // Dealer area
                    DealerArea(
                        game = game,
                        screenWidth = screenWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Player area
                    PlayerArea(
                        game = game,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Settlement card above buttons
                    if (game.phase == GamePhase.SETTLEMENT) {
                        SettlementCard(
                            game = game,
                            roundDecisions = viewModel.roundDecisions
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Action area — only buttons
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