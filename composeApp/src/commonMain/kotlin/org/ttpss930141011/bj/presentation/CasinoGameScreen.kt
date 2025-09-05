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
import kotlinx.coroutines.launch
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.*
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
    
    // Feedback notification settings
    var feedbackNotificationEnabled by remember { mutableStateOf(true) }
    var feedbackDurationSeconds by remember { mutableStateOf(2.5f) }
    
    LaunchedEffect(gameRules) {
        viewModel.initializeGame(gameRules, Player(id = AppConstants.Defaults.PLAYER_ID, chips = AppConstants.Defaults.PLAYER_STARTING_CHIPS))
    }
    
    // Handle feedback - add to history and manage ambient feedback
    LaunchedEffect(viewModel.feedback) {
        viewModel.feedback?.let { feedback ->
            notificationState.addNotification(feedback)
            // 不立即清除 feedback，讓 ActionButton 可以顯示視覺反饋
        }
    }
    
    // Delayed transition feedback - ensures ActionButton feedback is visible before phase changes
    DelayedTransitionFeedback(
        feedback = viewModel.feedback,
        onFeedbackShown = {
            // Allow time for ActionButton visual feedback, then proceed with phase transition
            // This gives users time to see the feedback before transitioning to dealer turn
        }
    )
    
    // Note: FeedbackSystem removed to prevent race condition with PersistentFeedbackToast
    // PersistentFeedbackToast now handles all feedback timing and cleanup
    
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
                        onClose = { showSettingsSheet = false },
                        feedbackNotificationEnabled = feedbackNotificationEnabled,
                        feedbackDurationSeconds = feedbackDurationSeconds,
                        onFeedbackSettingsChanged = { enabled, duration ->
                            feedbackNotificationEnabled = enabled
                            feedbackDurationSeconds = duration
                        }
                    )
                }
            ) {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                
                GameWithFeedbackDrawer(
                    feedbackHistory = notificationState.notifications,
                    onClearAll = { notificationState.clearAll() },
                    drawerState = drawerState
                ) {
                    CasinoGameContent(
                        game = game,
                        viewModel = viewModel,
                        currentPlayer = currentPlayer,
                        feedback = viewModel.feedback,
                        drawerState = drawerState,
                        feedbackHistory = notificationState.notifications,
                        onShowSettings = { showSettingsSheet = true },
                        feedbackNotificationEnabled = feedbackNotificationEnabled,
                        feedbackDurationSeconds = feedbackDurationSeconds
                    )
                }
            }
        } else {
            // Desktop: Full screen with feedback drawer
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            
            GameWithFeedbackDrawer(
                feedbackHistory = notificationState.notifications,
                onClearAll = { notificationState.clearAll() },
                drawerState = drawerState
            ) {
                CasinoGameContent(
                    game = game,
                    viewModel = viewModel,
                    currentPlayer = currentPlayer,
                    feedback = viewModel.feedback,
                    drawerState = drawerState,
                    feedbackHistory = notificationState.notifications,
                    onShowSettings = onShowSettings,
                    feedbackNotificationEnabled = feedbackNotificationEnabled,
                    feedbackDurationSeconds = feedbackDurationSeconds
                )
            }
        }
    }
}

@Composable
private fun CasinoGameContent(
    game: Game?,
    viewModel: GameViewModel,
    currentPlayer: Player,
    feedback: DecisionFeedback?,
    drawerState: DrawerState,
    feedbackHistory: List<NotificationItem>,
    onShowSettings: () -> Unit,
    feedbackNotificationEnabled: Boolean = true,
    feedbackDurationSeconds: Float = 2.5f
) {
    val scope = rememberCoroutineScope()
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
                onShowSummary = { viewModel.showGameSummary() },
                drawerButton = {
                    FeedbackDrawerButton(
                        feedbackHistory = feedbackHistory,
                        onOpenDrawer = {
                            scope.launch { 
                                drawerState.open() 
                            }
                        }
                    )
                }
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
                            viewModel = viewModel,
                            feedback = feedback
                        )
                        
                        // Error handling with auto-dismiss
                        viewModel.errorMessage?.let { error ->
                            LaunchedEffect(error) {
                                kotlinx.coroutines.delay(3000)
                                viewModel.clearError()
                            }
                        }
                    }
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
        
        if (viewModel.showGameSummary) {
            GameSummaryDialog(
                stats = viewModel.sessionStats,
                onDismiss = { viewModel.hideGameSummary() },
                onBackToMenu = { viewModel.hideGameSummary() }
            )
        }
    }
}

