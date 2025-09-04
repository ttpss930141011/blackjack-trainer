package org.ttpss930141011.bj.presentation.components.feedback

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
import org.ttpss930141011.bj.presentation.layout.Layout
import org.ttpss930141011.bj.presentation.layout.ScreenWidth
import org.ttpss930141011.bj.presentation.layout.isCompact
import org.ttpss930141011.bj.presentation.layout.isMedium
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.presentation.design.AppConstants

@Composable
fun NotificationSystem(
    notifications: List<NotificationItem>,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Layout { screenWidth ->
        when {
            screenWidth.isCompact -> MobileNotifications(
                notifications = notifications,
                onDismiss = onDismiss,
                modifier = modifier,
                screenWidth = screenWidth
            )
            screenWidth.isMedium -> TabletNotifications(
                notifications = notifications,
                onDismiss = onDismiss,
                modifier = modifier,
                screenWidth = screenWidth
            )
            else -> DesktopNotifications(
                notifications = notifications,
                onDismiss = onDismiss,
                modifier = modifier,
                screenWidth = screenWidth
            )
        }
    }
}

@Composable
private fun MobileNotifications(
    notifications: List<NotificationItem>,
    onDismiss: (String) -> Unit,
    modifier: Modifier,
    screenWidth: ScreenWidth
) {
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
                    animationSpec = tween(AppConstants.Animation.FAST.toInt())
                ) + fadeOut()
            ) {
                MobileNotificationCard(
                    notification = notification,
                    onDismiss = { onDismiss(notification.id) },
                    screenWidth = screenWidth
                )
            }
        }
    }
}

@Composable
private fun TabletNotifications(
    notifications: List<NotificationItem>,
    onDismiss: (String) -> Unit,
    modifier: Modifier,
    screenWidth: ScreenWidth
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(Tokens.padding(screenWidth)),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Tokens.spacing(screenWidth)),
            horizontalAlignment = Alignment.End
        ) {
            notifications.takeLast(2).forEachIndexed { index, notification ->
                val isTop = index == notifications.takeLast(2).size - 1
                
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn(),
                    exit = slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(AppConstants.Animation.FAST.toInt())
                    ) + fadeOut()
                ) {
                    TabletNotificationCard(
                        notification = notification,
                        onDismiss = { onDismiss(notification.id) },
                        isTop = isTop,
                        screenWidth = screenWidth
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
    modifier: Modifier,
    screenWidth: ScreenWidth
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(Tokens.padding(screenWidth)),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy((-Tokens.spacing(screenWidth))),
            horizontalAlignment = Alignment.End
        ) {
            notifications.takeLast(3).forEachIndexed { index, notification ->
                val isTop = index == notifications.takeLast(3).size - 1
                val zIndex = (index + 1).toFloat()
                val alpha = if (index == 0 && notifications.size > 1) AppConstants.Alpha.SEMI_TRANSPARENT else 1f
                
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn(),
                    exit = slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(AppConstants.Animation.FAST.toInt())
                    ) + fadeOut(),
                    modifier = Modifier.zIndex(zIndex)
                ) {
                    DesktopNotificationCard(
                        notification = notification,
                        isTop = isTop,
                        alpha = alpha,
                        onDismiss = { onDismiss(notification.id) },
                        screenWidth = screenWidth
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
    screenWidth: ScreenWidth
) {
    val feedback = notification.feedback
    
    LaunchedEffect(notification.id) {
        delay(AppConstants.Animation.NOTIFICATION_TIMEOUT_MOBILE)
        onDismiss()
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Tokens.padding(screenWidth)),
        colors = CardDefaults.cardColors(
            containerColor = if (feedback.isCorrect) {
                Color(0xFF4CAF50).copy(alpha = AppConstants.Alpha.NOTIFICATION_BACKGROUND)
            } else {
                Color(0xFFF44336).copy(alpha = AppConstants.Alpha.NOTIFICATION_BACKGROUND)
            }
        ),
        shape = RoundedCornerShape(Tokens.cornerRadius(screenWidth)),
        elevation = CardDefaults.cardElevation(defaultElevation = Tokens.Space.s)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Tokens.padding(screenWidth)),
            horizontalArrangement = Arrangement.spacedBy(Tokens.spacing(screenWidth)),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = if (feedback.isCorrect) "✅" else "❌",
                fontSize = 24.sp
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Tokens.Space.xs)
            ) {
                Text(
                    text = if (feedback.isCorrect) "Correct!" else "Not Optimal",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Text(
                    text = feedback.explanation,
                    color = Color.White.copy(alpha = AppConstants.Alpha.HIGHLIGHTED),
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
            
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.size(Tokens.Size.touchTarget),
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
    screenWidth: ScreenWidth
) {
    val feedback = notification.feedback
    
    if (isTop) {
        LaunchedEffect(notification.id) {
            delay(AppConstants.Animation.NOTIFICATION_TIMEOUT_DESKTOP)
            onDismiss()
        }
    }
    
    Card(
        modifier = Modifier.width(Tokens.notificationWidth(screenWidth) ?: 350.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (feedback.isCorrect) {
                Color(0xFF4CAF50).copy(alpha = AppConstants.Alpha.NOTIFICATION_BACKGROUND)
            } else {
                Color(0xFFF44336).copy(alpha = AppConstants.Alpha.NOTIFICATION_BACKGROUND)
            }
        ),
        shape = RoundedCornerShape(Tokens.cornerRadius(screenWidth)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isTop) Tokens.Space.s else Tokens.Space.xs)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Tokens.Space.l),
            horizontalArrangement = Arrangement.spacedBy(Tokens.spacing(screenWidth)),
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
                        color = Color.White.copy(alpha = AppConstants.Alpha.HIGHLIGHTED),
                        fontSize = 13.sp,
                        lineHeight = 16.sp
                    )
                }
            }
            
            if (isTop) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(Tokens.Size.iconLarge),
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
    alpha: Float,
    onDismiss: () -> Unit,
    screenWidth: ScreenWidth
) {
    val feedback = notification.feedback
    
    if (isTop) {
        LaunchedEffect(notification.id) {
            delay(AppConstants.Animation.NOTIFICATION_TIMEOUT_DESKTOP)
            onDismiss()
        }
    }
    
    Card(
        modifier = Modifier
            .width(Tokens.notificationWidth(screenWidth) ?: 300.dp)
            .shadow(
                elevation = Tokens.Space.s,
                shape = RoundedCornerShape(Tokens.cornerRadius(screenWidth))
            )
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = if (feedback.isCorrect) {
                Color(0xFF4CAF50).copy(alpha = AppConstants.Alpha.NOTIFICATION_BACKGROUND)
            } else {
                Color(0xFFF44336).copy(alpha = AppConstants.Alpha.NOTIFICATION_BACKGROUND)
            }
        ),
        shape = RoundedCornerShape(Tokens.cornerRadius(screenWidth)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isTop) Tokens.Space.s else Tokens.Space.xs)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Tokens.Space.l),
            horizontalArrangement = Arrangement.spacedBy(Tokens.spacing(screenWidth)),
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
                        color = Color.White.copy(alpha = AppConstants.Alpha.HIGHLIGHTED),
                        fontSize = 12.sp,
                        lineHeight = 14.sp
                    )
                }
            }
            
            if (isTop) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(Tokens.Size.iconMedium),
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