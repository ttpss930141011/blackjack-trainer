package org.ttpss930141011.bj.presentation.components.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.domain.valueobjects.ChipInSpot
import org.ttpss930141011.bj.presentation.components.displays.ChipImageDisplay
import org.ttpss930141011.bj.presentation.design.GameStatusColors
import org.ttpss930141011.bj.presentation.layout.Layout

/**
 * Betting circle component for placing and displaying bets
 * Shows bet amount and chip composition with clear bet functionality
 */

@Composable
fun BettingCircle(
    currentBet: Int,
    chipComposition: List<ChipInSpot>,
    onClearBet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Layout { screenWidth ->
        Box(
            modifier = modifier.size(Tokens.bettingCircleSize(screenWidth)),
            contentAlignment = Alignment.Center
        ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    if (currentBet == 0) {
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
            if (currentBet == 0) {
                Text(
                    text = "Place Bet",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
            } else {
                // Show dynamic chip composition instead of text
                ChipDisplay(chipComposition = chipComposition)
            }
        }
        
        // Clear button when bet is placed
        if (currentBet > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(Tokens.Size.iconLarge)
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
                        size = Tokens.Size.chipDiameter,
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