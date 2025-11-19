# CapyIDE Mobile (Capy Pocket)

AI-assisted mobile IDE for Android built with Kotlin + Jetpack Compose.

## Features

- **AI Code Assistance** - Integration with multiple AI providers (OpenRouter, Groq, Azure, Anthropic)
- **Code Editor** - Syntax-highlighted editor with future AI suggestions
- **Settings Management** - API key configuration and provider selection
- **Update System** - Automatic update checking via WorkManager
- **Material 3 Design** - Modern Compose UI with dynamic theming

## Architecture

Clean Architecture with:
```
dev.capyide.mobile/
├── core/           # Domain logic (AI, config, updates)
├── ui/             # Compose screens and navigation
└── MainActivity    # Entry point with NavHost
```

## Setup & Build

### Prerequisites
- Android Studio Koala | 2024.1.1 or later
- JDK 17+
- Android SDK API 35

### Build Instructions
```bash
cd CapyIDE Mobile
./gradlew clean assembleDebug
```

**Note:** Ensure Java is properly installed and in PATH for Gradle wrapper.

### Run
```bash
./gradlew installDebug
```

## Project Status
- ✅ Core architecture complete
- ✅ Navigation between Editor ↔ Settings
- ✅ Theme system implemented
- ✅ AI provider framework ready
- 🔄 Settings persistence stubbed (DataStore planned)
- 📱 Ready for APK generation

## CI/CD
GitHub Actions workflow in `.github/workflows/android-build.yml` builds debug APKs on push.

## Next Development
1. Real code editor (syntax highlighting, autocomplete)
2. AI integration with actual API calls
3. Secure settings persistence (EncryptedSharedPreferences)
4. File system integration
5. Project management (file tree, tabs)

## License
MIT License