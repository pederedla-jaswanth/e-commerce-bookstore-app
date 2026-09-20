package com.example.ebookstore.ui.navigation

/**
 * Sealed hierarchy of every navigation route in the app.
 *
 * - [BottomNav] routes are top-level tab destinations managed by the bottom bar.
 * - [Detail] routes are pushed on top of the back-stack from within a tab.
 *
 * Usage:
 *   navController.navigate(NavRoutes.BottomNav.Home.route)
 *   navController.navigate(NavRoutes.Detail.BookDetail.createRoute("abc123"))
 *
 * Adding a new screen:
 *   1. Add an object inside the appropriate sealed class.
 *   2. Add a composable() entry in EBookStoreNavGraph.kt.
 *   3. If it is a bottom-nav tab, add a BottomNavItem entry in BottomNavItem.kt.
 */
sealed class NavRoutes {

    // ── Bottom navigation tabs ────────────────────────────────────────────────
    sealed class BottomNav(val route: String) : NavRoutes() {
        object Home      : BottomNav("home")
        object Catalogue : BottomNav("catalogue")
        object Cart      : BottomNav("cart")
        object Orders    : BottomNav("orders")
        object Profile   : BottomNav("profile")
    }

    // ── Detail / full-screen destinations (pushed on the back-stack) ──────────
    sealed class Detail(val route: String) : NavRoutes() {
        object BookDetail : Detail("book_detail/{bookId}") {
            fun createRoute(bookId: String) = "book_detail/$bookId"
        }
        object Wishlist           : Detail("wishlist")
        object Login              : Detail("login")
        /** Login entry point when coming from the Cart checkout button — returns to Cart on success. */
        object LoginForCheckout   : Detail("login_for_checkout")
        object Register           : Detail("register")
        object Checkout    : Detail("checkout")
        object Payment     : Detail("payment")
        object OrderConfirmation : Detail("order_confirmation/{orderId}") {
            fun createRoute(orderId: String) = "order_confirmation/$orderId"
        }
        object OrderDetail : Detail("order_detail/{orderId}") {
            fun createRoute(orderId: String) = "order_detail/$orderId"
        }
    }
}
