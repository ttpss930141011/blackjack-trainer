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
import org.ttpss930141011.bj.presentation.responsive.ResponsiveLayout
import org.ttpss930141011.bj.presentation.responsive.WindowInfo
import org.ttpss930141011.bj.presentation.responsive.getPadding
import org.ttpss930141011.bj.presentation.responsive.getCardCornerRadius

@Composable
fun NotificationSystem(
    notifications: List<NotificationItem>,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ResponsiveLayout { windowInfo ->
        when {
            windowInfo.isCompact -> MobileNotifications(
                notifications = notifications,
                onDismiss = onDismiss,
                windowInfo = windowInfo,
                modifier = modifier
            )
            windowInfo.isMedium -> TabletNotifications(
                notifications = notifications,
                onDismiss = onDismiss,
                windowInfo = windowInfo,
                modifier = modifier
            )
            else -> DesktopNotifications(
                notifications = notifications,
                onDismiss = onDismiss,
                windowInfo = windowInfo,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun MobileNotifications(
    notifications: List<NotificationItem>,
    onDismiss: (String) -> Unit,
    windowInfo: WindowInfo,
    modifier: Modifier
) {
    // Mobile: Bottom sheet style, full width
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        notifications.lastOrNull()?.let { notification ->
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeOut()
            ) {
                MobileNotificationCard(
                    notification = notification,
                    onDismiss = { onDismiss(notification.id) },
                    windowInfo = windowInfo
                )
            }
        }
    }
}

@Composable
private fun TabletNotifications(
    notifications: List<NotificationItem>,
    onDismiss: (String) -> Unit,
    windowInfo: WindowInfo,
    modifier: Modifier
) {
    // Tablet: Bottom-right corner, but larger than mobile
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(windowInfo.getPadding()),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            notifications.takeLast(2).forEachIndexed { index, notification ->
                val isTop = index == notifications.takeLast(2).size - 1
                
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
                    ) + fadeOut()
                ) {
                    TabletNotificationCard(
                        notification = notification,
                        onDismiss = { onDismiss(notification.id) },
                        isTop = isTop,
                        windowInfo = windowInfo
                    )
                }
            }
        }
    }
}

@Composable
private fun DesktopNotifications(
    notifications: List<NotificationItem>,
    onDismiss: (String) -> Unit,
    windowInfo: WindowInfo,
    modifier: Modifier
) {
    // Desktop: Original sonner-style stacking
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(windowInfo.getPadding()),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy((-8).dp),
            horizontalAlignment = Alignment.End
        ) {
            notifications.takeLast(3).forEachIndexed { index, notification ->
                val isTop = index == notifications.takeLast(3).size - 1
                val zIndex = (index + 1).toFloat()
                val scale = 1f - (index * 0.05f)
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
                    DesktopNotificationCard(
                        notification = notification,
                        isTop = isTop,
                        scale = scale,
                        alpha = alpha,
                        onDismiss = { onDismiss(notification.id) },
                        windowInfo = windowInfo
                    )
                }
            }
        }
    }
}

@Composable
private fun MobileNotificationCard(
    notification: NotificationItem,
    onDismiss: () -> Unit,
    windowInfo: WindowInfo
) {
    val feedback = notification.feedback
    
    // Auto-dismiss after 3 seconds on mobile
    LaunchedEffect(notification.id) {
        delay(3000)
        onDismiss()
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = windowInfo.getPadding()),
        colors = CardDefaults.cardColors(
            containerColor = if (feedback.isCorrect) {
                Color(0xFF4CAF50).copy(alpha = 0.95f)
            } else {
                Color(0xFFF44336).copy(alpha = 0.95f)
            }
        ),
        shape = RoundedCornerShape(windowInfo.getCardCornerRadius()),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(windowInfo.getPadding()),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Status Icon
            Text(
                text = if (feedback.isCorrect) "✅" else "❌",
                fontSize = 24.sp
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (feedback.isCorrect) "Correct!" else "Not Optimal",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Text(
                    text = feedback.explanation,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
            
            // Dismiss button
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "×",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TabletNotificationCard(
    notification: NotificationItem,
    onDismiss: () -> Unit,
    isTop: Boolean,
    windowInfo: WindowInfo
) {
    val feedback = notification.feedback
    
    if (isTop) {
        LaunchedEffect(notification.id) {
            delay(4000)
            onDismiss()
        }
    }
    
    Card(
        modifier = Modifier.width(350.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (feedback.isCorrect) {
                Color(0xFF4CAF50).copy(alpha = 0.95f)
            } else {
                Color(0xFFF44336).copy(alpha = 0.95f)
            }
        ),
        shape = RoundedCornerShape(windowInfo.getCardCornerRadius()),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isTop) 8.dp else 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (feedback.isCorrect) "✅" else "❌",
                fontSize = 20.sp
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (feedback.isCorrect) "Correct!" else "Incorrect",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                
                if (isTop) {
                    Text(
                        text = feedback.explanation,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        lineHeight = 16.sp
                    )
                }
            }
            
            if (isTop) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp),
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
private fun DesktopNotificationCard(
    notification: NotificationItem,
    isTop: Boolean,
    scale: Float,
    alpha: Float,
    onDismiss: () -> Unit,
    windowInfo: WindowInfo
) {
    val feedback = notification.feedback
    
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
                shape = RoundedCornerShape(windowInfo.getCardCornerRadius())
            )
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = if (feedback.isCorrect) {
                Color(0xFF4CAF50).copy(alpha = 0.95f)
            } else {
                Color(0xFFF44336).copy(alpha = 0.95f)
            }
        ),
        shape = RoundedCornerShape(windowInfo.getCardCornerRadius()),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isTop) 8.dp else 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (feedback.isCorrect) "✅" else "❌",
                fontSize = 20.sp
            )
            
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