<div align="center">

# 📚 EBookStore

### A modern e-book shopping app built with Jetpack Compose & Material Design 3

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose BOM](https://img.shields.io/badge/Compose%20BOM-2026.02.01-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![AGP](https://img.shields.io/badge/AGP-9.4.0-green)](https://developer.android.com/build)
[![License](https://img.shields.io/badge/License-MIT-orange)](LICENSE)

</div>

---

## ✨ Overview

**EBookStore** is a fully featured native Android e-book store application. Browse thousands of books, manage your cart and wishlist, complete a real checkout flow with smart address auto-fill, track your order history, and earn gift points on every purchase — all wrapped in a polished Material Design 3 UI with full light/dark theme support.

---

## 📱 Screens & Features

| Screen | Features |
|--------|----------|
| 🏠 **Home** | Hero banner auto-carousel · Category chips · New Releases · Recommendations · Bestsellers · Trending row |
| 📖 **Catalogue** | Search bar · Category filter chips · Sort dropdown · Format & sale filters (bottom sheet) · Horizontal book list |
| 📗 **Book Detail** | Full cover · Rating & reviews · Author card · Related reads · Add to Cart (Snackbar + badge) · Wishlist toggle |
| 🛒 **Cart** | Quantity stepper · Live subtotal/shipping/total · Persistent (survives restarts) · Proceed to Checkout CTA |
| ❤️ **Wishlist** | Persistent wishlist · Move to Cart · Remove |
| 🏁 **Checkout** | Smart address form · Country dropdown with auto-fill · City autocomplete → State + PIN auto-fill · Order summary |
| 💳 **Payment** | Multiple payment methods (Card / UPI / Net Banking / COD) · Mock processing · Order confirmation |
| 📦 **Orders** | Live order history · Status chips · Buy Again · Order Detail · Cancel (48-hour window) |
| 👤 **Profile** | Avatar · Gift points balance · Wishlist shortcut · Theme toggle (System/Light/Dark) · Sign Out |
| 🔐 **Auth** | Login · Register · Form validation · Password visibility toggle · Guest checkout gate |

---

## 🏗️ Architecture

```
EBookStore/
├── data/
│   ├── local/              # Room database — entities, DAOs, relations
│   │   ├── dao/            # CartDao, WishlistDao, OrderDao, UserDao, GiftPointsDao
│   │   └── entity/         # CartItemEntity, WishlistItemEntity, OrderEntity …
│   ├── mock/               # MockData, MockOrderRepository, MockRecommendationRepository
│   ├── preferences/        # ThemePreferenceRepository (DataStore)
│   └── repository/         # RoomCartRepository, RoomOrderRepository …
│
├── di/                     # Hilt modules (AppModule, DatabaseModule, RepositoryModule)
│
├── domain/
│   ├── model/              # Book, Order, CartItem, CountryData, GiftPoints …
│   └── repository/         # Repository interfaces (contracts)
│
└── ui/
    ├── cart/               # CartViewModel, CartUiState
    ├── components/         # BookCard, HeroBanner, CategoryChipRow, StarRating …
    ├── navigation/         # NavRoutes, EBookStoreNavGraph, EBookStoreBottomBar
    ├── screens/
    │   ├── auth/           # LoginScreen, RegisterScreen, AuthViewModel
    │   ├── bookdetail/     # BookDetailScreen, BookDetailViewModel
    │   ├── cart/           # CartScreen
    │   ├── catalogue/      # CatalogueScreen, CatalogueViewModel
    │   ├── checkout/       # CheckoutScreen, CheckoutViewModel, PaymentScreen
    │   ├── home/           # HomeScreen, HomeViewModel
    │   ├── orders/         # OrdersScreen, OrderDetailScreen, OrdersViewModel
    │   ├── profile/        # ProfileScreen
    │   └── wishlist/       # WishlistScreen
    ├── theme/              # Color, Spacing, Typography, ThemeMode, EBookStoreTheme
    └── wishlist/           # WishlistViewModel
```

The app follows **MVVM + Clean Architecture** with a unidirectional data flow:

```
UI (Composables)  ←→  ViewModel (StateFlow)  ←→  Repository (interface)  ←→  Room / DataStore
```

---

## 🛠️ Tech Stack

| Layer | Library | Version |
|---|---|---|
| **Language** | Kotlin | 2.2.10 |
| **UI** | Jetpack Compose + Material 3 | BOM 2026.02.01 |
| **Navigation** | Navigation Compose | 2.10.1 |
| **DI** | Hilt (Dagger) | 2.60.1 |
| **Database** | Room (SQLite) | 2.8.5 |
| **Preferences** | DataStore Preferences | 1.2.1 |
| **Image Loading** | Coil | 2.7.0 |
| **Async** | Kotlin Coroutines + Flow | 1.11.0 |
| **Build** | AGP + KSP | 9.4.0 / 2.3.12 |

---

## 🗄️ Database Schema

Room database (`AppDatabase` v2) with the following tables:

```
users            — registered accounts (email, hashed password)
cart_items       — persistent cart line-items (bookId, title, price, qty …)
wishlist_items   — persistent wishlist entries
orders           — order headers (status, total, address, payment method)
order_items      — order line-items (FK → orders)
points_ledger    — gift-points earn/redeem entries
```

---

## 🎨 Design System

| Token | Value |
|---|---|
| Primary | `#F57C00` (Brand Orange) |
| Primary Dark | `#FFAD42` (Brand Orange Light) |
| Secondary | `#1B5E20` (Brand Green) |
| Star Rating | `#FFB300` (Brand Amber) — via `LocalRatingStarColor` |
| Background Light | `#FFF8F0` (warm off-white) |
| Background Dark | `#121212` |

Spacing is provided by `EBookStoreSpacing` tokens (`XXSmall` → `XXLarge`).  
No hardcoded `Color(…)` or raw `dp` values anywhere in the UI layer.

---

## 🚀 Getting Started

### Prerequisites

- Android Studio **Hedgehog** or later
- JDK 11+
- Android device / emulator running **API 26+**

### Clone & Run

```bash
# Clone the repository
git clone https://github.com/pederedla-jaswanth/e-commerce-bookstore-app.git
cd e-commerce-bookstore-app

# Build debug APK
./gradlew assembleDebug

# Install on connected device / emulator
./gradlew installDebug
```

> **Tip:** Prefer **Android Studio ▶ Run** for installs — `adb install` can silently hang on some emulators.

### Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires running emulator)
./gradlew connectedAndroidTest
```

---

## 🔐 Authentication Flow

```
Guest user
  ├── Browse, Search, View Book Detail   ✅ allowed
  ├── Add to Cart                        ✅ allowed
  ├── Proceed to Checkout                🔒 → redirected to Login → then Checkout
  ├── Wishlist heart button              🔒 → redirected to Login
  └── Order History tab                  🔒 → "Sign in" prompt shown
```

After login/register, users are returned exactly to where they were trying to go.

---

## 🌍 Smart Address Auto-Fill

The Checkout screen ships with intelligent address assistance for **75+ cities across 20 countries**:

- **Type a city name** → dropdown shows matching suggestions
- **Tap a suggestion** → City, State/Region, and PIN/ZIP are filled automatically
- **Change country** → postal code field pre-fills with a country-specific example format
- Clicking the dropdown item **closes the menu immediately** and never re-opens

---

## 🎁 Gift Points

Users earn **1 point per ₹10 spent** on every order. Points are stored in a Room ledger and displayed on the Profile screen. A welcome bonus is awarded on first registration.

---

## 🌙 Theme Support

Three modes selectable from the Profile screen:

| Mode | Behavior |
|---|---|
| **System** | Follows device dark/light setting |
| **Light** | Always light |
| **Dark** | Always dark |

Theme preference is persisted via DataStore and restored on next launch. Dynamic color is intentionally disabled to preserve brand identity.

---

## 📂 Project Structure at a Glance

```
app/
├── build.gradle.kts
└── src/main/
    ├── AndroidManifest.xml
    ├── java/com/example/ebookstore/
    │   ├── EBookStoreApplication.kt    # Hilt application class
    │   ├── MainActivity.kt             # Single-activity host
    │   ├── data/ …
    │   ├── di/   …
    │   ├── domain/ …
    │   └── ui/ …
    └── res/
        ├── drawable/
        │   ├── ic_launcher_background.xml   # Brand orange background
        │   └── ic_launcher_foreground.xml   # White open-book icon
        └── mipmap-*/                        # Adaptive icon densities
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch — `git checkout -b feature/my-feature`
3. Commit your changes — `git commit -m "feat: add my feature"`
4. Push to the branch — `git push origin feature/my-feature`
5. Open a Pull Request

---

## 📄 License

```
MIT License

Copyright (c) 2025 Pederedla Jaswanth

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

<div align="center">

Made with ❤️ using **Kotlin** + **Jetpack Compose**

⭐ Star this repo if you found it helpful!

</div>
