package org.ttpss930141011.bj.presentation.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.presentation.components.navigation.NavigationConstants.NAV_LABEL_HISTORY
import org.ttpss930141011.bj.presentation.components.navigation.NavigationConstants.NAV_LABEL_HOME
import org.ttpss930141011.bj.presentation.components.navigation.NavigationConstants.NAV_LABEL_SETTINGS
import org.ttpss930141011.bj.presentation.components.navigation.NavigationConstants.NAV_LABEL_STATISTICS
import org.ttpss930141011.bj.presentation.components.navigation.NavigationConstants.NAV_LABEL_STRATEGY

/**
 * Navigation bar for the blackjack strategy trainer following Material3 best practices.
 * Implements bottom navigation with 5 destinations as per Android NavigationBar guidelines.
 * Order: Strategy, History, Home (center), Statistics, Settings
 */
@Composable
fun GameNavigationBar(
    currentPage: NavigationPage,
    onPageSelected: (NavigationPage) -> Unit
) {
    NavigationBar(
        // 明確指定白色背景，覆蓋 darkColorScheme 設定
        containerColor = Color.White,
        contentColor = Color(0xFF49454F), // Material3 onSurface for light theme
        tonalElevation = 0.dp
    ) {
        // Strategy tab
        NavigationBarItem(
            selected = currentPage == NavigationPage.STRATEGY,
            onClick = { onPageSelected(NavigationPage.STRATEGY) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.PlaylistPlay,
                    contentDescription = NAV_LABEL_STRATEGY
                )
            },
            label = {
                NavigationLabel(
                    text = NAV_LABEL_STRATEGY,
                    selected = currentPage == NavigationPage.STRATEGY
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1976D2), // Material3 primary light
                selectedTextColor = Color(0xFF1976D2),
                unselectedIconColor = Color(0xFF757575), // Material3 onSurfaceVariant light  
                unselectedTextColor = Color(0xFF757575),
                indicatorColor = Color(0xFF1976D2).copy(alpha = 0.12f)
            )
        )
        
        // History tab  
        NavigationBarItem(
            selected = currentPage == NavigationPage.HISTORY,
            onClick = { onPageSelected(NavigationPage.HISTORY) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = NAV_LABEL_HISTORY
                )
            },
            label = {
                NavigationLabel(
                    text = NAV_LABEL_HISTORY,
                    selected = currentPage == NavigationPage.HISTORY
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1976D2), // Material3 primary light
                selectedTextColor = Color(0xFF1976D2),
                unselectedIconColor = Color(0xFF757575), // Material3 onSurfaceVariant light  
                unselectedTextColor = Color(0xFF757575),
                indicatorColor = Color(0xFF1976D2).copy(alpha = 0.12f)
            )
        )
        
        // Home tab (center position)
        NavigationBarItem(
            selected = currentPage == NavigationPage.HOME,
            onClick = { onPageSelected(NavigationPage.HOME) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = NAV_LABEL_HOME
                )
            },
            label = {
                NavigationLabel(
                    text = NAV_LABEL_HOME,
                    selected = currentPage == NavigationPage.HOME
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1976D2), // Material3 primary light
                selectedTextColor = Color(0xFF1976D2),
                unselectedIconColor = Color(0xFF757575), // Material3 onSurfaceVariant light  
                unselectedTextColor = Color(0xFF757575),
                indicatorColor = Color(0xFF1976D2).copy(alpha = 0.12f)
            )
        )
        
        // Statistics tab
        NavigationBarItem(
            selected = currentPage == NavigationPage.STATISTICS,
            onClick = { onPageSelected(NavigationPage.STATISTICS) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.BarChart,
                    contentDescription = NAV_LABEL_STATISTICS
                )
            },
            label = {
                NavigationLabel(
                    text = NAV_LABEL_STATISTICS,
                    selected = currentPage == NavigationPage.STATISTICS
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1976D2), // Material3 primary light
                selectedTextColor = Color(0xFF1976D2),
                unselectedIconColor = Color(0xFF757575), // Material3 onSurfaceVariant light  
                unselectedTextColor = Color(0xFF757575),
                indicatorColor = Color(0xFF1976D2).copy(alpha = 0.12f)
            )
        )
        
        // Settings tab
        NavigationBarItem(
            selected = currentPage == NavigationPage.SETTINGS,
            onClick = { onPageSelected(NavigationPage.SETTINGS) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = NAV_LABEL_SETTINGS
                )
            },
            label = {
                NavigationLabel(
                    text = NAV_LABEL_SETTINGS,
                    selected = currentPage == NavigationPage.SETTINGS
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1976D2), // Material3 primary light
                selectedTextColor = Color(0xFF1976D2),
                unselectedIconColor = Color(0xFF757575), // Material3 onSurfaceVariant light  
                unselectedTextColor = Color(0xFF757575),
                indicatorColor = Color(0xFF1976D2).copy(alpha = 0.12f)
            )
        )
    }
}


@Composable  
private fun NavigationLabel(
    text: String,
    selected: Boolean
) {
    Text(
        text = text,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        fontSize = if (selected) 12.sp else 11.sp
    )
}