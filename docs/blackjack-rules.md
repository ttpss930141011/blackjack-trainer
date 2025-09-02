# 21點規則權威參考

*防止幻覺和規則猜測的官方參考文件*

## 🃏 基本規則

### 卡牌價值
```yaml
數字牌 (2-10): 面值
人頭牌 (J, Q, K): 10點
Ace: 1點 或 11點 (取較有利的值)
```

### 手牌計算
```yaml
硬手牌 (Hard Hand): 
  - 沒有Ace 或 Ace只能算1點
  - 例: 10+7=17, A+8+5=14 (Ace=1)

軟手牌 (Soft Hand):
  - 有Ace且可算11點而不爆牌
  - 例: A+6=17 (Ace=11), A+A+9=21 (一個Ace=11, 一個Ace=1)

爆牌 (Bust): 超過21點
21點 (Blackjack): 前兩張牌為Ace+10點牌
```

## 🎯 基本策略表規則

### 策略動作定義
```yaml
Hit (要牌): 再拿一張牌
Stand (停牌): 不再要牌，結束回合
Double (加倍): 下注加倍，只能再拿一張牌
Split (分牌): 同點數的兩張牌分成兩手
Surrender (投降): 放棄手牌，拿回一半賭注
```

### 莊家規則
```yaml
軟17規則:
  - Dealer Stands on Soft 17: 莊家軟17停牌
  - Dealer Hits on Soft 17: 莊家軟17要牌
  
固定模式:
  - 莊家必須在16點或以下要牌
  - 莊家必須在硬17或以上停牌
  - 莊家沒有選擇權，嚴格按規則行動
```

## 📊 策略表解讀

### 基本策略 (4-8副牌，莊家軟17停牌)
**硬手牌策略**:
```yaml
8以下: 永遠Hit
9: 對3-6加倍，其他Hit  
10: 對2-9加倍，其他Hit
11: 對2-10加倍，對A要Hit
12: 對4-6停牌，其他Hit
13-16: 對2-6停牌，其他Hit
17+: 永遠Stand
```

**軟手牌策略**:
```yaml
A,2-A,3: 對5-6加倍，其他Hit
A,4-A,5: 對4-6加倍，其他Hit  
A,6: 對3-6加倍，其他Hit
A,7: 對3-6加倍，對2,7,8停牌，對9,10,A要Hit
A,8-A,9: 永遠Stand
```

**分牌策略**:
```yaml
A,A: 永遠Split
8,8: 永遠Split
10,10: 永遠Stand (不分牌)
5,5: 永遠加倍 (當作10處理)
4,4: 永遠Hit (不分牌)

其他對子:
2,2: 對2-7分牌
3,3: 對2-7分牌  
6,6: 對2-6分牌
7,7: 對2-7分牌
9,9: 對2-9分牌 (除了7), 對7,10,A停牌
```

## 🔢 高級規則

### 投降 (Surrender)
```yaml
早期投降 (Early Surrender): 莊家檢查21點前可投降
晚期投降 (Late Surrender): 莊家檢查21點後可投降

投降時機:
- 硬16 vs 莊家9,10,A
- 硬15 vs 莊家10
- 軟17 vs 莊家A (某些規則下)
```

### 多副牌影響
```yaml
副牌數量對策略的微調:
- 單副牌: 分牌和加倍更激進
- 4-8副牌: 標準策略表適用
- 更多副牌: 保守策略，減少分牌
```

### 分牌後規則
```yaml
分牌後加倍 (DAS): 分牌後可以加倍
分牌Ace限制: 分牌Ace通常只能拿一張牌
再分牌限制: 某些賭場限制再分牌次數
```

## ⚠️ 實作注意事項

### Ace計算陷阱
```kotlin
// ❌ 錯誤: 簡單加法
fun calculateValue(cards: List<Card>): Int {
    return cards.sumOf { it.value } // Ace永遠是1
}

// ✅ 正確: 考慮Ace的11點價值
fun calculateValue(cards: List<Card>): Pair<Int, Int> {
    val hardValue = cards.sumOf { if (it.rank == ACE) 1 else it.value }
    val aces = cards.count { it.rank == ACE }
    val softValue = if (aces > 0 && hardValue + 10 <= 21) hardValue + 10 else hardValue
    return Pair(hardValue, softValue)
}
```

### 邊界條件
```yaml
特殊情況:
  - 多個Ace: A+A+9 = 21 (不是32)
  - 分牌Ace+10: 通常不算Blackjack
  - 空手牌: 應該拋出錯誤或回傳特殊值
  - 超過21點: 自動成為硬手牌

測試必須包含:
  - 所有Ace組合 (A, AA, AAA, AAAA等)
  - 邊界值 (20, 21, 22)
  - 分牌條件的所有組合
```

## 📈 策略準確性驗證

### 策略表驗證
```yaml
驗證來源: wizardofodds.com 策略表
測試方法: 對照每個格子的決策
邊界測試: 特別注意軟17規則差異

重要變化:
  - 莊家軟17停牌 vs 要牌
  - 分牌後加倍允許 vs 禁止  
  - 投降允許 vs 禁止
```

### 期望值計算
```yaml
基本策略期望回報: ~99.5% (0.5%莊家優勢)
完美基本策略: 不同規則組合影響±0.1-0.6%
常見錯誤影響: 每個錯誤決策約增加0.1-0.5%莊家優勢
```

---

*此文件為21點規則的權威參考，任何領域實作都必須以此為準*  
*來源: 數學證明的最佳策略表 (wizardofodds.com)*  
*最後更新: 2025-08-31*