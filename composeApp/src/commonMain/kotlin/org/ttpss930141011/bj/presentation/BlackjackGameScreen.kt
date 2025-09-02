package org.ttpss930141011.bj.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ttpss930141011.bj.domain.*

@Composable
fun BlackjackGameScreen(
    gameRules: GameRules = GameRules(),
    onBackToMenu: () -> Unit = {}
) {
    var game by remember { mutableStateOf(
        Game.create(rules = gameRules).addPlayer(Player(id = "player1", chips = 500))
    ) }
    var feedback by remember { mutableStateOf<DecisionFeedback?>(null) }
    var sessionStats by remember { mutableStateOf(SessionStats()) }
    var roundDecisions by remember { mutableStateOf<List<PlayerDecision>>(emptyList()) }
    var showGameSummary by remember { mutableStateOf(false) }
    
    // 統一狀態來源：使用game中的玩家狀態
    val currentPlayer = game.player ?: Player(id = "player1", chips = 0)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 頂部導航
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBackToMenu) {
                Text("← Menu")
            }
            
            Text(
                text = "Strategy Trainer",
                style = MaterialTheme.typography.headlineSmall
            )
            
            TextButton(
                onClick = { showGameSummary = true },
                enabled = sessionStats.totalRounds > 0
            ) {
                Text("Exit Game")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 遊戲狀態顯示
        GameStatusDisplay(player = currentPlayer, stats = sessionStats)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 統一的全遊戲視圖 (所有階段都顯示) - TODO: 更新為Game版本
        UniversalFullGameLayout(
            game = game,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 根據遊戲狀態顯示對應的控制按鈕
        when (game.phase) {
            GamePhase.WAITING_FOR_BETS -> {
                NewRoundControls(
                    currentChips = currentPlayer.chips,
                    onStartRound = { bet ->
                        try {
                            game = game.placeBet(bet).dealRound()
                            feedback = null
                        } catch (e: Exception) {
                            // 處理錯誤 (籌碼不足等)
                        }
                    }
                )
            }
            
            GamePhase.PLAYER_ACTIONS -> {
                // 決策回饋顯示
                feedback?.let { fb ->
                    Card {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = fb.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (fb.isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // 行動按鈕
                game.currentHand?.let { hand ->
                    ActionButtons(
                        availableActions = game.availableActions().toList(),
                        onAction = { action ->
                            try {
                                // Capture hand state BEFORE action for strategy evaluation
                                val handBeforeAction = Hand(hand.cards)
                                game = game.playerAction(action)
                                
                                // 生成決策回饋並記錄決策
                                val strategyEngine = StrategyEngine()
                                feedback = DecisionFeedback.evaluate(
                                    playerHand = handBeforeAction,
                                    dealerUpCard = game.dealer.upCard!!,
                                    playerAction = action,
                                    strategyEngine = strategyEngine,
                                    rules = game.rules
                                )
                                
                                // 記錄決策用於統計
                                roundDecisions = roundDecisions + PlayerDecision(action, feedback?.isCorrect ?: false)
                            } catch (e: Exception) {
                                // 處理錯誤 (不能分牌等)
                            }
                        }
                    )
                }
            }
            
            GamePhase.DEALER_TURN -> {
                // Automatic dealer play - simplified in Game model
                Button(
                    onClick = { game = game.dealerPlayAutomated() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Play Dealer Turn")
                }
            }
            
            GamePhase.SETTLEMENT -> {
                SettlementControlsForGame(
                    game = game,
                    feedback = feedback,
                    roundDecisions = roundDecisions,
                    onSettle = {
                        // First settle the round to calculate proper hand statuses
                        game = game.settleRound()
                        
                        // THEN determine outcome from settled hand statuses
                        val outcome = if (game.playerHands.isNotEmpty()) {
                            val firstHand = game.playerHands[0]
                            when (firstHand.status) {
                                HandStatus.WIN -> "WIN"
                                HandStatus.LOSS, HandStatus.BUSTED -> "LOSS"
                                HandStatus.PUSH -> "PUSH"
                                else -> "UNKNOWN"
                            }
                        } else "UNKNOWN"
                        
                        // 更新統計含歷史記錄 with correct outcome
                        sessionStats = sessionStats.recordRoundWithHistory(roundDecisions, outcome)
                    },
                    onNextRound = {
                        // Application layer workflow control: SETTLEMENT → WAITING_FOR_BETS
                        // DEBUG: Add logging to trace the exact state change
                        println("DEBUG: Before reset - Phase: ${game.phase}, PlayerHands: ${game.playerHands.size}")
                        // Reset game state and transition to next round (Application layer responsibility)
                        game = game.resetForNewRound()
                        println("DEBUG: After reset - Phase: ${game.phase}, PlayerHands: ${game.playerHands.size}")
                        feedback = null
                        roundDecisions = emptyList() // 重設決策記錄
                    }
                )
            }
            
            else -> {
                Text("Preparing...")
            }
        }
        
        // 遊戲結束處理 - 只在WAITING_FOR_BETS階段檢查
        if (game.phase == GamePhase.WAITING_FOR_BETS && currentPlayer.chips <= 0) {
            GameOverDisplay(totalChips = currentPlayer.chips)
        }
    }
    
    // 遊戲統計對話框
    if (showGameSummary) {
        GameSummaryDialog(
            stats = sessionStats,
            onDismiss = { showGameSummary = false },
            onBackToMenu = {
                showGameSummary = false
                onBackToMenu()
            }
        )
    }
}

@Composable
private fun GameStatusDisplay(player: Player, stats: SessionStats) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Chips: ${player.chips}",
                style = MaterialTheme.typography.headlineSmall
            )
            
            if (stats.hasSignificantData) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Rounds: ${stats.totalRounds}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Accuracy: ${(stats.overallDecisionRate * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = when {
                            stats.overallDecisionRate >= 0.8 -> MaterialTheme.colorScheme.primary
                            stats.overallDecisionRate >= 0.6 -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        }
    }
}

// REMOVED: CurrentTableDisplay - replaced by UniversalFullGameLayout

@Composable
private fun ActionButtons(
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

@Composable
private fun NewRoundControls(
    currentChips: Int,
    onStartRound: (Int) -> Unit
) {
    var betAmount by remember { mutableStateOf(25) }
    val availableChips = ChipImageMapper.standardChipValues.filter { it <= currentChips }
    
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Place Your Bet",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Available: $currentChips",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 當前賭注顯示
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Bet:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$$betAmount",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 籌碼選擇區域
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(availableChips) { chipValue ->
                    ChipImageDisplay(
                        value = chipValue,
                        onClick = { betAmount += chipValue }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Clear按鈕
            TextButton(
                onClick = { betAmount = 0 }
            ) {
                Text("Clear")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = { onStartRound(betAmount) },
                enabled = betAmount <= currentChips && betAmount > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Deal Cards ($$betAmount)")
            }
        }
    }
}

// REMOVED: RoundCompletedDisplay - replaced by SettlementControlsForGame

// REMOVED: PlayerActionResultDisplay - replaced by direct feedback in UniversalFullGameLayout

// REMOVED: SettlementControls - replaced by SettlementControlsForGame

// REMOVED: DealerTurnControls - replaced by automated dealer turn in Game

@Composable
private fun GameOverDisplay(totalChips: Int) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
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

@Composable
private fun GameSummaryDialog(
    stats: SessionStats,
    onDismiss: () -> Unit,
    onBackToMenu: () -> Unit
) {
    if (stats.totalRounds == 0) {
        onDismiss()
        return
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // 標題
            Text(
                text = "Game Summary",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 整體統計
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Rounds: ${stats.totalRounds}")
                    Text("Perfect Rounds: ${stats.perfectRounds}")
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Overall Accuracy: ${(stats.overallDecisionRate * 100).toInt()}%")
                    Text("Perfect Rate: ${(stats.perfectRoundRate * 100).toInt()}%")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 歷史記錄表格
            Text(
                text = "Round History",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyColumn(
                modifier = Modifier.height(200.dp)
            ) {
                item {
                    // 表頭
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Round", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                        Text("Decisions", modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelMedium)
                        Text("Result", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                        Text("Correct", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                items(stats.roundHistory) { record ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("${record.roundNumber}", modifier = Modifier.weight(1f))
                        Text(
                            text = record.decisions.joinToString(",") { it.action.name.take(1) },
                            modifier = Modifier.weight(2f)
                        )
                        Text(record.outcome, modifier = Modifier.weight(1f))
                        Text(
                            text = "${record.decisions.count { it.isCorrect }}/${record.decisions.size}",
                            modifier = Modifier.weight(1f),
                            color = if (record.decisions.all { it.isCorrect }) 
                                MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 按鈕
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Continue Game")
                }
                Button(onClick = onBackToMenu) {
                    Text("Back to Menu")
                }
            }
        }
    }
}

// REMOVED: DealerTurnWithFullTable - replaced by automated dealer turn

// REMOVED: FullTableViewForDealerTurn - replaced by UniversalFullGameLayout

// REMOVED: DealerDisplayArea - replaced by UniversalDealerDisplayForGame

// REMOVED: TableSeatsLayout - replaced by PlayerHandsDisplay

// REMOVED: PlayerSeatDisplay - replaced by PlayerHandCard

// REMOVED: EmptySeatDisplay - not needed in single-player Game

// REMOVED: VisualSeat and seat mapping - replaced by direct PlayerHand display in Game

// === Unified Full Game Layout Component ===

@Composable
private fun UniversalFullGameLayout(
    game: Game,
    modifier: Modifier = Modifier
) {
    // DEBUG: Add logging to track actual state during render
    println("DEBUG: UniversalFullGameLayout render - Phase: ${game.phase}, PlayerHands.size: ${game.playerHands.size}")
    
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 階段標題
            val phaseTitle = when (game.phase) {
                GamePhase.WAITING_FOR_BETS -> "Place Your Bet"
                GamePhase.PLAYER_ACTIONS -> "Player Actions"
                GamePhase.DEALER_TURN -> "Dealer's Turn"
                GamePhase.SETTLEMENT -> "Round Results"
                else -> "Game Table"
            }
            
            Text(
                text = phaseTitle,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Dealer區域
            UniversalDealerDisplayForGame(
                dealerHand = game.dealer.hand,
                dealerUpCard = game.dealer.upCard,
                phase = game.phase
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 玩家手牌顯示 - ADD DEBUG LOGGING
            println("DEBUG: Checking hand display - hasPlayer: ${game.hasPlayer}, playerHands.isNotEmpty: ${game.playerHands.isNotEmpty()}")
            if (game.hasPlayer && game.playerHands.isNotEmpty()) {
                println("DEBUG: Showing Your Hands section with ${game.playerHands.size} hands")
                Text(
                    text = "Your Hands",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                PlayerHandsDisplay(
                    playerHands = game.playerHands,
                    currentHandIndex = game.currentHandIndex,
                    phase = game.phase
                )
            } else if (game.hasPlayer) {
                println("DEBUG: Showing Ready to play text - playerHands should be empty")
                Text(
                    text = "${game.player!!.id} - Ready to play",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun UniversalDealerDisplayForGame(
    dealerHand: Hand?,
    dealerUpCard: Card?,
    phase: GamePhase
) {
    Card {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Dealer",
                style = MaterialTheme.typography.titleMedium
            )
            
            when (phase) {
                GamePhase.WAITING_FOR_BETS -> {
                    Text(
                        text = "Waiting for bets...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                GamePhase.PLAYER_ACTIONS -> {
                    dealerUpCard?.let { upCard ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            CardImageDisplay(card = upCard, size = CardSize.MEDIUM)
                            // Hole card (face down) - 顯示空白卡背
                            HoleCardDisplay(size = CardSize.MEDIUM)
                        }
                        Text("Up Card: ${upCard.rank}")
                    }
                }
                GamePhase.DEALER_TURN-> {
                    dealerHand?.let { hand ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            // 顯示所有牌（包括revealed hole card）
                            hand.cards.forEach { card ->
                                CardImageDisplay(card = card, size = CardSize.MEDIUM)
                            }
                            HoleCardDisplay(size = CardSize.MEDIUM)
                        }
                        Text("Value: ${hand.bestValue}${if (hand.isSoft) " (soft)" else ""}")
                        if (hand.isBusted) {
                            Text("Busted!", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                 GamePhase.SETTLEMENT -> {
                    dealerHand?.let { hand ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            hand.cards.forEach { card ->
                                CardImageDisplay(card = card, size = CardSize.MEDIUM)
                            }
                        }
                        Text("Value: ${hand.bestValue}${if (hand.isSoft) " (soft)" else ""}")
                        if (hand.isBusted) {
                            Text("Busted!", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun PlayerHandsDisplay(
    playerHands: List<PlayerHand>,
    currentHandIndex: Int,
    phase: GamePhase
) {
    if (playerHands.size == 1) {
        // Single hand display
        PlayerHandCard(
            hand = playerHands[0],
            handIndex = 0,
            isActive = currentHandIndex == 0,
            phase = phase
        )
    } else {
        // Multiple hands (split scenario)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(playerHands) { index, hand ->
                PlayerHandCard(
                    hand = hand,
                    handIndex = index,
                    isActive = currentHandIndex == index,
                    phase = phase,
                    modifier = Modifier.width(200.dp)
                )
            }
        }
    }
}

@Composable
private fun PlayerHandCard(
    hand: PlayerHand,
    handIndex: Int,
    isActive: Boolean,
    phase: GamePhase,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isActive && phase == GamePhase.PLAYER_ACTIONS) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (handIndex > 0) {
                Text(
                    text = "Hand ${handIndex + 1}",
                    style = MaterialTheme.typography.labelMedium
                )
            }
            
            if (isActive && phase == GamePhase.PLAYER_ACTIONS) {
                Text(
                    text = "Your Turn",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Cards display
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(hand.cards) { card ->
                    CardImageDisplay(card = card, size = CardSize.MEDIUM)
                }
            }
            
            Text("Value: ${hand.bestValue}${if (hand.isSoft) " (soft)" else ""}")
            Text("Bet: $${hand.bet}")
            
            // Status display for settlement
            if (phase == GamePhase.SETTLEMENT) {
                val statusColor = when (hand.status) {
                    HandStatus.WIN -> MaterialTheme.colorScheme.primary
                    HandStatus.LOSS -> MaterialTheme.colorScheme.error
                    HandStatus.PUSH -> MaterialTheme.colorScheme.tertiary
                    HandStatus.BUSTED -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
                Text(
                    text = hand.status.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor
                )
            }
        }
    }
}

// === Settlement Controls for Game ===

@Composable
private fun SettlementControlsForGame(
    game: Game,
    feedback: DecisionFeedback?,
    roundDecisions: List<PlayerDecision>,
    onSettle: () -> Unit,
    onNextRound: () -> Unit
) {
    // Check if settlement is complete
    val isSettled = game.playerHands.any { hand ->
        hand.status == HandStatus.WIN || 
        hand.status == HandStatus.LOSS || 
        hand.status == HandStatus.PUSH 
    }
    
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isSettled) {
                Button(
                    onClick = onSettle,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Calculate Results")
                }
            } else {
                // Show round summary
                feedback?.let { fb ->
                    Card {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = fb.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (fb.isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Round decisions summary
                if (roundDecisions.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("This Round Decisions:")
                            roundDecisions.forEachIndexed { index, decision ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${index + 1}. ${decision.action.name}")
                                    Text(
                                        text = if (decision.isCorrect) "✓" else "✗",
                                        color = if (decision.isCorrect) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Button(
                    onClick = onNextRound,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Next Round")
                }
            }
        }
    }
}

// REMOVED: UniversalFullTableLayout - replaced by UniversalFullGameLayout

// REMOVED: UniversalDealerDisplay - replaced by UniversalDealerDisplayForGame

// REMOVED: UniversalTableSeatsLayout - replaced by PlayerHandsDisplay

// REMOVED: All remaining Table-based components below - replaced by Game-based equivalents

// REMOVED: UniversalSeatDisplay - replaced by PlayerHandCard

// REMOVED: SettlementControlsSimplified - replaced by SettlementControlsForGame

// REMOVED: DealerActionButtons - replaced by automated dealer play in Game