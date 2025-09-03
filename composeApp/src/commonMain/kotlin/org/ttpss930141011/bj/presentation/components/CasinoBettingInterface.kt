package org.ttpss930141011.bj.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.presentation.ChipImageDisplay
import org.ttpss930141011.bj.presentation.ChipImageMapper
import org.ttpss930141011.bj.presentation.ChipSize

@Composable
fun CasinoBettingInterface(
    currentChips: Int,
    onStartRound: (Int) -> Unit
) {
    var totalBet by remember { mutableStateOf(0) }
    val availableChips = ChipImageMapper.standardChipValues
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // "Place Your Bet" Display
        Card(
            modifier = Modifier.shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp)
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFC107) // Casino gold
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 48.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Place Your Bet",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                
                if (totalBet > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Total: $$totalBet",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        // Chip Selection
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            items(availableChips) { chipValue ->
                ChipImageDisplay(
                    value = chipValue,
                    onClick = { 
                        if (totalBet + chipValue <= currentChips) {
                            totalBet += chipValue
                        }
                    },
                    size = ChipSize.LARGE
                )
            }
        }
        
        // Betting Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clear Bet Button
            if (totalBet > 0) {
                OutlinedButton(
                    onClick = { totalBet = 0 },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.White, Color.White.copy(alpha = 0.7f))
                        )
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = "Clear",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        // Deal Cards Button (always visible)
        Button(
            onClick = { 
                onStartRound(totalBet)
                totalBet = 0 // Reset for next round
            },
            enabled = totalBet > 0,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(64.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(20.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFC107), // Casino gold
                contentColor = Color.Black,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = if (totalBet > 0) "Deal Cards ($$totalBet)" else "Deal Cards",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}

