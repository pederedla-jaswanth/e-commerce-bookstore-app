# UI Design Analysis Plan
## Phase: Visual Reference Analysis — Screens 1–3 (Revised)
### Last Updated: All blocking decisions resolved — plan complete

---

## Top-Level Overview

This plan defines the complete design system for the EBookStore Android app.
The three provided reference screenshots are used **only for layout, structure,
spacing, and component inspiration**. Their individual color schemes are
discarded. A single unified brand identity is applied to every screen.

**Reference screens used for layout inspiration only:**
1. **Home Screen** — "Bookdp World" (layout and section structure reference)
2. **Catalogue / Browse Screen** — "Book Worm" (filter, sidebar, card layout reference)
3. **Book Detail Screen** — "Book Worm" (detail layout, reviews, related reads reference)

**Confirmed decisions from stakeholder:**
- ✅ D2 — Brand palette: Orange `#F57C00` primary, Green `#1B5E20` secondary,
  Amber ratings, Red errors. Same palette in both light and dark themes.
- ✅ D3 — Dynamic color: **disabled** (`dynamicColor = false`) across all themes.
- ✅ Theme toggle: Three-option (System / Light / Dark) with DataStore persistence.
- ✅ All screens share one brand identity — no per-screen color variation.

**All blocking decisions resolved — ready for implementation:**
- ✅ D4 — Category navigation: horizontal scrollable chip row pinned below search bar
- ✅ D5 — Add to Cart: Snackbar confirmation + stay on screen + immediate cart badge update + "Open Cart" action

---

## Sub-Task 1 — Unified Brand Color System
### Status: [ ] pending

**Intent:** Define the complete Material 3 color token set for light and dark
schemes using only the confirmed brand palette. This replaces the generic
purple/pink scaffold palette currently in `Color.kt`.

**Expected Outcomes:**
- Full M3 `lightColorScheme` and `darkColorScheme` token tables
- Named brand constants ready for `Color.kt`
- Zero reliance on device dynamic color

### 1a — Brand Palette Constants

These are the raw hex values. Every color token below derives from them.

| Constant Name | Hex | Role |
|---|---|---|
| `BrandOrange` | `#F57C00` | Primary brand action color |
| `BrandOrangeLight` | `#FFAD42` | Primary container / light variant |
| `BrandOrangeDark` | `#BB4D00` | Primary dark variant |
| `BrandGreen` | `#1B5E20` | Secondary brand color |
| `BrandGreenLight` | `#4C8C4A` | Secondary container / light variant |
| `BrandGreenDark` | `#003300` | Secondary dark variant |
| `BrandAmber` | `#FFB300` | Star ratings (custom token, not M3) |
| `BrandRed` | `#D32F2F` | Error, sale badges |
| `BrandRedLight` | `#FF6659` | Error container light |
| `NeutralWhite` | `#FFFFFF` | Surfaces (light theme) |
| `NeutralOffWhite` | `#FFF8F0` | Background (light, warm tint) |
| `NeutralLightGrey` | `#F5F0EB` | Surface variant (light) |
| `NeutralMidGrey` | `#9E9E9E` | Muted / secondary text |
| `NeutralDarkGrey` | `#1C1C1E` | Surface (dark theme) |
| `NeutralDeepDark` | `#121212` | Background (dark theme) |
| `NeutralCardDark` | `#1E1E1E` | Surface variant (dark theme) |
| `NeutralTextDark` | `#E0E0E0` | On-background text (dark) |
| `NeutralTextLight` | `#212121` | On-background text (light) |

### 1b — Material 3 Light Color Scheme Mapping

| M3 Token | Value | Constant |
|---|---|---|
| `primary` | `#F57C00` | `BrandOrange` |
| `onPrimary` | `#FFFFFF` | `NeutralWhite` |
| `primaryContainer` | `#FFAD42` | `BrandOrangeLight` |
| `onPrimaryContainer` | `#1A0A00` | (derived dark) |
| `secondary` | `#1B5E20` | `BrandGreen` |
| `onSecondary` | `#FFFFFF` | `NeutralWhite` |
| `secondaryContainer` | `#4C8C4A` | `BrandGreenLight` |
| `onSecondaryContainer` | `#FFFFFF` | `NeutralWhite` |
| `tertiary` | `#1B5E20` | `BrandGreen` (genre chips, links) |
| `onTertiary` | `#FFFFFF` | `NeutralWhite` |
| `error` | `#D32F2F` | `BrandRed` |
| `onError` | `#FFFFFF` | `NeutralWhite` |
| `errorContainer` | `#FF6659` | `BrandRedLight` |
| `background` | `#FFF8F0` | `NeutralOffWhite` |
| `onBackground` | `#212121` | `NeutralTextLight` |
| `surface` | `#FFFFFF` | `NeutralWhite` |
| `onSurface` | `#212121` | `NeutralTextLight` |
| `surfaceVariant` | `#F5F0EB` | `NeutralLightGrey` |
| `onSurfaceVariant` | `#9E9E9E` | `NeutralMidGrey` |

### 1c — Material 3 Dark Color Scheme Mapping

| M3 Token | Value | Constant |
|---|---|---|
| `primary` | `#FFAD42` | `BrandOrangeLight` (lighter for contrast on dark) |
| `onPrimary` | `#1A0A00` | (derived dark) |
| `primaryContainer` | `#BB4D00` | `BrandOrangeDark` |
| `onPrimaryContainer` | `#FFAD42` | `BrandOrangeLight` |
| `secondary` | `#4C8C4A` | `BrandGreenLight` (lighter for contrast) |
| `onSecondary` | `#003300` | `BrandGreenDark` |
| `secondaryContainer` | `#1B5E20` | `BrandGreen` |
| `onSecondaryContainer` | `#E0E0E0` | `NeutralTextDark` |
| `tertiary` | `#4C8C4A` | `BrandGreenLight` |
| `onTertiary` | `#003300` | `BrandGreenDark` |
| `error` | `#FF6659` | `BrandRedLight` |
| `onError` | `#1A0000` | (derived dark) |
| `errorContainer` | `#D32F2F` | `BrandRed` |
| `background` | `#121212` | `NeutralDeepDark` |
| `onBackground` | `#E0E0E0` | `NeutralTextDark` |
| `surface` | `#1C1C1E` | `NeutralDarkGrey` |
| `onSurface` | `#E0E0E0` | `NeutralTextDark` |
| `surfaceVariant` | `#1E1E1E` | `NeutralCardDark` |
| `onSurfaceVariant` | `#9E9E9E` | `NeutralMidGrey` |

### 1d — Custom Token (outside M3 system)

| Token | Value | Usage |
|---|---|---|
| `RatingStarColor` | `#FFB300` — `BrandAmber` | StarRating composable only |

> Note: `RatingStarColor` is not a standard M3 token. It will be exposed via a
> custom `LocalRatingStarColor` CompositionLocal in `Theme.kt`.

**Todo List:**
- [ ] Replace all Purple/Pink/PurpleGrey constants in `Color.kt` with the brand
  constants from table 1a
- [ ] Implement `lightColorScheme(...)` using table 1b in `Theme.kt`
- [ ] Implement `darkColorScheme(...)` using table 1c in `Theme.kt`
- [ ] Set `dynamicColor = false` unconditionally in `EBookStoreTheme`
- [ ] Add `LocalRatingStarColor` CompositionLocal for amber star color
- [ ] Remove the unused `Activity` import in `Theme.kt` (already present, unused)

**Relevant Context:**
- [`app/src/main/java/com/example/ebookstore/ui/theme/Color.kt`](app/src/main/java/com/example/ebookstore/ui/theme/Color.kt)
- [`app/src/main/java/com/example/ebookstore/ui/theme/Theme.kt`](app/src/main/java/com/example/ebookstore/ui/theme/Theme.kt)

---

## Sub-Task 2 — Theme Toggle System (DataStore)
### Status: [ ] pending

**Intent:** Implement a user-controlled theme preference that supports three
modes — System Default, Light, and Dark — persisted across app restarts using
Jetpack DataStore Preferences.

**Expected Outcomes:**
- `ThemeMode` enum with three values
- DataStore key for persisting theme preference
- `ThemePreferenceRepository` interface to read/write the preference
- `EBookStoreTheme` updated to accept `ThemeMode` instead of raw `Boolean`
- A `ThemeToggle` composable for the Profile/Settings screen
- Theme applied consistently from `MainActivity` via a ViewModel-provided state

### 2a — Theme Mode Definition

```
enum class ThemeMode {
    SYSTEM,   // follows isSystemInDarkTheme()
    LIGHT,    // forces light scheme regardless of system
    DARK      // forces dark scheme regardless of system
}
```

### 2b — DataStore Preference Contract

- Preference file name: `theme_preferences`
- DataStore key: `Preferences.Key<String>("theme_mode")`
- Stored as String (enum name); default value: `"SYSTEM"`
- Read as `Flow<ThemeMode>` — collected as StateFlow in a ViewModel

### 2c — `ThemePreferenceRepository`

Single responsibility: read and write the theme preference.

```
interface ThemePreferenceRepository {
    val themeMode: Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}
```

Implementation class: `ThemePreferenceRepositoryImpl`
Location: `data/preferences/ThemePreferenceRepositoryImpl.kt`
Injected via Hilt into a `ThemeViewModel`.

### 2d — Updated `EBookStoreTheme` Contract

`EBookStoreTheme` will accept `themeMode: ThemeMode` (replaces raw `darkTheme:
Boolean`). Internally it resolves whether to use dark scheme:

```
val isDark = when (themeMode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT  -> false
    ThemeMode.DARK   -> true
}
```

`dynamicColor` is removed from the signature — it is permanently `false`.

### 2e — `ThemeViewModel`

Location: `ui/theme/ThemeViewModel.kt`
- Injects `ThemePreferenceRepository`
- Exposes `themeMode: StateFlow<ThemeMode>`
- Provides `setThemeMode(mode: ThemeMode)` function
- Scoped to the Activity (survives recomposition, does not recreate on theme change)

### 2f — `ThemeToggle` Composable (Profile/Settings screen)

Location: `ui/components/ThemeToggle.kt`
Three-segment control (radio group or segmented button):

```
[ System ]  [ Light ]  [ Dark ]
```

- Uses M3 `SegmentedButton` (Material 3 component)
- Current selection highlighted in primary orange
- On selection: calls `onThemeModeChange(ThemeMode)`
- Parameters: `currentMode: ThemeMode`, `onThemeModeChange: (ThemeMode) -> Unit`

### 2g — `MainActivity` wiring

`MainActivity` observes `ThemeViewModel.themeMode` and passes the value to
`EBookStoreTheme`. The theme wraps the entire Navigation host so all screens
inherit it automatically.

**Todo List:**
- [ ] Add DataStore Preferences dependency to `libs.versions.toml` and
  `app/build.gradle.kts`
- [ ] Create `ThemeMode` enum at `ui/theme/ThemeMode.kt`
- [ ] Create `ThemePreferenceRepository` interface and `Impl` at
  `data/preferences/`
- [ ] Create `ThemeViewModel` at `ui/theme/ThemeViewModel.kt`
- [ ] Update `EBookStoreTheme` signature: replace `dynamicColor`/`darkTheme`
  params with `themeMode: ThemeMode`
- [ ] Update `MainActivity` to observe `ThemeViewModel` and pass `themeMode`
- [ ] Create `ThemeToggle` composable at `ui/components/ThemeToggle.kt`
- [ ] Add `ThemeToggle` to Profile/Settings screen (planned in later phase)
- [ ] Add Hilt module to provide `ThemePreferenceRepository`

**Relevant Context:**
- [`app/src/main/java/com/example/ebookstore/ui/theme/Theme.kt`](app/src/main/java/com/example/ebookstore/ui/theme/Theme.kt)
- [`app/src/main/java/com/example/ebookstore/MainActivity.kt`](app/src/main/java/com/example/ebookstore/MainActivity.kt)

---

## Sub-Task 3 — Typography System
### Status: [ ] pending

**Intent:** Expand `Type.kt` beyond the single `bodyLarge` definition to cover
every text role observed in the three reference screens.

**Expected Outcomes:**
- All M3 type roles populated in `Typography`
- Font family decision documented
- Consistent type scale across all screens

**Observed Type Roles (layout-derived, brand-agnostic):**

| Visual Use | Screen | M3 Role | Size / Weight |
|---|---|---|---|
| Hero banner headline | 1 | `displayLarge` | 36–48sp, ExtraBold (W800) |
| Section heading ("New Releases") | 1, 2 | `titleLarge` | 22sp, SemiBold (W600) |
| Book title in card | 2, 3 | `titleMedium` | 16sp, SemiBold (W600) |
| Author name in card | 2, 3 | `bodyMedium` | 14sp, Normal (W400) |
| Price | 2, 3 | `titleSmall` | 14–16sp, Bold (W700) |
| Delivery estimate | 2, 3 | `bodySmall` | 12sp, Normal (W400) |
| Genre/format chips | 2, 3 | `labelSmall` | 11sp, Medium (W500) |
| Review body text | 3 | `bodyMedium` | 14sp, Normal (W400) |
| Breadcrumb links | 3 | `labelMedium` | 13sp, Normal (W400) |
| Button labels | 3 | `labelLarge` | 14sp, Medium (W500) |
| Metadata row (Language · Rating · Sells) | 3 | `bodySmall` | 12sp, Normal (W400) |
| App bar wordmark / logo text | All | `titleMedium` | 16sp, Bold (W700) |

> ⚠️ DECISION (open, low priority) — Custom font family: The screenshots
> appear to use system Roboto. If you want a branded font (e.g. Google Fonts
> "Nunito" or "Poppins"), it must be declared before `Type.kt` is implemented.
> Default: system font (Roboto) unless you specify otherwise.

**Todo List:**
- [ ] Confirm custom font or system font decision
- [ ] Expand `Type.kt` with all roles from the table above
- [ ] Ensure `bodyLarge` existing definition is preserved or updated consistently

**Relevant Context:**
- [`app/src/main/java/com/example/ebookstore/ui/theme/Type.kt`](app/src/main/java/com/example/ebookstore/ui/theme/Type.kt)

---

## Sub-Task 4 — Spacing and Shape System
### Status: [ ] pending

**Intent:** Define spacing and shape tokens as Kotlin constants so every
composable references named values, not magic numbers.

**New files to create:**
- `ui/theme/Spacing.kt` — spacing constants
- Shape configuration added to `Theme.kt` via M3 `Shapes`

**Spacing Tokens (layout-derived from screenshots):**

| Token | Value | Where Observed |
|---|---|---|
| `SpacingXXSmall` | 2dp | Chip internal vertical padding |
| `SpacingXSmall` | 4dp | Between genre chips, small gaps |
| `SpacingSmall` | 8dp | Card internal padding, badge margins |
| `SpacingMedium` | 16dp | Section horizontal padding, card grid gap |
| `SpacingLarge` | 24dp | Section vertical spacing |
| `SpacingXLarge` | 32dp | Hero banner vertical padding |
| `SpacingXXLarge` | 48dp | Screen top/bottom safe area padding |

**Shape Tokens (M3 `Shapes` object):**

| Shape Role | Corner Radius | Usage |
|---|---|---|
| `extraSmall` | 4dp | Chips internal (M3 default) |
| `small` | 8dp | Book cards, text fields, filter dropdowns |
| `medium` | 12dp | Bottom sheets, dialogs |
| `large` | 16dp | Large cards, modals |
| `extraLarge` | 50dp (pill) | Genre chips, category chips, badge pills |

**Fixed Ratio:**
- All book cover images must use a **2:3 aspect ratio** container. This is
  consistent across all three reference screens.

**Todo List:**
- [ ] Create `ui/theme/Spacing.kt` with `object EBookStoreSpacing`
- [ ] Add M3 `Shapes` object to `Theme.kt`
- [ ] Document `BookCoverAspectRatio = 0.667f` constant in Spacing or Dimens file
- [ ] Audit all composables during implementation to ensure no hardcoded dp values

---

## Sub-Task 5 — Reusable Component Catalogue
### Status: [ ] pending

**Intent:** Define the complete component library — the atoms from which every
screen is assembled. These are layout and behavior definitions only; colors
always come from `MaterialTheme.colorScheme` tokens, not hardcoded values.

> RULE: No component may hardcode a color value. All colors reference
> `MaterialTheme.colorScheme.*` or `LocalRatingStarColor.current` for amber.
> This ensures both light and dark themes render correctly from a single codebase.

**Location for all components:** `ui/components/`

### C1 — `EBookStoreTopAppBar`
Two structural variants (same brand colors in both):
- **Home variant**: logo + app name left, search bar centre, cart icon + account right
- **Inner screen variant**: back arrow left, screen title centre, cart icon right

Parameters: `variant`, `title`, `cartItemCount`, `onSearchClick`, `onCartClick`,
`onBackClick`, `onAccountClick`

### C2 — `SearchBar`
Full-width rounded input with search icon. Used on Home and Catalogue.
Orange search button uses `MaterialTheme.colorScheme.primary`.

Parameters: `query`, `onQueryChange`, `onSearchSubmit`, `placeholder`

### C3 — `BookCard`
Two layout modes from a single composable:
- `VERTICAL`: cover top, sale badge overlay, title + author below
- `HORIZONTAL`: cover left (~80×120dp, 2:3), title/author/format/price/delivery right

All text colors from `MaterialTheme.colorScheme`. Sale badge uses `error` token.

Parameters: `book: BookUiModel`, `onCardClick`, `onWishlistClick`,
`showSaleBadge`, `layoutMode: CardLayoutMode`

### C4 — `SaleBadge`
Red pill/rectangle overlay. Uses `MaterialTheme.colorScheme.error` background,
`MaterialTheme.colorScheme.onError` text.

Parameters: `label: String = "Sale"`, `isVisible: Boolean`

### C5 — `GenreChip` / `CategoryChip`
Pill-shaped tappable chip. Two states: selected (filled, primary orange) and
unselected (outlined, secondary green border). Uses M3 `FilterChip` internally.

Parameters: `label`, `isSelected`, `onClick`

### C6 — `SectionHeader`
Bold title left, optional "See all" tappable link right (primary orange color).

Parameters: `title`, `onSeeAllClick: (() -> Unit)?`

### C7 — `HeroBanner`
Full-width auto-scrolling pager (M3 `HorizontalPager` + `PagerIndicator`).
Banner background color provided by data model, not hardcoded.

Parameters: `banners: List<BannerUiModel>`, `onBannerClick`

### C8 — `FilterBar`
Horizontal row of 4 dropdown triggers: Language, Format, Price Range, Sort By.
Each opens a `DropdownMenu` or bottom sheet. Outlined style using
`surfaceVariant` background.

Parameters: `filters: FilterState`, `onFilterChange: (FilterState) -> Unit`

### C9 — `CategoryChipRow` ✅ D4 RESOLVED
Horizontally scrollable row of `FilterChip` items, pinned directly below the
`SearchBar` on the Catalogue screen. Chips are reusable, accessible, and
stateless — the ViewModel owns the selected state.

Behaviour:
- "All" chip always first; tapping it clears the category filter
- Selected chip: filled, `MaterialTheme.colorScheme.primary` (orange) background
- Unselected chips: outlined, `MaterialTheme.colorScheme.secondary` (green) border
- Row scrolls horizontally; no wrap/overflow — single line only
- Chip tap calls `onCategorySelect` and scrolls selected chip into view

Accessibility:
- Each chip carries a `contentDescription` with its label
- Selected chip adds "selected" to its `contentDescription`
- `semantics { role = Role.RadioButton }` on each chip

Parameters: `categories: List<String>`, `selectedCategory: String`,
`onCategorySelect: (String) -> Unit`

### C10 — `BookDetailHero` ✅ D5 RESOLVED
Two-column layout (cover left, details right) on tablet; single-column stacked
on phone.

"Add to Cart" button behaviour (confirmed):
- Button tap triggers `onAddToCart` callback in the ViewModel
- ViewModel adds item to cart, updates `CartUiState.itemCount` immediately
- `CartBadge` (C17) re-renders with new count via StateFlow — no navigation
- Host screen shows a `Snackbar` with message "Added to cart" and an
  "Open Cart" action button that navigates to `CartScreen`
- If item is already in cart: button label changes to "In Cart" (disabled state)
  using `MaterialTheme.colorScheme.surfaceVariant` to indicate it's inactive

"Add to Wishlist" button:
- Outlined button, `MaterialTheme.colorScheme.secondary` (green) border
- Toggles in-place; icon changes filled/outlined based on `isInWishlist`

Parameters: `book: BookDetailUiModel`, `onAddToCart: () -> Unit`,
`onAddToWishlist: () -> Unit`, `isInCart: Boolean`, `isInWishlist: Boolean`

### C11 — `StarRating`
Row of 5 stars. Filled star color: `LocalRatingStarColor.current` (amber).
Two modes: read-only (display) and interactive (review form).

Parameters: `rating: Float`, `isInteractive: Boolean`, `onRatingChange: (Float) -> Unit`

### C12 — `ReviewCard`
Card with reviewer name (`titleSmall`), star rating (C11), review text
(`bodyMedium`). Surface: `MaterialTheme.colorScheme.surfaceVariant`.

Parameters: `review: ReviewUiModel`

### C13 — `ReviewInputForm`
`OutlinedTextField` (0/100 char counter), interactive `StarRating` (C11),
"Submit" button (primary orange).

Parameters: `onSubmit: (text: String, rating: Float) -> Unit`, `charLimit: Int = 100`

### C14 — `RelatedBooksPanel`
Section header ("Related Reads") + `LazyColumn` of `BookCard(HORIZONTAL)`.
On phone: renders below main detail content as a vertical list section.

Parameters: `books: List<BookUiModel>`, `onBookClick: (String) -> Unit`

### C15 — `BreadcrumbNav`
Horizontal `Row` of text links separated by "/" dividers. All tappable except
last (current page). Tappable segments: `MaterialTheme.colorScheme.primary` color.

Parameters: `crumbs: List<BreadcrumbItem>`, `onCrumbClick: (BreadcrumbItem) -> Unit`

### C16 — `TrustInfoBanner`
Horizontal scrollable row of icon + short text pairs ("Free shipping over £10",
etc.). Surface: `MaterialTheme.colorScheme.surfaceVariant`.

Parameters: `items: List<TrustInfoItem>`

### C17 — `CartBadge`
Numeric badge overlay on cart icon. Uses M3 `BadgedBox` with
`MaterialTheme.colorScheme.error` background.

Parameters: `count: Int`

### C18 — `AboutAuthorCard`
Author photo (`AsyncImage`, circular crop), name (`titleMedium`), bio
(`bodyMedium`). Card surface: `MaterialTheme.colorScheme.surfaceVariant`.

Parameters: `author: AuthorUiModel`

### C19 — `ThemeToggle` *(new — added this revision)*
Three-segment M3 `SegmentedButton` row: "System · Light · Dark".
Selected segment: `MaterialTheme.colorScheme.primary` (orange) fill.
Placed on the Profile/Settings screen.

Parameters: `currentMode: ThemeMode`, `onThemeModeChange: (ThemeMode) -> Unit`

---

## Sub-Task 6 — Navigation Relationships
### Status: [ ] pending

**Intent:** Define all screen-to-screen navigation edges from the three
reference screens plus the theme toggle destination.

**Navigation triggers observed:**

```
HomeScreen
  ├── [SearchBar submit]              → CatalogueScreen (query param)
  ├── [BookCard tap]                  → BookDetailScreen (bookId)
  ├── [Category nav strip tap]        → CatalogueScreen (category param)
  ├── [HeroBanner tap]                → CatalogueScreen (promotional filter)
  ├── [Sign in / Register tap]        → LoginScreen
  └── [Cart icon tap]                 → CartScreen

CatalogueScreen
  ├── [BookCard tap]                  → BookDetailScreen (bookId)
  ├── [Category item tap]             → CatalogueScreen (self, updated category)
  ├── [Filter change]                 → CatalogueScreen (self, updated filters)
  ├── [My Orders tab]                 → OrderHistoryScreen
  ├── [My Wishlist tab]               → WishlistScreen
  └── [Cart icon tap]                 → CartScreen

BookDetailScreen
  ├── [Breadcrumb "Home"]             → HomeScreen
  ├── [Breadcrumb category tap]       → CatalogueScreen (category param)
  ├── [Author name tap]               → AuthorProfileScreen (future)
  ├── [Publisher tap]                 → PublisherScreen (future)
  ├── [GenreChip tap]                 → CatalogueScreen (genre filter)
  ├── [Related book card tap]         → BookDetailScreen (different bookId)
  ├── [Add to Cart]                   → Snackbar shown; cart badge updates; "Open Cart" → CartScreen
  ├── [Add to Wishlist]               → in-place toggle (no navigation)
  └── [Back]                          → previous backstack entry

ProfileScreen  ← new destination for theme toggle
  ├── [ThemeToggle change]            → in-place theme change (no navigation)
  └── [Logout]                        → LoginScreen
```

**Preliminary route constants:**

| Route | Arguments |
|---|---|
| `home` | — |
| `catalogue?query={query}&category={category}` | `query: String?`, `category: String?` |
| `book_detail/{bookId}` | `bookId: String` (required) |
| `cart` | — |
| `wishlist` | — |
| `order_history` | — |
| `login` | — |
| `register` | — |
| `profile` | — |
| `checkout` | — |
| `payment` | — |
| `order_confirmation/{orderId}` | `orderId: String` |

---

## Sub-Task 7 — Per-Screen UI State Shapes
### Status: [ ] pending

**Intent:** Define the UiState data classes for the three analyzed screens plus
the new theme state so ViewModels have a clear contract.

**HomeScreen:**
```
data class HomeUiState(
    val banners: List<BannerUiModel> = emptyList(),
    val newReleases: List<BookUiModel> = emptyList(),
    val recommendations: List<BookUiModel> = emptyList(),
    val bestsellers: List<BookUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
```

**CatalogueScreen:**
```
data class CatalogueUiState(
    val books: List<BookUiModel> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "All",
    val filters: FilterState = FilterState(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

data class FilterState(
    val language: String? = null,
    val format: String? = null,       // "Paperback" | "eBook" | "Hardcover"
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val sortBy: SortOption = SortOption.RELEVANCE
)

enum class SortOption { RELEVANCE, PRICE_ASC, PRICE_DESC, NEWEST }
```

**BookDetailScreen:**
```
data class BookDetailUiState(
    val book: BookDetailUiModel? = null,
    val relatedBooks: List<BookUiModel> = emptyList(),
    val reviews: List<ReviewUiModel> = emptyList(),
    val isInWishlist: Boolean = false,
    val isInCart: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)
```

**App-level ThemeState (in ThemeViewModel):**
```
data class ThemeUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
```

---

## Decisions Summary

| # | Decision | Status | Resolution |
|---|---|---|---|
| D1 | Default theme mode | ✅ RESOLVED | `ThemeMode.SYSTEM` default; user overrides in Profile |
| D2 | Brand palette | ✅ RESOLVED | Orange `#F57C00`, Green `#1B5E20`, Amber `#FFB300`, Red `#D32F2F` |
| D3 | Dynamic color | ✅ RESOLVED | `dynamicColor = false` permanently across all themes |
| D4 | Category navigation on mobile | ✅ RESOLVED | Horizontal scrollable `CategoryChipRow` pinned below search bar |
| D5 | Add-to-cart post-action | ✅ RESOLVED | Snackbar + stay on screen + immediate badge update + "Open Cart" action |
| D6 | Custom font family | ⚠️ LOW PRIORITY | System Roboto default — confirm before `Type.kt` implementation |

## Remaining Review: Missing Requirements Check

The following items from the full feature list (provided in the earlier project
brief) are **not yet covered by this plan** and will need their own sub-tasks
in future planning phases. They are noted here so nothing is forgotten.

### Screens not yet analyzed (no reference screenshots provided)
| Screen | Key Design Questions Needed |
|---|---|
| Login / Registration | Form layout, validation error states, social login? |
| Shopping Cart | Item list, quantity stepper, price summary, remove action |
| Address Management | Form fields, saved addresses list, default address selection |
| Checkout | Multi-step or single page? Order summary + address confirmation |
| Payment Screen | Card entry form, payment method selector, security indicators |
| Order Confirmation | Success state, order number, CTA buttons |
| Order History | List layout, order status chips, "Buy Again" trigger |
| Wishlist | Grid or list? Same `BookCard` component applies |
| User Profile | Avatar, personal details, settings section, `ThemeToggle` placement |
| Gift Points | Points balance display, earn/redeem history |

### Missing component specifications (derived from feature list)
| Component | Required For |
|---|---|
| `C20 — QuantityStepper` | Cart screen — increment/decrement item quantity |
| `C21 — OrderStatusChip` | Order History — "Processing", "Shipped", "Delivered", "Cancelled" |
| `C22 — AddressCard` | Address Management, Checkout |
| `C23 — PaymentMethodCard` | Payment screen — card selector |
| `C24 — OrderSummaryCard` | Checkout, Order Confirmation |
| `C25 — GiftPointsBadge` | Profile screen, potentially Checkout |
| `C26 — EmptyStateView` | Cart, Wishlist, Order History (zero items state) |
| `C27 — ErrorStateView` | Any screen — network error / API failure state |
| `C28 — LoadingShimmer` | All screens — skeleton loading placeholder |

### Missing UiState contracts (derived from feature list)
- `CartUiState` — items list, total price, `itemCount` (drives C17 CartBadge)
- `CheckoutUiState` — selected address, order summary, validation state
- `PaymentUiState` — payment method, processing state, success/error
- `OrderConfirmationUiState` — confirmed order details
- `OrderHistoryUiState` — list of past orders with status
- `ProfileUiState` — user details, gift points balance, theme preference
- `WishlistUiState` — wishlist items list

### Architecture items not yet in this plan
| Item | Notes |
|---|---|
| Bottom navigation bar | Home · Catalogue · Cart · Orders · Profile tabs |
| Cancellation 48-hour timer | Order detail screen — countdown display |
| "Buy Again" flow | OrderHistory → adds item(s) directly to cart |
| Recommendations engine | Home + Catalogue — may be same API endpoint or separate |
| Gift points earn/redeem rules | Needs business rule confirmation |
| Internet permission | Missing from `AndroidManifest.xml` — needed before any network call |
| Hilt `@HiltAndroidApp` | `Application` class not yet created — required for Hilt to function |
| Navigation bottom bar route | `NavBar` composable not yet in component catalogue |

---

## Relevant Context Files

- [`app/src/main/java/com/example/ebookstore/ui/theme/Color.kt`](app/src/main/java/com/example/ebookstore/ui/theme/Color.kt)
- [`app/src/main/java/com/example/ebookstore/ui/theme/Theme.kt`](app/src/main/java/com/example/ebookstore/ui/theme/Theme.kt)
- [`app/src/main/java/com/example/ebookstore/ui/theme/Type.kt`](app/src/main/java/com/example/ebookstore/ui/theme/Type.kt)
- [`app/src/main/java/com/example/ebookstore/MainActivity.kt`](app/src/main/java/com/example/ebookstore/MainActivity.kt)
- [`app/build.gradle.kts`](app/build.gradle.kts)
- [`gradle/libs.versions.toml`](gradle/libs.versions.toml)
