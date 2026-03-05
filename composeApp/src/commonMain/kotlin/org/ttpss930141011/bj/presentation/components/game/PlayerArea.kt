package org.ttpss930141011.bj.presentation.components.game

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.enums.ChipValue
import org.ttpss930141011.bj.domain.services.*
import org.ttpss930141011.bj.presentation.components.displays.ChipImageDisplay
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.presentation.mappers.ChipImageMapper

/**
 * Player area component that handles player hand display
 * Shows betting circle during betting phase, player hands during game
 * 
 * @param game Current game state
 * @param viewModel Game view model for user interactions
 * @param modifier Compose modifier for styling
 */
@Composable
fun PlayerArea(
    game: Game,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val chipCompositionService = remember { ChipCompositionService() }
    
    org.ttpss930141011.bj.presentation.layout.Layout { screenWidth ->
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (game.phase) {
                GamePhase.WAITING_FOR_BETS -> {
                    val scrollState = rememberScrollState()
                    val playerChips = game.player?.chips ?: 0
                    val currentBet = viewModel.currentBetAmount

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Tokens.Space.m)
                    ) {
                        // Betting circle
                        BettingCircle(
                            currentBet = currentBet,
                            chipComposition = viewModel.chipComposition,
                            onClearBet = { viewModel.clearBet() }
                        )

                        // Chip selection row — below the circle
                        val mobileContentWidth = Tokens.Size.chipDiameter * 8
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .width(mobileContentWidth)
                                    .horizontalScroll(scrollState)
                                    .padding(horizontal = Tokens.Space.s),
                                horizontalArrangement = Arrangement.spacedBy(Tokens.Space.s, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ChipImageMapper.standardChipValues.forEach { chipValue ->
                                    ChipImageDisplay(
                                        value = chipValue,
                                        size = Tokens.Size.chipDiameter,
                                        onClick = {
                                            if (currentBet + chipValue <= playerChips) {
                                                ChipValue.fromValue(chipValue)?.let { viewModel.addChipToBet(it) }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {
                    SmartHandCarousel(
                        playerHands = game.playerHands,
                        currentHandIndex = game.currentHandIndex,
                        phase = game.phase,
                        chipCompositionService = chipCompositionService,
                        screenWidth = screenWidth
                    )
                }
            }
        }
    }
}

