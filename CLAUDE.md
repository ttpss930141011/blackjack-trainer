# CLAUDE.md - Blackjack Strategy Trainer Memory System

## 🎯 專案概述
Kotlin Multiplatform 21點策略訓練器，採用 DDD + TDD + 漸進式CQRS 架構

## 🛡️ 防錯機制 (Anti-Pattern Prevention)

### 1. 錯誤預防 (Error Prevention)
```yaml
架構驗證規則:
  - Domain層: 純邏輯，無外部依賴，100%測試覆蓋
  - Application層: 使用case驅動，清楚的輸入輸出
  - Infrastructure層: 實作細節，可替換

必須檢查清單:
  - 新增任何21點邏輯前，先檢查 docs/blackjack-rules.md
  - 修改策略引擎前，確認現有測試仍通過
  - 跨平台代碼前，檢查 docs/kmp-patterns.md
```

### 2. 幻覺防護 (Hallucination Prevention)
```yaml
驗證協議:
  - 任何21點規則引用必須標註來源 (docs/blackjack-rules.md)
  - Kotlin/Compose API使用前先確認版本兼容性
  - 不確定時明確說"需要驗證"，不要猜測
  
知識邊界:
  確定: Kotlin語法, Compose基礎, 21點規則
  需驗證: 特定API版本, 平台特定實作, 第三方庫
  禁止猜測: 效能數據, 特定版本行為, 複雜算法實作
```

### 3. 亂重構防止 (Random Refactoring Prevention)
```yaml
重構安全協議:
  前置條件:
    - 所有測試通過 (./gradlew test)
    - 明確的重構目標和理由
    - 估算影響範圍和風險

  執行規則:
    - 一次只重構一個模組
    - 保持TDD綠燈狀態
    - 重構後立即驗證平台兼容性

  禁止行為:
    - 「順便」重構無關代碼
    - 沒有測試保護的重構
    - 跨層級的大規模重構
```

### 4. 唬爛防範 (BS Prevention) 
```yaml
誠實協議:
  承認不確定: "我不確定這個API的行為，需要查文件"
  請求澄清: "您的需求是X還是Y？"
  標記推測: "根據常見模式，可能是...，但需要驗證"

知識邊界聲明:
  - Kotlin Multiplatform: 基礎了解，特定功能需查證
  - 21點策略: 基本規則清楚，複雜變化需參考文件
  - Compose跨平台: 需要實際測試驗證行為差異
```

### 5. 過度工程控制 (Over-Engineering Prevention)
```yaml
漸進式原則:
  階段1: 純Domain + 基本UI (2-3週)
    目標: 可運行的策略訓練器
    限制: 不使用複雜設計模式
    
  階段2: Application層 + CQRS分離 (2-4週)
    觸發條件: UI變複雜 OR 需要統計功能
    驗證: 確實需要才實作
    
  階段3: 完整CQRS + Event Sourcing (未來)
    觸發條件: 多人模式 OR 複雜狀態管理需求

複雜度門檻:
  - 新增抽象層前，先證明必要性
  - 超過3個類別互動時才考慮中介模式
  - 設計模式必須解決實際問題，非理論需要
```

## 📋 開發原則 (Development Principles)

### DDD實施指導
```kotlin
// 正確：純領域概念
data class Hand(private val cards: List<Card>) {
    val value: HandValue
    val isSoft: Boolean
    fun canSplit(): Boolean = cards.size == 2 && cards[0].rank == cards[1].rank
}

// 錯誤：混合技術概念
data class Hand(val cards: List<Card>, val viewModel: HandViewModel) // 領域污染
```

### TDD週期追蹤
```yaml
紅燈階段: 寫失敗測試，確認測試有意義
綠燈階段: 最簡實作讓測試通過，不過度設計
重構階段: 改善代碼品質，但保持測試通過

狀態檢查:
  - 永遠保持在已知的TDD狀態
  - 中斷前記錄當前TDD階段
  - 恢復時從記錄的狀態繼續
```

### 平台特定規則
```kotlin
// 使用expect/actual進行平台抽象
expect class PlatformSpecificStorage {
    fun saveGameStats(stats: PlayerStats)
    fun loadGameStats(): PlayerStats?
}

// 避免平台洩漏到domain
// 錯誤：domain/Hand.kt 依賴 androidx.lifecycle
// 正確：domain/Hand.kt 純Kotlin，無外部依賴
```

## 🏗️ 架構決策記錄 (ADR)

### ADR-001: 採用漸進式架構
**日期**: 2025-08-31  
**狀態**: 已決定  
**決策**: 從DDD+TDD開始，根據需要漸進引入CQRS  
**原因**: 避免過度工程，保持學習價值  
**影響**: 每階段都有可運行的產品

### ADR-002: Kotlin Multiplatform with Compose
**日期**: 2025-08-31  
**狀態**: 已決定  
**決策**: 使用Compose Multiplatform作為UI框架  
**原因**: 單一代碼庫支援多平台  
**影響**: 需要了解平台差異和限制

## 🎮 領域知識 (Domain Knowledge)

### 21點策略核心
```yaml
基本策略表: 莊家軟17規則影響決策
卡牌計算: Ace = 1或11, J/Q/K = 10
可分牌條件: 同rank兩張牌
可加倍條件: 前兩張牌
投降規則: 某些情況下最佳選擇
```

### 策略實作邊界
```yaml
確定實作: 基本策略表邏輯
需要驗證: 特殊規則變化 (投降、多副牌等)
平台差異: UI互動方式、儲存機制
```

## 🧪 測試策略

### 測試層級
```yaml
Domain Tests (100%覆蓋): 
  - Card, Hand, StrategyEngine
  - 所有21點規則邏輯
  - 邊界條件和特殊情況

Application Tests (80%覆蓋):
  - Use cases and command handlers
  - 業務流程和狀態管理

UI Tests (選擇性):
  - 關鍵用戶流程
  - 平台特定功能
```

### 測試優先級
```yaml
高優先級: 策略決策邏輯, 卡牌計算, 分牌邏輯
中優先級: 統計計算, 遊戲狀態管理
低優先級: UI互動, 動畫效果
```

## 🔧 工具鏈 (Toolchain)

### 開發命令
```bash
# 主要開發命令 (網頁版)
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# 測試與品質
./gradlew test                                    # 所有測試
./gradlew :composeApp:testDebugUnitTest          # Android單元測試
./gradlew :composeApp:commonTest                 # 共用平台測試

# 建置各平台
./gradlew build                                  # 全平台建置
./gradlew :composeApp:runDebug                   # 桌面版
./gradlew :composeApp:packageDebugAab            # Android debug
```

### 品質檢查
```yaml
編譯檢查: Kotlin編譯器即為主要品質檢查
測試執行: 必須在每次commit前執行
平台測試: 定期在各目標平台驗證
```

## 📝 會話協議 (Session Protocol)

### 開始會話
1. 讀取當前ADR狀態
2. 檢查最後的TDD階段
3. 確認當前開發重點

### 進行中
1. 更新相關記憶體文件
2. 記錄重要決策和學習
3. 保持TDD狀態追蹤

### 結束會話  
1. 記錄當前TDD狀態
2. 更新學習心得
3. 標記未完成項目

## ⚠️ 開發警告

### 絕對避免
- 在domain層引用UI或persistence框架
- 沒有測試保護的重構
- 跨平台假設（每個平台都要驗證）
- 複製網路上的21點規則（使用docs/blackjack-rules.md）

### 謹慎處理
- expect/actual平台抽象（容易出錯）
- Ace的1/11計算邏輯（邊界條件多）
- 多副牌對策略的影響
- 記憶體管理（特別是Web平台）

---

*最後更新: 2025-08-31*  
*版本: 1.0 - 初始架構建立*