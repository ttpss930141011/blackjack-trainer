package org.ttpss930141011.bj.presentation

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.domain.*
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.presentation.layout.Layout
import org.ttpss930141011.bj.presentation.layout.isCompact
import org.ttpss930141011.bj.presentation.components.*
import org.ttpss930141011.bj.presentation.components.feedback.*
import org.ttpss930141011.bj.presentation.components.dialogs.*
import org.ttpss930141011.bj.presentation.components.navigation.Header
import org.ttpss930141011.bj.presentation.design.GameStatusColors
import org.ttpss930141011.bj.presentation.design.AppConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasinoGameScreen(
    gameRules: GameRules = GameRules(),
    onShowSettings: () -> Unit = {},
    onRulesChanged: (GameRules) -> Unit = {}
) {
    var showSettingsSheet by remember { mutableStateOf(false) }
    val viewModel = remember { GameViewModel() }
    val notificationState = rememberNotificationState()
    
    LaunchedEffect(gameRules) {
        viewModel.initializeGame(gameRules, Player(id = AppConstants.Defaults.PLAYER_ID, chips = AppConstants.Defaults.PLAYER_STARTING_CHIPS))
    }
    
    // Handle feedback notifications
    LaunchedEffect(viewModel.feedback) {
        viewModel.feedback?.let { feedback ->
            notificationState.addNotification(feedback)
            viewModel.clearFeedback() // Clear after adding to notifications
        }
    }
    
    val game = viewModel.game
    val currentPlayer = game?.player ?: Player(id = AppConstants.Defaults.PLAYER_ID, chips = AppConstants.Defaults.PLAYER_STARTING_CHIPS)
    
    Layout { screenWidth ->
        if (screenWidth.isCompact) {
            // Mobile: BottomSheetScaffold
            val bottomSheetState = rememberBottomSheetScaffoldState()
            
            LaunchedEffect(showSettingsSheet) {
                if (showSettingsSheet) {
                    bottomSheetState.bottomSheetState.expand()
                } else {
                    bottomSheetState.bottomSheetState.partialExpand()
                }
            }
            
            BottomSheetScaffold(
                scaffoldState = bottomSheetState,
                sheetShape = RoundedCornerShape(topStart = Tokens.cornerRadius(screenWidth), topEnd = Tokens.cornerRadius(screenWidth)),
                sheetContainerColor = GameStatusColors.casinoGreen,
                sheetPeekHeight = 0.dp,
                sheetContent = {
                    SettingsSheetContent(
                        currentRules = gameRules,
                        onRulesChanged = { newRules -> 
                            onRulesChanged(newRules)
                            showSettingsSheet = false 
                        },
                        onClose = { showSettingsSheet = false }
                    )
                }
            ) {
                CasinoGameContent(
                    game = game,
                    viewModel = viewModel,
                    currentPlayer = currentPlayer,
                    notificationState = notificationState,
                    onShowSettings = { showSettingsSheet = true }
                )
            }
        } else {
            // Desktop: Full screen
            CasinoGameContent(
                game = game,
                viewModel = viewModel,
                currentPlayer = currentPlayer,
                notificationState = notificationState,
                onShowSettings = onShowSettings
            )
        }
    }
}

@Composable
private fun CasinoGameContent(
    game: Game?,
    viewModel: GameViewModel,
    currentPlayer: Player,
    notificationState: NotificationState,
    onShowSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = GameStatusColors.casinoBackgroundGradient
                )
            )
    ) {
        Layout { contentScreenWidth ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Tokens.padding(contentScreenWidth)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Header(
                balance = currentPlayer.chips,
                onShowSettings = onShowSettings,
                hasStats = viewModel.sessionStats.totalRounds > 0,
                onShowSummary = { viewModel.showGameSummary() }
            )
            
            Spacer(modifier = Modifier.height(Tokens.Space.l))
            
            // Casino Table Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = GameStatusColors.casinoGreen
                ),
                shape = RoundedCornerShape(Tokens.cornerRadius(contentScreenWidth)),
                elevation = CardDefaults.cardElevation(defaultElevation = Tokens.Space.m)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(Tokens.cornerRadius(contentScreenWidth)))
                        .background(
                            brush = Brush.radialGradient(
                                colors = GameStatusColors.casinoTableGradient,
                                radius = 800f
                            )
                        )
                        .padding(Tokens.Space.xl)
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

