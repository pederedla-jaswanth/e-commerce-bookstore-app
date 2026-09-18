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
- `cartItemCount` badge in `EBookStoreBottomBar` comes from `EBookStoreNavGraph`'s parameter — a shared `CartViewModel` is needed at the NavGraph level to wire this correctly.
- `ThemeViewModel` lives at `MainActivity` level and passes `ThemeMode` down to `EBookStoreTheme` — it is NOT scoped to any individual screen.
- `HeroBanner` (home pager) handles its own auto-scroll timer via `LaunchedEffect` — do not plan an external ticker.

## Planned Screen Hierarchy (Future Phases)

```
NavGraph (Scaffold owner)
├── HomeScreen           (Phase 6 — partial)
├── CatalogueScreen      (Phase 7)
├── CartScreen           (Phase 9)
├── OrdersScreen         (Phase 13)
├── ProfileScreen        (Phase 12)
└── Detail routes (no bottom bar):
    ├── BookDetailScreen (Phase 8)
    ├── LoginScreen      (Phase 10)
    ├── RegisterScreen   (Phase 10)
    ├── CheckoutScreen   (Phase 11)
    ├── PaymentScreen    (Phase 11)
    └── OrderConfirmationScreen (Phase 11)
```

## Key Design Decisions (Not Changeable)

- D4: Category navigation = horizontal scrollable chip row below search bar (not sidebar/drawer).
- D5: Add to Cart = Snackbar + stay on screen + immediate badge update + "Open Cart" action.
- Theme toggle = three-option (System / Light / Dark) on Profile screen, persisted via DataStore.
