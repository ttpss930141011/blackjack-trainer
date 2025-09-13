package org.ttpss930141011.bj.presentation.components.game

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.presentation.components.displays.ChipImageDisplay
import org.ttpss930141011.bj.presentation.mappers.ChipImageMapper
import org.ttpss930141011.bj.presentation.design.CasinoTheme
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
                currentBet = viewModel.currentBetAmount,
                lastBetAmount = viewModel.lastBetAmount,
                onChipSelected = { chipValue ->
                    ChipValue.fromValue(chipValue)?.let { viewModel.addChipToBet(it) }
                },
                onDealCards = {
                    viewModel.dealCards()
                },
                onRepeatLastBet = {
                    viewModel.repeatLastBet()
                },
                modifier = modifier
            )
        }
        GamePhase.PLAYER_TURN -> {
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
    lastBetAmount: Int?,
    onChipSelected: (Int) -> Unit,
    onDealCards: () -> Unit,
    onRepeatLastBet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    // Auto-apply last bet when entering betting phase
    LaunchedEffect(lastBetAmount, currentBet) {
        if (lastBetAmount != null && lastBetAmount > 0 && currentBet == 0) {
            // Only auto-apply if user hasn't started betting manually
            onRepeatLastBet()
        }
    }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Tokens.Space.l)
    ) {
        // Chip selection - horizontally scrollable for mobile devices
        // Use fixed width to ensure scrolling works on mobile (7 chips * 80dp + spacing ≈ 624dp)
        val mobileContentWidth = Tokens.Size.chipDiameter * 8 // Buffer for spacing and padding
        
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .width(mobileContentWidth)
                    .horizontalScroll(scrollState)
                    .padding(horizontal = Tokens.Space.s),
                horizontalArrangement = Arrangement.spacedBy(Tokens.Space.s, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
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
        }
        
        // Deal button - centered
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onDealCards,
                enabled = currentBet > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Tokens.Size.buttonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CasinoTheme.ButtonPrimary,
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
        Spacer(modifier = Modifier.height(Tokens.Size.chipDiameter))
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
            // Compact: Single row with custom ordering: Double, Hit, Stand, Split, Surrender
            val orderedActions = availableActions.sortedWith { a, b ->
                val order = mapOf(
                    Action.DOUBLE to 1,
                    Action.HIT to 2, 
                    Action.STAND to 3,
                    Action.SPLIT to 4,
                    Action.SURRENDER to 5
                )
                (order[a] ?: 99) - (order[b] ?: 99)
            }
            
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    Tokens.Space.s,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                orderedActions.forEach { action ->
                    ActionButton(
                        action = action,
                        onAction = onAction,
                        feedback = feedback,
                        modifier = Modifier.weight(1f) // 让按钮平均分配宽度
                    )
                }
            }
        },
        expanded = {
            // Expanded: LazyRow with same custom ordering: Double, Hit, Stand, Split, Surrender
            val orderedActions = availableActions.sortedWith { a, b ->
                val order = mapOf(
                    Action.DOUBLE to 1,
                    Action.HIT to 2, 
                    Action.STAND to 3,
                    Action.SPLIT to 4,
                    Action.SURRENDER to 5
                )
                (order[a] ?: 99) - (order[b] ?: 99)
            }
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Tokens.Space.m, Alignment.CenterHorizontally),
                modifier = modifier
            ) {
                items(orderedActions) { action ->
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
        Action.HIT -> CasinoTheme.HitButtonBackground to "+"
        Action.STAND -> CasinoTheme.ButtonDanger to "−"
        Action.DOUBLE -> CasinoTheme.CasinoAccentSecondary to "×2"
        Action.SURRENDER -> CasinoTheme.ButtonSecondary to "↓"
        Action.SPLIT -> CasinoTheme.CasinoAccentPrimary to "⁝⁝"
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
        modifier = modifier.height(Tokens.Size.buttonHeight),
        contentPadding = PaddingValues(horizontal = Tokens.Space.xs, vertical = Tokens.Space.xs) // 减少内边距以提供更多文本空间
    ) {
        BreakpointLayout(
            compact = {
                // Compact: Show icon + hint, 对Double按钮特殊处理
                if (action == Action.DOUBLE) {
                    // Double按钮使用紧凑布局，避免×2分离
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = icon,
                            fontWeight = FontWeight.Bold,
                            fontSize = Tokens.Typography.actionButtonIconCompact,
                            maxLines = 1
                        )
                        if (showHint) {
                            Text(
                                text = "💡",
                                fontSize = Tokens.Typography.actionButtonHintCompact
                            )
                        }
                    }
                } else {
                    // 其他按钮使用垂直布局优化空间利用
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = icon,
                            fontWeight = FontWeight.Bold,
                            fontSize = Tokens.Typography.actionButtonIconExpanded,
                            maxLines = 1
                        )
                        if (showHint) {
                            Text(
                                text = "💡",
                                fontSize = Tokens.Typography.actionButtonHintCompact
                            )
                        }
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
                        fontSize = Tokens.Typography.actionButtonIconExpanded,
                        maxLines = 1
                    )
                    Text(
                        text = action.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = Tokens.Typography.actionButtonTextExpanded,
                        maxLines = 1
                    )
                    if (showHint) {
                        Text(
                            text = "💡",
                            fontSize = Tokens.Typography.actionButtonHintExpanded
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
                .fillMaxWidth()
                .height(Tokens.Size.buttonHeight),
            colors = ButtonDefaults.buttonColors(
                containerColor = CasinoTheme.ButtonPrimary,
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
                containerColor = CasinoTheme.ButtonPrimary,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(Tokens.Space.m),
            modifier = Modifier
                .height(Tokens.Size.buttonHeight)
                .fillMaxWidth()
        ) {
            Text(
                text = "Next Round",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}