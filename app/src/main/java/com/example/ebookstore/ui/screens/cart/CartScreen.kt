package com.example.ebookstore.ui.screens.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.ShoppingCartCheckout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.ebookstore.data.mock.MockData
import com.example.ebookstore.ui.cart.CartItem
import com.example.ebookstore.ui.cart.CartUiState
import com.example.ebookstore.ui.cart.CartViewModel
import com.example.ebookstore.ui.components.QuantityStepper
import com.example.ebookstore.ui.components.SaleBadge
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme
import com.example.ebookstore.ui.theme.ThemeMode

// ─────────────────────────────────────────────────────────────────────────────
// Entry point (Hilt-wired)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Cart screen — top-level tab destination (Phase 9).
 *
 * Layout:
 * - Top app bar with item count
 * - [LazyColumn] of [CartItemRow]s — each with cover, title, price, [QuantityStepper], delete
 * - Sticky bottom summary panel: subtotal, shipping (free ≥ ₹500), grand total, Checkout CTA
 * - Empty state when cart is empty
 *
 * @param onNavigateToBook     Called with bookId when a cart item title is tapped.
 * @param onNavigateToCheckout Called when "Proceed to Checkout" is tapped (Phase 11).
 * @param cartViewModel        Shared NavGraph-level ViewModel.
 * @param modifier             Applied to root Surface.
 */
@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    modifier: Modifier = Modifier,
    onNavigateToBook: (String) -> Unit = {},
    onNavigateToCheckout: () -> Unit = {},
) {
    val uiState by cartViewModel.uiState.collectAsStateWithLifecycle()

    CartScreenContent(
        uiState              = uiState,
        onIncreaseQuantity   = cartViewModel::increaseQuantity,
        onDecreaseQuantity   = cartViewModel::decreaseQuantity,
        onRemoveItem         = cartViewModel::removeItem,
        onClearCart          = cartViewModel::clearCart,
        onNavigateToBook     = onNavigateToBook,
        onNavigateToCheckout = onNavigateToCheckout,
        modifier             = modifier,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Stateless content layer (preview-friendly)
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreenContent(
    uiState: CartUiState,
    onIncreaseQuantity: (String) -> Unit,
    onDecreaseQuantity: (String) -> Unit,
    onRemoveItem: (String) -> Unit,
    onClearCart: () -> Unit,
    onNavigateToBook: (String) -> Unit,
    onNavigateToCheckout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top App Bar ───────────────────────────────────────────────────
            TopAppBar(
                title = {
                    Text(
                        text  = if (uiState.isEmpty) "Cart"
                                else "Cart (${uiState.itemCount})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                actions = {
                    if (!uiState.isEmpty) {
                        TextButton(onClick = onClearCart) {
                            Text(
                                text  = "Clear all",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )

            // ── Body ──────────────────────────────────────────────────────────
            if (uiState.isEmpty) {
                EmptyCartState()
            } else {
                // Items list + summary panel stacked in a Box so the summary
                // sits at the bottom and items scroll beneath it.
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier       = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top    = EBookStoreSpacing.Small,
                            // Extra bottom padding so the last item is not
                            // hidden behind the summary panel (~180dp tall).
                            bottom = 200.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(EBookStoreSpacing.Small),
                    ) {
                        items(items = uiState.items, key = { it.book.id }) { item ->
                            CartItemRow(
                                item               = item,
                                onIncrease         = { onIncreaseQuantity(item.book.id) },
                                onDecrease         = { onDecreaseQuantity(item.book.id) },
                                onRemove           = { onRemoveItem(item.book.id) },
                                onTitleClick       = { onNavigateToBook(item.book.id) },
                                modifier           = Modifier.padding(
                                    horizontal = EBookStoreSpacing.Medium,
                                ),
                            )
                        }
                    }

                    // Sticky bottom summary panel
                    CartSummaryPanel(
                        uiState              = uiState,
                        onNavigateToCheckout = onNavigateToCheckout,
                        modifier             = Modifier.align(Alignment.BottomCenter),
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Cart item row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    onTitleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val book = item.book

    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = MaterialTheme.shapes.small,
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(EBookStoreSpacing.Small),
            verticalAlignment = Alignment.Top,
        ) {
            // Cover with optional sale badge
            Box {
                val context = LocalContext.current
                AsyncImage(
                    model              = ImageRequest.Builder(context)
                        .data(book.coverUrl)
                        .crossfade(300)
                        .build(),
                    contentDescription = book.title,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .width(72.dp)
                        .aspectRatio(EBookStoreSpacing.BookCoverAspectRatio)
                        .clip(MaterialTheme.shapes.extraSmall),
                )
                if (book.originalPrice != null) {
                    SaleBadge(modifier = Modifier.padding(EBookStoreSpacing.XXSmall))
                }
            }

            Spacer(Modifier.width(EBookStoreSpacing.Medium))

            // Metadata + stepper column
            Column(modifier = Modifier.weight(1f)) {
                // Title (tappable → book detail)
                TextButton(
                    onClick        = onTitleClick,
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(
                        text     = book.title,
                        style    = MaterialTheme.typography.titleSmall,
                        color    = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Text(
                    text  = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(EBookStoreSpacing.XSmall))

                // Price row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(EBookStoreSpacing.XSmall),
                ) {
                    Text(
                        text       = "₹${book.price.toInt()}",
                        style      = MaterialTheme.typography.titleSmall,
                        color      = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text  = "× ${item.quantity}  =  ₹${item.lineTotal.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(Modifier.height(EBookStoreSpacing.Small))

                // Stepper + delete
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    QuantityStepper(
                        quantity   = item.quantity,
                        onIncrease = onIncrease,
                        onDecrease = onDecrease,
                    )

                    IconButton(
                        onClick  = onRemove,
                        modifier = Modifier.size(36.dp),
                    ) {
                        Icon(
                            imageVector        = Icons.Filled.DeleteOutline,
                            contentDescription = "Remove ${book.title} from cart",
                            tint               = MaterialTheme.colorScheme.error,
                            modifier           = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Cart summary panel (sticky bottom)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CartSummaryPanel(
    uiState: CartUiState,
    onNavigateToCheckout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier      = modifier.fillMaxWidth(),
        color         = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(EBookStoreSpacing.Medium),
        ) {
            // Subtotal
            SummaryRow(
                label = "Subtotal (${uiState.itemCount} item${if (uiState.itemCount != 1) "s" else ""})",
                value = "₹${uiState.subtotal.toInt()}",
            )

            Spacer(Modifier.height(EBookStoreSpacing.XSmall))

            // Shipping
            SummaryRow(
                label = "Shipping",
                value = if (uiState.shipping == 0.0) "FREE" else "₹${uiState.shipping.toInt()}",
                valueColor = if (uiState.shipping == 0.0)
                    MaterialTheme.colorScheme.secondary
                else
                    MaterialTheme.colorScheme.onSurface,
            )

            if (uiState.shipping > 0) {
                Text(
                    text  = "Add ₹${(500 - uiState.subtotal.toInt())} more for free shipping",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            HorizontalDivider(
                modifier  = Modifier.padding(vertical = EBookStoreSpacing.Small),
                color     = MaterialTheme.colorScheme.outlineVariant,
                thickness = 0.5.dp,
            )

            // Grand total
            SummaryRow(
                label      = "Total",
                value      = "₹${uiState.grandTotal.toInt()}",
                isBold     = true,
                valueColor = MaterialTheme.colorScheme.primary,
            )

            Spacer(Modifier.height(EBookStoreSpacing.Medium))

            // Checkout CTA
            Button(
                onClick  = onNavigateToCheckout,
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor   = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Icon(
                    imageVector        = Icons.Outlined.ShoppingCartCheckout,
                    contentDescription = null,
                    modifier           = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(EBookStoreSpacing.XSmall))
                Text(
                    text  = "Proceed to Checkout",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isBold: Boolean = false,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(
            text       = label,
            style      = if (isBold) MaterialTheme.typography.titleSmall
                         else MaterialTheme.typography.bodyMedium,
            color      = MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isBold) FontWeight.Bold else null,
        )
        Text(
            text       = value,
            style      = if (isBold) MaterialTheme.typography.titleMedium
                         else MaterialTheme.typography.bodyMedium,
            color      = valueColor,
            fontWeight = if (isBold) FontWeight.Bold else null,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Empty state
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmptyCartState(modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier
            .fillMaxSize()
            .padding(EBookStoreSpacing.XLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector        = Icons.Outlined.ShoppingCartCheckout,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier           = Modifier.size(72.dp),
        )
        Spacer(Modifier.height(EBookStoreSpacing.Medium))
        Text(
            text      = "Your cart is empty",
            style     = MaterialTheme.typography.titleMedium,
            color     = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(EBookStoreSpacing.Small))
        Text(
            text      = "Browse our catalogue and add books you love.",
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews — stateless layer only (no Hilt)
// ─────────────────────────────────────────────────────────────────────────────

private val previewItems = MockData.books.take(3).mapIndexed { idx, book ->
    CartItem(book = book, quantity = idx + 1)
}

@Preview(name = "Cart — Light (with items)", showBackground = true)
@Composable
private fun CartScreenLightPreview() {
    EBookStoreTheme {
        CartScreenContent(
            uiState              = CartUiState(items = previewItems),
            onIncreaseQuantity   = {},
            onDecreaseQuantity   = {},
            onRemoveItem         = {},
            onClearCart          = {},
            onNavigateToBook     = {},
            onNavigateToCheckout = {},
        )
    }
}

@Preview(name = "Cart — Dark (with items)", showBackground = true)
@Composable
private fun CartScreenDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        CartScreenContent(
            uiState              = CartUiState(items = previewItems),
            onIncreaseQuantity   = {},
            onDecreaseQuantity   = {},
            onRemoveItem         = {},
            onClearCart          = {},
            onNavigateToBook     = {},
            onNavigateToCheckout = {},
        )
    }
}

@Preview(name = "Cart — Empty state", showBackground = true)
@Composable
private fun CartScreenEmptyPreview() {
    EBookStoreTheme {
        CartScreenContent(
            uiState              = CartUiState(),
            onIncreaseQuantity   = {},
            onDecreaseQuantity   = {},
            onRemoveItem         = {},
            onClearCart          = {},
            onNavigateToBook     = {},
            onNavigateToCheckout = {},
        )
    }
}
