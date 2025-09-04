package org.ttpss930141011.bj.presentation.components.feedback

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import org.ttpss930141011.bj.domain.DecisionFeedback

@Composable
fun FeedbackSystem(
    feedback: DecisionFeedback?,
    onFeedbackConsumed: () -> Unit
) {
    // 環境反饋系統 - 不顯示覆蓋式通知
    // 反饋將應用到遊戲元素（ActionButton、卡牌等）上
    LaunchedEffect(feedback) {
        feedback?.let { 
            delay(1500) // 給予學習時間
            onFeedbackConsumed()
        }
    }
}

