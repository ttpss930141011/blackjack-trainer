package org.ttpss930141011.bj.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.domain.Action

@Composable
fun CasinoActionButtons(
    availableActions: List<Action>,
    onAction: (Action) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "Choose Your Action",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        
        // Action buttons in rows
        val primaryActions = availableActions.filter { it in listOf(Action.HIT, Action.STAND) }
        val secondaryActions = availableActions.filter { it !in listOf(Action.HIT, Action.STAND) }
        
        // Primary row (HIT/STAND)
        if (primaryActions.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                primaryActions.forEach { action ->
                    CasinoActionButton(
                        action = action,
                        onClick = { onAction(action) },
                        isPrimary = true
                    )
                }
            }
        }
        
        // Secondary row (DOUBLE/SPLIT/SURRENDER)
        if (secondaryActions.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                secondaryActions.forEach { action ->
                    CasinoActionButton(
                        action = action,
                        onClick = { onAction(action) },
                        isPrimary = false
                    )
                }
            }
        }
    }
}

@Composable
private fun CasinoActionButton(
    action: Action,
    onClick: () -> Unit,
    isPrimary: Boolean
) {
    val buttonColor = getActionButtonColor(action)
    val buttonSize = if (isPrimary) 140.dp else 120.dp
    val buttonHeight = if (isPrimary) 56.dp else 48.dp
    val textSize = if (isPrimary) 18.sp else 16.sp
    
    val scale by animateFloatAsState(
        targetValue = 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(buttonSize)
            .height(buttonHeight)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        )
    ) {
        Text(
            text = action.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontSize = textSize
        )
    }
}

private fun getActionButtonColor(action: Action): Color = when (action) {
    Action.HIT -> Color(0xFF4CAF50)      // Green
    Action.STAND -> Color(0xFFF44336)    // Red
    Action.DOUBLE -> Color(0xFFFF9800)   // Orange
    Action.SPLIT -> Color(0xFF2196F3)    // Blue
    Action.SURRENDER -> Color(0xFF9E9E9E) // Gray
}