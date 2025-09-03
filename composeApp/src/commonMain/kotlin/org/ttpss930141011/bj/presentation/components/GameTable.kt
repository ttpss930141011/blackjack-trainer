package org.ttpss930141011.bj.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.domain.*
import org.ttpss930141011.bj.presentation.*
import org.ttpss930141011.bj.presentation.shared.GameStatusColors

/**
 * Unified game table that adapts to all game phases.
 * Eliminates the need for separate BettingTableView and ResponsiveGameTable.
 */
@Composable
fun GameTable(
    game: Game,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = GameStatusColors.casinoGreen.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
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

@Composable
private fun DealerArea(
    game: Game,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (game.phase) {
            GamePhase.WAITING_FOR_BETS -> {
                DealerWaitingDisplay()
            }
            else -> {
                DealerHandDisplay(
                    dealerHand = game.dealer.hand,
                    dealerUpCard = game.dealer.upCard,
                    phase = game.phase
                )
            }
        }
    }
}

@Composable
private fun DealerWaitingDisplay() {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Dealer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Show placeholder cards
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(2) {
                    PlaceholderCard()
                }
            }
            
            Text(
                text = "Waiting for bets...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DealerHandDisplay(
    dealerHand: Hand?,
    dealerUpCard: Card?,
    phase: GamePhase
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Dealer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            when (phase) {
                GamePhase.PLAYER_ACTIONS -> {
                    dealerUpCard?.let { upCard ->
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            CardImageDisplay(card = upCard, size = CardSize.MEDIUM)
                            HoleCardDisplay(size = CardSize.MEDIUM)
                        }
                        Text("Up Card: ${upCard.rank}")
                    }
                }
                else -> {
                    dealerHand?.let { hand ->
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(hand.cards) { card ->
                                CardImageDisplay(card = card, size = CardSize.MEDIUM)
                            }
                        }
                        Text(
                            text = "Value: ${hand.bestValue}${if (hand.isSoft) " (soft)" else ""}",
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                        if (hand.isBusted) {
                            Text(
                                text = "Busted!",
                                color = GameStatusColors.bustColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerArea(
    game: Game,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (game.phase) {
            GamePhase.WAITING_FOR_BETS -> {
                BettingCircle(
                    bettingTableState = viewModel.bettingTableState,
                    onClearBet = { viewModel.clearBet() }
                )
            }
            else -> {
                PlayerHandsDisplay(
                    playerHands = game.playerHands,
                    currentHandIndex = game.currentHandIndex,
                    phase = game.phase
                )
            }
        }
    }
}

@Composable
private fun BettingCircle(
    bettingTableState: BettingTableState?,
    onClearBet: () -> Unit
) {
    Box(
        modifier = Modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    if (bettingTableState?.currentBet == 0) {
                        Color.White.copy(alpha = 0.1f)
                    } else {
                        GameStatusColors.activeColor.copy(alpha = 0.3f)
                    }
                )
                .border(
                    2.dp,
                    Color.White.copy(alpha = 0.5f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (bettingTableState?.currentBet == 0 || bettingTableState == null) {
                Text(
                    text = "Place Bet",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
            } else {
                // Show dynamic chip composition instead of text
                ChipDisplay(chipComposition = bettingTableState.chipComposition)
            }
        }
        
        // Clear button when bet is placed
        if ((bettingTableState?.currentBet ?: 0) > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.7f))
                    .clickable { onClearBet() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "×",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun PlayerHandsDisplay(
    playerHands: List<PlayerHand>,
    currentHandIndex: Int,
    phase: GamePhase
) {
    if (playerHands.isEmpty()) return
    
    if (playerHands.size == 1) {
        PlayerHandCard(
            hand = playerHands[0],
            handIndex = 0,
            isActive = currentHandIndex == 0,
            phase = phase
        )
    } else {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(playerHands) { index, hand ->
                PlayerHandCard(
                    hand = hand,
                    handIndex = index,
                    isActive = currentHandIndex == index,
                    phase = phase,
                    modifier = Modifier.width(200.dp)
                )
            }
        }
    }
}

@Composable
private fun PlayerHandCard(
    hand: PlayerHand,
    handIndex: Int,
    isActive: Boolean,
    phase: GamePhase,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isActive && phase == GamePhase.PLAYER_ACTIONS) {
                GameStatusColors.activeColor.copy(alpha = 0.8f)
            } else {
                GameStatusColors.casinoGreen.copy(alpha = 0.6f)
            }
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Hand title
            val title = when {
                handIndex > 0 && isActive && phase == GamePhase.PLAYER_ACTIONS -> "Hand ${handIndex + 1} (Your Turn)"
                handIndex > 0 -> "Hand ${handIndex + 1}"
                isActive && phase == GamePhase.PLAYER_ACTIONS -> "Your Turn"
                else -> "Your Hand"
            }
            
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = if (isActive && phase == GamePhase.PLAYER_ACTIONS) {
                    GameStatusColors.casinoGold
                } else {
                    Color.White
                },
                fontWeight = FontWeight.Bold
            )
            
            // Cards
            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items(hand.cards) { card ->
                    CardImageDisplay(card = card, size = CardSize.MEDIUM)
                }
            }
            
            // Hand value
            Text(
                text = "Value: ${hand.bestValue}${if (hand.isSoft) " (soft)" else ""}",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            
            // Bet amount
            Text(
                text = "Bet: $${hand.bet}",
                color = GameStatusColors.betColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            
            // Settlement status
            if (phase == GamePhase.SETTLEMENT) {
                Text(
                    text = hand.status.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = GameStatusColors.getHandStatusColor(hand.status),
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Busted indicator
            if (hand.isBusted) {
                Text(
                    text = "Busted!",
                    color = GameStatusColors.bustColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun ActionArea(
    game: Game,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Player balance
        Text(
            text = "Balance: $$playerChips",
            style = MaterialTheme.typography.titleMedium,
            color = GameStatusColors.casinoGold,
            fontWeight = FontWeight.Bold
        )
        
        // Chip selection
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(availableChips) { chipValue ->
                ChipImageDisplay(
                    value = chipValue,
                    size = ChipSize.LARGE,
                    onClick = {
                        if (currentBet + chipValue <= playerChips) {
                            onChipSelected(chipValue)
                        }
                    }
                )
            }
        }
        
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
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
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
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        modifier = modifier
    ) {
        items(availableActions) { action ->
            Button(
                onClick = { onAction(action) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GameStatusColors.casinoGold,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(48.dp)
                    .widthIn(min = 80.dp)
            ) {
                Text(
                    text = action.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
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
                .fillMaxWidth(0.6f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GameStatusColors.casinoGold,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
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
            shape = RoundedCornerShape(12.dp),
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

@Composable
private fun PlaceholderCard() {
    Box(
        modifier = Modifier
            .size(CardSize.MEDIUM.width, CardSize.MEDIUM.height)
            .background(
                Color.Gray.copy(alpha = 0.3f),
                RoundedCornerShape(4.dp)
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.3f),
                RoundedCornerShape(4.dp)
            )
    )
}

@Composable
private fun ChipDisplay(
    chipComposition: List<ChipInSpot>
) {
    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        chipComposition.forEachIndexed { index, chipInSpot ->
            val offsetX = (index * 8).dp
            val offsetY = (index * 2).dp
            
            Box(
                modifier = Modifier.offset(x = offsetX, y = -offsetY)
            ) {
                repeat(chipInSpot.count) { stackIndex ->
                    ChipImageDisplay(
                        value = chipInSpot.value.value,
                        onClick = { },
                        size = ChipSize.MEDIUM,
                        modifier = Modifier.offset(
                            x = (stackIndex * 2).dp,
                            y = (stackIndex * 1).dp
                        )
                    )
                }
            }
        }
        
    }
}