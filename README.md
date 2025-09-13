# Blackjack Strategy Trainer

A 21-point strategy trainer implemented with DDD architecture using Kotlin Multiplatform.

## DDD Architectural Decisions

### Bounded Context Design
**Decision**: Single Bounded Context  
**Rationale**: Both subdomains share core concepts (`Hand`, `Card`, `Action`) with the same Ubiquitous Language

### Subdomain Identification
This project contains two subdomains:

#### 1. Game Subdomain - Blackjack Game Logic
Responsible for core game execution
- `Game`, `Player`, `Dealer` - Game state management
- `RoundManager`, `SettlementService` - Game flow control
- `ChipCompositionService` - Chip system

#### 2. Learning Subdomain - Strategy Learning
Responsible for strategy evaluation and learning tracking
- `StrategyEngine` - Basic strategy calculation
- `DecisionEvaluator`, `LearningRecorder` - Decision evaluation and learning coordination
- `SessionStats`, `ScenarioErrorStat` - Learning analytics

### Shared Core Concepts
Both subdomains share these domain objects:
- `Hand`, `Card`, `Deck` - Basic game concepts
- `Action`, `GameRules` - Game rules and actions
- `DecisionRecord`, `DecisionFeedback` - Cross-subdomain decision records

### Why Not Separate Bounded Contexts?
1. **High Concept Overlap**: `Hand` means the same thing in both game and learning contexts
2. **No Language Ambiguity**: No same-name concepts with different meanings
3. **Small Team Size**: Single team development, no need for language boundaries
4. **Avoid Over-Engineering**: Translation layers and anti-corruption layers would add unnecessary complexity

## Domain Layer Structure

### entities/
- `Game.kt` - Aggregate root managing entire game state and lifecycle
```kotlin
// Domain logic improvement: Moving business logic from Application back to Domain layer
enum class RoundOutcome { WIN, LOSS, PUSH, UNKNOWN }
fun getRoundOutcome(): RoundOutcome  // Round result determination
fun shouldAutoAdvance(): Boolean     // Auto state transition logic
val isGameOver: Boolean             // Game over state
```
- `Player.kt` - Player chip management and betting behavior
- `Dealer.kt` - Dealer state, hole card handling, and automatic hit logic

### valueobjects/
- `Hand.kt` - Hand calculation: soft 17 logic, bust detection, split conditions
- `PlayerHand.kt` - Player hand: bet binding, status tracking, action availability
- `Card.kt` - Playing card basics: suit, rank, blackjack value calculation
- `Deck.kt` - Deck management: shuffle, deal, remaining count
```kotlin
fun dealCard(): Pair<Card, Deck>
fun dealCards(count: Int): Pair<List<Card>, Deck>
val needsShuffle: Boolean = remainingCards < 26
```
- `DecisionRecord.kt` - Decision record: hand, dealer card, player action, correctness
- `DecisionFeedback.kt` - Strategy feedback: error analysis and explanation generation
- `GameRules.kt` - Rule configuration: soft 17, surrender, blackjack payout
- `ChipInSpot.kt` - Chip stacking: denomination and quantity combination
- `AddChipResult.kt` - Bet addition result: success/failure with error message
- `SessionStats.kt` - Session statistics: rule-aware learning progress tracking
- `ScenarioErrorStat.kt` - Scenario error statistics: error rate analysis for specific scenarios
- `StrategyChartData.kt` - Strategy chart data structure

### services/
- `StrategyEngine.kt` - Basic strategy calculation core
```kotlin
fun getOptimalAction(playerHand: Hand, dealerUpCard: Card, rules: GameRules): Action
// Implements complete strategy logic for splits, soft hands, hard hands, surrender
```
- `RoundManager.kt` - Round flow management: dealing, player action processing, state transitions
- `SettlementService.kt` - Settlement service: win/loss determination and chip calculation
- `ChipCompositionService.kt` - Chip composition optimization: greedy algorithm for minimum chip count
- `LearningRepository.kt` - Learning record interface definition

### enums/
- `Action.kt` - Player actions
```kotlin
enum class Action { HIT, STAND, DOUBLE, SPLIT, SURRENDER }
```
- `GamePhase.kt` - Game phase control
```kotlin
enum class GamePhase { WAITING_FOR_BETS, DEALING, PLAYER_TURN, DEALER_TURN, SETTLEMENT }
```
- `HandStatus.kt` - Hand status
```kotlin
enum class HandStatus { ACTIVE, STANDING, BUSTED, SURRENDERED, WIN, LOSS, PUSH }
```
- `ChipValue.kt` - Chip denominations
```kotlin
enum class ChipValue(val value: Int) { FIVE(5), TEN(10), TWENTY_FIVE(25), ... }
```
- `RoundResult.kt` - Round results
```kotlin
enum class RoundResult { PLAYER_WIN, PLAYER_BLACKJACK, DEALER_WIN, PUSH, SURRENDER }
```

## Application Layer - Use Case Coordination

### Core Coordinators
- `GameViewModel.kt` - Main coordinator, delegates to four specialized Managers
- `DecisionEvaluator.kt` - Decision evaluation coordination: connecting strategy engine with feedback generation
- `LearningRecorder.kt` - Learning progress recording and statistical coordination
- `GameService.kt` - Application layer wrapper for game domain services

### Responsibility Separation Managers (Internal Implementation)
**Why This Architecture**: The original GameViewModel was a 305-line God Object mixing multiple concerns. This refactoring solves the Single Responsibility Principle violation while maintaining backward compatibility.

#### `GameStateManager.kt` (Internal)
```kotlin
// Focus: Pure game state management
fun initializeGame(gameRules: GameRules, player: Player)
fun startRound(betAmount: Int): GameStateResult
fun executePlayerAction(action: Action): GameActionResult?
fun processDealerTurn(): GameStateResult
```

#### `FeedbackManager.kt` (Internal)
```kotlin
// Focus: Decision feedback and evaluation
fun evaluatePlayerAction(handBeforeAction, dealerUpCard, action, rules): DecisionFeedback
val roundDecisions: List<PlayerDecision> // Round decision tracking
```

#### `AnalyticsManager.kt` (Internal)
```kotlin
// Focus: Learning analysis and statistics
fun recordPlayerAction(hand, dealerCard, action, isCorrect, rules)
fun getWorstScenarios(minSamples: Int): List<ScenarioErrorStat>
val sessionStats: SessionStats
```

#### `UIStateManager.kt` (Internal)
```kotlin
// Focus: UI state and notifications
fun setError(message: String?)
fun handleRuleChangeNotification(currentRules, newRules, sessionStats)
fun calculateChipComposition(amount: Int): List<ChipInSpot>
```

### Refactoring Benefits
- **Single Responsibility**: Each Manager focuses on one concern
- **Backward Compatibility**: GameViewModel maintains the same public API
- **Test Friendly**: Managers can be tested independently
- **Maintainability**: Problem location is more precise

## Infrastructure Layer - Technical Implementation

- `InMemoryLearningRepository.kt` - In-memory decision record storage
```kotlin
override fun save(decision: DecisionRecord) { decisions.add(decision) }
override fun getErrorStatsByRule(ruleHash: String, minSamples: Int): List<ScenarioErrorStat>
```

## Presentation Layer - UI Presentation

### components/game/
- `PlayerArea.kt` - Player area: smart switching between betting circle and hand display
- `SmartHandCarousel.kt` - Multi-hand carousel: single hand centered, multi-hand scrollable
- `BettingCircle.kt` - Betting interface: visual chip stacking and clear functionality

### pages/
- `HistoryPage.kt` - Decision history review
- `StatisticsPage.kt` - Learning statistics and progress analysis
- `StrategyPage.kt` - Basic strategy chart display
- `SettingsPage.kt` - Game rules configuration

## Key Implementation Patterns

### Aggregate Root Pattern
```kotlin
// Game controls aggregate consistency + encapsulates business logic
fun dealRound(): Game = RoundManager().dealRound(this)
fun playerAction(action: Action): Game = RoundManager().processPlayerAction(this, action)
fun getRoundOutcome(): RoundOutcome = // Domain logic returns to Domain layer
    if (phase == GamePhase.SETTLEMENT) determineOutcome() else RoundOutcome.UNKNOWN
```

### Application Layer Responsibility Separation Pattern
```kotlin
// GameViewModel delegation pattern: one coordinator + four specialized Managers
class GameViewModel {
    private val gameStateManager = GameStateManager(gameService)     // Game state
    private val feedbackManager = FeedbackManager(decisionEvaluator) // Decision feedback
    private val analyticsManager = AnalyticsManager(learningRecorder) // Learning statistics
    private val uiStateManager = UIStateManager(chipService)         // UI state
    
    // Provides unified API externally, internally delegates to specialized Managers
    val game: Game? get() = gameStateManager.game
    val feedback: DecisionFeedback? get() = feedbackManager.feedback
}
```

### Domain Service Separation
Complex business logic extracted from entities into specialized services:
- Strategy calculation → `StrategyEngine`
- Round management → `RoundManager`
- Settlement processing → `SettlementService`

### Immutable Value Objects
```kotlin
fun addCard(card: Card): Hand = Hand(cards + card)  // Returns new instance
```

### Result Type Pattern
```kotlin
// Simple practical error handling, avoiding over-engineering
sealed class GameStateResult {
    object Success : GameStateResult()
    data class Error(val message: String) : GameStateResult()
}
```

## Development Commands

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun  # Web version
./gradlew test                                     # Run tests
./gradlew build                                    # Multi-platform build
```