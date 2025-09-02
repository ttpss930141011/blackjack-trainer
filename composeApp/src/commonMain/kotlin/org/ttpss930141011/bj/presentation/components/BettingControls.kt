package org.ttpss930141011.bj.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.presentation.ChipImageDisplay
import org.ttpss930141011.bj.presentation.ChipImageMapper

@Composable
fun BettingControls(
    currentChips: Int,
    onStartRound: (Int) -> Unit
) {
    var betAmount by remember { mutableStateOf(25) }
    val availableChips = ChipImageMapper.standardChipValues.filter { it <= currentChips }
    
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Place Your Bet",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Bet:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$$betAmount",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(availableChips) { chipValue ->
                    ChipImageDisplay(
                        value = chipValue,
                        onClick = { betAmount += chipValue }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextButton(
                onClick = { betAmount = 0 }
            ) {
                Text("Clear")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = { onStartRound(betAmount) },
                enabled = betAmount <= currentChips && betAmount > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Deal Cards ($$betAmount)")
            }
        }
    }
}