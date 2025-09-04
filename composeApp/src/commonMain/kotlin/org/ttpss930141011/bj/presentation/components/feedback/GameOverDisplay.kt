package org.ttpss930141011.bj.presentation.components.feedback

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.presentation.design.Tokens

@Composable
fun GameOverDisplay(totalChips: Int) {
    Card {
        Column(
            modifier = Modifier.padding(Tokens.Space.l),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Game Over!",
                style = MaterialTheme.typography.headlineSmall
            )
            Text("Final Chips: $totalChips")
        }
    }
}