package org.ttpss930141011.bj.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.domain.*
import org.ttpss930141011.bj.presentation.components.*
import org.ttpss930141011.bj.presentation.shared.GameStatusColors

@Composable
fun CasinoGameScreen(
    gameRules: GameRules = GameRules(),
    onShowSettings: () -> Unit = {}
) {
    val viewModel = remember { GameViewModel() }
    val notificationState = rememberNotificationState()
    
    LaunchedEffect(gameRules) {
        viewModel.initializeGame(gameRules, Player(id = "player1", chips = 1000))
    }
    
    // Handle feedback notifications
    LaunchedEffect(viewModel.feedback) {
        viewModel.feedback?.let { feedback ->
            notificationState.addNotification(feedback)
            viewModel.clearFeedback() // Clear after adding to notifications
        }
    }
    
    val game = viewModel.game
    val currentPlayer = game?.player ?: Player(id = "player1", chips = 1000)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = GameStatusColors.casinoBackgroundGradient
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CasinoHeader(
                balance = currentPlayer.chips,
                onShowSettings = onShowSettings,
                hasStats = viewModel.sessionStats.totalRounds > 0,
                onShowSummary = { viewModel.showGameSummary() }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Casino Table Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = GameStatusColors.casinoGreen
                ),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.radialGradient(
                                colors = GameStatusColors.casinoTableGradient,
                                radius = 800f
                            )
                        )
                        .padding(24.dp)
                ) {
                    game?.let { currentGame ->
                        GamePhaseManager(
                            game = currentGame,
                            viewModel = viewModel
                        )
                        
                        // Error handling
                        viewModel.errorMessage?.let { error ->
                            LaunchedEffect(error) {
                                viewModel.clearError()
                            }
                        }
                    }
                }
            }
        }
        
        // Notification System
        NotificationSystem(
            notifications = notificationState.notifications,
            onDismiss = { id -> notificationState.dismissNotification(id) }
        )
        
        if (viewModel.showGameSummary) {
            GameSummaryDialog(
                stats = viewModel.sessionStats,
                onDismiss = { viewModel.hideGameSummary() },
                onBackToMenu = { viewModel.hideGameSummary() }
            )
        }
    }
}

