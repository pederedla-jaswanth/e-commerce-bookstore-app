package com.example.ebookstore.ui.navigation

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ebookstore.ui.theme.EBookStoreTheme

/**
 * The main bottom navigation bar for the EBookStore app.
 *
 * Renders five tabs (Home, Catalogue, Cart, Orders, Profile) using M3
 * [NavigationBar] and [NavigationBarItem].
 *
 * The Cart tab shows a numeric badge when [cartItemCount] > 0.
 *
 * Tab selection is driven by the current back-stack entry so that deep-links
 * and programmatic navigation keep the correct tab highlighted automatically.
 *
 * Navigation uses [NavController.navigate] with [NavGraph.findStartDestination]
 * and `launchSingleTop = true` + `restoreState = true` for correct back-stack
 * behavior: re-selecting a tab restores its previous scroll/state rather than
 * creating a new back-stack entry.
 *
 * @param navController  The [NavController] shared with [EBookStoreNavGraph].
 * @param cartItemCount  Number of items in the cart — shows a badge on the Cart tab.
 */
@Composable
fun EBookStoreBottomBar(
    navController: NavController,
    cartItemCount: Int = 0,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor   = MaterialTheme.colorScheme.onSurface,
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick  = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to avoid
                        // building a large back-stack when tapping tabs repeatedly.
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
                icon = {
                    val isBadged = item.route == NavRoutes.BottomNav.Cart.route
                        && cartItemCount > 0

                    if (isBadged) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor   = MaterialTheme.colorScheme.onError,
                                    modifier       = Modifier.semantics {
                                        contentDescription = "$cartItemCount items in cart"
                                    },
                                ) {
                                    // Show up to 99; display "99+" beyond that
                                    Text(
                                        text  = if (cartItemCount > 99) "99+" else "$cartItemCount",
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector        = if (isSelected) item.selectedIcon
                                                     else item.unselectedIcon,
                                contentDescription = item.label,
                            )
                        }
                    } else {
                        Icon(
                            imageVector        = if (isSelected) item.selectedIcon
                                                 else item.unselectedIcon,
                            contentDescription = item.label,
                        )
                    }
                },
                label = {
                    Text(
                        text  = item.label,
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor       = MaterialTheme.colorScheme.primary,
                    selectedTextColor       = MaterialTheme.colorScheme.primary,
                    unselectedIconColor     = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor     = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor          = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Bottom Bar — Light, no badge")
@Composable
private fun BottomBarLightPreview() {
    EBookStoreTheme {
        EBookStoreBottomBar(navController = rememberNavController())
    }
}

@Preview(name = "Bottom Bar — Light, cart badge")
@Composable
private fun BottomBarBadgePreview() {
    EBookStoreTheme {
        EBookStoreBottomBar(
            navController = rememberNavController(),
            cartItemCount = 3,
        )
    }
}

@Preview(name = "Bottom Bar — Dark, cart badge")
@Composable
private fun BottomBarDarkPreview() {
    EBookStoreTheme(themeMode = com.example.ebookstore.ui.theme.ThemeMode.DARK) {
        EBookStoreBottomBar(
            navController = rememberNavController(),
            cartItemCount = 3,
        )
    }
}
