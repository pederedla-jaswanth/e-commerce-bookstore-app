# AGENTS.md — Ask Mode

This file provides documentation and orientation context for agents in Ask mode.

## Where Things Actually Live

- **Authoritative design spec**: [`ui-design-analysis-plan.md`](../../ui-design-analysis-plan.md) in project root — not in-code comments.
- **All dependency versions**: [`gradle/libs.versions.toml`](../../gradle/libs.versions.toml) — single source of truth.
- **Brand colors (raw hex)**: [`ui/theme/Color.kt`](../../app/src/main/java/com/example/ebookstore/ui/theme/Color.kt).
- **Spacing tokens**: [`ui/theme/Spacing.kt`](../../app/src/main/java/com/example/ebookstore/ui/theme/Spacing.kt) — `EBookStoreSpacing` object.
- **Navigation routes**: [`ui/navigation/NavRoutes.kt`](../../app/src/main/java/com/example/ebookstore/ui/navigation/NavRoutes.kt) sealed class.
- **Mock data (only data source currently)**: [`data/mock/MockData.kt`](../../app/src/main/java/com/example/ebookstore/data/mock/MockData.kt).

## Non-Obvious Architecture Notes

- `LocalRatingStarColor` is a custom `CompositionLocal` for amber star color — it's NOT in the M3 color scheme. It throws if accessed outside `EBookStoreTheme`.
- `EBookStoreTheme` wraps both `MaterialTheme` and `CompositionLocalProvider` — it is the single entry point for all theming.
- `ThemeMode` enum (SYSTEM/LIGHT/DARK) is persisted via DataStore through `ThemePreferenceRepository`; `ThemeViewModel` (Activity-scoped, in `MainActivity`) reads it and passes it into `EBookStoreTheme`.
- `CartViewModel` and `WishlistViewModel` are NavGraph-scoped — they live in `EBookStoreNavGraph` and are passed as parameters into screens, not re-created per screen.
- `EBookStoreNavGraph` owns the `Scaffold` + `BottomBar` — no screen has its own Scaffold.
- `hilt-navigation-compose 1.4.0` emits deprecation warnings for `hiltViewModel` — cosmetic only, works correctly.

## Phase Completion Status

| Phase | Status | Key Files |
|-------|--------|-----------|
| 3 — Architecture | ✅ Done | `EBookStoreApplication`, `di/AppModule` |
| 4 — Theme | ✅ Done | `ui/theme/*` |
| 5 — Navigation | ✅ Done | `ui/navigation/*` |
| 6 — Home Screen | ✅ Done | `ui/screens/home/*`, all components |
| 7 — Catalogue | ✅ Done | `ui/screens/catalogue/*` |
| 8 — Book Detail | ✅ Done | `ui/screens/bookdetail/*` |
| 9 — Cart + Wishlist | ✅ Done | `ui/screens/cart/*`, `ui/screens/wishlist/*` |
| 10 — Auth + Profile | 🔄 Next | `ui/screens/auth/` is empty |
| 11 — Checkout + Payment | ⬜ Not started | — |
| 13 — Order History | ⬜ Not started | — |
