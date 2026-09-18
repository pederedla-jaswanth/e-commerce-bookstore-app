# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## Project

Native Android Kotlin + Jetpack Compose E-Book Store app.
Package: `com.example.ebookstore` — **never change this**.

## Build Commands

```powershell
# Debug build (run from project root)
.\gradlew.bat assembleDebug

# Install on connected device/emulator
.\gradlew.bat installDebug

# Unit tests
.\gradlew.bat test

# Single test class
.\gradlew.bat test --tests "com.example.ebookstore.ExampleUnitTest"

# Instrumented tests (requires running emulator)
.\gradlew.bat connectedAndroidTest
```

> Prefer Android Studio ▶ Run for installs — `adb install` can silently hang.

## Critical Toolchain Constraints

- **Kotlin 2.2.10 + AGP 9.4.0**: Do NOT add `kotlin.android` plugin — `kotlin.compose` already implies it. Adding it causes `Cannot add extension 'kotlin'` crash.
- **No `kotlinOptions { jvmTarget }`** — use `compileOptions` Java 11 only (no `kotlin.android` plugin = no `kotlinOptions`).
- **KSP 2.x standalone versioning**: KSP version has no Kotlin prefix (e.g. `ksp = "2.3.12"`, not `2.2.10-2.3.12`).
- **`compileSdk { version = release(37) }`** — AGP 9 DSL; keep the `release()` wrapper when bumping SDK.
- **Gradle Configuration Cache is ON** — build scripts must be cache-compatible.
- **`material-icons-extended` must be declared explicitly** even though it's BOM-managed.
- Release `optimization { enable = false }` — do not add minification without reviewing this.

## Theme & Color Rules

- **No hardcoded colors anywhere** — always use `MaterialTheme.colorScheme.*` tokens.
- `dynamicColor` is permanently disabled — never re-enable it.
- Star ratings use `LocalRatingStarColor.current` (not `MaterialTheme.colorScheme`), provided by `EBookStoreTheme`.
  - Accessing it outside `EBookStoreTheme` will throw at runtime.
- Brand constants live in [`ui/theme/Color.kt`](app/src/main/java/com/example/ebookstore/ui/theme/Color.kt) — do not use raw hex values in composables.
- In dark theme, `primary = BrandOrangeLight` (not `BrandOrange`) for WCAG contrast.

## Spacing & Layout

- Use `EBookStoreSpacing.*` from [`ui/theme/Spacing.kt`](app/src/main/java/com/example/ebookstore/ui/theme/Spacing.kt) — no hardcoded `dp` values.
- All book cover images use `EBookStoreSpacing.BookCoverAspectRatio` (= `2f/3f`) via `Modifier.aspectRatio(...)`.

## Navigation

- Route strings are in [`NavRoutes.kt`](app/src/main/java/com/example/ebookstore/ui/navigation/NavRoutes.kt) sealed class — never write raw route strings inline.
- New screen checklist: add object to `NavRoutes` → add `composable()` in `EBookStoreNavGraph` → add `BottomNavItem` if bottom-nav tab.
- Bottom-bar visibility is controlled inside `EBookStoreNavGraph` — detail routes (BookDetail, Checkout, etc.) must NOT be in `bottomNavItems`.

## Code Conventions

- All `@HiltViewModel` classes use `@Inject constructor()`.
- UI state is a single immutable `data class *UiState` exposed as `StateFlow<*UiState>` — update via `_uiState.update { it.copy(...) }`.
- Every screen composable must have `@Preview` for both light and dark themes (see [`HomeScreen.kt`](app/src/main/java/com/example/ebookstore/ui/screens/home/HomeScreen.kt) for pattern).
- Preview dark theme: `EBookStoreTheme(themeMode = ThemeMode.DARK)`.
- `BookCard` has `VERTICAL` (grid/row) and `HORIZONTAL` (list) modes via `CardLayoutMode` enum.

## Design Authority

- [`ui-design-analysis-plan.md`](ui-design-analysis-plan.md) in project root is the **authoritative design spec** — read it before implementing any UI screen.
- Reference screenshots are for layout/structure only — **discard their individual color schemes**.
- D4: Category navigation = horizontal scrollable chip row below search bar.
- D5: Add to Cart = Snackbar + stay on screen + immediate badge update + "Open Cart" action.

## Current Phase Status

- Phases 3–5 complete (Architecture, Theme, Navigation + bottom bar).
- Phase 6 (Home Screen) partially done — all components built, `HomeScreen.kt` is still a placeholder needing full assembly.
- `EBookStoreNavGraph.kt` needs `cartItemCount` wired from a shared `CartViewModel`.
- Phases 7–13 (Catalogue, Book Detail, Cart, Auth, Checkout/Payment, Profile, Order History) not started.
