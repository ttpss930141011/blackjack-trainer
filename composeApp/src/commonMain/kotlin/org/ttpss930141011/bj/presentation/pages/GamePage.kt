package org.ttpss930141011.bj.presentation.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.presentation.design.CasinoTheme
import org.ttpss930141011.bj.presentation.layout.ScreenWidth
import org.ttpss930141011.bj.presentation.components.dialogs.GamePhaseManager
import org.ttpss930141011.bj.presentation.components.navigation.NavigationPage
import org.ttpss930141011.bj.presentation.components.feedback.PersistentFeedbackToast

/**
 * Main game page component that displays the blackjack game interface
 * 
 * Provides the core game experience with responsive design and proper game state management.
 * This page is focused solely on the game content without navigation concerns.
 */
@Composable
fun GamePage(
    game: Game?,
    viewModel: GameViewModel,
    currentPlayer: Player,
    feedback: DecisionFeedback?,
    currentPage: NavigationPage?,
    screenWidth: ScreenWidth,
    feedbackNotificationEnabled: Boolean = true,
    feedbackDurationSeconds: Float = 2.5f,
    modifier: Modifier = Modifier
) {
    // Direct background without unnecessary gradients for better performance
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CasinoTheme.CasinoBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Game area fills remaining space directly
            game?.let { currentGame ->
                GamePhaseManager(
                    game = currentGame,
                    viewModel = viewModel,
                    feedback = feedback,
                    screenWidth = screenWidth
                )

                // Error handling with auto-dismiss
                viewModel.errorMessage?.let { error ->
                    LaunchedEffect(error) {
                        kotlinx.coroutines.delay(3000)
                        viewModel.clearError()
                    }
                }

                // Rule change notification with auto-dismiss
                viewModel.uiStateManager.ruleChangeNotification?.let { notification ->
                    LaunchedEffect(notification) {
                        kotlinx.coroutines.delay(5000) // Show longer for rule changes
                        viewModel.uiStateManager.dismissRuleChangeNotification()
                    }
                }
            }
        }

        // Persistent feedback toast overlay - shows feedback across phase transitions
        PersistentFeedbackToast(
            feedback = feedback,
            durationSeconds = feedbackDurationSeconds,
            enabled = feedbackNotificationEnabled,
            onFeedbackConsumed = { viewModel.clearFeedback() },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Rule change notification overlay
        viewModel.uiStateManager.ruleChangeNotification?.let { notification ->
            RuleChangeNotificationToast(
                message = notification,
                onDismiss = { viewModel.uiStateManager.dismissRuleChangeNotification() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

/**
 * Rule change notification toast component
 */
@Composable
private fun RuleChangeNotificationToast(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(0.9f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.weight(1f)
            )

            TextButton(onClick = onDismiss) {
                Text(
                    text = "OK",
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}