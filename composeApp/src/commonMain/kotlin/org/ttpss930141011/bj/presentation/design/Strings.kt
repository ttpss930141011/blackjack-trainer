package org.ttpss930141011.bj.presentation.design

/**
 * Centralized UI strings. Replaces hardcoded literals scattered across composables.
 * When i18n is needed, swap this for a resource-based solution.
 */
object Strings {

    object Game {
        const val PLACE_YOUR_BET = "Place Your Bet"
        const val YOUR_TURN = "Your Turn"
        const val DEALERS_TURN = "Dealer's Turn"
        const val ROUND_RESULTS = "Round Results"
        const val PREPARING = "Preparing..."
        const val DEAL_CARDS = "Deal Cards"
        const val NEXT_ROUND = "Next Round"
        const val PLAY_DEALER_TURN = "Play Dealer Turn"
        fun dealCardsWithBet(bet: Int) = "Deal Cards (\$$bet)"
    }

    object Feedback {
        const val PERFECT_STRATEGY = "Perfect strategy!"
        const val SKILL_AND_LUCK = "Skill + luck 🎯"
        const val RIGHT_CALL_UNLUCKY = "Right call — just unlucky"
        const val PLAYED_IT_RIGHT = "Played it right"
        const val WON_BUT_REVIEW = "Won, but review your play"
        const val CHECK_STRATEGY = "Check strategy guide ←"
        fun correctCount(correct: Int, total: Int) = "$correct/$total correct"
    }

    object Nav {
        const val HOME = "Home"
        const val STRATEGY = "Strategy"
        const val STRATEGY_CHART = "Strategy Chart"
        const val STRATEGY_GUIDE = "Strategy Guide"
        const val HISTORY = "History"
        const val GAME_HISTORY = "Game History"
        const val SETTINGS = "Settings"
    }
}
