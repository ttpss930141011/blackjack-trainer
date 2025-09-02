# DDD + TDD + CQRS 架構指導

*防止過度工程和架構混亂的實施指南*

## 🏗️ 分層架構

### 依賴方向規則
```yaml
依賴流向: Presentation → Application → Domain ← Infrastructure
          
核心原則: 
  - Domain層不依賴任何其他層
  - Application層協調Domain和Infrastructure
  - Presentation層只依賴Application層
  - Infrastructure層實作Domain層介面
```

### 各層職責界定
```yaml
Domain (核心業務邏輯):
  職責: 21點規則, 策略計算, 業務邏輯
  包含: Entity, Value Object, Domain Service, Repository Interface
  禁止: UI, Database, Network, Platform-specific code

Application (使用案例):
  職責: 編排Domain邏輯, 處理Command/Query
  包含: Use Case, Command Handler, Query Handler, Application Service
  禁止: UI邏輯, 直接database操作

Presentation (使用者介面):
  職責: UI渲染, 用戶互動, 狀態管理
  包含: Compose UI, ViewModel, UI State
  禁止: 業務邏輯, 直接Domain操作

Infrastructure (技術實作):
  職責: 持久化, 外部服務, Platform-specific實作
  包含: Repository Implementation, External API, Storage
  禁止: 業務邏輯, UI邏輯
```

## 🎯 DDD實施策略

### 漸進式建模方法
```yaml
第1步: 核心實體 (Entity)
  - Card, Hand, Game
  - 專注於身份和生命週期
  - 100%測試覆蓋

第2步: 值物件 (Value Object)  
  - Action, GameRules, HandValue
  - 不可變，無身份
  - 業務概念的封裝

第3步: 領域服務 (Domain Service)
  - StrategyEngine, HandEvaluator
  - 無狀態的業務邏輯
  - 複雜規則的實作

第4步: 聚合 (Aggregate)
  - Game作為聚合根
  - 維持業務不變性
  - 控制狀態變更
```

### Bounded Context規劃
```yaml
Strategy Context (策略計算):
  - 基本策略表邏輯
  - 最佳決策計算
  - 策略驗證

Game Context (遊戲進行):
  - 遊戲狀態管理
  - 回合流程控制
  - 結果計算

Statistics Context (統計分析):
  - 玩家表現追蹤
  - 學習進度分析
  - 歷史數據管理
```

## 🧪 TDD實施指導

### 紅-綠-重構週期
```yaml
紅燈階段 (Red):
  目標: 寫一個失敗的測試
  檢查: 測試確實失敗，錯誤訊息有意義
  時間: 2-5分鐘

綠燈階段 (Green):
  目標: 讓測試通過的最簡實作
  檢查: 所有測試通過，實作滿足需求
  禁止: 過度設計，實作未測試的功能
  時間: 5-15分鐘

重構階段 (Refactor):
  目標: 改善代碼品質但保持測試通過
  檢查: 所有測試仍然通過，代碼更清晰
  重點: 消除重複，改善命名，提取方法
  時間: 5-20分鐘
```

### TDD狀態管理
```yaml
狀態追蹤: 在CLAUDE.local.md記錄當前TDD階段
中斷處理: 記錄當前狀態，恢復時繼續
安全網: 永遠保持測試通過的狀態
進度指標: 測試數量，覆蓋率，功能完成度
```

## 🔄 簡化CQRS設計

### Command側 (寫入)
```kotlin
// Command - 表達用戶意圖
sealed class GameCommand {
    object StartNewRound : GameCommand()
    data class PlayerAction(val action: Action) : GameCommand()
    object ShowHint : GameCommand()
    object ResetStatistics : GameCommand()
}

// Command Handler - 執行業務邏輯
class GameCommandHandler(
    private val gameRepository: GameRepository,
    private val strategyEngine: StrategyEngine
) {
    fun handle(command: GameCommand): GameEvent {
        return when (command) {
            is StartNewRound -> handleStartNewRound()
            is PlayerAction -> handlePlayerAction(command.action)
            // ...
        }
    }
}
```

### Query側 (讀取)
```kotlin
// Query Model - 為UI優化
data class GameStateView(
    val playerHand: HandView,
    val dealerHand: HandView,
    val availableActions: List<Action>,
    val recommendation: Action?,
    val isCorrectAction: Boolean?,
    val statistics: PlayerStatsView
)

// Query Handler - 讀取操作
class GameQueryService {
    fun getCurrentGameState(): GameStateView
    fun getPlayerStatistics(): PlayerStatsView
    fun getStrategyChart(rules: GameRules): StrategyChartView
}
```

### Event設計
```kotlin
// Domain Event - 重要業務事件
sealed class GameEvent {
    data class RoundStarted(
        val playerHand: Hand,
        val dealerUpCard: Card,
        val gameId: String
    ) : GameEvent()
    
    data class ActionTaken(
        val action: Action,
        val wasOptimal: Boolean,
        val playerHand: Hand,
        val timestamp: Long
    ) : GameEvent()
    
    data class RoundCompleted(
        val result: RoundResult,
        val finalPlayerHand: Hand,
        val finalDealerHand: Hand
    ) : GameEvent()
}
```

## 🚦 漸進式實施計劃

### 階段1: Domain + TDD (第1-2週)
```yaml
目標: 可運行的策略訓練核心
範圍: 
  - Card, Hand, StrategyEngine
  - 完整測試套件
  - 基本UI (直接使用Domain)

完成標準:
  - 100% Domain測試覆蓋
  - 所有策略表邏輯正確
  - 跨平台編譯成功

禁止:
  - 複雜設計模式
  - 過度抽象化
  - 預測性實作
```

### 階段2: Application + 簡化CQRS (第3-4週)
```yaml
觸發條件:
  - UI變得複雜 (>3個screen)
  - 需要統計功能
  - 狀態管理困難

目標: 分離讀寫關注點
範圍:
  - Command/Query分離
  - 基本事件系統
  - 統計功能

觸發評估:
  - UI複雜度評分 >0.6
  - 狀態管理問題出現
  - 新功能需求超出簡單實作
```

### 階段3: 完整CQRS (未來)
```yaml
觸發條件:
  - 多人模式需求
  - 複雜狀態管理
  - 事件溯源需要

實作:
  - Event Sourcing
  - 複雜Query Models
  - 分散式架構
```

## 🔍 架構驗證檢查

### Domain層純粹性檢查
```bash
# 檢查是否有外部依賴
grep -r "import" composeApp/src/commonMain/kotlin/*/domain/ | grep -v "kotlin"

# 檢查是否有副作用
grep -r "println\|log\|save\|load" composeApp/src/commonMain/kotlin/*/domain/

# 檢查測試覆蓋率
./gradlew :composeApp:commonTest --info
```

### 依賴方向驗證
```bash
# 確認Domain層不依賴其他層
grep -r "application\|presentation\|infrastructure" composeApp/src/commonMain/kotlin/*/domain/

# 確認Application層不直接訪問Infrastructure
grep -r "Repository" composeApp/src/commonMain/kotlin/*/application/ | grep "import"
```

## 📏 品質指標

### 架構健康度量
```yaml
Domain純粹性: 
  - 0個外部依賴
  - 100%測試覆蓋率
  - 0個副作用函數

CQRS分離度:
  - Command和Query完全分離
  - 讀寫模型針對不同需求優化
  - Event驅動的狀態更新

TDD依從度:
  - 所有功能都先有測試
  - 綠燈狀態維持率 >95%
  - 重構安全性 (測試保護)
```

### 架構債務警告
```yaml
技術債務信號:
  - Domain層出現外部依賴
  - 測試覆蓋率下降
  - 跨層直接調用
  - 業務邏輯散落在UI層

債務處理:
  - 立即停止新功能開發
  - 重構回到乾淨架構
  - 補齊缺失的測試
  - 記錄債務產生原因
```

---

*架構決策的權威參考，確保開發方向正確*  
*最後更新: 2025-08-31*