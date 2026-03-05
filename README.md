# CapyIDE Mobile

cross-platform mobile IDE for Android and iOS. kotlin multiplatform + compose multiplatform.

write code on your phone with syntax highlighting, AI assistance from 4 providers, and a proper file tree. because the "editor" being a hardcoded `Text()` composable wasn't cutting it.

## what works

- **code editor** - Sora Editor on Android, CodeMirror 6 via WKWebView on iOS. 10 language syntaxes (kotlin, java, js/ts, python, html, css, json, xml, markdown).
- **AI code assist** - OpenRouter, Groq, Azure OpenAI, Anthropic. explain, complete, refactor, or just chat about your code.
- **file management** - project file tree drawer, create/open/save files
- **secure settings** - EncryptedSharedPreferences on Android, Keychain on iOS. per-provider API key storage.
- **update checker** - WorkManager job checks GitHub releases every 12h, surfaces notification with changelog
- **cross-platform UI** - Compose Multiplatform with custom capybara-inspired color scheme
- **monetization** - AdMob banner ads, removable via monthly ($3.99) or yearly ($19.99) subscription

## architecture

```
CapyIDE-Mobile/
├── shared/          # KMP module - business logic, AI providers, file system, DI
│   ├── commonMain/  # interfaces, data classes, viewmodels, Ktor HTTP
│   ├── androidMain/ # EncryptedSharedPrefs, java.io.File
│   └── iosMain/     # Keychain, NSFileManager
├── composeApp/      # Compose Multiplatform UI
│   ├── commonMain/  # all screens, navigation, theme, AI assist panel
│   ├── androidMain/ # Sora Editor AndroidView wrapper
│   └── iosMain/     # CodeMirror WKWebView wrapper + iOS entry point
├── androidApp/      # thin Android shell (CapyApp + MainActivity + WorkManager)
└── iosApp/          # SwiftUI entry point + StoreKit 2 + AdMob
```

DI via Koin. HTTP via Ktor. serialization via kotlinx-serialization. no Hilt, no Retrofit, no OkHttp.

## setup

### prerequisites
- Android Studio Koala 2024.1.1+ (or just Gradle CLI)
- JDK 17
- Android SDK API 35
- Xcode 15+ (for iOS)
- Apple Developer account (for device deployment + App Store)

### build

```bash
# android
./gradlew :androidApp:assembleDebug

# iOS - open in Xcode
open iosApp/iosApp.xcodeproj
```

### iOS setup

1. open `iosApp/iosApp.xcodeproj` in Xcode
2. add Google Mobile Ads SDK via File > Add Package Dependencies
   - URL: `https://github.com/googleads/swift-package-manager-google-mobile-ads`
3. replace the test GADApplicationIdentifier in `Info.plist` with your real AdMob app ID
4. select your device, hit Run (first build compiles the Kotlin framework via Gradle, takes a few minutes)

### StoreKit testing

the `Configuration.storekit` file is included for local subscription testing in Xcode. set it as the StoreKit Configuration in your scheme's Run settings to test purchases without App Store Connect.

## AI providers

| provider | base URL | auth |
|----------|----------|------|
| OpenRouter | openrouter.ai/api/v1 | Bearer token |
| Groq | api.groq.com/openai/v1 | Bearer token |
| Azure OpenAI | {resource}.openai.azure.com | api-key header |
| Anthropic | api.anthropic.com/v1 | x-api-key + anthropic-version headers |

configure API keys per-provider in settings.

## monetization

| tier | price | what you get |
|------|-------|-------------|
| free | $0 | full app with banner ads |
| pro monthly | $3.99/mo | no ads |
| pro yearly | $19.99/yr | no ads, ~58% savings |

ads managed via AdMob iOS SDK. subscriptions via StoreKit 2 with server-side receipt verification. paywall accessible from the app, restore purchases supported.

## CI/CD

GitHub Actions runs on push to main:
- android: lint, test, assembleDebug, upload APK artifact
- android-release: assembleRelease on main pushes
- ios: build iOS framework on macos-latest

## stack

| layer | tech |
|-------|------|
| language | Kotlin 2.1.0 |
| UI | Compose Multiplatform 1.7.3 |
| DI | Koin 4.0.2 |
| HTTP | Ktor 3.0.3 |
| serialization | kotlinx-serialization 1.7.3 |
| android editor | Sora Editor 0.23.4 |
| iOS editor | CodeMirror 6 (WKWebView) |
| ads | Google AdMob |
| subscriptions | StoreKit 2 |
| build | Gradle 8.5.2 AGP |

## license

MIT
