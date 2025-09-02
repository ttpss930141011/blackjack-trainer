package org.ttpss930141011.bj.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.GameRules

@Composable
fun SettingsScreen(
    currentRules: GameRules,
    onRulesChanged: (GameRules) -> Unit,
    onBack: () -> Unit
) {
    var rules by remember { mutableStateOf(currentRules) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Game Settings",
                style = MaterialTheme.typography.headlineMedium
            )
            
            TextButton(onClick = onBack) {
                Text("Back")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Dealer Rules",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dealer hits on soft 17")
                    Switch(
                        checked = rules.dealerHitsOnSoft17,
                        onCheckedChange = { 
                            rules = rules.copy(dealerHitsOnSoft17 = it)
                        }
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Surrender allowed")
                    Switch(
                        checked = rules.surrenderAllowed,
                        onCheckedChange = { 
                            rules = rules.copy(surrenderAllowed = it)
                        }
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Double after split")
                    Switch(
                        checked = rules.doubleAfterSplit,
                        onCheckedChange = { 
                            rules = rules.copy(doubleAfterSplit = it)
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { onRulesChanged(rules) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Settings")
        }
    }
}