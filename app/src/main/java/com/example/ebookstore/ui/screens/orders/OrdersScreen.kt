package com.example.ebookstore.ui.screens.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.ebookstore.data.mock.MockOrderRepository
import com.example.ebookstore.domain.model.Order
import com.example.ebookstore.domain.model.OrderStatus
import com.example.ebookstore.ui.cart.CartViewModel
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme
import com.example.ebookstore.ui.theme.ThemeMode
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// ─────────────────────────────────────────────────────────────────────────────
// Stateful entry point
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Orders screen — top-level tab destination (Phase 12).
 *
 * Shows order history list. Navigates to detail on card tap.
 * "Buy Again" adds all order items to cart and navigates to Cart.
 *
 * @param ordersViewModel  NavGraph-scoped; passed as parameter.
 * @param cartViewModel    NavGraph-scoped; receives Buy-Again items.
 * @param onNavigateToOrderDetail  Navigate to Order Detail for [orderId].
 * @param onNavigateToCart         Navigate to Cart after Buy Again.
 */
@Composable
fun OrdersScreen(
    ordersViewModel: OrdersViewModel = hiltViewModel(),
    cartViewModel: CartViewModel? = null,
    isLoggedIn: Boolean = false,
    onNavigateToOrderDetail: (String) -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val uiState by ordersViewModel.uiState.collectAsStateWithLifecycle()
    val event   by ordersViewModel.event.collectAsStateWithLifecycle()

    // Show sign-in gate when not authenticated.
    if (!isLoggedIn) {
        OrdersSignInPrompt(onNavigateToLogin = onNavigateToLogin, modifier = modifier)
        return
    }

    // Handle one-shot events.
    LaunchedEffect(event) {
        when (val e = event) {
            is OrdersEvent.BuyAgainReady -> {
                e.books.forEach { book -> cartViewModel?.addToCart(book) }
                ordersViewModel.clearEvent()
                onNavigateToCart()
            }
            is OrdersEvent.CancelSuccess -> ordersViewModel.clearEvent()
            is OrdersEvent.CancelFailed  -> ordersViewModel.clearEvent()
            else -> Unit
        }
    }

    OrdersScreenContent(
        uiState              = uiState,
        onRetry              = { /* Room Flow is live; no manual retry needed */ },
        onOrderClick         = onNavigateToOrderDetail,
        onBuyAgain           = ordersViewModel::buyAgain,
        modifier             = modifier,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Stateless content composable
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun OrdersScreenContent(
    uiState: OrdersUiState,
    onRetry: () -> Unit,
    onOrderClick: (String) -> Unit,
    onBuyAgain: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.background,
    ) {
        when (uiState) {
            is OrdersUiState.Loading -> OrdersLoadingState()
            is OrdersUiState.Empty   -> OrdersEmptyState()
            is OrdersUiState.Error   -> OrdersErrorState(message = uiState.message, onRetry = onRetry)
            is OrdersUiState.Success -> OrdersList(
                orders       = uiState.orders,
                onOrderClick = onOrderClick,
                onBuyAgain   = onBuyAgain,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Loading / empty / error
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun OrdersLoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun OrdersEmptyState() {
    Column(
        modifier            = Modifier.fillMaxSize().padding(EBookStoreSpacing.Large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        androidx.compose.material3.Icon(
            imageVector        = Icons.Default.ShoppingBag,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
            modifier           = Modifier.size(80.dp),
        )
        Spacer(Modifier.height(EBookStoreSpacing.Medium))
        Text(
            text      = "No orders yet",
            style     = MaterialTheme.typography.titleMedium,
            color     = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(EBookStoreSpacing.XSmall))
        Text(
            text      = "Your placed orders will appear here.",
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun OrdersErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier            = Modifier.fillMaxSize().padding(EBookStoreSpacing.Large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text      = message,
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(EBookStoreSpacing.Medium))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sign-in prompt (shown when not authenticated)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun OrdersSignInPrompt(
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(EBookStoreSpacing.Large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            androidx.compose.material3.Icon(
                imageVector        = Icons.Default.AccountCircle,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
                modifier           = Modifier.size(80.dp),
            )
            Spacer(Modifier.height(EBookStoreSpacing.Medium))
            Text(
                text      = "Sign in to view your orders",
                style     = MaterialTheme.typography.titleMedium,
                color     = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(EBookStoreSpacing.XSmall))
            Text(
                text      = "Your order history is linked to your account.",
                style     = MaterialTheme.typography.bodyMedium,
                color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(EBookStoreSpacing.Large))
            Button(
                onClick  = onNavigateToLogin,
                modifier = Modifier.fillMaxWidth(0.7f),
            ) {
                Text("Sign In")
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Orders list
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun OrdersList(
    orders: List<Order>,
    onOrderClick: (String) -> Unit,
    onBuyAgain: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(EBookStoreSpacing.Medium),
        verticalArrangement = Arrangement.spacedBy(EBookStoreSpacing.Medium),
    ) {
        item {
            Text(
                text  = "My Orders",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.height(EBookStoreSpacing.XSmall))
        }
        items(orders, key = { it.id }) { order ->
            OrderCard(
                order        = order,
                onClick      = { onOrderClick(order.id) },
                onBuyAgain   = { onBuyAgain(order.id) },
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Order card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun OrderCard(
    order: Order,
    onClick: () -> Unit,
    onBuyAgain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick  = onClick,
        modifier = modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape    = MaterialTheme.shapes.medium,
    ) {
        Column(modifier = Modifier.padding(EBookStoreSpacing.Medium)) {

            // ── Header row: order ID + date + status chip ──────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = order.id,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text  = formatEpoch(order.placedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                OrderStatusChip(status = order.status)
            }

            Spacer(Modifier.height(EBookStoreSpacing.Small))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            Spacer(Modifier.height(EBookStoreSpacing.Small))

            // ── Items preview (first cover + overflow label) ───────────────
            val context = LocalContext.current
            Row(verticalAlignment = Alignment.CenterVertically) {
                order.items.take(3).forEach { item ->
                    AsyncImage(
                        model             = ImageRequest.Builder(context)
                            .data(item.coverUrl)
                            .crossfade(300)
                            .build(),
                        contentDescription = item.title,
                        contentScale      = ContentScale.Crop,
                        modifier          = Modifier
                            .size(width = 32.dp, height = 48.dp)
                            .clip(MaterialTheme.shapes.extraSmall),
                    )
                    Spacer(Modifier.width(EBookStoreSpacing.XSmall))
                }
                if (order.items.size > 3) {
                    Text(
                        text  = "+${order.items.size - 3} more",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text  = "₹${"%.0f".format(order.grandTotal)}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(Modifier.height(EBookStoreSpacing.Small))

            // ── Action row ─────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                if (order.status == OrderStatus.DELIVERED || order.status == OrderStatus.CANCELLED) {
                    TextButton(onClick = onBuyAgain) {
                        Text(
                            text  = "Buy Again",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Status chip
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun OrderStatusChip(status: OrderStatus, modifier: Modifier = Modifier) {
    val (bg, fg) = when (status) {
        OrderStatus.PROCESSING -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        OrderStatus.CONFIRMED  -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        OrderStatus.SHIPPED    -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        OrderStatus.DELIVERED  -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.secondary
        OrderStatus.CANCELLED  -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error
    }
    Surface(
        modifier = modifier,
        shape    = MaterialTheme.shapes.extraLarge,
        color    = bg,
    ) {
        Text(
            text     = status.label,
            style    = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color    = fg,
            modifier = Modifier.padding(horizontal = EBookStoreSpacing.Small, vertical = EBookStoreSpacing.XXSmall),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

private val dateFormatter = DateTimeFormatter
    .ofPattern("d MMM yyyy, h:mm a")
    .withZone(ZoneId.systemDefault())

internal fun formatEpoch(epochMillis: Long): String =
    dateFormatter.format(Instant.ofEpochMilli(epochMillis))

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Orders Screen — Success — Light")
@Composable
private fun OrdersSuccessLightPreview() {
    EBookStoreTheme {
        OrdersScreenContent(
            uiState      = OrdersUiState.Success(MockOrderRepository().getOrders()),
            onRetry      = {},
            onOrderClick = {},
            onBuyAgain   = {},
        )
    }
}

@Preview(name = "Orders Screen — Success — Dark")
@Composable
private fun OrdersSuccessDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        OrdersScreenContent(
            uiState      = OrdersUiState.Success(MockOrderRepository().getOrders()),
            onRetry      = {},
            onOrderClick = {},
            onBuyAgain   = {},
        )
    }
}

@Preview(name = "Orders Screen — Empty — Light")
@Composable
private fun OrdersEmptyLightPreview() {
    EBookStoreTheme {
        OrdersScreenContent(
            uiState      = OrdersUiState.Empty,
            onRetry      = {},
            onOrderClick = {},
            onBuyAgain   = {},
        )
    }
}

@Preview(name = "Orders Screen — Loading — Dark")
@Composable
private fun OrdersLoadingDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        OrdersScreenContent(
            uiState      = OrdersUiState.Loading,
            onRetry      = {},
            onOrderClick = {},
            onBuyAgain   = {},
        )
    }
}
