package org.ttpss930141011.bj.presentation

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
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
import kotlinx.coroutines.launch
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.*
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.presentation.layout.Layout
import org.ttpss930141011.bj.presentation.layout.ScreenWidth
import org.ttpss930141011.bj.presentation.layout.isCompact
import org.ttpss930141011.bj.presentation.components.*
import org.ttpss930141011.bj.presentation.components.feedback.*
import org.ttpss930141011.bj.presentation.components.history.*
import org.ttpss930141011.bj.presentation.components.dialogs.*
import org.ttpss930141011.bj.presentation.components.navigation.Header
import org.ttpss930141011.bj.presentation.components.navigation.GameNavigationDrawer
import org.ttpss930141011.bj.presentation.components.navigation.GameNavigationBar
import org.ttpss930141011.bj.presentation.components.navigation.NavigationPage
import org.ttpss930141011.bj.presentation.pages.*
import org.ttpss930141011.bj.presentation.design.AppConstants
import org.ttpss930141011.bj.presentation.design.CasinoTheme

/**
 * Main casino game screen with responsive navigation
 * 
 * Provides the main game interface with adaptive navigation based on screen size:
 * - Compact screens: Bottom navigation bar
 * - Large screens: Side navigation drawer
 * 
 * Manages game state through GameViewModel and handles screen navigation between
 * game, history, statistics, and settings screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasinoGameScreen() {
    val viewModel = remember { GameViewModel() }
    val notificationState = rememberNotificationState()

    // Navigation state (HOME is default)
    var currentPage by remember { mutableStateOf<NavigationPage?>(NavigationPage.HOME) }

    // Feedback notification settings
    var feedbackNotificationEnabled by remember { mutableStateOf(true) }
    var feedbackDurationSeconds by remember { mutableStateOf(2.5f) }

    // Get current game rules from user preferences (persistent storage)
    val currentGameRules = viewModel.userPreferences.preferredRules

    // Initialize game only once, using user's preferred rules
    LaunchedEffect(Unit) {
        viewModel.initializeGame(
            currentGameRules,
            Player(id = AppConstants.Defaults.PLAYER_ID, chips = AppConstants.Defaults.PLAYER_STARTING_CHIPS)
        )
    }

    // Reload user preferences when returning to HOME page
    LaunchedEffect(currentPage) {
        if (currentPage == NavigationPage.HOME || currentPage == null) {
            viewModel.loadUserPreferences()
        }
    }

    // Handle rule changes from user preferences (not external gameRules parameter)
    LaunchedEffect(currentGameRules) {
        viewModel.handleRuleChange(currentGameRules)
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
    val currentPlayer = game?.player ?: Player(
        id = AppConstants.Defaults.PLAYER_ID,
        chips = AppConstants.Defaults.PLAYER_STARTING_CHIPS
    )

    Layout { screenWidth ->
        if (screenWidth.isCompact) {
            // Mobile: NavigationBar at bottom (NO drawer for compact)
            Scaffold(
                containerColor = CasinoTheme.PageBackground,
                bottomBar = {
                    GameNavigationBar(
                        currentPage = currentPage ?: NavigationPage.HOME,
                        onPageSelected = { currentPage = it }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CasinoTheme.PageBackground)
                        .padding(paddingValues)
                ) {
                    // Add header for non-Home pages (no drawer button in compact)
                    if (currentPage != NavigationPage.HOME && currentPage != null) {
                        Header(
                            balance = currentPlayer.chips,
                            currentPage = currentPage,
                            drawerButton = null // No drawer in compact layout
                        )
                    }

                    when (currentPage) {
                        NavigationPage.STRATEGY -> Layout { screenWidth ->
                            StrategyPage(
                                gameRules = currentGameRules,
                                screenWidth = screenWidth
                            )
                        }

                        NavigationPage.HISTORY -> Layout { screenWidth ->
                            // 使用新的載入方式
                            LaunchedEffect(Unit) {
                                viewModel.loadRecentRounds()
                            }
                            
                            HistoryPage(
                                roundHistory = viewModel.recentRounds,
                                screenWidth = screenWidth
                            )
                        }

                        NavigationPage.STATISTICS -> Layout { screenWidth ->
                            // Load both scenario statistics and decision history for enhanced analytics
                            LaunchedEffect(Unit) {
                                viewModel.loadScenarioStats()
                                viewModel.loadDecisionHistory()
                            }
                            
                            StatisticsPage(
                                scenarioStats = viewModel.scenarioStats,
                                decisionHistory = viewModel.decisionHistory,
                                screenWidth = screenWidth
                            )
                        }

                        NavigationPage.SETTINGS -> Layout { screenWidth ->
                            // 載入用戶偏好設定
                            LaunchedEffect(Unit) {
                                viewModel.loadUserPreferences()
                            }
                            
                            SettingsPage(
                                userPreferences = viewModel.userPreferences,
                                onPreferencesChanged = { newPreferences ->
                                    viewModel.updateUserPreferences(newPreferences)
                                }
                            )
                        }

                        NavigationPage.HOME, null -> {
                            // Default to game content (Home) - no drawer state in compact
                            CasinoGameContent(
                                game = game,
                                viewModel = viewModel,
                                currentPlayer = currentPlayer,
                                feedback = viewModel.feedback,
                                currentPage = currentPage,
                                screenWidth = screenWidth,
                                feedbackNotificationEnabled = feedbackNotificationEnabled,
                                feedbackDurationSeconds = feedbackDurationSeconds
                            )
                        }
                    }
                }
            }
        } else {
            // Desktop: Full screen with feedback drawer
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            GameWithNavigationDrawer(
                currentPage = currentPage,
                onPageSelected = { currentPage = it },
                drawerState = drawerState
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CasinoTheme.PageBackground) // Consistent background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Add header for non-Home pages  
                        if (currentPage != NavigationPage.HOME && currentPage != null) {
                            Header(
                                balance = currentPlayer.chips,
                                currentPage = currentPage,
                                drawerButton = {
                                    TextButton(
                                        onClick = {
                                            scope.launch { drawerState.open() }
                                        }
                                    ) {
                                        Text(
                                            text = "☰",
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                    }
                                }
                            )
                        }

                        when (currentPage) {
                            NavigationPage.STRATEGY -> Layout { screenWidth ->
                                StrategyPage(
                                    gameRules = currentGameRules,
                                    screenWidth = screenWidth
                                )
                            }

                            NavigationPage.HISTORY -> Layout { screenWidth ->
                                // 使用新的載入方式
                                LaunchedEffect(Unit) {
                                    viewModel.loadRecentRounds()
                                }
                                
                                HistoryPage(
                                    roundHistory = viewModel.recentRounds,
                                    screenWidth = screenWidth
                                )
                            }

                            NavigationPage.STATISTICS -> Layout { screenWidth ->
                                // Load both scenario statistics and decision history for enhanced analytics
                                LaunchedEffect(Unit) {
                                    viewModel.loadScenarioStats()
                                    viewModel.loadDecisionHistory()
                                }
                                
                                StatisticsPage(
                                    scenarioStats = viewModel.scenarioStats,
                                    decisionHistory = viewModel.decisionHistory,
                                    screenWidth = screenWidth
                                )
                            }

                            NavigationPage.SETTINGS -> Layout { screenWidth ->
                                // 載入用戶偏好設定
                                LaunchedEffect(Unit) {
                                    viewModel.loadUserPreferences()
                                }
                                
                                SettingsPage(
                                    userPreferences = viewModel.userPreferences,
                                    onPreferencesChanged = { newPreferences ->
                                        viewModel.updateUserPreferences(newPreferences)
                                    }
                                )
                            }

                            NavigationPage.HOME, null -> {
                                // Default to game content (Home)
                                Layout { screenWidth ->
                                    CasinoGameContent(
                                        game = game,
                                        viewModel = viewModel,
                                        currentPlayer = currentPlayer,
                                        feedback = viewModel.feedback,
                                        currentPage = currentPage,
                                        screenWidth = screenWidth,
                                        feedbackNotificationEnabled = feedbackNotificationEnabled,
                                        feedbackDurationSeconds = feedbackDurationSeconds,
                                        drawerState = drawerState
                                    )
                                }
                            }
                        }
                    }
                }
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
    currentPage: NavigationPage?,
    screenWidth: ScreenWidth,
    feedbackNotificationEnabled: Boolean = true,
    feedbackDurationSeconds: Float = 2.5f,
    drawerState: DrawerState? = null
) {
    val scope = rememberCoroutineScope()
    // 直接使用單色背景，移除多餘漸層
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CasinoTheme.CasinoBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(
                balance = currentPlayer.chips,
                currentPage = currentPage,
                drawerButton = drawerState?.let { drawer ->
                    {
                        HistoryDrawerButton(
                            decisionCount = viewModel.recentRounds.size, // Use recentRounds instead
                            onOpenDrawer = {
                                scope.launch {
                                    drawer.open()
                                }
                            }
                        )
                    }
                }
            )

            // 遊戲區域直接填滿剩餘空間，移除多餘包裝
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

@Composable
fun GameWithNavigationDrawer(
    currentPage: NavigationPage?,
    onPageSelected: (NavigationPage?) -> Unit,
    drawerState: DrawerState,
    gameContent: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            GameNavigationDrawer(
                currentPage = currentPage ?: NavigationPage.HOME, // Fallback for drawer display
                onPageSelected = onPageSelected,
                onCloseDrawer = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        gameContent()
    }
}

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

