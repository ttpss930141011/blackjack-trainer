package org.ttpss930141011.bj.presentation.components.feedback

import androidx.compose.runtime.*
import org.ttpss930141011.bj.domain.valueobjects.DecisionFeedback

data class NotificationItem(
    val id: String,
    val feedback: DecisionFeedback,
    val timestamp: Long = kotlin.random.Random.nextLong()
)

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