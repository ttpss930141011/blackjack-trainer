# Tech Debt Audit — 2026-03-05

## Top 5 (按優先序)

### 1. ✅ GameViewModel.kt (545 行) — God Class
- 18 public functions, 60 public members, 7 dependencies
- 混雜: 遊戲邏輯 + UI 狀態 + 持久化 + 分析 + 音訊
- **修法**: 拆成 GamePlayViewModel / BettingViewModel / HistoryViewModel / PreferencesViewModel

### 2. Game.kt (345 行) — Aggregate Root 過重
- 16 public functions
- 下注邏輯 (tryAddChipToBet 28 行) 應該抽出 BettingPolicy
- **修法**: 簡化為 state holder，複雜邏輯委派 domain services

### 3. RoomPersistenceRepository.kt — Unsafe Casts
- 行 95, 101, 112, 116: `as? T`, `as List<T>`
- **修法**: 用 when + reified generics 取代

### 4. Hardcoded Strings (40+ 處)
- ActionArea.kt, GameTable.kt, Header, Navigation 等
- **修法**: 集中到 Strings object 或 string resources

### 5. Wildcard Imports (28 處)
- `.*` imports 散布各處
- **修法**: 展開為 explicit imports

## 其他問題

### Major
- [ ] App.kt (165 行) — 3 個分散的 LaunchedEffect，導航邏輯混雜
- [ ] ActionArea.kt 行 343 — empty catch block (swallowed exception)
- [ ] StrategySection.kt (636 行) — presentation 最大檔案，應拆分

### Minor
- [ ] @Suppress("UNCHECKED_CAST") in DataLoader.kt 行 49
- [ ] Magic numbers in presentation (8.dp, 12.dp, 14.sp 等未用 Tokens)
- [ ] 中英混雜註解，應統一

### ✅ 通過的檢查
- Clean Architecture 分層隔離 ✅
- 無 circular dependency ✅
- Domain services stateless ✅
- Naming conventions consistent ✅
