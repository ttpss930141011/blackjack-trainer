package org.ttpss930141011.bj.presentation.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

/**
 * Material 3 NavigationMenu component
 * Replaces bottom navigation bar with a clean dropdown menu
 * Contains Strategy, History, and Settings options
 */
@Composable
fun NavigationMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onNavigate: (NavigationPage) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        // Strategy menu item
        DropdownMenuItem(
            text = { Text("Strategy") },
            leadingIcon = { 
                Icon(
                    imageVector = Icons.Filled.PlaylistPlay,
                    contentDescription = "Strategy"
                )
            },
            onClick = {
                onNavigate(NavigationPage.STRATEGY)
                onDismiss()
            }
        )
        
        // History menu item
        DropdownMenuItem(
            text = { Text("History") },
            leadingIcon = { 
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = "History"
                )
            },
            onClick = {
                onNavigate(NavigationPage.HISTORY)
                onDismiss()
            }
        )
        
        // Settings menu item
        DropdownMenuItem(
            text = { Text("Settings") },
            leadingIcon = { 
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
            },
            onClick = {
                onNavigate(NavigationPage.SETTINGS)
                onDismiss()
            }
        )
    }
}

/**
 * Material 3 Menu button for triggering the dropdown menu
 */
@Composable
fun NavigationMenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "Menu",
            tint = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f)
        )
    }
}