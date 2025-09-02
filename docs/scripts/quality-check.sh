#!/bin/bash
# quality-check.sh - 完整品質門檻檢查腳本

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
DOMAIN_VIOLATIONS=$(find composeApp/src/commonMain/kotlin -path "*/domain/*" -name "*.kt" -exec grep -l "import.*\(android\|compose\|ktor\|kotlinx\.coroutines\)" {} \; 2>/dev/null | wc -l)
if [ $DOMAIN_VIOLATIONS -gt 0 ]; then
    echo "❌ Domain層發現外部依賴違規:"
    find composeApp/src/commonMain/kotlin -path "*/domain/*" -name "*.kt" -exec grep -l "import.*\(android\|compose\|ktor\|kotlinx\.coroutines\)" {} \; 2>/dev/null
    exit 1
fi

# 檢查Domain層是否有副作用方法
SIDE_EFFECTS=$(find composeApp/src/commonMain/kotlin -path "*/domain/*" -name "*.kt" -exec grep -l "fun.*\(save\|load\|display\|show\|print\)" {} \; 2>/dev/null | wc -l)
if [ $SIDE_EFFECTS -gt 0 ]; then
    echo "⚠️ Domain層發現潛在副作用方法:"
    find composeApp/src/commonMain/kotlin -path "*/domain/*" -name "*.kt" -exec grep -n "fun.*\(save\|load\|display\|show\|print\)" {} \; 2>/dev/null
fi

# 檢查測試覆蓋率 (基本檢查)
DOMAIN_FILES=$(find composeApp/src/commonMain/kotlin -path "*/domain/*" -name "*.kt" | wc -l)
TEST_FILES=$(find composeApp/src/commonTest/kotlin -name "*Test.kt" | wc -l)
if [ $DOMAIN_FILES -gt 0 ] && [ $TEST_FILES -eq 0 ]; then
    echo "⚠️ 發現Domain檔案但沒有測試檔案"
elif [ $DOMAIN_FILES -gt 0 ] && [ $TEST_FILES -lt $((DOMAIN_FILES / 2)) ]; then
    echo "⚠️ 測試檔案數量可能不足 (Domain: $DOMAIN_FILES, Tests: $TEST_FILES)"
fi

# Level 8: 跨平台編譯檢查
echo "🌐 檢查跨平台編譯..."
./gradlew :composeApp:compileKotlinAndroid :composeApp:compileKotlinJvm :composeApp:compileKotlinWasmJs
if [ $? -ne 0 ]; then
    echo "❌ 跨平台編譯失敗"
    exit 1
fi

echo "✅ 所有品質檢查通過！"
echo ""
echo "📊 品質摘要:"
echo "   Domain檔案: $DOMAIN_FILES"
echo "   測試檔案: $TEST_FILES"
echo "   架構違規: 0"
echo "   編譯狀態: ✅ 通過"
echo "   測試狀態: ✅ 通過"