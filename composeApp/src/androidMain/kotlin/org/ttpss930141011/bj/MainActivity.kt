package org.ttpss930141011.bj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import org.ttpss930141011.bj.infrastructure.PlatformContext
import org.ttpss930141011.bj.presentation.components.navigation.NavigationPage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Initialize PlatformContext for Android-specific services (including audio)
        PlatformContext.initialize(this)

        setContent {
            AppWithBackHandler()
        }
    }
}

@Composable
fun AppWithBackHandler() {
    // Track navigation state to handle Android system back button properly
    var currentPage by remember { mutableStateOf<NavigationPage?>(NavigationPage.HOME) }
    
    // Handle Android system back button - this is the key fix
    // When not on HOME page, go back to HOME instead of exiting app
    BackHandler(enabled = currentPage != NavigationPage.HOME && currentPage != null) {
        currentPage = NavigationPage.HOME
    }
    
    // On HOME page, use default back behavior (exit app)
    // No BackHandler here allows system default behavior
    
    App(
        initialPage = currentPage,
        onNavigationChange = { page -> currentPage = page }
    )
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}