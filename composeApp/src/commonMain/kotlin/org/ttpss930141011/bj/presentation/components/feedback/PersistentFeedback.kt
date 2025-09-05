package org.ttpss930141011.bj.presentation.components.feedback

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import org.ttpss930141011.bj.domain.valueobjects.DecisionFeedback

/**
 * Persistent feedback system that shows feedback across phase transitions
 * Solves the problem of ActionButton feedback disappearing too quickly
 */
@Composable
fun PersistentFeedbackToast(
    feedback: DecisionFeedback?,
    durationSeconds: Float = 2.5f,
    enabled: Boolean = true,
    onFeedbackConsumed: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showToast by remember { mutableStateOf(false) }
    var currentFeedback by remember { mutableStateOf<DecisionFeedback?>(null) }
    
    LaunchedEffect(feedback, enabled) {
        if (!enabled) {
            showToast = false
            currentFeedback = null
            return@LaunchedEffect
        }
        
        feedback?.let { newFeedback ->
            currentFeedback = newFeedback
            showToast = true
            
            // Show toast for configurable duration
            delay((durationSeconds * 1000).toLong())
            showToast = false
            
            // Clear after fade out animation
            delay(300)
            if (currentFeedback == newFeedback) {
                currentFeedback = null
                onFeedbackConsumed?.invoke() // Clear feedback from ViewModel
            }
        }
    }
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = enabled && showToast && currentFeedback != null,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(300)
            ) + fadeOut(),
            modifier = Modifier
                .zIndex(10f)
                .padding(top = 80.dp) // Below header
        ) {
            currentFeedback?.let { feedbackData ->
                FeedbackToastCard(feedback = feedbackData)
            }
        }
    }
}

@Composable
private fun FeedbackToastCard(
    feedback: DecisionFeedback
) {
    val backgroundColor = if (feedback.isCorrect) {
        Color(0xFF4CAF50).copy(alpha = 0.95f)
    } else {
        Color(0xFFF44336).copy(alpha = 0.95f)
    }
    
    // Pulsing animation for attention
    val pulseScale by animateFloatAsState(
        targetValue = if (feedback.isCorrect) 1f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Large animated emoji
            Text(
                text = if (feedback.isCorrect) "✅" else "❌",
                fontSize = 32.sp,
                modifier = Modifier.scale(pulseScale)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Action result
                Text(
                    text = "${feedback.playerAction.name} → ${feedback.optimalAction.name}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                // Quick feedback
                Text(
                    text = if (feedback.isCorrect) "Perfect!" else "Consider ${feedback.optimalAction.name}",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }
            
            // Strategy hint indicator  
            if (!feedback.isCorrect) {
                Box(
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(6.dp)
                ) {
                    Text(
                        text = "💡",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

/**
 * Enhanced ActionButton feedback with pre-transition delay
 */
@Composable
fun DelayedTransitionFeedback(
    feedback: DecisionFeedback?,
    onFeedbackShown: () -> Unit
) {
    LaunchedEffect(feedback) {
        feedback?.let {
            // Give time for ActionButton visual feedback to be seen
            delay(800) 
            onFeedbackShown()
        }
    }
}