package org.ttpss930141011.bj.presentation.components

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
import org.ttpss930141011.bj.domain.*
import org.ttpss930141011.bj.presentation.components.game.*
import org.ttpss930141011.bj.presentation.design.GameStatusColors

/**
 * Unified game table that adapts to all game phases.
 * Main orchestrator that delegates to specialized area components.
 */
@Composable
fun GameTable(
    game: Game,
    viewModel: GameViewModel,
    feedback: DecisionFeedback? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = GameStatusColors.casinoGreen.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(Tokens.Size.iconSmall),
        elevation = CardDefaults.cardElevation(defaultElevation = Tokens.Space.s)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Tokens.Space.xl),
            verticalArrangement = Arrangement.spacedBy(Tokens.Space.xl)
        ) {
            // Phase title
            PhaseHeader(game.phase)
            
            // Dealer area - consistent across all phases
            DealerArea(
                game = game,
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
            
            // Action area - adapts based on phase  
            ActionArea(
                game = game,
                viewModel = viewModel,
                feedback = feedback,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PhaseHeader(phase: GamePhase) {
    val title = when (phase) {
        GamePhase.WAITING_FOR_BETS -> "Place Your Bet"
        GamePhase.PLAYER_ACTIONS -> "Your Turn"
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