# Blackjack Strategy Trainer

A Kotlin Multiplatform application for learning optimal blackjack strategy using domain-driven design principles.

## Project Structure

* `composeApp/src/commonMain` - Shared business logic and UI
* `composeApp/src/androidMain` - Android-specific implementations  
* `composeApp/src/jvmMain` - Desktop-specific implementations
* `composeApp/src/wasmJsMain` - Web-specific implementations

## Development

```bash
# Run web version
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Run tests
./gradlew test

# Build all platforms
./gradlew build
```

## Architecture

Following Domain-Driven Design with progressive CQRS implementation:
- **Domain Layer**: Pure business logic (Card, Hand, Game, Strategy)
- **Application Layer**: Use cases and command handling
- **Infrastructure Layer**: Platform-specific implementations

See `CLAUDE.md` for detailed development guidelines.