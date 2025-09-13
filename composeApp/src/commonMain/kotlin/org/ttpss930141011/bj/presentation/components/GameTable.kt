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
import org.ttpss930141011.bj.presentation.components.feedback.GameOverOverlay
import org.ttpss930141011.bj.presentation.design.CasinoTheme
import org.ttpss930141011.bj.presentation.design.AppConstants
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
                // Compact: Action buttons in middle position for better mobile UX
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
                    
                    Spacer(modifier = Modifier.weight(0.5f))
                    
                    // Action area - moved to middle for compact layout
                    ActionArea(
                        game = game,
                        viewModel = viewModel,
                        feedback = feedback,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.weight(0.5f))
                    
                    // Player area - moved to bottom for chip visibility
                    PlayerArea(
                        game = game,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            medium = {
                // Medium/Expanded: Keep original bottom action button layout
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
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Player area - adapts based on phase
                    PlayerArea(
                        game = game,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Action area - kept at bottom for larger screens
                    ActionArea(
                        game = game,
                        viewModel = viewModel,
                        feedback = feedback,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
        
        // Game over overlay - following StatusOverlay pattern
        if (viewModel.isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                GameOverOverlay(
                    onNewGame = {
                        // Reset to new game with starting chips
                        viewModel.initializeGame(
                            game.rules, 
                            Player(
                                id = game.player?.id ?: "player1",
                                chips = AppConstants.Defaults.PLAYER_STARTING_CHIPS
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun PhaseHeader(phase: GamePhase) {
    val title = when (phase) {
        GamePhase.WAITING_FOR_BETS -> "Place Your Bet"
        GamePhase.PLAYER_TURN -> "Your Turn"
        GamePhase.DEALER_TURN -> "Dealer's Turn"
        GamePhase.SETTLEMENT -> "Round Results"
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