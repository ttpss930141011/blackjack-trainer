package org.ttpss930141011.bj.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.Action

@Composable
fun ActionButtons(
    availableActions: List<Action>,
    onAction: (Action) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(availableActions) { action ->
            Button(onClick = { onAction(action) }) {
                Text(action.name)
            }
        }
    }
}