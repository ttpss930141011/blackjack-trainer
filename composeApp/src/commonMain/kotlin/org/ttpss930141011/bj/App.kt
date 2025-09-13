package org.ttpss930141011.bj

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.application.ApplicationService
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.presentation.layout.Layout
import org.ttpss930141011.bj.presentation.components.feedback.*
import org.ttpss930141011.bj.presentation.components.navigation.Header
import org.ttpss930141011.bj.presentation.components.navigation.NavigationPage
import org.ttpss930141011.bj.presentation.pages.*
import org.ttpss930141011.bj.presentation.design.AppConstants
import org.ttpss930141011.bj.presentation.design.CasinoTheme
import org.ttpss930141011.bj.presentation.design.CasinoColorScheme

/**
 * Main application composable
 * 
 * Provides the complete casino game application with navigation,
 * applying the unified casino theme throughout the application.
 * Manages game state through GameViewModel and handles screen navigation between
 * game, history, statistics, and settings screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    val viewModel = remember { GameViewModel() }
    val notificationState = rememberNotificationState()

    // Navigation state (HOME is default)
    var currentPage by remember { mutableStateOf<NavigationPage?>(NavigationPage.HOME) }
    
    // Menu state
    var isMenuExpanded by remember { mutableStateOf(false) }

    // Feedback notification settings
    var feedbackNotificationEnabled by remember { mutableStateOf(true) }
    var feedbackDurationSeconds by remember { mutableStateOf(2.5f) }

    // Get current game rules from user preferences (persistent storage)
    val currentGameRules = viewModel.userPreferences.preferredRules

    // Initialize application services and game only once
    LaunchedEffect(Unit) {
        // Initialize application-level services
        ApplicationService.getInstance().initialize()
        
        // Initialize game with user's preferred rules
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

    MaterialTheme(colorScheme = CasinoColorScheme) {
        Layout { screenWidth ->
            // Single layout approach - no more compact/expanded split
            Scaffold(
                containerColor = CasinoTheme.PageBackground
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CasinoTheme.PageBackground)
                        .padding(paddingValues)
                ) {
                        // Header for all pages with integrated navigation menu
                        Header(
                            balance = currentPlayer.chips,
                            currentPage = currentPage,
                            onBackClick = if (currentPage != NavigationPage.HOME) {
                                { currentPage = NavigationPage.HOME }
                            } else null,
                            isMenuExpanded = isMenuExpanded,
                            onMenuExpandedChange = { isMenuExpanded = it },
                            onNavigate = { page -> currentPage = page }
                        )

                        when (currentPage) {
                            NavigationPage.STRATEGY -> {
                                StrategyPage(
                                    gameRules = currentGameRules,
                                    screenWidth = screenWidth
                                )
                            }

                            NavigationPage.HISTORY -> {
                                // 使用新的載入方式
                                LaunchedEffect(Unit) {
                                    viewModel.loadRecentRounds()
                                }
                                
                                HistoryPage(
                                    roundHistory = viewModel.recentRounds,
                                    screenWidth = screenWidth
                                )
                            }

                            NavigationPage.SETTINGS -> {
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
                                GamePage(
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
            }
        }
    }