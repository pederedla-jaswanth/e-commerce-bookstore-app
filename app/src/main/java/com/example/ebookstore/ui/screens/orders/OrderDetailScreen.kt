package com.example.ebookstore.ui.screens.orders

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

// ─────────────────────────────────────────────────────────────────────────────
// Stateful entry point
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Order detail screen — full-screen detail for a single order.
 *
 * Derives the displayed order from [OrdersViewModel.uiState] (which is a
 * [StateFlow] refreshed after every cancel), so status updates are reactive.
 * Falls back to a "not found" state if the ID is absent from the list.
 *
 * @param orderId          Order to display (from nav argument).
 * @param ordersViewModel  NavGraph-scoped; provides data and cancel/buy-again actions.
 * @param cartViewModel    NavGraph-scoped; receives Buy-Again items.
 * @param onNavigateBack   Pop back to Orders list.
 * @param onNavigateToCart Navigate to Cart after Buy Again.
 */
@Composable
fun OrderDetailScreen(
    orderId: String,
    ordersViewModel: OrdersViewModel,
    cartViewModel: CartViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Observe the live list so status chip re-renders after a cancel mutation.
    val uiState by ordersViewModel.uiState.collectAsStateWithLifecycle()
    val event   by ordersViewModel.event.collectAsStateWithLifecycle()

    // Derive the order reactively from the success list.
    val order = when (val s = uiState) {
        is OrdersUiState.Success -> s.orders.find { it.id == orderId }
        else                     -> ordersViewModel.getOrderById(orderId) // fallback during loading
    }

    // Consume one-shot events.
    LaunchedEffect(event) {
        when (val e = event) {
            is OrdersEvent.BuyAgainReady -> {
                e.books.forEach { book -> cartViewModel.addToCart(book) }
                ordersViewModel.clearEvent()
                onNavigateToCart()
            }
            is OrdersEvent.CancelSuccess -> ordersViewModel.clearEvent()
            is OrdersEvent.CancelFailed  -> ordersViewModel.clearEvent()
            is OrdersEvent.None          -> Unit   // nothing to do
        }
    }

    if (order == null) {
        OrderNotFound(onNavigateBack = onNavigateBack, modifier = modifier)
    } else {
        OrderDetailContent(
            order          = order,
            onNavigateBack = onNavigateBack,
            onCancel       = { ordersViewModel.cancelOrder(orderId) },
            onBuyAgain     = { ordersViewModel.buyAgain(orderId) },
            modifier       = modifier,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Stateless content composable
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailContent(
    order: Order,
    onNavigateBack: () -> Unit,
    onCancel: () -> Unit,
    onBuyAgain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top app bar ────────────────────────────────────────────────
            TopAppBar(
                title = {
                    Text(
                        text  = "Order Details",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // ── Scrollable body ────────────────────────────────────────────
            LazyColumn(
                modifier            = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding      = PaddingValues(EBookStoreSpacing.Medium),
                verticalArrangement = Arrangement.spacedBy(EBookStoreSpacing.Medium),
            ) {

                // ── Status header ──────────────────────────────────────────
                item {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text  = order.id,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Text(
                                text  = "Placed on ${formatEpoch(order.placedAt)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            )
                        }
                        Spacer(Modifier.width(EBookStoreSpacing.Small))
                        OrderStatusChip(status = order.status)
                    }
                }

                // ── Delivery info card ─────────────────────────────────────
                item {
                    DetailCard(title = "Delivery Info") {
                        DetailRow(label = "Estimated delivery", value = order.estimatedDelivery)
                        DetailRow(label = "Payment method",     value = order.paymentMethod)
                        DetailRow(label = "Shipping to",        value = order.shippingAddress)
                    }
                }

                // ── Items ──────────────────────────────────────────────────
                item {
                    Text(
                        text  = "Items (${order.items.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                items(order.items, key = { "${it.bookId}:${it.title}" }) { item ->
                    OrderItemRow(item = item)
                }

                // ── Price summary ──────────────────────────────────────────
                item {
                    DetailCard(title = "Price Summary") {
                        DetailRow("Subtotal", "₹${"%.0f".format(order.subtotal)}")
                        DetailRow(
                            label = "Shipping",
                            value = if (order.shipping == 0.0) "FREE"
                                    else "₹${"%.0f".format(order.shipping)}",
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = EBookStoreSpacing.XSmall),
                            color    = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        )
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text  = "Total",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text  = "₹${"%.0f".format(order.grandTotal)}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }

                // ── Actions ────────────────────────────────────────────────
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(EBookStoreSpacing.Small)) {
                        if (order.status == OrderStatus.DELIVERED ||
                            order.status == OrderStatus.CANCELLED
                        ) {
                            Button(
                                onClick  = onBuyAgain,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("Buy Again")
                            }
                        }
                        if (order.isCancellable) {
                            OutlinedButton(
                                onClick  = { showCancelDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors   = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error,
                                ),
                            ) {
                                Text("Cancel Order")
                            }
                        } else if (order.status != OrderStatus.DELIVERED &&
                                   order.status != OrderStatus.CANCELLED
                        ) {
                            Text(
                                text      = "Cancellation window has closed (48 h after placing order).",
                                style     = MaterialTheme.typography.bodySmall,
                                color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                modifier  = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(EBookStoreSpacing.Medium)) }
            }
        }
    }

    // ── Cancellation confirmation dialog ───────────────────────────────────
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title   = { Text("Cancel Order?") },
            text    = {
                Text(
                    "Are you sure you want to cancel order ${order.id}? " +
                    "This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    onCancel()
                }) {
                    Text("Yes, Cancel", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep Order")
                }
            },
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DetailCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape    = MaterialTheme.shapes.medium,
    ) {
        Column(modifier = Modifier.padding(EBookStoreSpacing.Medium)) {
            Text(
                text  = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(EBookStoreSpacing.Small))
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.Top,
    ) {
        Text(
            text     = label,
            style    = MaterialTheme.typography.bodySmall,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f),
        )
        Text(
            text      = value,
            style     = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color     = MaterialTheme.colorScheme.onSurface,
            modifier  = Modifier.weight(0.6f),
            textAlign = TextAlign.End,
        )
    }
    Spacer(Modifier.height(EBookStoreSpacing.XSmall))
}

@Composable
private fun OrderItemRow(item: com.example.ebookstore.domain.model.OrderItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape    = MaterialTheme.shapes.small,
    ) {
        Row(
            modifier          = Modifier.padding(EBookStoreSpacing.Small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val context = LocalContext.current
            AsyncImage(
                model              = ImageRequest.Builder(context)
                    .data(item.coverUrl)
                    .crossfade(300)
                    .build(),
                contentDescription = item.title,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .size(width = 44.dp, height = 66.dp)
                    .clip(MaterialTheme.shapes.extraSmall),
            )
            Spacer(Modifier.width(EBookStoreSpacing.Small))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text     = item.title,
                    style    = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color    = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                )
                Text(
                    text  = item.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text  = item.format,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(EBookStoreSpacing.Small))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text  = "₹${"%.0f".format(item.priceAtPurchase)}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )
                if (item.quantity > 1) {
                    Text(
                        text  = "×${item.quantity}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderNotFound(onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier
            .fillMaxSize()
            .padding(EBookStoreSpacing.Medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text      = "Order not found.",
            style     = MaterialTheme.typography.titleMedium,
            color     = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(EBookStoreSpacing.Medium))
        Button(onClick = onNavigateBack) { Text("Go Back") }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

private val previewRepo = MockOrderRepository()

@Preview(name = "Order Detail — Cancellable — Light")
@Composable
private fun OrderDetailCancellableLightPreview() {
    EBookStoreTheme {
        OrderDetailContent(
            order          = previewRepo.getOrders().first { it.isCancellable },
            onNavigateBack = {},
            onCancel       = {},
            onBuyAgain     = {},
        )
    }
}

@Preview(name = "Order Detail — Delivered — Dark")
@Composable
private fun OrderDetailDeliveredDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        OrderDetailContent(
            order          = previewRepo.getOrders().first { it.status == OrderStatus.DELIVERED },
            onNavigateBack = {},
            onCancel       = {},
            onBuyAgain     = {},
        )
    }
}

@Preview(name = "Order Detail — Cancelled — Light")
@Composable
private fun OrderDetailCancelledLightPreview() {
    EBookStoreTheme {
        OrderDetailContent(
            order          = previewRepo.getOrders().first { it.status == OrderStatus.CANCELLED },
            onNavigateBack = {},
            onCancel       = {},
            onBuyAgain     = {},
        )
    }
}

@Preview(name = "Order Detail — Shipped (no cancel) — Dark")
@Composable
private fun OrderDetailShippedDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        OrderDetailContent(
            order          = previewRepo.getOrders().first { it.status == OrderStatus.SHIPPED },
            onNavigateBack = {},
            onCancel       = {},
            onBuyAgain     = {},
        )
    }
}
