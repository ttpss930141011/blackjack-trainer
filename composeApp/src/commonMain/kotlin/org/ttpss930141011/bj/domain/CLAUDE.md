# Domain Layer 記憶體指導

## 🎯 Domain層核心原則

### 純粹性要求
```yaml
強制規則:
  - 零外部依賴: 不能import任何非Kotlin標準庫的內容
  - 無副作用: 所有function都是純函數
  - 不變性: 優先使用data class和val
  - 測試驅動: 100%測試覆蓋率，先測試後實作

檢查清單:
  - 每個class都有對應測試
  - 沒有println或logging
  - 沒有platform-specific代碼
  - 沒有UI或persistence相關import
```

## 🃏 21點領域模型

### 核心實體設計模式
```kotlin
// 標準模板：不可變，職責單一
data class Card(val suit: Suit, val rank: Rank) {
    val value: Int get() = rank.blackjackValue
}

data class Hand(private val cards: List<Card>) {
    val hardValue: Int = cards.sumOf { it.value }
    val softValue: Int = if (hasAce) hardValue + 10 else hardValue
    val isSoft: Boolean = hasAce && softValue <= 21
    val bestValue: Int = if (isSoft) softValue else hardValue
    
    private val hasAce: Boolean = cards.any { it.rank == Rank.ACE }
}

// 領域服務模式
class StrategyEngine {
    fun getOptimalAction(
        playerHand: Hand,
        dealerUpCard: Card,
        gameRules: GameRules
    ): Action = when {
        // 策略表邏輯，參考 docs/blackjack-rules.md
    }
}
```

### 值物件設計
```kotlin
// 枚舉：領域概念的明確表達
enum class Action { HIT, STAND, DOUBLE, SPLIT, SURRENDER }
enum class Suit { HEARTS, DIAMONDS, CLUBS, SPADES }
enum class Rank(val blackjackValue: Int) {
    ACE(1), TWO(2), THREE(3), // ... 
    JACK(10), QUEEN(10), KING(10)
}

// 值物件：複雜概念的封裝
data class GameRules(
    val dealerHitsOnSoft17: Boolean = true,
    val doubleAfterSplit: Boolean = true,
    val surrenderAllowed: Boolean = true,
    val blackjackPayout: Double = 1.5
)
```

## 🧪 測試模式與標準

### TDD測試模板
```kotlin
class HandTest {
    @Test
    fun `given ace and 6 when calculating value then should be soft 17`() {
        // Given - 準備測試數據
        val hand = Hand(listOf(Card(HEARTS, ACE), Card(SPADES, SIX)))
        
        // When - 執行測試操作
        val value = hand.bestValue
        val isSoft = hand.isSoft
        
        // Then - 驗證期望結果
        assertEquals(17, value)
        assertTrue(isSoft)
    }
    
    @Test
    fun `given two 8s when checking can split then should return true`() {
        // 測試邊界條件和領域規則
    }
}
```

### 測試覆蓋要求
```yaml
必須測試:
  - 所有公開方法
  - 邊界條件 (Ace計算, 爆牌, 21點)
  - 業務規則 (分牌條件, 加倍規則)
  - 錯誤情況 (無效輸入, 空手牌)

測試命名規約:
  - 使用反引號包圍描述性名稱
  - given-when-then結構
  - 領域語言，不用技術術語
```

## 🚫 Domain層禁止事項

### 絕對不能出現
```kotlin
// ❌ 錯誤示例
class Hand {
    fun save() // persistence邏輯
    fun display() // UI邏輯  
    val viewModel: HandViewModel // UI污染
    private val repository: HandRepository // infrastructure污染
}

// ❌ 外部依賴
import androidx.compose.* // UI框架
import kotlinx.coroutines.* // 異步框架
import io.ktor.* // 網路框架
```

### 邊界違反警告
```yaml
警告信號:
  - 需要import非標準庫的dependency
  - 方法名包含save, load, display, show等動詞
  - 想要添加suspend function
  - 想要處理錯誤（應該在上層處理）

正確方式:
  - 使用純函數和不可變數據
  - 錯誤用sealed class或Result<T>表示
  - 異步操作在Application層處理
  - UI邏輯在Presentation層實作
```

## 💡 常見模式與反模式

### ✅ 推薦模式
```kotlin
// 策略模式：封裝決策邏輯
sealed class HandType {
    object Hard : HandType()
    object Soft : HandType()  
    object Pair : HandType()
}

// 工廠模式：創建複雜領域物件
object HandFactory {
    fun createFromCards(cards: List<Card>): Hand = Hand(cards)
}

// 規範模式：封裝業務規則
class CanSplitSpecification {
    fun isSatisfiedBy(hand: Hand): Boolean = 
        hand.cards.size == 2 && hand.cards[0].rank == hand.cards[1].rank
}
```

### ❌ 避免反模式
```kotlin
// 貧血模型：只有數據沒有行為
data class Hand(val cards: List<Card>) // 沒有業務邏輯

// 上帝類別：承擔太多責任  
class BlackjackGame { // 包含所有邏輯
    fun dealCards() 
    fun calculateValue()
    fun determineWinner()
    fun saveStats()
    fun renderUI()
}

// 領域洩漏：技術概念混入領域
data class Hand(val cards: List<Card>, val isDirty: Boolean) // persistence概念
```

## 📚 參考資源

### 內部文件
- `docs/blackjack-rules.md`: 21點規則權威參考
- `docs/architecture.md`: DDD實施指導
- `docs/kmp-patterns.md`: Kotlin Multiplatform模式

### 驗證方式
```bash
# Domain層驗證命令
./gradlew :composeApp:commonTest  # 執行domain測試
./gradlew build                   # 檢查編譯錯誤
```

---

*專用於Domain層，其他層請參考對應目錄的CLAUDE.md*  
*最後更新: 2025-08-31*