package com.example.ebookstore.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.ebookstore.ui.cart.CartViewModel
import com.example.ebookstore.ui.screens.auth.AuthViewModel
import com.example.ebookstore.ui.screens.auth.LoginScreen
import com.example.ebookstore.ui.screens.auth.RegisterScreen
import com.example.ebookstore.ui.screens.bookdetail.BookDetailScreen
import com.example.ebookstore.ui.screens.cart.CartScreen
import com.example.ebookstore.ui.screens.catalogue.CatalogueScreen
import com.example.ebookstore.ui.screens.checkout.CheckoutScreen
import com.example.ebookstore.ui.screens.checkout.CheckoutViewModel
import com.example.ebookstore.ui.screens.checkout.OrderConfirmationScreen
import com.example.ebookstore.ui.screens.checkout.PaymentScreen
import com.example.ebookstore.ui.screens.giftpoints.GiftPointsViewModel
import com.example.ebookstore.ui.screens.home.HomeScreen
import com.example.ebookstore.ui.screens.orders.OrderDetailScreen
import com.example.ebookstore.ui.screens.orders.OrdersScreen
import com.example.ebookstore.ui.screens.orders.OrdersViewModel
import com.example.ebookstore.ui.screens.profile.ProfileScreen
import com.example.ebookstore.ui.screens.recommendations.RecommendationsViewModel
import com.example.ebookstore.ui.screens.wishlist.WishlistScreen
import com.example.ebookstore.ui.theme.ThemeMode
import com.example.ebookstore.ui.wishlist.WishlistViewModel

/**
 * Root navigation graph for the EBookStore app.
 *
 * Owns the [Scaffold]; bottom bar is visible only on bottom-nav tab destinations
 * and hidden on all detail/full-screen routes.
 *
 * NavGraph-scoped ViewModels created here:
 * - [CartViewModel]       — shared cart state (badge + CartScreen)
 * - [WishlistViewModel]   — shared wishlist state (WishlistScreen + BookDetail heart)
 * - [AuthViewModel]       — shared auth state (Login/Register + ProfileScreen)
 * - [CheckoutViewModel]   — shared checkout/payment flow state
 * - [OrdersViewModel]     — shared order history + detail + cancel/buy-again state
 *
 * Theme state is Activity-scoped — passed in as [themeMode]/[onThemeModeChange].
 */
@Composable
fun EBookStoreNavGraph(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    navController: NavHostController = rememberNavController(),
) {
    val cartViewModel: CartViewModel                    = hiltViewModel()
    val wishlistViewModel: WishlistViewModel            = hiltViewModel()
    val authViewModel: AuthViewModel                    = hiltViewModel()
    val checkoutViewModel: CheckoutViewModel            = hiltViewModel()
    val ordersViewModel: OrdersViewModel                = hiltViewModel()
    val giftPointsViewModel: GiftPointsViewModel        = hiltViewModel()
    val recommendationsViewModel: RecommendationsViewModel = hiltViewModel()

    val cartState        by cartViewModel.uiState.collectAsStateWithLifecycle()
    val authState        by authViewModel.uiState.collectAsStateWithLifecycle()
    val checkoutState    by checkoutViewModel.uiState.collectAsStateWithLifecycle()
    val giftPointsState  by giftPointsViewModel.uiState.collectAsStateWithLifecycle()

    // Hide bottom bar on detail/full-screen routes.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute    = navBackStackEntry?.destination?.route
    val bottomNavRoutes = bottomNavItems.map { it.route }.toSet()
    val showBottomBar   = currentRoute in bottomNavRoutes

    Scaffold(
        modifier  = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                EBookStoreBottomBar(
                    navController = navController,
                    cartItemCount = cartState.itemCount,
                )
            }
        },
    ) { innerPadding ->

        NavHost(
            navController    = navController,
            startDestination = NavRoutes.BottomNav.Home.route,
            modifier         = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // ── Bottom navigation destinations ────────────────────────────────

            composable(route = NavRoutes.BottomNav.Home.route) {
                HomeScreen(
                    cartViewModel            = cartViewModel,
                    recommendationsViewModel = recommendationsViewModel,
                    onNavigateToCart         = {
                        navController.navigate(NavRoutes.BottomNav.Cart.route)
                    },
                    onNavigateToBook         = { bookId ->
                        navController.navigate(NavRoutes.Detail.BookDetail.createRoute(bookId))
                    },
                    onNavigateToCatalogue    = {
                        navController.navigate(NavRoutes.BottomNav.Catalogue.route)
                    },
                    onNavigateToProfile      = {
                        navController.navigate(NavRoutes.BottomNav.Profile.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    },
                )
            }

            composable(route = NavRoutes.BottomNav.Catalogue.route) {
                CatalogueScreen(
                    cartViewModel    = cartViewModel,
                    onNavigateToBook = { bookId ->
                        navController.navigate(NavRoutes.Detail.BookDetail.createRoute(bookId))
                    },
                    onNavigateToCart = {
                        navController.navigate(NavRoutes.BottomNav.Cart.route)
                    },
                )
            }

            composable(route = NavRoutes.BottomNav.Cart.route) {
                CartScreen(
                    cartViewModel        = cartViewModel,
                    onNavigateToBook     = { bookId ->
                        navController.navigate(NavRoutes.Detail.BookDetail.createRoute(bookId))
                    },
                    onNavigateToCheckout = {
                        if (authState.isLoggedIn) {
                            // Authenticated — seed checkout and proceed normally.
                            checkoutViewModel.seedFromCart(
                                items    = cartState.items,
                                subtotal = cartState.subtotal,
                                shipping = cartState.shipping,
                            )
                            navController.navigate(NavRoutes.Detail.Checkout.route)
                        } else {
                            // Guest — send to login; on success we return here and
                            // immediately continue to checkout.
                            navController.navigate(NavRoutes.Detail.LoginForCheckout.route)
                        }
                    },
                )
            }

            composable(route = NavRoutes.BottomNav.Orders.route) {
                OrdersScreen(
                    ordersViewModel         = ordersViewModel,
                    cartViewModel           = cartViewModel,
                    isLoggedIn              = authState.isLoggedIn,
                    onNavigateToOrderDetail = { orderId ->
                        navController.navigate(NavRoutes.Detail.OrderDetail.createRoute(orderId))
                    },
                    onNavigateToCart        = {
                        navController.navigate(NavRoutes.BottomNav.Cart.route)
                    },
                    onNavigateToLogin       = {
                        navController.navigate(NavRoutes.Detail.Login.route)
                    },
                )
            }

            composable(route = NavRoutes.BottomNav.Profile.route) {
                ProfileScreen(
                    currentUser          = authState.currentUser,
                    themeMode            = themeMode,
                    onThemeModeChange    = onThemeModeChange,
                    pointsBalance        = giftPointsState.balance,
                    onNavigateToWishlist = {
                        navController.navigate(NavRoutes.Detail.Wishlist.route)
                    },
                    onNavigateToLogin    = {
                        navController.navigate(NavRoutes.Detail.Login.route)
                    },
                    onLogout             = { authViewModel.logout() },
                )
            }

            // ── Detail destinations ───────────────────────────────────────────

            composable(
                route     = NavRoutes.Detail.BookDetail.route,
                arguments = listOf(navArgument("bookId") { type = NavType.StringType }),
            ) {
                BookDetailScreen(
                    cartViewModel            = cartViewModel,
                    recommendationsViewModel = recommendationsViewModel,
                    onNavigateBack           = { navController.popBackStack() },
                    onNavigateToCart         = {
                        navController.navigate(NavRoutes.BottomNav.Cart.route)
                    },
                    onNavigateToBook         = { bookId ->
                        navController.navigate(NavRoutes.Detail.BookDetail.createRoute(bookId))
                    },
                    isLoggedIn               = authState.isLoggedIn,
                    onLoginRequired          = {
                        navController.navigate(NavRoutes.Detail.Login.route)
                    },
                )
            }

            composable(route = NavRoutes.Detail.Wishlist.route) {
                WishlistScreen(
                    wishlistViewModel = wishlistViewModel,
                    cartViewModel     = cartViewModel,
                    onNavigateBack    = { navController.popBackStack() },
                    onNavigateToBook  = { bookId ->
                        navController.navigate(NavRoutes.Detail.BookDetail.createRoute(bookId))
                    },
                )
            }

            composable(route = NavRoutes.Detail.Login.route) {
                LoginScreen(
                    authViewModel        = authViewModel,
                    onNavigateBack       = { navController.popBackStack() },
                    onNavigateToRegister = {
                        navController.navigate(NavRoutes.Detail.Register.route) {
                            popUpTo(NavRoutes.Detail.Login.route) { inclusive = true }
                        }
                    },
                    onLoginSuccess       = {
                        navController.navigate(NavRoutes.BottomNav.Profile.route) {
                            popUpTo(NavRoutes.Detail.Login.route) { inclusive = true }
                        }
                    },
                )
            }

            // Login entered from the Cart "Proceed to Checkout" button.
            // On success: seed checkout from cart state and navigate to Checkout
            // (removing LoginForCheckout from the back-stack so Back goes to Cart).
            composable(route = NavRoutes.Detail.LoginForCheckout.route) {
                LoginScreen(
                    authViewModel        = authViewModel,
                    onNavigateBack       = { navController.popBackStack() },
                    onNavigateToRegister = {
                        navController.navigate(NavRoutes.Detail.Register.route) {
                            popUpTo(NavRoutes.Detail.LoginForCheckout.route) { inclusive = true }
                        }
                    },
                    onLoginSuccess       = {
                        checkoutViewModel.seedFromCart(
                            items    = cartState.items,
                            subtotal = cartState.subtotal,
                            shipping = cartState.shipping,
                        )
                        navController.navigate(NavRoutes.Detail.Checkout.route) {
                            popUpTo(NavRoutes.Detail.LoginForCheckout.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(route = NavRoutes.Detail.Register.route) {
                RegisterScreen(
                    authViewModel     = authViewModel,
                    onNavigateBack    = { navController.popBackStack() },
                    onNavigateToLogin = {
                        navController.navigate(NavRoutes.Detail.Login.route) {
                            popUpTo(NavRoutes.Detail.Register.route) { inclusive = true }
                        }
                    },
                    onRegisterSuccess = {
                        // Award welcome bonus on first registration.
                        giftPointsViewModel.awardWelcomeBonus()
                        navController.navigate(NavRoutes.BottomNav.Profile.route) {
                            popUpTo(NavRoutes.Detail.Register.route) { inclusive = true }
                        }
                    },
                )
            }

            // ── Checkout flow ─────────────────────────────────────────────────

            composable(route = NavRoutes.Detail.Checkout.route) {
                CheckoutScreen(
                    checkoutViewModel   = checkoutViewModel,
                    onNavigateBack      = { navController.popBackStack() },
                    onNavigateToPayment = {
                        navController.navigate(NavRoutes.Detail.Payment.route)
                    },
                )
            }

            composable(route = NavRoutes.Detail.Payment.route) {
                PaymentScreen(
                    checkoutViewModel = checkoutViewModel,
                    onNavigateBack    = { navController.popBackStack() },
                    onOrderSuccess    = { orderId ->
                        // Award gift points for the completed order.
                        giftPointsViewModel.awardOrderPoints(
                            orderId    = orderId,
                            grandTotal = checkoutState.grandTotal,
                        )
                        // Clear the cart only on success.
                        cartViewModel.clearCart()
                        navController.navigate(
                            NavRoutes.Detail.OrderConfirmation.createRoute(orderId)
                        ) {
                            // Remove Checkout and Payment from back-stack so back goes to Cart.
                            popUpTo(NavRoutes.Detail.Checkout.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(
                route     = NavRoutes.Detail.OrderConfirmation.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId")
                OrderConfirmationScreen(
                    orderId            = orderId,
                    items              = checkoutState.items,
                    subtotal           = checkoutState.subtotal,
                    shipping           = checkoutState.shipping,
                    grandTotal         = checkoutState.grandTotal,
                    pointsEarned       = giftPointsViewModel.lastAwardedPoints,
                    onContinueShopping = {
                        giftPointsViewModel.clearLastAwardedPoints()
                        checkoutViewModel.reset()
                        navController.navigate(NavRoutes.BottomNav.Home.route) {
                            popUpTo(NavRoutes.BottomNav.Home.route) { inclusive = true }
                        }
                    },
                    onRetryPayment     = {
                        giftPointsViewModel.clearLastAwardedPoints()
                        checkoutViewModel.retryPayment()
                        navController.navigate(NavRoutes.Detail.Payment.route) {
                            popUpTo(NavRoutes.Detail.OrderConfirmation.route) { inclusive = true }
                        }
                    },
                )
            }

            // ── Order detail ──────────────────────────────────────────────────

            composable(
                route     = NavRoutes.Detail.OrderDetail.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderDetailScreen(
                    orderId         = orderId,
                    ordersViewModel = ordersViewModel,
                    cartViewModel   = cartViewModel,
                    onNavigateBack  = { navController.popBackStack() },
                    onNavigateToCart = {
                        navController.navigate(NavRoutes.BottomNav.Cart.route)
                    },
                )
            }

            // ── Future destinations ───────────────────────────────────────────
        }
    }
}
