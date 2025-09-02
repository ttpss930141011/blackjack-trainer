package org.ttpss930141011.bj.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.Action

@Composable
fun ActionButtons(
    availableActions: List<Action>,
    currentChips: Int? = null,
    onAction: (Action) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Action buttons on the left
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(availableActions) { action ->
                ActionButton(
                    action = action,
                    onClick = { onAction(action) }
                )
            }
        }
        
        // Chips display on the right
        currentChips?.let { chips ->
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .width(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF1B5E20), // Dark green
                                Color(0xFF4CAF50)  // Light green
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "💰",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "$$chips",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    action: Action,
    onClick: () -> Unit
) {
    val buttonColors = when (action) {
        Action.HIT -> ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAF50), // Green
            contentColor = Color.White
        )
        Action.STAND -> ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF44336), // Red
            contentColor = Color.White
        )
        Action.DOUBLE -> ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF9800), // Orange
            contentColor = Color.White
        )
        Action.SPLIT -> ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2196F3), // Blue
            contentColor = Color.White
        )
        Action.SURRENDER -> ButtonDefaults.buttonColors(
            containerColor = Color(0xFF9E9E9E), // Gray
            contentColor = Color.White
        )
    }
    
    Button(
        onClick = onClick,
        colors = buttonColors,
        modifier = Modifier
            .height(48.dp)
            .width(140.dp)
    ) {
        Text(
            text = action.name,
            style = MaterialTheme.typography.labelLarge
        )
    }
}