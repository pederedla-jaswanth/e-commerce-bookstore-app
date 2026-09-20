# AGENTS.md — Agent (Coding) Mode

This file provides coding-specific guidance for agents working in this repository.

## Gotchas That Will Break the Build

- **Never add `kotlin.android` plugin** — `kotlin.compose` implies it; adding both crashes with `Cannot add extension 'kotlin'`.
- **Never use `kotlinOptions { jvmTarget }`** — block is unavailable without `kotlin.android`.
- **`material-icons-extended` must be in `dependencies {}`** even though BOM-managed — omitting it causes unresolved reference errors.
- **All new deps go in `gradle/libs.versions.toml` first**, then referenced via `libs.*` alias — never use direct coordinate strings in `app/build.gradle.kts`.
- **`compileSdk { version = release(37) }`** — AGP 9 DSL. If bumping SDK, keep `release()` wrapper.
- **KSP annotation processors must use `ksp(...)` not `kapt(...)`** — project has no kapt.

## Required Patterns for Every New Screen

1. Create `ui/screens/<name>/<Name>Screen.kt` + `<Name>ViewModel.kt` in the same package.
2. UiState = single `data class <Name>UiState(...)` in the same file as the ViewModel.
3. Expose state as `val uiState: StateFlow<…> = _uiState.asStateFlow()`.
4. Split into stateful entry-point (`<Name>Screen`) + stateless `<Name>ScreenContent` — previews must use the stateless overload (calling `hiltViewModel()` in a preview crashes).
5. Add both light + dark `@Preview` annotations — dark preview must pass `themeMode = ThemeMode.DARK`.
6. Add route object to `NavRoutes` sealed class, then `composable()` entry in `EBookStoreNavGraph`.

## ViewModel Scoping (Non-Obvious)

- **Never call `hiltViewModel<CartViewModel>()`** inside `HomeScreen`, `CatalogueScreen`, `BookDetailScreen`, or `CartScreen`. Those screens receive `cartViewModel` as a **parameter** because the instance is created at NavGraph level.
- Same rule for `WishlistViewModel` — it is NavGraph-scoped and passed as a parameter to `WishlistScreen` and `BookDetailScreen`.
- **`ThemeViewModel` is Activity-scoped** (`by viewModels()` in `MainActivity`) — pass `themeMode: ThemeMode` and `onThemeModeChange: (ThemeMode) -> Unit` down as parameters to `ProfileScreen` via `EBookStoreNavGraph`.

## Color / Theme Non-Negotiables

- **Zero hardcoded colors** — always `MaterialTheme.colorScheme.*`.
- **Star color** = `LocalRatingStarColor.current` — only valid inside `EBookStoreTheme`.
- **Spacing** = `EBookStoreSpacing.*` — no raw `dp` literals.
- **Book cover aspect ratio** = `Modifier.aspectRatio(EBookStoreSpacing.BookCoverAspectRatio)` (2:3).
- `dynamicColor` must stay `false` — never pass `dynamicColor = true` to `EBookStoreTheme`.

## Hilt Injection Pattern

```kotlin
@HiltViewModel
class FooViewModel @Inject constructor(
    private val someRepo: SomeRepository,
) : ViewModel()
```

New repository bindings go in [`di/AppModule.kt`](../../app/src/main/java/com/example/ebookstore/di/AppModule.kt) as `@Binds` or `@Provides`.

## Mock Data Phase

Currently no real API. All data comes from [`data/mock/MockData.kt`](../../app/src/main/java/com/example/ebookstore/data/mock/MockData.kt).
Do not add Retrofit calls until the API phase is explicitly started.

## Phase 10 Coding Checklist

- `AuthViewModel` in `ui/screens/auth/` — mock login/register/logout, exposes `AuthUiState` with `isLoggedIn`, `currentUser`, `isLoading`, `error`.
- `LoginScreen` + `RegisterScreen` in `ui/screens/auth/` — form validation, password visibility toggle, `*Content` stateless overload, light+dark previews.
- Uncomment Login/Register `composable()` entries in `EBookStoreNavGraph`.
- `ProfileScreen` full replacement: add `themeMode: ThemeMode` + `onThemeModeChange: (ThemeMode) -> Unit` + `onLogout: () -> Unit` params; implement `SingleChoiceSegmentedButtonRow` for theme toggle.
- Wire `themeMode`/`onThemeModeChange` through `EBookStoreNavGraph` (which receives them as params from `MainActivity`).

## Validation

Run `.\gradlew.bat assembleDebug` after every implementation phase and confirm it exits with `BUILD SUCCESSFUL`.
