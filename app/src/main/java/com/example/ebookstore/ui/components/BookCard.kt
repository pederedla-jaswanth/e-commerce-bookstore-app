package com.example.ebookstore.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.ebookstore.data.mock.MockData
import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme

// ─────────────────────────────────────────────────────────────────────────────
// Layout mode
// ─────────────────────────────────────────────────────────────────────────────

/** Controls whether BookCard renders as a vertical grid card or horizontal list card. */
enum class CardLayoutMode { VERTICAL, HORIZONTAL }

// ─────────────────────────────────────────────────────────────────────────────
// BookCard
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Reusable book card used throughout the app.
 *
 * **VERTICAL** — cover on top (2:3 ratio), title + author below. Used in
 * horizontal lazy rows on the Home screen.
 *
 * **HORIZONTAL** — cover on the left (~80×120 dp), all metadata stacked on
 * the right. Used in Catalogue list and Related Books panel.
 *
 * All colors come from [MaterialTheme.colorScheme]; none are hardcoded.
 *
 * @param book          The book to display.
 * @param onCardClick   Called when the user taps the card body.
 * @param onWishlistClick Called when the wishlist (heart) icon is tapped.
 * @param layoutMode    [CardLayoutMode.VERTICAL] or [CardLayoutMode.HORIZONTAL].
 * @param modifier      Applied to the root [Card].
 */
@Composable
fun BookCard(
    book: Book,
    onCardClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onWishlistClick: (String) -> Unit = {},
    layoutMode: CardLayoutMode = CardLayoutMode.VERTICAL,
) {
    Card(
        modifier = modifier.clickable { onCardClick(book.id) },
        shape    = MaterialTheme.shapes.small,
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        when (layoutMode) {
            CardLayoutMode.VERTICAL   -> VerticalCardContent(book, onWishlistClick)
            CardLayoutMode.HORIZONTAL -> HorizontalCardContent(book, onWishlistClick)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vertical layout
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun VerticalCardContent(
    book: Book,
    onWishlistClick: (String) -> Unit,
) {
    Column {
        // Cover image with sale badge overlay
        Box {
            val context = LocalContext.current
            AsyncImage(
                model             = ImageRequest.Builder(context)
                    .data(book.coverUrl)
                    .crossfade(300)
                    .build(),
                contentDescription = book.title,
                contentScale      = ContentScale.Crop,
                modifier          = Modifier
                    .fillMaxWidth()
                    .aspectRatio(EBookStoreSpacing.BookCoverAspectRatio)
                    .clip(MaterialTheme.shapes.small),
            )
            if (book.originalPrice != null) {
                SaleBadge(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(EBookStoreSpacing.XSmall),
                )
            }
        }

        // Metadata below the cover
        Column(modifier = Modifier.padding(EBookStoreSpacing.Small)) {
            Text(
                text     = book.title,
                style    = MaterialTheme.typography.titleMedium,
                color    = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(EBookStoreSpacing.XXSmall))
            Text(
                text  = book.author,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(EBookStoreSpacing.XXSmall))
            StarRating(rating = book.rating, starSize = 12.dp)
            Spacer(Modifier.height(EBookStoreSpacing.XXSmall))
            Text(
                text  = "₹${book.price.toInt()}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Horizontal layout
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HorizontalCardContent(
    book: Book,
    onWishlistClick: (String) -> Unit,
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(EBookStoreSpacing.Small),
        verticalAlignment = Alignment.Top,
    ) {
        // Cover image
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
                    .width(80.dp)
                    .aspectRatio(EBookStoreSpacing.BookCoverAspectRatio)
                    .clip(MaterialTheme.shapes.small),
            )
            if (book.originalPrice != null) {
                SaleBadge(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(EBookStoreSpacing.XXSmall),
                )
            }
        }

        Spacer(Modifier.width(EBookStoreSpacing.Small))

        // Metadata column
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text     = book.title,
                style    = MaterialTheme.typography.titleMedium,
                color    = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(EBookStoreSpacing.XXSmall))
            Text(
                text  = book.author,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(EBookStoreSpacing.XXSmall))
            Text(
                text  = book.format,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
            )
            Spacer(Modifier.height(EBookStoreSpacing.XXSmall))
            Row(
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(EBookStoreSpacing.XSmall),
            ) {
                StarRating(rating = book.rating, starSize = 12.dp)
                Text(
                    text  = "(${book.ratingCount})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(EBookStoreSpacing.XXSmall))
            Text(
                text  = "₹${book.price.toInt()}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(EBookStoreSpacing.XXSmall))
            Text(
                text  = "Delivery by ${book.deliveryDate}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Wishlist icon
        IconButton(onClick = { onWishlistClick(book.id) }) {
            Icon(
                imageVector        = Icons.Outlined.FavoriteBorder,
                contentDescription = "Add ${book.title} to wishlist",
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "BookCard Vertical — Light")
@Composable
private fun BookCardVerticalPreview() {
    EBookStoreTheme {
        BookCard(
            book        = MockData.books.first(),
            onCardClick = {},
            modifier    = Modifier.width(160.dp),
        )
    }
}

@Preview(name = "BookCard Horizontal — Dark")
@Composable
private fun BookCardHorizontalDarkPreview() {
    EBookStoreTheme(themeMode = com.example.ebookstore.ui.theme.ThemeMode.DARK) {
        BookCard(
            book        = MockData.books[2],
            onCardClick = {},
            layoutMode  = CardLayoutMode.HORIZONTAL,
        )
    }
}
