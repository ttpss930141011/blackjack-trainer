# CI/CD Pipeline Documentation

## 🚀 Overview

Comprehensive CI/CD pipeline for Blackjack Strategy Trainer - a Kotlin Multiplatform project with robust quality gates, security scanning, and multi-platform testing.

## 📊 Pipeline Architecture

### 🔄 Main CI Pipeline (`ci.yml`)
**Triggers**: Push to master/main/develop, PRs to master/main, manual dispatch

**Jobs Flow**:
```
Quality Gates → Multi-Platform Tests → Multi-Platform Builds
     ↓              ↓                      ↓
Security Scan → E2E Tests → Performance → Release (tags only)
     ↓
CI Summary
```

**Key Features**:
- ⚡ **Fast Feedback**: Quality gates complete in <15 minutes
- 🧪 **Comprehensive Testing**: JVM, Android, Common, WASM platforms
- 📦 **Multi-Platform Builds**: Android APK, Desktop (Windows/Mac/Linux), Web WASM
- 🛡️ **Security Integration**: CodeQL analysis and vulnerability scanning
- 🎯 **Performance Monitoring**: Bundle size analysis and build performance

### 🧪 Testing Strategy (`test-strategy.yml`)
**Triggers**: Weekly schedule (Monday 2 AM), manual dispatch with scope selection

**Testing Layers**:
- **Domain Tests**: Core business logic (Cards, Hands, Strategy Engine)
- **Integration Tests**: Cross-layer validation and use cases
- **Performance Tests**: Strategy engine benchmarks and WASM bundle analysis
- **Platform Compatibility**: Compilation validation across all targets

### 🛡️ Security Pipeline (`security.yml`)
**Triggers**: Push/PR to master/main, weekly security scans (Monday 6 AM)

**Security Layers**:
- **Static Analysis**: CodeQL with security queries
- **Pattern Scanning**: Hardcoded secrets, SQL injection, unsafe deserialization
- **Dependency Audit**: SBOM generation and vulnerability scanning
- **Build Security**: Reproducible builds and wrapper integrity

### ✅ PR Quality Gates (`pr-checks.yml`)
**Triggers**: PR opened/updated/ready for review

**Validation Gates**:
- **Fast Compilation**: Metadata and common compilation check
- **Quick Tests**: Domain layer tests for immediate feedback
- **Architecture Compliance**: Domain layer purity validation
- **Change Impact**: Analysis of modifications and test coverage

## 🎯 Platform Support

| Platform | Build Task | Test Task | Artifacts |
|----------|------------|-----------|-----------|
| **Android** | `assembleDebug/Release` | `testDebugUnitTest` | APK files |
| **Desktop JVM** | `packageUberJarForCurrentOS` | `jvmTest` | Executable JAR |
| **Web WASM** | `wasmJsBrowserDistribution` | `wasmJsTest` | Web bundle |
| **Windows** | `packageMsi` | `jvmTest` | MSI installer |
| **macOS** | `packageDmg` | `jvmTest` | DMG installer |

## 🔧 Quality Standards

### ✅ Passing Criteria
- **Compilation**: All platforms compile successfully
- **Tests**: ≥80% test-to-implementation ratio for domain layer
- **Security**: No critical vulnerabilities or hardcoded secrets
- **Architecture**: Domain layer remains pure (no UI/persistence imports)
- **Performance**: WASM bundle <5MB, build time <30 minutes

### ⚠️ Quality Warnings
- **Test Coverage**: Domain changes without corresponding tests
- **Dependencies**: Outdated or vulnerable dependencies detected
- **Architecture**: Violations of DDD/Clean Architecture principles
- **Performance**: Bundle size growth >20% or build time increase >50%

## 🛠️ Development Workflow

### 1. Feature Development
```bash
# Create feature branch
git checkout -b feature/new-strategy-logic

# Develop with TDD
./gradlew commonTest --continuous
# Write failing test → implement → refactor

# Validate locally
./gradlew check
```

### 2. Pull Request Process
```bash
# Push feature branch
git push origin feature/new-strategy-logic

# Create PR → Automated checks run:
# - PR Validation (15 min)
# - Full CI Pipeline (30-45 min)
# - Security Analysis (20-25 min)

# Review automated feedback
# - Check PR analysis comment
# - Review security dashboard
# - Verify all platforms built successfully
```

### 3. Release Process
```bash
# Tag release
git tag v1.0.0
git push origin v1.0.0

# Automated release build triggers:
# - All platforms built
# - Release artifacts uploaded
# - GitHub release created (draft)
# - Performance report generated
```

## 🔍 Monitoring & Debugging

### Artifact Downloads
All runs generate downloadable artifacts:
- **Test Results**: Platform-specific test reports and coverage
- **Build Artifacts**: Platform-specific binaries and bundles
- **Quality Reports**: Code quality, architecture compliance, performance
- **Security Reports**: Vulnerability scans, SBOM, security patterns

### Failure Investigation
1. **Check CI Summary**: Overall pipeline status and failed jobs
2. **Download Artifacts**: Specific failure reports and logs
3. **Local Reproduction**: Use same Gradle commands as CI
4. **Security Dashboard**: Review security findings and recommendations

### Performance Tracking
- **Weekly Builds**: Automated performance benchmarks
- **Bundle Analysis**: WASM bundle size tracking over time
- **Build Time**: Gradle build performance monitoring
- **Platform Metrics**: Compilation time per platform

## 🚦 Status Badges

Add these to your main README.md:

```markdown
[![CI/CD Pipeline](https://github.com/USERNAME/blackjack-strategy-trainer/workflows/CI/CD%20Pipeline/badge.svg)](https://github.com/USERNAME/blackjack-strategy-trainer/actions/workflows/ci.yml)
[![Security Analysis](https://github.com/USERNAME/blackjack-strategy-trainer/workflows/Security%20%26%20Dependency%20Scanning/badge.svg)](https://github.com/USERNAME/blackjack-strategy-trainer/actions/workflows/security.yml)
[![Code Quality](https://github.com/USERNAME/blackjack-strategy-trainer/workflows/Quality%20Gates%20%26%20Code%20Standards/badge.svg)](https://github.com/USERNAME/blackjack-strategy-trainer/actions/workflows/quality-gates.yml)
```

## 🔄 Maintenance

### Weekly Tasks (Automated)
- **Dependabot**: Dependency updates with grouping
- **Security Scans**: Weekly vulnerability assessment
- **Performance Tests**: Benchmark tracking and regression detection

### Monthly Tasks (Manual)
- Review security dashboard trends
- Update CI pipeline based on project evolution
- Validate artifact retention and cleanup policies

## 🛡️ Security Features

- **CodeQL Analysis**: GitHub's semantic code analysis
- **Dependency Scanning**: OWASP-style vulnerability detection  
- **Secret Detection**: Hardcoded credential prevention
- **Build Security**: Reproducible builds and integrity validation
- **Branch Protection**: Automated PR validation and merge requirements

---

**Next Steps**: 
1. Push these workflows to trigger first CI run
2. Review generated reports and adjust thresholds
3. Configure branch protection rules in GitHub repository settings
4. Set up GitHub repository secrets if needed for deployment