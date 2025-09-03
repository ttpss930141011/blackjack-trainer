package org.ttpss930141011.bj.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import org.ttpss930141011.bj.domain.DecisionFeedback

data class NotificationItem(
    val id: String,
    val feedback: DecisionFeedback,
    val timestamp: Long = kotlin.random.Random.nextLong()
)

@Composable
fun NotificationStack(
    notifications: List<NotificationItem>,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy((-8).dp), // Overlap by 8dp
            horizontalAlignment = Alignment.End
        ) {
            notifications.takeLast(3).forEachIndexed { index, notification ->
                val isTop = index == notifications.size - 1
                val zIndex = (index + 1).toFloat()
                val scale = 1f - (index * 0.05f) // Slightly smaller for stacked items
                val alpha = if (index == 0 && notifications.size > 1) 0.7f else 1f
                
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn(),
                    exit = slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(300)
                    ) + fadeOut(),
                    modifier = Modifier.zIndex(zIndex)
                ) {
                    NotificationCard(
                        notification = notification,
                        isTop = isTop,
                        scale = scale,
                        alpha = alpha,
                        onDismiss = { onDismiss(notification.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: NotificationItem,
    isTop: Boolean,
    scale: Float,
    alpha: Float,
    onDismiss: () -> Unit
) {
    val feedback = notification.feedback
    
    // Auto-dismiss after 4 seconds for top notification
    if (isTop) {
        LaunchedEffect(notification.id) {
            delay(4000)
            onDismiss()
        }
    }
    
    Card(
        modifier = Modifier
            .width(300.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = if (feedback.isCorrect) {
                Color(0xFF4CAF50).copy(alpha = 0.95f) // Success green
            } else {
                Color(0xFFF44336).copy(alpha = 0.95f) // Error red
            }
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isTop) 8.dp else 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon
            Text(
                text = if (feedback.isCorrect) "✅" else "❌",
                fontSize = 20.sp
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (feedback.isCorrect) "Correct!" else "Incorrect",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                
                if (isTop) {
                    Text(
                        text = feedback.explanation,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 14.sp
                    )
                }
            }
            
            // Close button (only for top notification)
            if (isTop) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "×",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun rememberNotificationState(): NotificationState {
    return remember { NotificationState() }
}

class NotificationState {
    private var _notifications by mutableStateOf<List<NotificationItem>>(emptyList())
    val notifications: List<NotificationItem> get() = _notifications
    
    fun addNotification(feedback: DecisionFeedback) {
        val notification = NotificationItem(
            id = generateId(),
            feedback = feedback
        )
        _notifications = _notifications + notification
        
        // Auto-cleanup old notifications (keep max 5)
        if (_notifications.size > 5) {
            _notifications = _notifications.takeLast(5)
        }
    }
    
    fun dismissNotification(id: String) {
        _notifications = _notifications.filter { it.id != id }
    }
    
    fun clearAll() {
        _notifications = emptyList()
    }
    
    private fun generateId(): String {
        return "notification_${kotlin.random.Random.nextInt(100000)}_${kotlin.random.Random.nextInt(9999)}"
    }
}