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
import org.ttpss930141011.bj.presentation.components.history.*
import org.ttpss930141011.bj.presentation.components.dialogs.*
import org.ttpss930141011.bj.presentation.components.navigation.Header
import org.ttpss930141011.bj.presentation.components.navigation.GameNavigationDrawer
import org.ttpss930141011.bj.presentation.components.navigation.GameNavigationBar
import org.ttpss930141011.bj.presentation.components.navigation.NavigationPage
import org.ttpss930141011.bj.presentation.pages.*
import org.ttpss930141011.bj.presentation.design.GameStatusColors
import org.ttpss930141011.bj.presentation.design.AppConstants
import org.ttpss930141011.bj.presentation.design.CasinoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasinoGameScreen(
    gameRules: GameRules = GameRules(),
    onShowSettings: () -> Unit = {},
    onRulesChanged: (GameRules) -> Unit = {}
) {
    val viewModel = remember { GameViewModel() }
    val notificationState = rememberNotificationState()
    
    // Navigation state (HOME is default)
    var currentPage by remember { mutableStateOf<NavigationPage?>(NavigationPage.HOME) }
    
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
            // Mobile: NavigationBar at bottom
            Scaffold(
                bottomBar = {
                    GameNavigationBar(
                        currentPage = currentPage ?: NavigationPage.HOME,
                        onPageSelected = { currentPage = it }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (currentPage) {
                        NavigationPage.STRATEGY -> Layout { screenWidth ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(CasinoTheme.PageBackground)
                            ) {
                                StrategyPage(
                                    gameRules = gameRules,
                                    screenWidth = screenWidth
                                )
                            }
                        }
                        NavigationPage.HISTORY -> Layout { screenWidth ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(CasinoTheme.PageBackground)
                            ) {
                                HistoryPage(
                                    decisionHistory = viewModel.getRecentDecisions(),
                                    onClearHistory = { viewModel.clearAllLearningData() },
                                    screenWidth = screenWidth
                                )
                            }
                        }
                        NavigationPage.STATISTICS -> Layout { screenWidth ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(CasinoTheme.PageBackground)
                            ) {
                                StatisticsPage(
                                    scenarioStats = viewModel.getScenarioStats(),
                                    screenWidth = screenWidth
                                )
                            }
                        }
                        NavigationPage.SETTINGS -> Layout { screenWidth ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(CasinoTheme.PageBackground)
                            ) {
                                SettingsPage(
                                    screenWidth = screenWidth
                                )
                            }
                        }
                        NavigationPage.HOME, null -> {
                            // Default to game content (Home)
                            CasinoGameContent(
                                game = game,
                                viewModel = viewModel,
                                currentPlayer = currentPlayer,
                                feedback = viewModel.feedback,
                                onShowSettings = onShowSettings,
                                currentPage = currentPage,
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
                gameRules = gameRules,
                decisionHistory = viewModel.getRecentDecisions(),
                scenarioStats = viewModel.getScenarioStats(),
                onClearHistory = { viewModel.clearAllLearningData() },
                onPageSelected = { currentPage = it },
                drawerState = drawerState
            ) {
                when (currentPage) {
                    NavigationPage.STRATEGY -> Layout { screenWidth ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(CasinoTheme.PageBackground)
                        ) {
                            StrategyPage(
                                gameRules = gameRules,
                                screenWidth = screenWidth
                            )
                        }
                    }
                    NavigationPage.HISTORY -> Layout { screenWidth ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(CasinoTheme.PageBackground)
                        ) {
                            HistoryPage(
                                decisionHistory = viewModel.getRecentDecisions(),
                                onClearHistory = { viewModel.clearAllLearningData() },
                                screenWidth = screenWidth
                            )
                        }
                    }
                    NavigationPage.STATISTICS -> Layout { screenWidth ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(CasinoTheme.PageBackground)
                        ) {
                            StatisticsPage(
                                scenarioStats = viewModel.getScenarioStats(),
                                screenWidth = screenWidth
                            )
                        }
                    }
                    NavigationPage.SETTINGS -> Layout { screenWidth ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(CasinoTheme.PageBackground)
                        ) {
                            SettingsPage(
                                screenWidth = screenWidth
                            )
                        }
                    }
                    NavigationPage.HOME, null -> {
                        // Default to game content (Home)
                        CasinoGameContent(
                            game = game,
                            viewModel = viewModel,
                            currentPlayer = currentPlayer,
                            feedback = viewModel.feedback,
                            onShowSettings = onShowSettings,
                            currentPage = currentPage,
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

@Composable
private fun CasinoGameContent(
    game: Game?,
    viewModel: GameViewModel,
    currentPlayer: Player,
    feedback: DecisionFeedback?,
    onShowSettings: () -> Unit,
    currentPage: NavigationPage?,
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
                onShowSettings = onShowSettings,
                hasStats = viewModel.sessionStats.totalRounds > 0,
                onShowSummary = { viewModel.showGameSummary() },
                currentPage = currentPage,
                drawerButton = drawerState?.let { drawer ->
                    {
                        HistoryDrawerButton(
                            decisionCount = viewModel.getRecentDecisions().size,
                            onOpenDrawer = {
                                scope.launch { 
                                    drawer.open() 
                                }
                            }
                        )
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 遊戲區域直接填滿剩餘空間，移除多餘包裝
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

@Composable
fun GameWithNavigationDrawer(
    currentPage: NavigationPage?,
    gameRules: GameRules,
    decisionHistory: List<org.ttpss930141011.bj.domain.valueobjects.DecisionRecord>,
    scenarioStats: Map<String, org.ttpss930141011.bj.infrastructure.ScenarioStats>,
    onClearHistory: () -> Unit,
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

