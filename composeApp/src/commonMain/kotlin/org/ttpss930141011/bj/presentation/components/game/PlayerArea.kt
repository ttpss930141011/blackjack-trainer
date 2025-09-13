package org.ttpss930141011.bj.presentation.components.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.application.GameViewModel
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.*
import org.ttpss930141011.bj.presentation.design.Tokens

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
                    // Use consistent height container to match SmartHandCarousel
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Tokens.Space.xs)
                    ) {
                        // Card area equivalent space to match SmartHandCard structure
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(
                                    Tokens.Card.medium.height + 
                                    Tokens.Space.m * 2 + 
                                    Tokens.Space.s + 
                                    Tokens.Space.l // 使用合适的间距token代替文本高度
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            BettingCircle(
                                currentBet = viewModel.currentBetAmount,
                                chipComposition = viewModel.chipComposition,
                                onClearBet = { viewModel.clearBet() }
                            )
                        }
                        
                        // Chips area equivalent space to match ChipStackDisplay
                        Spacer(
                            modifier = Modifier.height(org.ttpss930141011.bj.presentation.design.AppConstants.Dimensions.CHIP_SIZE_COMPACT.dp)
                        )
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

