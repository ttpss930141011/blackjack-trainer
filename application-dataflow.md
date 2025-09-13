# Application Layer 數據流架構

## 完整的數據流圖表

```mermaid
graph TB
    %% UI層
    UI[CasinoGameScreen/UI Components]
    
    %% 主要協調器
    VM[GameViewModel<br/>Main Coordinator]
    
    %% 四個專用管理器
    GSM[GameStateManager<br/>Game State]
    FM[FeedbackManager<br/>Decision Feedback]
    AM[AnalyticsManager<br/>Learning Stats]
    USM[UIStateManager<br/>UI State]
    
    %% 應用服務
    GS[GameService<br/>Domain Wrapper]
    DE[DecisionEvaluator<br/>Strategy Evaluation]
    PS[PersistenceService<br/>Dual-Stream Persistence]
    
    %% 域服務
    SE[StrategyEngine<br/>Domain Service]
    RM[RoundManager<br/>Domain Service]
    SS[SettlementService<br/>Domain Service]
    
    %% 持久化
    Repo[PersistenceRepository<br/>Interface]
    InMem[InMemoryPersistence<br/>Development]
    Room[RoomPersistence<br/>Production]
    
    %% 數據流
    UI -->|User Actions| VM
    VM -->|Delegate| GSM
    VM -->|Delegate| FM
    VM -->|Delegate| AM
    VM -->|Delegate| USM
    
    GSM -->|Game Operations| GS
    GS -->|Domain Logic| RM
    GS -->|Domain Logic| SS
    
    FM -->|Strategy Check| DE
    DE -->|Optimal Action| SE
    
    AM -->|Persist Data| PS
    PS -->|Dual Stream| Repo
    Repo -.->|Dev Mode| InMem
    Repo -.->|Production| Room
    
    %% 返回數據流
    SE -->|Feedback| DE
    DE -->|Decision Result| FM
    RM -->|Game State| GS
    GS -->|Updated Game| GSM
    GSM -->|State| VM
    FM -->|Feedback| VM
    AM -->|Analytics| VM
    VM -->|Render Data| UI
```

## 雙流持久化架構

```mermaid
graph LR
    %% 用戶操作
    Action[Player Action]
    
    %% 決策評估
    DE[DecisionEvaluator]
    
    %% 持久化服務
    PS[PersistenceService<br/>Dual-Stream]
    
    %% 兩個數據流
    DR[DecisionRecord<br/>Atomic Decision Data]
    RH[RoundHistory<br/>Complete Round Context]
    
    %% 存儲
    Repo[PersistenceRepository]
    
    %% 用途
    Stats[Statistics Page<br/>Cross-game Analytics]
    History[History Page<br/>User Replay]
    
    Action --> DE
    DE -->|Evaluate| PS
    PS -->|Stream 1| DR
    PS -->|Stream 2| RH
    DR --> Repo
    RH --> Repo
    DR -->|Analytics Data| Stats
    RH -->|Complete Context| History
```

## 管理器職責分離

```mermaid
graph TD
    VM[GameViewModel<br/>305 lines → 4 Managers]
    
    subgraph "Specialized Managers"
        GSM[GameStateManager<br/>- initializeGame<br/>- startRound<br/>- executePlayerAction<br/>- processDealerTurn]
        
        FM[FeedbackManager<br/>- evaluatePlayerAction<br/>- roundDecisions<br/>- clearFeedback]
        
        AM[AnalyticsManager<br/>- recordPlayerAction<br/>- sessionStats<br/>- getRoundHistory]
        
        USM[UIStateManager<br/>- setError<br/>- calculateChipComposition<br/>- handleRuleChange]
    end
    
    VM -.->|Delegate| GSM
    VM -.->|Delegate| FM
    VM -.->|Delegate| AM
    VM -.->|Delegate| USM
    
    style VM fill:#ffcccc
    style GSM fill:#ccffcc
    style FM fill:#ccccff
    style AM fill:#ffffcc
    style USM fill:#ffccff
```

## 關鍵數據流解釋

### 1. 遊戲操作流程
1. **UI** → **GameViewModel** → **GameStateManager** → **GameService** → **Domain Services**
2. 遊戲狀態更新後，返回路徑相同但反向

### 2. 決策評估流程
1. **Player Action** → **FeedbackManager** → **DecisionEvaluator** → **StrategyEngine**
2. **Strategy Result** → **DecisionFeedback** → **UI Feedback**

### 3. 持久化流程（雙流）
- **決策記錄流**: `DecisionRecord` → 統計分析用途
- **回合歷史流**: `RoundHistory` → 完整回放用途

### 4. 分離的好處
- **單一職責**: 每個管理器專注一個領域
- **可測試性**: 獨立測試各個管理器
- **向後兼容**: GameViewModel API 保持不變
- **維護性**: 問題定位更精確