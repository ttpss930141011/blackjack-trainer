package org.ttpss930141011.bj.presentation.components.feedback

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.domain.valueobjects.DecisionFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameWithFeedbackDrawer(
    feedbackHistory: List<NotificationItem>,
    onClearAll: () -> Unit,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    modifier: Modifier = Modifier,
    gameContent: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            FeedbackDrawerContent(
                feedbackHistory = feedbackHistory,
                onClearAll = onClearAll
            )
        }
    ) {
        gameContent()
    }
}

@Composable
fun FeedbackDrawerButton(
    feedbackHistory: List<NotificationItem>,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onOpenDrawer,
        modifier = modifier
    ) {
        BadgedBox(
            badge = {
                if (feedbackHistory.isNotEmpty()) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ) {
                        Text("${feedbackHistory.size}")
                    }
                }
            }
        ) {
            Text(
                text = "☰",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun FeedbackDrawerContent(
    feedbackHistory: List<NotificationItem>,
    onClearAll: () -> Unit
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            // 標題區域
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Learning History",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (feedbackHistory.isNotEmpty()) {
                    TextButton(onClick = onClearAll) {
                        Text("Clear All")
                    }
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            if (feedbackHistory.isEmpty()) {
                // 空狀態
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "No learning records yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Start playing to collect feedback",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // 統計摘要
                FeedbackSummarySection(feedbackHistory)
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                // 反饋歷史列表（最新在上）
                LazyColumn {
                    items(feedbackHistory.reversed()) { notification ->
                        FeedbackDrawerItem(
                            feedback = notification.feedback,
                            timestamp = notification.timestamp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FeedbackDrawerItem(
    feedback: DecisionFeedback,
    timestamp: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (feedback.isCorrect) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 結果圖示
                Text(
                    text = if (feedback.isCorrect) "✅" else "❌",
                    fontSize = 18.sp
                )
                
                // 動作摘要
                Text(
                    text = "${feedback.playerAction.name} → ${feedback.optimalAction.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(Modifier.weight(1f))
                
                // 時間戳
                Text(
                    text = formatTimestamp(timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 説明
            Text(
                text = feedback.explanation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun FeedbackSummarySection(feedbackHistory: List<NotificationItem>) {
    val totalFeedback = feedbackHistory.size
    val correctFeedback = feedbackHistory.count { it.feedback.isCorrect }
    val accuracy = if (totalFeedback > 0) (correctFeedback * 100 / totalFeedback) else 0
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatisticItem(
            label = "Total",
            value = "$totalFeedback"
        )
        StatisticItem(
            label = "Accuracy",
            value = "$accuracy%"
        )
        StatisticItem(
            label = "Today",
            value = "${feedbackHistory.count { isToday(it.timestamp) }}"
        )
    }
}

@Composable
fun StatisticItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    // 簡化版時間戳格式化，避免平台特定依賴
    return "記錄 #${(timestamp % 10000)}"
}

private fun isToday(timestamp: Long): Boolean {
    // 簡化版，假設最近的都算今日
    return true
}