# Phase 2 Refactoring Plan: GameViewModel BettingTableState Elimination

## 🎯 Objective
Remove `_bettingTableState` completely from GameViewModel while maintaining all chip display functionality using pure domain logic through `Game.pendingBet` and `ChipCompositionService`.

## 📊 Current State Analysis

### Problems Identified:
1. **Dual State**: `_bettingTableState` + `Game.pendingBet` manage same data
2. **Complex Bridging**: `BettingTableState.fromGame()` and `toGameBet()` unnecessary
3. **UI Dependency**: GameViewModel depends on domain value object
4. **Sync Issues**: Two state sources can diverge

### Current Method Issues:
- `addChipToBet()`: Updates both states redundantly  
- `clearBet()`: Uses BettingTableState for validation
- `dealCards()`: Depends on BettingTableState.canDeal and toGameBet()

## 🔧 Implementation Steps

### Step 1: Create Domain-Based Chip Display Logic
```kotlin
// NEW: Pure domain logic for chip display
private val chipCompositionService = ChipCompositionService()

// REPLACE: BettingTableState properties with domain calculations
val currentBetAmount: Int get() = _game?.pendingBet ?: 0
val canDealCards: Boolean get() = _game?.canDealCards ?: false  
val chipComposition: List<ChipInSpot> 
    get() = _game?.pendingBet?.let { amount ->
        if (amount > 0) chipCompositionService.calculateOptimalComposition(amount)
        else emptyList()
    } ?: emptyList()
val availableBalance: Int get() = _game?.player?.chips ?: 0
```

### Step 2: Refactor addChipToBet() Method
```kotlin
// BEFORE: Updates both Game and BettingTableState
fun addChipToBet(chipValue: ChipValue) {
    val result = currentGame.tryAddChipToPendingBet(chipValue)
    if (result.success) {
        _game = result.updatedGame
        _bettingTableState = BettingTableState.fromGame(result.updatedGame) // ❌ Remove this
    }
}

// AFTER: Pure domain logic only
fun addChipToBet(chipValue: ChipValue) {
    val currentGame = _game ?: return
    if (currentGame.phase != GamePhase.WAITING_FOR_BETS) return
    
    val result = currentGame.tryAddChipToPendingBet(chipValue)
    if (result.success) {
        _game = result.updatedGame
        _errorMessage = null
    } else {
        _errorMessage = result.errorMessage
    }
}
```

### Step 3: Refactor clearBet() Method
```kotlin
// BEFORE: Mixed state management
fun clearBet() {
    val currentTable = _bettingTableState ?: BettingTableState.fromGame(currentGame)
    _bettingTableState = currentTable.clearBet()
    _game = currentGame.clearBet()
}

// AFTER: Pure domain logic
fun clearBet() {
    val currentGame = _game ?: return
    if (currentGame.phase != GamePhase.WAITING_FOR_BETS) return
    
    try {
        _game = currentGame.clearPendingBet()
        _errorMessage = null
    } catch (e: Exception) {
        _errorMessage = e.message
    }
}
```

### Step 4: Refactor dealCards() Method  
```kotlin
// BEFORE: BettingTableState dependency
fun dealCards() {
    val currentTable = _bettingTableState ?: return
    if (!currentTable.canDeal) return
    
    val gameWithBet = currentTable.toGameBet(currentGame)
    _game = gameService.dealRound(gameWithBet)
    _bettingTableState = null
}

// AFTER: Pure domain logic
fun dealCards() {
    val currentGame = _game ?: return
    if (!currentGame.canDealCards) {
        _errorMessage = "Cannot deal cards at this time"
        return
    }
    
    try {
        // Commit pending bet and deal
        val gameWithCommittedBet = currentGame.commitPendingBet()
        _game = gameService.dealRound(gameWithCommittedBet)
        _errorMessage = null
    } catch (e: Exception) {
        _errorMessage = e.message
    }
}
```

### Step 5: Remove All BettingTableState References
- Remove `_bettingTableState` property and backing field
- Remove `val bettingTableState: BettingTableState?` getter
- Remove `initializeBettingTableState()` method
- Remove call to `initializeBettingTableState()` in `nextRound()`

### Step 6: Update UI Layer Compatibility
Ensure UI components can access chip display data through domain-based properties:
- `currentBetAmount` instead of `bettingTableState?.currentBet`  
- `canDealCards` instead of `bettingTableState?.canDeal`
- `chipComposition` instead of `bettingTableState?.chipComposition`
- `availableBalance` instead of `bettingTableState?.availableBalance`

## ✅ Success Criteria

1. **Zero BettingTableState Usage**: No references to BettingTableState in GameViewModel
2. **All Tests Pass**: Domain and integration tests remain green
3. **UI Functionality Preserved**: Chip display and betting work identically  
4. **Single Source of Truth**: Game entity is the only state source
5. **Clean DDD Architecture**: No domain value objects in application layer

## 🚨 Risk Mitigation

1. **UI Compatibility**: Provide clear property mapping for UI layer
2. **State Consistency**: Ensure Game.pendingBet behaves identically to old system
3. **Error Handling**: Match existing error behavior and messages
4. **Edge Cases**: Test all boundary conditions (insufficient chips, wrong phase, etc.)

## 📊 Expected Improvements

- **Reduced Complexity**: Single state management path
- **Better DDD Compliance**: Clean layer separation
- **Easier Testing**: No complex state synchronization
- **Maintainability**: Single source of truth for all betting state

---
*Phase 2 Target: Complete `_bettingTableState` elimination with preserved functionality*