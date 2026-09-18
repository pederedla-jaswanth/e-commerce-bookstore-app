package com.example.ebookstore.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

// Custom icons not in the default filled/outlined sets are referenced below.
// We use AutoMirrored variants where available for RTL support.
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.outlined.Receipt

/**
 * Represents a single tab in the bottom navigation bar.
 *
 * @param route         The [NavRoutes.BottomNav] route this item navigates to.
 * @param label         String label shown below the icon.
 * @param selectedIcon  Icon displayed when this tab is currently selected.
 * @param unselectedIcon Icon displayed when this tab is not selected.
 */
data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

/**
 * The five bottom navigation tabs, in display order.
 *
 * Icons follow the Material convention: filled = selected, outlined = unselected.
 */
val bottomNavItems = listOf(
    BottomNavItem(
        route          = NavRoutes.BottomNav.Home.route,
        label          = "Home",
        selectedIcon   = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    ),
    BottomNavItem(
        route          = NavRoutes.BottomNav.Catalogue.route,
        label          = "Catalogue",
        selectedIcon   = Icons.AutoMirrored.Filled.MenuBook,
        unselectedIcon = Icons.AutoMirrored.Outlined.MenuBook,
    ),
    BottomNavItem(
        route          = NavRoutes.BottomNav.Cart.route,
        label          = "Cart",
        selectedIcon   = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart,
    ),
    BottomNavItem(
        route          = NavRoutes.BottomNav.Orders.route,
        label          = "Orders",
        selectedIcon   = Icons.Filled.Receipt,
        unselectedIcon = Icons.Outlined.Receipt,
    ),
    BottomNavItem(
        route          = NavRoutes.BottomNav.Profile.route,
        label          = "Profile",
        selectedIcon   = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
    ),
)
