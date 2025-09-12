package org.ttpss930141011.bj.presentation.components.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.presentation.design.CasinoTheme

enum class NavigationPage {
    HOME, STRATEGY, HISTORY, SETTINGS
}

data class NavigationItem(
    val title: String,
    val page: NavigationPage?,
    val icon: ImageVector
)

@Composable
fun GameNavigationDrawer(
    currentPage: NavigationPage,
    onPageSelected: (NavigationPage?) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Use fixed width for multiplatform compatibility
    val drawerWidth = 280.dp
    
    val allNavigationItems = listOf(
        NavigationItem("Home", NavigationPage.HOME, Icons.Filled.Home),
        NavigationItem("Strategy Chart", NavigationPage.STRATEGY, Icons.Filled.PlaylistPlay),
        NavigationItem("Decision History", NavigationPage.HISTORY, Icons.Filled.History),
        NavigationItem("Settings", NavigationPage.SETTINGS, Icons.Filled.Settings)
    )
    
    Surface(
        modifier = modifier.width(drawerWidth),
        color = CasinoTheme.NavigationBackground  // Dark green background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Header
            item {
                DrawerHeader()
            }
            
            // All Navigation Items (no sections)
            items(allNavigationItems) { item ->
                NavigationItem(
                    item = item,
                    selected = currentPage == item.page,
                    onClick = { 
                        item.page?.let { onPageSelected(it) }
                        onCloseDrawer() 
                    }
                )
            }
        }
    }
}

@Composable
private fun DrawerHeader() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Blackjack Trainer",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 16.dp),
            color = Color.White.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun NavigationItem(
    item: NavigationItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title
            )
        },
        label = { 
            Text(
                text = item.title,
                color = if (selected) Color.White else CasinoTheme.NavigationUnselected,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = CasinoTheme.NavigationSelected,
            selectedTextColor = Color.White,
            unselectedTextColor = CasinoTheme.NavigationUnselected,
            selectedIconColor = Color.White,
            unselectedIconColor = CasinoTheme.NavigationUnselected
        )
    )
}