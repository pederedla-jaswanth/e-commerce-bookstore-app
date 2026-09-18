package com.example.ebookstore.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ebookstore.ui.cart.CartViewModel
import com.example.ebookstore.ui.screens.cart.CartScreen
import com.example.ebookstore.ui.screens.catalogue.CatalogueScreen
import com.example.ebookstore.ui.screens.home.HomeScreen
import com.example.ebookstore.ui.screens.orders.OrdersScreen
import com.example.ebookstore.ui.screens.profile.ProfileScreen

/**
 * Root navigation graph for the EBookStore app.
 *
 * Owns the [Scaffold] so that the [EBookStoreBottomBar] is placed at the
 * correct level: visible for all bottom-nav destinations, but hidden for
 * full-screen detail routes (BookDetail, Checkout, Payment) added in later phases.
 *
 * A single [CartViewModel] is created here (NavGraph scope) so that every
 * screen in the graph shares the same cart state — the badge count on the
 * bottom bar and the CartScreen both read from it.
 *
 * The [Scaffold]'s inner padding is forwarded to each screen composable so that
 * content is never obscured by the bottom bar or system bars.
 *
 * @param navController  Shared [NavHostController]; defaults to a newly created
 *   one. Pass an existing controller from MainActivity if you need to drive
 *   navigation from outside this composable.
 */
@Composable
fun EBookStoreNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    // Single CartViewModel scoped to this NavGraph — all screens share it.
    val cartViewModel: CartViewModel = hiltViewModel()
    val cartState by cartViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier  = Modifier.fillMaxSize(),
        bottomBar = {
            EBookStoreBottomBar(
                navController = navController,
                cartItemCount = cartState.itemCount,
            )
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
                    cartViewModel       = cartViewModel,
                    onNavigateToCart    = {
                        navController.navigate(NavRoutes.BottomNav.Cart.route)
                    },
                    onNavigateToBook    = { bookId ->
                        // Phase 8: navController.navigate(NavRoutes.Detail.BookDetail.createRoute(bookId))
                    },
                    onNavigateToCatalogue = {
                        navController.navigate(NavRoutes.BottomNav.Catalogue.route)
                    },
                )
            }

            composable(route = NavRoutes.BottomNav.Catalogue.route) {
                CatalogueScreen()
            }

            composable(route = NavRoutes.BottomNav.Cart.route) {
                CartScreen()
            }

            composable(route = NavRoutes.BottomNav.Orders.route) {
                OrdersScreen()
            }

            composable(route = NavRoutes.BottomNav.Profile.route) {
                ProfileScreen()
            }

            // ── Detail destinations (added in future phases) ──────────────────
            // composable(
            //     route = NavRoutes.Detail.BookDetail.route,
            //     arguments = listOf(navArgument("bookId") { type = NavType.StringType }),
            // ) { backStackEntry ->
            //     val bookId = backStackEntry.arguments?.getString("bookId") ?: return@composable
            //     BookDetailScreen(bookId = bookId, cartViewModel = cartViewModel)
            // }
        }
    }
}
