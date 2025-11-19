# CapyIDE Mobile (Capy Pocket) - Snapshot

## Project Overview

**Description:** AI-assisted mobile IDE scaffold built with Kotlin + Jetpack Compose  
**Current Status:** Build, lint, and unit tests green on Kotlin 2.0 / Compose compiler (Nov 2025).  
**Primary Branch:** `main`

## Architecture & Stack

- **Language:** Kotlin 2.0.20 / JVM 17
- **Android API:** MinSdk 26, Target/Compile 35
- **UI:** Jetpack Compose + Material 3
- **Build:** Gradle Kotlin DSL, Compose BOM 2024.06, Compose compiler plugin
- **Structure:**
  ```
  dev.capyide.mobile/
  |- core/    # AI, config, update modules
  |- ui/      # Editor, Settings, Navigation, Theme
  |- CapyApp  # Application + DI container
  `- MainActivity
  ```

## Recent Improvements (Nov 2025)

1. Secure settings repository backed by EncryptedSharedPreferences with Flow exposure.
2. ViewModel-driven Settings screen: auto-update toggle, manual update checks, persisted API keys.
3. WorkManager scheduler paired with GitHub release integration + user notifications (with runtime notification permission gating).
4. Build alignment: Kotlin 2.0.20, Compose compiler plugin 1.6.20, JVM 21, deterministic versioning, local JDK bootstrap in `gradlew`.
5. Permissions/privacy tightened with INTERNET/ACCESS_NETWORK_STATE/POST_NOTIFICATIONS + backup exclusions.
6. Docs/CI refresh: README rewrite, snapshot update, workflow runs `lint`, `test`, `assembleDebug`.

## Outstanding Work

- Rich code editor (syntax highlighting, autocomplete, multi-file tabs)
- Real AI provider integrations routed through Retrofit/OkHttp
- File/project storage + import/export
- Offline support, Git integration, and workspace management
- Automated UI tests + screenshot baselines

## Validation Checklist

- `./gradlew lint test assembleDebug`
- WorkManager enqueues background update worker
- Settings survive process death & rotation
- Update notifications trigger when GitHub releases expose newer APKs

_Last updated: November 19, 2025_
