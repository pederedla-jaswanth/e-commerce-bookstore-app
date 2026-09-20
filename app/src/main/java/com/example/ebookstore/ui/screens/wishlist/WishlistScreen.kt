package com.example.ebookstore.ui.screens.wishlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ebookstore.data.mock.MockData
import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.ui.cart.CartViewModel
import com.example.ebookstore.ui.components.BookCard
import com.example.ebookstore.ui.components.CardLayoutMode
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme
import com.example.ebookstore.ui.theme.ThemeMode
import com.example.ebookstore.ui.wishlist.WishlistUiState
import com.example.ebookstore.ui.wishlist.WishlistViewModel

// ─────────────────────────────────────────────────────────────────────────────
// Entry point (Hilt-wired)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Wishlist screen — full-screen detail destination.
 *
 * Layout:
 * - Top app bar with back arrow + item count
 * - [LazyColumn] of [BookCard] (HORIZONTAL mode) with a "Move to Cart" action
 *   and a remove (delete) icon button on each row
 * - Empty state when wishlist is empty
 *
 * @param onNavigateBack    Called when back arrow is tapped.
 * @param onNavigateToBook  Called with bookId when a card is tapped.
 * @param wishlistViewModel Shared NavGraph-level ViewModel.
 * @param cartViewModel     Shared NavGraph-level ViewModel.
 * @param modifier          Applied to root Surface.
 */
@Composable
fun WishlistScreen(
    wishlistViewModel: WishlistViewModel,
    cartViewModel: CartViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNavigateToBook: (String) -> Unit = {},
) {
    val uiState by wishlistViewModel.uiState.collectAsStateWithLifecycle()

    WishlistScreenContent(
        uiState          = uiState,
        onNavigateBack   = onNavigateBack,
        onNavigateToBook = onNavigateToBook,
        onRemove         = wishlistViewModel::removeFromWishlist,
        onMoveToCart     = { book ->
            cartViewModel.addToCart(book)
            wishlistViewModel.removeFromWishlist(book.id)
        },
        modifier         = modifier,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Stateless content layer (preview-friendly)
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreenContent(
    uiState: WishlistUiState,
    onNavigateBack: () -> Unit,
    onNavigateToBook: (String) -> Unit,
    onRemove: (String) -> Unit,
    onMoveToCart: (Book) -> Unit,
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
                        text  = if (uiState.isEmpty) "Wishlist"
                                else "Wishlist (${uiState.itemCount})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint               = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )

            // ── Body ──────────────────────────────────────────────────────────
            if (uiState.isEmpty) {
                EmptyWishlistState()
            } else {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(
                        horizontal = EBookStoreSpacing.Medium,
                        vertical   = EBookStoreSpacing.Small,
                    ),
                    verticalArrangement = Arrangement.spacedBy(EBookStoreSpacing.Small),
                ) {
                    items(items = uiState.items, key = { it.id }) { book ->
                        WishlistItemRow(
                            book         = book,
                            onCardClick  = onNavigateToBook,
                            onRemove     = { onRemove(book.id) },
                            onMoveToCart = { onMoveToCart(book) },
                            modifier     = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Wishlist item row — BookCard + action icons
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun WishlistItemRow(
    book: Book,
    onCardClick: (String) -> Unit,
    onRemove: () -> Unit,
    onMoveToCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Wrap BookCard(HORIZONTAL) with action buttons in a Column so the
    // "Move to Cart" + "Remove" buttons sit flush below the card.
    Column(modifier = modifier) {
        BookCard(
            book        = book,
            onCardClick = onCardClick,
            layoutMode  = CardLayoutMode.HORIZONTAL,
            modifier    = Modifier.fillMaxWidth(),
        )

        // Action row
        androidx.compose.foundation.layout.Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = EBookStoreSpacing.Small),
            horizontalArrangement = Arrangement.spacedBy(EBookStoreSpacing.XSmall),
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            // Move to Cart
            androidx.compose.material3.OutlinedButton(
                onClick  = onMoveToCart,
                modifier = Modifier.weight(1f),
                colors   = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text(
                    text  = "Move to Cart",
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            // Remove from wishlist
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector        = Icons.Filled.Delete,
                    contentDescription = "Remove ${book.title} from wishlist",
                    tint               = MaterialTheme.colorScheme.error,
                    modifier           = Modifier.size(20.dp),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Empty state
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmptyWishlistState(modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier
            .fillMaxSize()
            .padding(EBookStoreSpacing.XLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector        = Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier           = Modifier.size(72.dp),
        )
        Spacer(Modifier.height(EBookStoreSpacing.Medium))
        Text(
            text      = "Your wishlist is empty",
            style     = MaterialTheme.typography.titleMedium,
            color     = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(EBookStoreSpacing.Small))
        Text(
            text      = "Save books you love and come back to them anytime.",
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews — stateless layer only (no Hilt)
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Wishlist — Light (with items)", showBackground = true)
@Composable
private fun WishlistScreenLightPreview() {
    EBookStoreTheme {
        WishlistScreenContent(
            uiState          = WishlistUiState(items = MockData.books.take(3)),
            onNavigateBack   = {},
            onNavigateToBook = {},
            onRemove         = {},
            onMoveToCart     = {},
        )
    }
}

@Preview(name = "Wishlist — Dark (with items)", showBackground = true)
@Composable
private fun WishlistScreenDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        WishlistScreenContent(
            uiState          = WishlistUiState(items = MockData.books.take(3)),
            onNavigateBack   = {},
            onNavigateToBook = {},
            onRemove         = {},
            onMoveToCart     = {},
        )
    }
}

@Preview(name = "Wishlist — Empty state", showBackground = true)
@Composable
private fun WishlistScreenEmptyPreview() {
    EBookStoreTheme {
        WishlistScreenContent(
            uiState          = WishlistUiState(),
            onNavigateBack   = {},
            onNavigateToBook = {},
            onRemove         = {},
            onMoveToCart     = {},
        )
    }
}
