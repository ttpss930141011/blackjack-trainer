package org.ttpss930141011.bj.presentation.components.navigation

/**
 * Constants for navigation components to avoid magic numbers and maintain consistency
 */
object NavigationConstants {
    // NavigationBar item count constraint (Material Design recommends 3-5 items)
    const val NAVIGATION_BAR_MIN_ITEMS = 3
    const val NAVIGATION_BAR_MAX_ITEMS = 5
    const val NAVIGATION_BAR_ITEM_COUNT = 5
    
    // NavigationBar item order indices
    const val NAV_INDEX_STRATEGY = 0
    const val NAV_INDEX_HISTORY = 1  
    const val NAV_INDEX_HOME = 2
    const val NAV_INDEX_STATISTICS = 3
    const val NAV_INDEX_SETTINGS = 4
    
    // NavigationBar labels
    const val NAV_LABEL_STRATEGY = "Strategy"
    const val NAV_LABEL_HISTORY = "History"
    const val NAV_LABEL_HOME = "Home"
    const val NAV_LABEL_STATISTICS = "Stats"
    const val NAV_LABEL_SETTINGS = "Settings"
}