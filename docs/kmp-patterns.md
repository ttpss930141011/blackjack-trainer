# Kotlin Multiplatform 模式與最佳實踐

*防止平台特定錯誤和跨平台問題的技術參考*

## 🎯 專案結構模式

### 標準KMP目錄結構
```
composeApp/src/
├── commonMain/kotlin/          # 共享代碼 (90%+)
│   ├── domain/                 # 純Kotlin領域邏輯
│   ├── application/           # 使用案例層
│   └── presentation/          # 共享UI邏輯
├── androidMain/kotlin/         # Android特定實作
├── iosMain/kotlin/            # iOS特定實作  
├── jvmMain/kotlin/            # Desktop特定實作
├── wasmJsMain/kotlin/         # Web特定實作
└── commonTest/kotlin/         # 共享測試
```

### 代碼分布原則
```yaml
commonMain (90%): 
  - 所有業務邏輯
  - UI組件和狀態管理
  - 數據模型和算法

platformMain (10%):
  - 檔案存儲
  - 平台特定API
  - 效能優化
  - 系統整合
```

## 🔌 expect/actual 模式

### 正確使用方式
```kotlin
// commonMain: 定義平台介面
expect class PlatformStorage {
    fun saveGameStats(stats: PlayerStats)
    fun loadGameStats(): PlayerStats?
}

expect fun getCurrentTimestamp(): Long

// androidMain: Android實作
actual class PlatformStorage {
    actual fun saveGameStats(stats: PlayerStats) {
        // SharedPreferences實作
    }
    actual fun loadGameStats(): PlayerStats? {
        // SharedPreferences讀取
    }
}

// jvmMain: Desktop實作  
actual class PlatformStorage {
    actual fun saveGameStats(stats: PlayerStats) {
        // 檔案系統實作
    }
    actual fun loadGameStats(): PlayerStats? {
        // 檔案讀取
    }
}
```

### expect/actual 最佳實踐
```yaml
使用時機:
  - 平台特定API (檔案存儲, 時間, 系統資訊)
  - 效能優化 (平台特定算法)
  - 系統整合 (通知, 分享, 設備存取)

避免濫用:
  - 不要為了分離而分離
  - 避免expect/actual用於業務邏輯
  - 優先使用commonMain實作

設計原則:
  - 介面盡可能簡單
  - 避免平台特定類型在expect中
  - 使用普通Kotlin類型做參數和回傳值
```

## 🎨 Compose Multiplatform 模式

### 跨平台UI組件
```kotlin
// 共享UI組件 (commonMain)
@Composable
fun GameScreen(
    gameState: GameState,
    onAction: (Action) -> Unit
) {
    Column {
        DealerHandDisplay(gameState.dealerHand)
        PlayerHandDisplay(gameState.playerHand)
        ActionButtons(
            availableActions = gameState.availableActions,
            onAction = onAction
        )
    }
}

// 平台特定調整 (如果需要)
@Composable
expect fun PlatformSpecificActionButtons(
    actions: List<Action>,
    onAction: (Action) -> Unit
)
```

### 響應式設計模式
```kotlin
// 共享響應式邏輯
@Composable
fun ResponsiveGameLayout(
    windowSize: WindowSizeClass,
    content: @Composable () -> Unit
) {
    when (windowSize) {
        WindowSizeClass.Compact -> CompactLayout(content)
        WindowSizeClass.Medium -> MediumLayout(content)
        WindowSizeClass.Expanded -> ExpandedLayout(content)
    }
}

// 平台特定視窗大小檢測
expect fun getWindowSizeClass(): WindowSizeClass
```

## 📱 平台特定考量

### Android平台
```yaml
特殊考量:
  - 生命週期管理: Activity/Fragment lifecycle
  - 記憶體限制: 避免記憶體洩漏
  - 權限系統: 檔案存取權限
  - 多螢幕支援: 平板和手機適配

實作重點:
  - 使用ViewModel處理配置變更
  - 實作適當的back按鈕處理
  - 考慮深色模式支援
  - 遵循Material Design指導
```

### iOS平台
```yaml
特殊考量:
  - 記憶體管理: ARC和Kotlin互動
  - 生命週期: UIViewController lifecycle
  - 人機介面指南: 遵循Apple HIG
  - App Store審核: 避免違規行為

實作重點:
  - 適當的Navigation處理
  - iPhone和iPad適配
  - 深色模式和動態字型
  - 無障礙功能支援
```

### Web平台 (Wasm)
```yaml
特殊考量:
  - 載入時間: Wasm檔案大小優化
  - 瀏覽器相容性: 現代瀏覽器支援
  - DOM互動: 限制直接DOM操作
  - SEO考量: 單頁應用的限制

實作重點:
  - 漸進式載入
  - 適當的錯誤處理
  - 響應式設計
  - 無障礙功能支援
```

### Desktop平台 (JVM)
```yaml
特殊考量:
  - 視窗管理: 多視窗和調整大小
  - 系統整合: 檔案系統和系統通知
  - 效能: JVM啟動時間和記憶體使用
  - 部署: 原生安裝包建立

實作重點:
  - 視窗狀態持久化
  - 鍵盤快捷鍵支援
  - 系統主題偵測
  - 檔案拖放支援
```

## 🧪 跨平台測試策略

### 測試分層
```yaml
commonTest (共享測試):
  - Domain邏輯測試 (100%覆蓋)
  - Application邏輯測試
  - 共享UI組件測試

platformTest (平台測試):
  - expect/actual實作測試
  - 平台特定功能測試
  - 整合測試

UI測試:
  - 關鍵用戶流程
  - 跨平台一致性驗證
  - 效能和記憶體測試
```

### 測試執行命令
```bash
# 所有平台測試
./gradlew test

# 特定平台測試  
./gradlew :composeApp:testDebugUnitTest        # Android
./gradlew :composeApp:jvmTest                  # Desktop
./gradlew :composeApp:wasmJsTest               # Web

# 共享邏輯測試
./gradlew :composeApp:commonTest
```

## ⚠️ 常見陷阱與解決方案

### expect/actual 陷阱
```kotlin
// ❌ 錯誤: 平台特定類型洩漏
expect class PlatformFile(val file: java.io.File) // Java特定

// ✅ 正確: 使用通用接口
expect class PlatformFile {
    fun readText(): String
    fun writeText(content: String)
}

// ❌ 錯誤: 過度使用expect/actual
expect fun formatNumber(value: Double): String // 不需要平台特定

// ✅ 正確: 使用共享實作
fun formatNumber(value: Double): String = "%.2f".format(value)
```

### 依賴管理陷阱
```kotlin
// ❌ 錯誤: 在commonMain引用平台特定庫
import android.content.Context // Android特定

// ✅ 正確: 通過expect/actual抽象
expect class PlatformContext {
    fun getApplicationName(): String
}
```

### 資源管理陷阱
```yaml
問題: 各平台資源路徑不同
解決: 使用expect/actual封裝資源讀取

問題: 字體和圖片平台差異
解決: 提供平台特定資源或使用向量圖

問題: 本地化字串管理
解決: 統一的字串資源系統
```

## 🚀 效能優化模式

### 跨平台效能考量
```yaml
Wasm平台:
  - 最小化Bundle大小
  - 避免過度的物件創建
  - 優化初始載入時間

Mobile平台:
  - 記憶體使用優化
  - 電池使用效率
  - 響應時間優化

Desktop平台:
  - JVM垃圾收集優化
  - CPU使用效率
  - 視窗渲染效能
```

### 共享效能策略
```kotlin
// 延遲初始化共享資源
object StrategyTableCache {
    private val cache: Map<GameRules, StrategyTable> by lazy {
        generateAllStrategyTables()
    }
    
    fun getTable(rules: GameRules): StrategyTable = cache[rules]!!
}

// 不可變數據結構減少平台差異
data class GameState(
    val playerHand: Hand,
    val dealerHand: Hand,
    val availableActions: List<Action>
) {
    // 避免可變狀態的平台同步問題
}
```

## 🔧 建置與部署

### Gradle設定模式
```kotlin
// 正確的平台配置
kotlin {
    androidTarget()
    jvm("desktop")
    wasmJs {
        browser()
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
}
```

### 建置驗證命令
```bash
# 驗證所有平台編譯
./gradlew build

# 個別平台建置
./gradlew :composeApp:compileKotlinAndroid
./gradlew :composeApp:compileKotlinJvm  
./gradlew :composeApp:compileKotlinWasmJs
./gradlew :composeApp:compileKotlinIosX64
```

---

*跨平台開發的技術參考，避免平台特定問題*  
*適用版本: Kotlin 2.0+, Compose Multiplatform 1.7+*  
*最後更新: 2025-08-31*