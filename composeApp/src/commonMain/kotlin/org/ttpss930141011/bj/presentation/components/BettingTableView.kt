package org.ttpss930141011.bj.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.domain.*
import org.ttpss930141011.bj.presentation.*

/**
 * Betting table view that shows the table layout during betting phase
 * Provides visual consistency between betting and game phases
 */
@Composable
fun BettingTableView(
    game: Game,
    onChipSelected: (ChipValue) -> Unit,
    onClearBet: () -> Unit,
    onDealCards: () -> Unit,
    modifier: Modifier = Modifier
) {
    require(game.phase == GamePhase.WAITING_FOR_BETS) { 
        "BettingTableView only works in WAITING_FOR_BETS phase" 
    }
    
    val bettingTable by remember(game) {
        mutableStateOf(BettingTableState.fromGame(game))
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Phase title - matches GameTableDisplay format
        Text(
            text = "Place Your Bet",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Dealer area - shows waiting state
        DealerBettingArea(
            message = bettingTable.dealerMessage,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Betting spot in center - shows current bet and chips with clear button
        BettingSpotDisplay(
            bettingTable = bettingTable,
            onClearBet = onClearBet,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Player hands area - replaced with chip selection during betting
        PlayerBettingArea(
            availableChips = bettingTable.availableChips,
            availableBalance = bettingTable.availableBalance,
            onChipSelected = onChipSelected,
            modifier = Modifier.fillMaxWidth()
        )

        // Deal Cards button - now full width since Clear moved to betting spot
        BettingDealButton(
            canDeal = bettingTable.canDeal,
            onDealCards = onDealCards,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Dealer area during betting phase - shows waiting message
 */
@Composable
private fun DealerBettingArea(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Dealer",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Show placeholder card backs or waiting message
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .size(40.dp, 56.dp)
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
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Central betting spot showing current bet and chip composition
 */
@Composable
private fun BettingSpotDisplay(
    bettingTable: BettingTableState,
    onClearBet: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Betting circle with clear button - where chips are visually placed
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            // Main betting circle
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        if (bettingTable.isEmpty) {
                            Color.White.copy(alpha = 0.1f)
                        } else {
                            Color(0xFF388E3C).copy(alpha = 0.3f)
                        }
                    )
                    .border(
                        2.dp,
                        Color.White.copy(alpha = 0.5f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (bettingTable.isEmpty) {
                    Text(
                        text = "Place Bet",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    ChipStackDisplay(
                        chipComposition = bettingTable.chipComposition,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }
            
            // Clear button - small X in top-left corner when chips are present
            if (!bettingTable.isEmpty && onClearBet != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
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

        // Total bet amount - simple white text
        if (bettingTable.currentBet > 0) {
            Text(
                text = "$${bettingTable.currentBet}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * Displays a stack of chips in the betting spot
 */
@Composable
private fun ChipStackDisplay(
    chipComposition: List<ChipInSpot>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        chipComposition.forEachIndexed { index, chipInSpot ->
            repeat(minOf(chipInSpot.count, 5)) { stackIndex -> // Max 5 visible chips per stack
                ChipImageDisplay(
                    value = chipInSpot.value.value,
                    size = ChipSize.MEDIUM,
                    onClick = { },
                    modifier = Modifier.offset(
                        x = (index * 8).dp,
                        y = -(stackIndex * 2).dp
                    )
                )
            }
        }
    }
}

/**
 * Player area during betting - shows available chips instead of hands
 */
@Composable
private fun PlayerBettingArea(
    availableChips: List<ChipValue>,
    availableBalance: Int,
    onChipSelected: (ChipValue) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Available Chips",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Balance: $$availableBalance",
                    fontSize = 14.sp,
                    color = Color(0xFFFFC107)
                )
            }

            // Chip selection row - centered
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(availableChips) { chipValue ->
                    ChipImageDisplay(
                        value = chipValue.value,
                        size = ChipSize.LARGE,
                        onClick = { onChipSelected(chipValue) }
                    )
                }
            }
        }
    }
}

/**
 * Full-width Deal Cards button for betting phase
 */
@Composable
private fun BettingDealButton(
    canDeal: Boolean,
    onDealCards: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onDealCards,
        enabled = canDeal,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Text(
            text = "Deal Cards",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}