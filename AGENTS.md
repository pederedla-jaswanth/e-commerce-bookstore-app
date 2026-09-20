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
- Bottom-bar visibility is controlled inside `EBookStoreNavGraph` — detail routes must NOT be in `bottomNavItems`.
- **`EBookStoreNavGraph` owns the `Scaffold`** — screens must NOT contain their own Scaffold.

## ViewModel Scoping

- `CartViewModel` and `WishlistViewModel` are **NavGraph-scoped** (created via `hiltViewModel()` inside `EBookStoreNavGraph`). Pass them as parameters to screens — do not call `hiltViewModel()` again inside child screens.
- `ThemeViewModel` is **Activity-scoped** (created in `MainActivity` via `by viewModels()`). Profile screen receives `themeMode` and `onThemeModeChange` as parameters — it does NOT call `hiltViewModel<ThemeViewModel>()` itself.

## Code Conventions

- All `@HiltViewModel` classes use `@Inject constructor()`.
- UI state is a single immutable `data class *UiState` exposed as `StateFlow<*UiState>` — update via `_uiState.update { it.copy(...) }`.
- Every screen composable must have `@Preview` for both light and dark themes.
  - Dark preview: `EBookStoreTheme(themeMode = ThemeMode.DARK)`.
  - Previews use a stateless `*Content()` overload — `hiltViewModel()` crashes in preview context.
- `BookCard` has `VERTICAL` (grid/row) and `HORIZONTAL` (list) modes via `CardLayoutMode` enum.

## Design Authority

- [`ui-design-analysis-plan.md`](ui-design-analysis-plan.md) in project root is the **authoritative design spec** — read it before implementing any UI screen.
- Reference screenshots are for layout/structure only — **discard their individual color schemes**.
- D4: Category navigation = horizontal scrollable chip row below search bar.
- D5: Add to Cart = Snackbar + stay on screen + immediate badge update + "Open Cart" action.

## Current Phase Status

| Phase | Status | Notes |
|-------|--------|-------|
| 3–5 | ✅ Done | Architecture, Theme, Navigation + bottom bar |
| 6 | ✅ Done | Home Screen fully assembled |
| 7 | ✅ Done | Catalogue Screen with filter/sort |
| 8 | ✅ Done | Book Detail Screen |
| 9 | ✅ Done | Cart + Wishlist screens |
| 10 | 🔄 Next | Auth (Login/Register) + full Profile Screen |
| 11 | ⬜ | Checkout + Payment |
| 13 | ⬜ | Order History |

**Phase 10 specifics**: `AuthViewModel` + `LoginScreen` + `RegisterScreen` go in `ui/screens/auth/`. `ProfileScreen` needs replacing — it currently receives `onNavigateToWishlist: () -> Unit` and needs `themeMode: ThemeMode` + `onThemeModeChange: (ThemeMode) -> Unit` added (passed from `MainActivity` → `EBookStoreNavGraph` → `ProfileScreen`). Login/Register composable entries in NavGraph are currently commented out.
