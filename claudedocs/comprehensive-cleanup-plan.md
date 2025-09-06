# Comprehensive Codebase Cleanup Plan

## 🎯 Overview
This cleanup focuses on removing deprecated code, optimizing imports, eliminating dead code, and improving file organization while maintaining DDD architecture and TDD principles.

## 🚨 Pre-Cleanup Validation
- ✅ All tests are currently passing
- ✅ Build succeeds with 12 deprecation warnings (all related to GameStatusColors)
- ✅ No compilation errors exist

## 🧹 Cleanup Categories

### 1. Deprecated GameStatusColors Replacement (HIGH PRIORITY)
**Issue**: 12 deprecation warnings from GameStatusColors usage throughout codebase

**Files requiring GameStatusColors → CasinoTheme migration**:
- `CasinoGameScreen.kt` - Import unused, can be removed
- `CardDisplay.kt` - Used in bustColor and betColor (lines 79, 89)  
- `GameTable.kt` - Import unused, can be removed
- `DealerArea.kt` - Used in casinoGreen (lines 62, 95)
- `BettingCircle.kt` - Used in activeColor (line 48)
- `StatusOverlay.kt` - Used in statusOverlayColor (line 42)

**Migration Strategy**:
```kotlin
// Replace GameStatusColors usage with CasinoTheme equivalents
GameStatusColors.bustColor      → CasinoTheme.CasinoError
GameStatusColors.betColor       → CasinoTheme.CasinoAccentSecondary  
GameStatusColors.casinoGreen    → CasinoTheme.CasinoPrimary
GameStatusColors.activeColor    → CasinoTheme.CasinoPrimary
GameStatusColors.statusOverlayColor → Color.Black.copy(alpha = 0.5f)
```

### 2. Unused Import Cleanup (MEDIUM PRIORITY)
**Dead Imports Identified**:
- `CasinoGameScreen.kt` line 32: `import org.ttpss930141011.bj.presentation.design.GameStatusColors`
- `GameTable.kt` line 21: `import org.ttpss930141011.bj.presentation.design.GameStatusColors`

### 3. File Organization & Documentation (MEDIUM PRIORITY)
**Temporary/Obsolete Files**:
- `PHASE2-COMPLETION-SUMMARY.md` - Could be archived to `claudedocs/`
- `refactor-phase2-plan.md` - Could be archived to `claudedocs/` 
- Error log files in composeApp: `hs_err_pid*.log` files should be cleaned

**Project Structure Validation**:
- Domain layer: ✅ Pure, no external dependencies
- Application layer: ✅ Clean use cases and view models  
- Infrastructure layer: ✅ Repository implementations
- Presentation layer: ✅ UI components with proper separation

### 4. Code Optimization (LOW PRIORITY)
**Performance Improvements**:
- Consider lazy initialization where appropriate
- Validate @Composable function efficiency
- Review large file structures for potential modularization

## 📋 Execution Plan

### Phase 1: Critical Cleanup (GameStatusColors Migration)
1. Replace all GameStatusColors usage with CasinoTheme equivalents
2. Remove unused GameStatusColors imports  
3. Validate build with zero warnings
4. Run full test suite to ensure functionality preserved

### Phase 2: Import & File Cleanup
1. Remove unused imports across all files
2. Clean up temporary files and logs
3. Organize documentation files appropriately
4. Validate clean build

### Phase 3: Validation & Testing
1. Run full test suite: `./gradlew test`
2. Build all platforms: `./gradlew build`
3. Validate zero warnings/errors
4. Performance smoke test

## ⚠️ Safety Measures
- **TDD Protection**: All tests must pass before and after each phase
- **DDD Preservation**: Domain layer must remain pure with no external dependencies
- **Atomic Changes**: Each file change tested individually
- **Backup Points**: Git commits after each successful phase

## 📊 Expected Outcomes
- **Zero build warnings** (eliminate all 12 GameStatusColors deprecation warnings)
- **Cleaner imports** (remove ~6-8 unused import statements)
- **Better file organization** (archive obsolete documentation)
- **Improved maintainability** (consistent use of CasinoTheme)
- **Performance validation** (ensure no regressions)

## 🔧 Validation Commands
```bash
# Build validation
./gradlew clean build

# Test validation  
./gradlew test

# Warning check
./gradlew build 2>&1 | grep -E "(WARNING|deprecated)"

# Import analysis (manual review of each file)
grep -r "import.*GameStatusColors" composeApp/src/
```

---
*Created: 2025-09-06*
*Status: Ready for execution*