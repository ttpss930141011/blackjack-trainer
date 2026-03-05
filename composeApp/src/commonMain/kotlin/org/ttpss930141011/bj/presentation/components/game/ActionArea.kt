package org.ttpss930141011.bj.presentation.components.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
            SettlementReview(
                game = game,
                roundDecisions = viewModel.roundDecisions,
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
    
    // Flash animation: briefly change button color after player's choice
    val isChosen = feedback?.playerAction == action
    val isOptimal = feedback?.optimalAction == action
    
    // Animate flash: chosen button flashes green (correct) or red (wrong)
    // Optimal button gets green border if player was wrong
    var flashActive by remember { mutableStateOf(false) }
    
    LaunchedEffect(feedback) {
        if (feedback != null && (isChosen || (isOptimal && !feedback.isCorrect))) {
            flashActive = true
            kotlinx.coroutines.delay(700)
            flashActive = false
        }
    }
    
    val flashColor = when {
        !flashActive -> baseColor
        isChosen && feedback?.isCorrect == true -> Color(0xFF4CAF50) // green
        isChosen && feedback?.isCorrect == false -> Color(0xFFF44336) // red
        isOptimal && feedback?.isCorrect == false -> Color(0xFF4CAF50) // green hint
        else -> baseColor
    }
    
    val animatedColor by animateColorAsState(
        targetValue = flashColor,
        animationSpec = tween(durationMillis = if (flashActive) 150 else 400)
    )
    
    Button(
        onClick = { onAction(action) },
        colors = ButtonDefaults.buttonColors(
            containerColor = animatedColor,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(Tokens.Space.m),
        modifier = modifier.height(Tokens.Size.buttonHeight),
        contentPadding = PaddingValues(horizontal = Tokens.Space.xs, vertical = Tokens.Space.xs)
    ) {
        BreakpointLayout(
            compact = {
                Text(
                    text = icon,
                    fontWeight = FontWeight.Bold,
                    fontSize = Tokens.Typography.actionButtonIconCompact,
                    maxLines = 1
                )
            },
            expanded = {
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
                }
            }
        )
    }
}

@Composable
private fun SettlementReview(
    game: Game,
    roundDecisions: List<PlayerDecision>,
    onNextRound: () -> Unit,
    modifier: Modifier = Modifier
) {
    val outcome = if (game.phase == GamePhase.SETTLEMENT) game.getRoundOutcome() else RoundOutcome.UNKNOWN
    val totalDecisions = roundDecisions.size
    val correctDecisions = roundDecisions.count { it.isCorrect }
    val allCorrect = totalDecisions > 0 && correctDecisions == totalDecisions
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Tokens.Space.m),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Round result + strategy summary card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.1f)
            ),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Strategy line
                if (totalDecisions > 0) {
                    val strategyEmoji = if (allCorrect) "✅" else "📊"
                    val strategyText = if (allCorrect) {
                        "Perfect strategy!"
                    } else {
                        "$correctDecisions/$totalDecisions correct"
                    }
                    
                    Text(
                        text = "$strategyEmoji $strategyText",
                        color = if (allCorrect) Color(0xFF4CAF50) else Color(0xFFFFB74D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    // Encouragement message: separate luck from skill
                    val message = when {
                        allCorrect && outcome == RoundOutcome.WIN -> "Skill + luck 🎯"
                        allCorrect && outcome == RoundOutcome.LOSS -> "Right call — just unlucky"
                        allCorrect && outcome == RoundOutcome.PUSH -> "Played it right"
                        !allCorrect && outcome == RoundOutcome.WIN -> "Won, but review your play"
                        !allCorrect && outcome == RoundOutcome.LOSS -> "Check strategy guide ←"
                        else -> ""
                    }
                    if (message.isNotEmpty()) {
                        Text(
                            text = message,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        
        // Next round button
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
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = "Next Round",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
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

