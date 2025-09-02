#!/bin/bash
# quick-check.sh - 快速品質檢查腳本 (開發中使用)

echo "⚡ 快速品質檢查..."

# 基本編譯檢查
echo "📝 檢查共享代碼編譯..."
./gradlew :composeApp:compileKotlinJvm
if [ $? -ne 0 ]; then
    echo "❌ 共享代碼編譯失敗"
    exit 1
fi

# Domain測試 (最重要的測試)
echo "🧪 執行Domain測試..."
./gradlew :composeApp:commonTest
if [ $? -ne 0 ]; then
    echo "❌ Domain測試失敗"
    exit 1
fi

# 快速架構檢查
echo "🏗️ 快速架構檢查..."
DOMAIN_IMPORTS=$(find composeApp/src/commonMain/kotlin -path "*/domain/*" -name "*.kt" -exec grep -c "^import" {} \; 2>/dev/null | awk '{sum += $1} END {print sum+0}')
if [ $DOMAIN_IMPORTS -gt 10 ]; then
    echo "⚠️ Domain層import數量較多 ($DOMAIN_IMPORTS)，請檢查是否有不必要的依賴"
fi

# TDD狀態提醒
if [ -f "CLAUDE.local.md" ]; then
    TDD_STATUS=$(grep -A 1 "當前階段:" CLAUDE.local.md | tail -1 | sed 's/.*: //')
    if [ "$TDD_STATUS" != "未開始" ] && [ "$TDD_STATUS" != "" ]; then
        echo "🔄 TDD狀態: $TDD_STATUS"
    fi
fi

echo "✅ 快速檢查完成"
echo ""
echo "💡 提醒: 使用 ./docs/scripts/quality-check.sh 進行完整檢查"