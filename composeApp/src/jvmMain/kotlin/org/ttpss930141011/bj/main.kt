package org.ttpss930141011.bj

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "blackjack-strategy-trainer",
    ) {
        App()
    }
}