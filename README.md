# CapyIDE Mobile (Capy Pocket)

AI-assisted mobile IDE for Android built with Kotlin + Jetpack Compose.

## Features

- **AI Code Assistance** - Multi-provider framework (OpenRouter, Groq, Azure, Anthropic)
- **Code Editor** - Compose-based scaffold ready for syntax highlighting + AI suggestions
- **Secure Settings** - Encrypted API key storage with provider selection + auto-update toggle
- **Update System** - WorkManager job checks GitHub releases and surfaces notifications
- **Material You UI** - Dynamic color palette, edge-to-edge layout, and responsive components

## Architecture

Clean architecture with core/domain isolation:
```
dev.capyide.mobile/
|- core/        # Domain logic (AI, config, updates)
|- ui/          # Compose screens + navigation
|- CapyApp.kt   # Application container + WorkManager wiring
`- MainActivity # Entry point hosting NavHost
```

## Setup & Build

### Prerequisites
- Android Studio Koala | 2024.1.1 or later
- JDK 17+
- Android SDK API 35

### Build Instructions
```bash
./gradlew clean assembleDebug
```

### Install On Device
```bash
./gradlew installDebug
```

## Project Status
- [x] Core architecture & Compose navigation
- [x] Material 3 theme + dynamic color
- [x] Secure settings persistence with encrypted storage
- [x] GitHub-powered update checks via WorkManager
- [ ] Real code editor (syntax/AI suggestions) in progress
- [ ] AI provider networking + response rendering next milestone

## CI/CD
GitHub Actions workflow (`.github/workflows/android-build.yml`) now runs `lint`, `test`, and `assembleDebug` on pushes to `main` or `dev`, publishing signed debug APK artifacts.

## Next Development
1. Real code editor (syntax highlighting, autocomplete)
2. AI integration with live provider calls + streaming responses
3. File system integration + project tree/tabs
4. Offline project caching + Git operations
5. Enhanced QA: screenshot tests, performance benchmarks

## Screenshots

![CapyIDE Mobile Editor](screenshots/editor.png)  
![CapyIDE Mobile Settings](screenshots/settings.png)

## License
MIT License
