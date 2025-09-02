# 品質門檻與驗證機制

*防止低品質代碼和架構債務的自動化檢查系統*

## 🚦 品質門檻系統

### 8級品質檢查流程
```yaml
Level 1 - 語法檢查:
  工具: Kotlin編譯器
  標準: 零編譯錯誤
  自動化: ./gradlew compileKotlin
  
Level 2 - 型別檢查:
  工具: Kotlin型別系統
  標準: 零型別錯誤，正確的nullability
  自動化: 編譯過程自動檢查

Level 3 - 代碼風格:
  工具: Kotlin編譯器警告
  標準: 零警告，一致的命名規範
  自動化: ./gradlew build --warning-mode all

Level 4 - 安全檢查:
  工具: 手動review + 靜態分析
  標準: 無明顯安全問題，輸入驗證
  檢查點: Domain邊界，使用者輸入處理

Level 5 - 測試覆蓋:
  工具: 單元測試 + 整合測試
  標準: Domain層100%, Application層80%+
  自動化: ./gradlew test

Level 6 - 效能檢查:
  工具: 手動profile + benchmark
  標準: 響應時間<100ms, 記憶體使用合理
  檢查點: 算法複雜度，記憶體洩漏

Level 7 - 文件完整性:
  工具: 手動review
  標準: 重要決策有文件，API有註解
  檢查點: ADR更新，複雜邏輯說明

Level 8 - 整合驗證:
  工具: 跨平台測試
  標準: 所有平台正常運行
  自動化: 各平台建置和基本測試
```

## 🔍 自動化檢查腳本

### 完整品質檢查
```bash
#!/bin/bash
# quality-check.sh - 完整品質門檻檢查

echo "🔍 開始品質檢查..."

# Level 1-3: 編譯和語法檢查
echo "📝 檢查編譯和語法..."
./gradlew build --warning-mode all
if [ $? -ne 0 ]; then
    echo "❌ 編譯失敗，請修復後重試"
    exit 1
fi

# Level 5: 測試執行
echo "🧪 執行測試套件..."
./gradlew test
if [ $? -ne 0 ]; then
    echo "❌ 測試失敗，請修復後重試"
    exit 1
fi

# Level 4: 架構純粹性檢查
echo "🏗️ 檢查架構純粹性..."
DOMAIN_VIOLATIONS=$(grep -r "import.*\(android\|compose\|ktor\)" composeApp/src/commonMain/kotlin/*/domain/ 2>/dev/null | wc -l)
if [ $DOMAIN_VIOLATIONS -gt 0 ]; then
    echo "❌ Domain層發現外部依賴違規"
    grep -r "import.*\(android\|compose\|ktor\)" composeApp/src/commonMain/kotlin/*/domain/
    exit 1
fi

# Level 8: 跨平台編譯檢查
echo "🌐 檢查跨平台編譯..."
./gradlew :composeApp:compileKotlinAndroid :composeApp:compileKotlinJvm :composeApp:compileKotlinWasmJs
if [ $? -ne 0 ]; then
    echo "❌ 跨平台編譯失敗"
    exit 1
fi

echo "✅ 所有品質檢查通過！"
```

### 快速檢查 (開發中使用)
```bash
#!/bin/bash
# quick-check.sh - 快速品質檢查

echo "⚡ 快速品質檢查..."

# 基本編譯
./gradlew :composeApp:compileKotlinCommon
if [ $? -ne 0 ]; then
    echo "❌ 共享代碼編譯失敗"
    exit 1
fi

# Domain測試
./gradlew :composeApp:commonTest
if [ $? -ne 0 ]; then
    echo "❌ Domain測試失敗"
    exit 1
fi

echo "✅ 快速檢查通過"
```

## 📊 測試覆蓋率標準

### 覆蓋率要求
```yaml
Domain層: 100%覆蓋率
  原因: 核心業務邏輯，錯誤代價最高
  檢查: 每個public方法都有測試
  例外: 無例外情況

Application層: 80%+覆蓋率
  原因: 編排邏輯，重要但不如domain關鍵
  檢查: 主要use case有測試
  例外: 簡單的delegation方法可略過

Presentation層: 選擇性覆蓋
  原因: UI邏輯變化快，維護成本高
  檢查: 關鍵用戶流程有E2E測試
  重點: 互動邏輯而非視覺外觀

Infrastructure層: 整合測試
  原因: 外部依賴，需要真實環境測試
  檢查: Repository實作的正確性
  方法: 使用memory實作進行測試
```

### 測試品質指標
```yaml
測試命名: 使用given-when-then格式，描述性名稱
測試隔離: 每個測試獨立，無共享狀態
測試速度: 單元測試<10ms, 整合測試<100ms
測試可讀性: 測試本身就是活文件
```

## 🛡️ 架構債務預防

### 自動警告系統
```bash
# 架構違規檢查
check-architecture-violations() {
    echo "🔍 檢查架構違規..."
    
    # Domain層純粹性
    if grep -r "\.save\|\.load\|\.display" composeApp/src/commonMain/kotlin/*/domain/ 2>/dev/null; then
        echo "⚠️ Domain層發現副作用方法"
    fi
    
    # 循環依賴檢查
    if grep -r "presentation" composeApp/src/commonMain/kotlin/*/domain/ 2>/dev/null; then
        echo "⚠️ Domain層依賴Presentation層"
    fi
    
    # 測試缺失檢查
    DOMAIN_FILES=$(find composeApp/src/commonMain/kotlin/*/domain/ -name "*.kt" | wc -l)
    TEST_FILES=$(find composeApp/src/commonTest/kotlin/ -name "*Test.kt" | wc -l)
    if [ $TEST_FILES -lt $DOMAIN_FILES ]; then
        echo "⚠️ 測試檔案數量少於domain檔案"
    fi
}
```

### 技術債務預警
```yaml
債務信號:
  - 測試覆蓋率下降 >5%
  - 編譯警告數量增加
  - Domain層出現外部import
  - TDD週期被跳過

債務處理流程:
  1. 立即停止新功能開發
  2. 分析債務根本原因
  3. 制定還債計劃
  4. 執行重構並驗證
  5. 更新預防機制
```

## 📈 持續改進機制

### 每週品質回顧
```yaml
檢查項目:
  - 測試覆蓋率趨勢
  - 編譯時間變化
  - 代碼複雜度指標
  - 架構純粹性維持

改進行動:
  - 識別品質下降原因
  - 調整開發流程
  - 更新檢查腳本
  - 記錄學習心得
```

### 品質指標追蹤
```yaml
關鍵指標:
  domain_test_coverage: ">= 100%"
  application_test_coverage: ">= 80%"
  build_time: "< 30秒"
  test_execution_time: "< 10秒"
  architecture_violations: "= 0"

追蹤方式:
  - 每次commit後自動檢查
  - 趨勢分析和預警
  - 品質退化時立即通知
```

## 🚨 緊急處理協議

### 品質危機響應
```yaml
Level 1 (輕微): 1-2項檢查失敗
  行動: 當日修復，記錄原因
  
Level 2 (中等): 3-5項檢查失敗 
  行動: 停止新功能，專注修復
  
Level 3 (嚴重): >5項檢查失敗
  行動: 回滾到最後穩定版本，重新規劃

恢復流程:
  1. 停止所有開發活動
  2. 評估損害範圍
  3. 制定修復計劃
  4. 逐步恢復品質
  5. 分析失敗原因
  6. 更新預防機制
```

### 回滾策略
```yaml
安全回滾點:
  - 每個TDD週期完成後
  - 所有測試通過的commit
  - 架構檢查全過的狀態

回滾觸發:
  - 無法在2小時內修復的品質問題
  - 架構純粹性嚴重破壞
  - 測試覆蓋率急劇下降

回滾執行:
  1. git log --oneline -10 (找到最後穩定點)
  2. git reset --hard <stable-commit>
  3. 重新開始，更小步驟進行
```

## 🔧 工具整合

### IDE整合建議
```yaml
IntelliJ IDEA設定:
  - 啟用Kotlin警告
  - 自動執行測試
  - 顯示測試覆蓋率
  - 架構圖視覺化

自動化觸發:
  - 保存時執行快速檢查
  - commit前執行完整檢查
  - push前執行跨平台驗證
```

### Git Hooks整合
```bash
# pre-commit hook
#!/bin/bash
echo "🔍 Pre-commit品質檢查..."
./docs/scripts/quick-check.sh
if [ $? -ne 0 ]; then
    echo "❌ 品質檢查失敗，commit被阻止"
    exit 1
fi
```

---

*自動化品質保證系統，確保代碼品質始終維持高標準*  
*整合到開發流程中，預防勝於治療*  
*最後更新: 2025-08-31*