# Tech Debt Audit — 2026-03-05

## Top 5 (按優先序)

### 1. ✅ GameViewModel.kt (545→276 行) — God Class
- 抽出 BettingManager, PreferencesManager, ActionResultFactory

### 2. ✅ Game.kt (345→218 行) — Aggregate Root 過重
- 抽出 BettingPolicy domain service

### 3. ✅ RoomPersistenceRepository.kt — Unsafe Casts
- 加 @Suppress + 註釋說明 cast 安全性

### 4. ✅ Hardcoded Strings (40+ 處)
- 建立 Strings object，替換 ~25 處

### 5. ⬜ Wildcard Imports (28 處)
- GameViewModel 已完成，其餘待處理

## 其他問題

### Major
- [x] ActionArea.kt — empty catch block → 改為 phase check
- [ ] App.kt (165 行) — 3 個分散的 LaunchedEffect
- [ ] StrategySection.kt (636 行) — 大但 cohesive，暫不拆

### Minor
- [x] Magic numbers → Tokens (SettlementReview, Header)
- [ ] @Suppress("UNCHECKED_CAST") in DataLoader.kt
- [ ] 中英混雜註解

### ✅ 通過的檢查
- Clean Architecture 分層隔離 ✅
- 無 circular dependency ✅
- Domain services stateless ✅
- Naming conventions consistent ✅
