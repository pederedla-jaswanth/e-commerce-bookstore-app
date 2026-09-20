# AGENTS.md — Plan Mode

This file provides architectural constraints for agents in Plan mode.

## Hard Architectural Constraints

- **Single-module app** — no multi-module Gradle setup. Everything is under `app/`.
- **No kapt anywhere** — Hilt and Room both use KSP; any plan involving `kapt` will break the build.
- **Compose-only UI** — no XML layouts. Do not plan Views, Fragments, or Activity-based navigation.
- **No dynamic color** — `dynamicColor = false` is permanent brand requirement, not a TODO.
- **Mock data until API phase** — do not plan Retrofit/OkHttp usage until an explicit API phase starts.

## Component Coupling to Know Before Planning

- `EBookStoreNavGraph` owns the `Scaffold` + `BottomBar` — screens must NOT contain their own Scaffold.
- `CartViewModel` and `WishlistViewModel` are NavGraph-scoped — they are created once in `EBookStoreNavGraph` and passed down as parameters. Do NOT plan to call `hiltViewModel()` for these inside any screen composable.
- `ThemeViewModel` lives at `MainActivity` level — it is NOT accessible in the NavGraph via `hiltViewModel()`. Plan a parameter-passing chain: `MainActivity` → `EBookStoreNavGraph(themeMode, onThemeModeChange)` → `ProfileScreen(themeMode, onThemeModeChange)`.
- `HeroBanner` (home pager) handles its own auto-scroll timer via `LaunchedEffect` — do not plan an external ticker.

## Phase 10 Architectural Plan

- `AuthViewModel` (mock auth): lives at NavGraph scope or screen scope — it does NOT need Activity scope.
- `LoginScreen` and `RegisterScreen` are **detail routes** (no bottom bar) — they are already declared in `NavRoutes.Detail` and the `composable()` entries are commented out in `EBookStoreNavGraph`.
- `ProfileScreen` currently accepts only `onNavigateToWishlist: () -> Unit`. Phase 10 must add `themeMode: ThemeMode`, `onThemeModeChange: (ThemeMode) -> Unit`, and `onLogout: () -> Unit` — this requires updating the `EBookStoreNavGraph` ProfileScreen composable call and the `MainActivity` → `EBookStoreNavGraph` call site.

## Screen Hierarchy

```
NavGraph (Scaffold owner)
├── HomeScreen           (Phase 6 — ✅)
├── CatalogueScreen      (Phase 7 — ✅)
├── CartScreen           (Phase 9 — ✅)
├── OrdersScreen         (Phase 13 — stub)
├── ProfileScreen        (Phase 10 — stub, needs replacement)
└── Detail routes (no bottom bar):
    ├── BookDetailScreen         (Phase 8 — ✅)
    ├── WishlistScreen           (Phase 9 — ✅)
    ├── LoginScreen              (Phase 10 — not yet wired)
    ├── RegisterScreen           (Phase 10 — not yet wired)
    ├── CheckoutScreen           (Phase 11)
    ├── PaymentScreen            (Phase 11)
    └── OrderConfirmationScreen  (Phase 11)
```

## Key Design Decisions (Not Changeable)

- D4: Category navigation = horizontal scrollable chip row below search bar (not sidebar/drawer).
- D5: Add to Cart = Snackbar + stay on screen + immediate badge update + "Open Cart" action.
- Theme toggle = three-option (System / Light / Dark) on Profile screen via `SingleChoiceSegmentedButtonRow`, persisted via DataStore.
