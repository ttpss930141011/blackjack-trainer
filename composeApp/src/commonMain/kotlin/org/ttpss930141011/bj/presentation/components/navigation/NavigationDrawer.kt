package org.ttpss930141011.bj.presentation.components.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class NavigationPage {
    HOME, STRATEGY, HISTORY, STATISTICS, SETTINGS
}

data class NavigationItem(
    val title: String,
    val page: NavigationPage?,
    val isSpecial: Boolean = false // For spacers or special items
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
    
    val mainNavigationItems = listOf(
        NavigationItem("Home", NavigationPage.HOME),
        NavigationItem("Strategy Chart", NavigationPage.STRATEGY),
        NavigationItem("Decision History", NavigationPage.HISTORY),
        NavigationItem("Statistics", NavigationPage.STATISTICS)
    )
    
    val settingsItems = listOf(
        NavigationItem("Settings", NavigationPage.SETTINGS)
    )
    
    Surface(
        modifier = modifier.width(drawerWidth),
        color = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Header
            item {
                DrawerHeader()
            }
            
            // Main Navigation Items
            item {
                NavigationSection(
                    title = "Navigation",
                    items = mainNavigationItems,
                    currentPage = currentPage,
                    onItemClick = { page ->
                        page?.let { onPageSelected(it) }
                        onCloseDrawer()
                    }
                )
            }
            
            // Spacer
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Settings Section
            item {
                NavigationSection(
                    title = "Settings",
                    items = settingsItems,
                    currentPage = currentPage,
                    onItemClick = { page ->
                        page?.let { onPageSelected(it) }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Blackjack Strategy Trainer",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = "Learn Basic Strategy",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
    
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    )
}

@Composable
private fun NavigationSection(
    title: String,
    items: List<NavigationItem>,
    currentPage: NavigationPage,
    onItemClick: (NavigationPage?) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        
        items.forEach { item ->
            NavigationItem(
                item = item,
                selected = item.page?.let { currentPage == it } ?: false,
                onClick = { onItemClick(item.page) }
            )
        }
    }
}

@Composable
private fun NavigationItem(
    item: NavigationItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(text = item.title) },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
    )
}