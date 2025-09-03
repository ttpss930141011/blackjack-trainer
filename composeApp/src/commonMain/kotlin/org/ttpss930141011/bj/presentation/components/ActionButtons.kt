package org.ttpss930141011.bj.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.domain.Action
import org.ttpss930141011.bj.presentation.responsive.ResponsiveLayout
import org.ttpss930141011.bj.presentation.responsive.WindowInfo
import org.ttpss930141011.bj.presentation.responsive.getSpacing
import org.ttpss930141011.bj.presentation.responsive.getCardCornerRadius
import org.ttpss930141011.bj.presentation.responsive.getButtonHeight

@Composable
fun ActionButtons(
    availableActions: List<Action>,
    onAction: (Action) -> Unit
) {
    ResponsiveLayout { windowInfo ->
        when {
            windowInfo.isCompact -> MobileActionButtons(
                availableActions = availableActions,
                onAction = onAction,
                windowInfo = windowInfo
            )
            windowInfo.isMedium -> TabletActionButtons(
                availableActions = availableActions,
                onAction = onAction,
                windowInfo = windowInfo
            )
            else -> DesktopActionButtons(
                availableActions = availableActions,
                onAction = onAction,
                windowInfo = windowInfo
            )
        }
    }
}

@Composable
private fun MobileActionButtons(
    availableActions: List<Action>,
    onAction: (Action) -> Unit,
    windowInfo: WindowInfo
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(windowInfo.getSpacing())
    ) {
        Text(
            text = "Your Action",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        
        // Mobile: 2-column grid for better touch accessibility
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(availableActions) { action ->
                MobileActionButton(
                    action = action,
                    onClick = { onAction(action) },
                    windowInfo = windowInfo
                )
            }
        }
    }
}

@Composable
private fun TabletActionButtons(
    availableActions: List<Action>,
    onAction: (Action) -> Unit,
    windowInfo: WindowInfo
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(windowInfo.getSpacing())
    ) {
        Text(
            text = "Choose Your Action",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        
        // Tablet: Mix of rows and single line based on action count
        if (availableActions.size <= 3) {
            // Single row for few actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                availableActions.forEach { action ->
                    TabletActionButton(
                        action = action,
                        onClick = { onAction(action) },
                        windowInfo = windowInfo
                    )
                }
            }
        } else {
            // Primary and secondary rows
            val primaryActions = availableActions.filter { it in listOf(Action.HIT, Action.STAND) }
            val secondaryActions = availableActions.filter { it !in listOf(Action.HIT, Action.STAND) }
            
            if (primaryActions.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    primaryActions.forEach { action ->
                        TabletActionButton(
                            action = action,
                            onClick = { onAction(action) },
                            windowInfo = windowInfo,
                            isPrimary = true
                        )
                    }
                }
            }
            
            if (secondaryActions.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    secondaryActions.forEach { action ->
                        TabletActionButton(
                            action = action,
                            onClick = { onAction(action) },
                            windowInfo = windowInfo,
                            isPrimary = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DesktopActionButtons(
    availableActions: List<Action>,
    onAction: (Action) -> Unit,
    windowInfo: WindowInfo
) {
    // Desktop: Use original layout but with responsive spacing
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(windowInfo.getSpacing())
    ) {
        Text(
            text = "Choose Your Action",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        
        val primaryActions = availableActions.filter { it in listOf(Action.HIT, Action.STAND) }
        val secondaryActions = availableActions.filter { it !in listOf(Action.HIT, Action.STAND) }
        
        if (primaryActions.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                primaryActions.forEach { action ->
                    DesktopActionButton(
                        action = action,
                        onClick = { onAction(action) },
                        windowInfo = windowInfo,
                        isPrimary = true
                    )
                }
            }
        }
        
        if (secondaryActions.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                secondaryActions.forEach { action ->
                    DesktopActionButton(
                        action = action,
                        onClick = { onAction(action) },
                        windowInfo = windowInfo,
                        isPrimary = false
                    )
                }
            }
        }
    }
}

@Composable
private fun MobileActionButton(
    action: Action,
    onClick: () -> Unit,
    windowInfo: WindowInfo
) {
    val buttonColor = getActionButtonColor(action)
    val buttonHeight = windowInfo.getButtonHeight()
    
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(buttonHeight)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(windowInfo.getCardCornerRadius())
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(windowInfo.getCardCornerRadius()),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 3.dp
        )
    ) {
        Text(
            text = action.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun TabletActionButton(
    action: Action,
    onClick: () -> Unit,
    windowInfo: WindowInfo,
    isPrimary: Boolean = false
) {
    val buttonColor = getActionButtonColor(action)
    val buttonWidth = if (isPrimary) 130.dp else 110.dp
    val buttonHeight = if (isPrimary) 52.dp else 46.dp
    val textSize = if (isPrimary) 17.sp else 15.sp
    
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(buttonWidth)
            .height(buttonHeight)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(windowInfo.getCardCornerRadius())
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(windowInfo.getCardCornerRadius()),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 3.dp
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

@Composable
private fun DesktopActionButton(
    action: Action,
    onClick: () -> Unit,
    windowInfo: WindowInfo,
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
                shape = RoundedCornerShape(windowInfo.getCardCornerRadius())
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(windowInfo.getCardCornerRadius()),
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