package org.ttpss930141011.bj.presentation.components.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.ttpss930141011.bj.domain.valueobjects.DecisionRecord
import org.ttpss930141011.bj.domain.valueobjects.GameRules
import org.ttpss930141011.bj.domain.valueobjects.ScenarioErrorStat
import org.ttpss930141011.bj.presentation.layout.Layout
import org.ttpss930141011.bj.presentation.layout.ScreenWidth
import org.ttpss930141011.bj.presentation.design.Tokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameWithHistoryDrawer(
    gameRules: GameRules,
    decisionHistory: List<DecisionRecord>,
    scenarioStats: List<ScenarioErrorStat>,
    onClearHistory: () -> Unit,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    modifier: Modifier = Modifier,
    gameContent: @Composable () -> Unit
) {
    Layout { screenWidth ->
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                HistoryDrawerContent(
                    gameRules = gameRules,
                    decisionHistory = decisionHistory,
                    scenarioStats = scenarioStats,
                    onClearHistory = onClearHistory,
                    screenWidth = screenWidth
                )
            },
            modifier = modifier
        ) {
            gameContent()
        }
    }
}

@Composable
fun HistoryDrawerButton(
    decisionCount: Int,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Layout { screenWidth ->
        IconButton(
            onClick = onOpenDrawer,
            modifier = modifier
        ) {
            BadgedBox(
                badge = {
                    if (decisionCount > 0) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(decisionCount.toString())
                        }
                    }
                }
            ) {
                Text(
                    text = "☰",
                    fontSize = Tokens.iconSize(screenWidth).value.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun HistoryDrawerContent(
    gameRules: GameRules,
    decisionHistory: List<DecisionRecord>,
    scenarioStats: List<ScenarioErrorStat>,
    onClearHistory: () -> Unit,
    screenWidth: ScreenWidth
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    
    ModalDrawerSheet(
        modifier = Modifier.fillMaxWidth(
            if (screenWidth == ScreenWidth.COMPACT) 1f else 0.85f
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(Tokens.padding(screenWidth))
        ) {
            // Header with title
            Text(
                text = "Learning Center",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(Tokens.spacing(screenWidth)))
            
            // Tab Row for sections
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth()
            ) {
                val tabTitles = listOf("Strategy", "History", "Stats")
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    )
                }
            }
            
            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> StrategySection(
                        gameRules = gameRules,
                        screenWidth = screenWidth,
                        modifier = Modifier.fillMaxSize()
                    )
                    1 -> HistorySection(
                        decisionHistory = decisionHistory,
                        onClearHistory = onClearHistory,
                        screenWidth = screenWidth,
                        modifier = Modifier.fillMaxSize()
                    )
                    2 -> StatisticsSection(
                        scenarioStats = scenarioStats,
                        screenWidth = screenWidth,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}