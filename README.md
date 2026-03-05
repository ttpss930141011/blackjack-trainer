# 🃏 Blackjack Strategy Trainer

A cross-platform blackjack trainer that teaches you perfect basic strategy through practice — not memorization.

Built with **Kotlin Multiplatform** + **Compose Multiplatform**. Runs on Android, iOS, and Desktop (JVM).

[![CI](https://github.com/ttpss930141011/blackjack-trainer/actions/workflows/simple-ci.yml/badge.svg)](https://github.com/ttpss930141011/blackjack-trainer/actions)

## Features

- **Real-time strategy feedback** — instant visual cues tell you if your decision matches basic strategy
- **Three-layer feedback system** — button flash (correct/wrong) → settlement review card → session accuracy badge
- **Strategy separated from luck** — feedback is about *your decision*, not whether you won or lost
- **Interactive strategy chart** — full basic strategy reference, always accessible
- **Configurable rules** — soft 17, surrender, blackjack payout (3:2 or 6:5)
- **Session-scoped accuracy** — tracks your progress without discouraging new players
- **Game history** — review past rounds and learn from mistakes
- **Chip-based betting** — realistic casino-style chip system

## Getting Started

### Prerequisites

- JDK 17+
- Android Studio (for Android development)
- Xcode (for iOS development, macOS only)

### Run

```bash
# Desktop (JVM)
./gradlew :composeApp:run

# Android
./gradlew :composeApp:installDebug

# Tests
./gradlew test
```

## Architecture

Clean Architecture with DDD, organized into four layers:

```
domain/          # Entities, value objects, domain services (pure Kotlin, no framework deps)
application/     # Use cases, view models, manager pattern
infrastructure/  # Room database, platform implementations
presentation/    # Compose UI, components, navigation
```

Key design decisions:
- **Single bounded context** — Game and Learning subdomains share the same ubiquitous language
- **Aggregate root pattern** — `Game` entity controls all state transitions
- **Immutable value objects** — `Hand`, `Card`, `Deck` return new instances on mutation
- **Manager delegation** — GameViewModel delegates to specialized managers (state, feedback, analytics, UI)

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for detailed architecture documentation.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 2.2 |
| UI | Compose Multiplatform 1.8 |
| Database | Room 2.7 (KMP) |
| Build | Gradle (Version Catalog) |
| CI | GitHub Actions |

## Project Structure

```
composeApp/src/
├── commonMain/          # Shared code (95%+ of the codebase)
│   └── kotlin/.../bj/
│       ├── domain/      # Game logic, strategy engine, settlement
│       ├── application/ # ViewModels, services, managers
│       ├── infrastructure/ # Room DB, audio
│       └── presentation/   # Compose UI
├── androidMain/         # Android-specific (audio, DB)
├── iosMain/             # iOS-specific
└── jvmMain/             # Desktop-specific
```

## License

MIT
