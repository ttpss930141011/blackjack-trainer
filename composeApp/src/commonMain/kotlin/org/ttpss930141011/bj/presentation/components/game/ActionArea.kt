package org.ttpss930141011.bj.presentation.components.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.domain.*
import org.ttpss930141011.bj.presentation.components.displays.ChipImageDisplay
import org.ttpss930141011.bj.presentation.mappers.ChipImageMapper
import org.ttpss930141011.bj.presentation.design.GameStatusColors
import org.ttpss930141011.bj.presentation.layout.BreakpointLayout

/**
 * Action area component that handles phase-specific user actions
 * Shows different interfaces for betting, playing, dealer turn, and settlement
 */

@Composable
fun ActionArea(
    game: Game,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier,
    feedback: DecisionFeedback? = null
) {
    when (game.phase) {
        GamePhase.WAITING_FOR_BETS -> {
            ChipSelection(
                availableChips = ChipImageMapper.standardChipValues,
                playerChips = game.player?.chips ?: 0,
                currentBet = viewModel.bettingTableState?.currentBet ?: 0,
                onChipSelected = { chipValue ->
                    ChipValue.fromValue(chipValue)?.let { viewModel.addChipToBet(it) }
                },
                onDealCards = {
                    viewModel.dealCards()
                },
                modifier = modifier
            )
        }
        GamePhase.PLAYER_ACTIONS -> {
            ActionButtons(
                availableActions = game.availableActions().toList(),
                onAction = { action ->
                    viewModel.playerAction(action)
                },
                feedback = feedback,
                modifier = modifier
            )
        }
        GamePhase.DEALER_TURN -> {
            DealerTurnButton(
                onPlayDealerTurn = {
                    viewModel.dealerTurn()
                },
                modifier = modifier
            )
        }
        GamePhase.SETTLEMENT -> {
            NextRoundButton(
                onNextRound = { viewModel.nextRound() },
                modifier = modifier
            )
        }
        else -> {
            Text(
                text = "Preparing...",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ChipSelection(
    availableChips: List<Int>,
    playerChips: Int,
    currentBet: Int,
    onChipSelected: (Int) -> Unit,
    onDealCards: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Tokens.Space.l)
    ) {
        // Player balance
        Text(
            text = "Balance: $$playerChips",
            style = MaterialTheme.typography.titleMedium,
            color = GameStatusColors.casinoGold,
            fontWeight = FontWeight.Bold
        )
        
        // Chip selection - responsive wrapping
        BreakpointLayout(
            compact = {
                // Compact: FlowRow for wrapping
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        Tokens.Space.s, 
                        Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(Tokens.Space.s)
                ) {
                    availableChips.forEach { chipValue ->
                        ChipImageDisplay(
                            value = chipValue,
                            size = Tokens.Size.chipDiameter,
                            onClick = {
                                if (currentBet + chipValue <= playerChips) {
                                    onChipSelected(chipValue)
                                }
                            }
                        )
                    }
                }
            },
            expanded = {
                // Expanded: LazyRow
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Tokens.Space.m, Alignment.CenterHorizontally),
                    contentPadding = PaddingValues(horizontal = Tokens.Space.l)
                ) {
                    items(availableChips) { chipValue ->
                        ChipImageDisplay(
                            value = chipValue,
                            size = Tokens.Size.chipDiameter,
                            onClick = {
                                if (currentBet + chipValue <= playerChips) {
                                    onChipSelected(chipValue)
                                }
                            }
                        )
                    }
                }
            }
        )
        
        // Deal button
        Button(
            onClick = onDealCards,
            enabled = currentBet > 0,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GameStatusColors.casinoGold,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(Tokens.Space.l),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = Tokens.Space.s)
        ) {
            Text(
                text = if (currentBet > 0) "Deal Cards ($$currentBet)" else "Deal Cards",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ActionButtons(
    availableActions: List<Action>,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
    feedback: DecisionFeedback? = null
) {
    BreakpointLayout(
        compact = {
            // Compact: FlowRow for wrapping
            FlowRow(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    Tokens.Space.s, 
                    Alignment.CenterHorizontally
                ),
                verticalArrangement = Arrangement.spacedBy(Tokens.Space.s)
            ) {
                availableActions.forEach { action ->
                    ActionButton(
                        action = action,
                        onAction = onAction,
                        feedback = feedback
                    )
                }
            }
        },
        expanded = {
            // Expanded: LazyRow
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Tokens.Space.m, Alignment.CenterHorizontally),
                modifier = modifier
            ) {
                items(availableActions) { action ->
                    ActionButton(
                        action = action,
                        onAction = onAction,
                        feedback = feedback
                    )
                }
            }
        }
    )
}

@Composable
private fun ActionButton(
    action: Action,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
    feedback: DecisionFeedback? = null
) {
    val (baseColor, icon) = when (action) {
        Action.HIT -> GameStatusColors.hitColor to "+"
        Action.STAND -> GameStatusColors.standColor to "−"
        Action.DOUBLE -> GameStatusColors.doubleColor to "×2"
        Action.SURRENDER -> GameStatusColors.surrenderColor to "↓"
        Action.SPLIT -> GameStatusColors.casinoGold to "⁝⁝"
    }
    
    // Show hint only (no color changes)
    val isOptimal = feedback?.optimalAction == action
    val showHint = feedback != null && !feedback.isCorrect && isOptimal
    
    Button(
        onClick = { onAction(action) },
        colors = ButtonDefaults.buttonColors(
            containerColor = baseColor,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(Tokens.Space.m),
        modifier = modifier
            .height(Tokens.Size.buttonHeight)
            .widthIn(min = Tokens.Size.chipDiameter)
    ) {
        BreakpointLayout(
            compact = {
                // Compact: Show icon + hint
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = icon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    if (showHint) {
                        Text(
                            text = "💡",
                            fontSize = 12.sp
                        )
                    }
                }
            },
            expanded = {
                // Expanded: Show icon + text + hint
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Tokens.Space.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = icon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = action.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (showHint) {
                        Text(
                            text = "💡",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun DealerTurnButton(
    onPlayDealerTurn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onPlayDealerTurn,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GameStatusColors.casinoGold,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(Tokens.Space.l),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = Tokens.Space.s)
        ) {
            Text(
                text = "Play Dealer Turn",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun NextRoundButton(
    onNextRound: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onNextRound,
            colors = ButtonDefaults.buttonColors(
                containerColor = GameStatusColors.casinoGreen,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(Tokens.Space.m),
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth(0.6f)
        ) {
            Text(
                text = "Next Round",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}