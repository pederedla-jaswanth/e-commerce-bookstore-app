package com.example.ebookstore.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ebookstore.data.mock.MockData
import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme
import com.example.ebookstore.ui.theme.ThemeMode

/**
 * Reusable horizontal scrollable row of [BookCard]s with a [SectionHeader] title.
 *
 * Used by BookDetailScreen (Related Reads, Recommended for You) and HomeScreen
 * (Category Picks, Trending).  All callers pass a [books] list — no ViewModel
 * coupling inside this component.
 *
 * @param title          Section heading text.
 * @param books          The ordered list of books to display.
 * @param onBookClick    Called with the bookId when a card is tapped.
 * @param onSeeAllClick  Optional callback for a "See all" action in the header.
 * @param modifier       Applied to the outer [Column].
 */
@Composable
fun RecommendationRow(
    title: String,
    books: List<Book>,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSeeAllClick: (() -> Unit)? = null,
) {
    if (books.isEmpty()) return

    Column(modifier = modifier) {
        SectionHeader(
            title        = title,
            onSeeAllClick = onSeeAllClick,
        )
        Spacer(Modifier.height(EBookStoreSpacing.XSmall))
        LazyRow(
            contentPadding        = PaddingValues(horizontal = EBookStoreSpacing.Medium),
            horizontalArrangement = Arrangement.spacedBy(EBookStoreSpacing.Small),
        ) {
            items(items = books, key = { it.id }) { book ->
                BookCard(
                    book        = book,
                    onCardClick = onBookClick,
                    layoutMode  = CardLayoutMode.VERTICAL,
                    modifier    = Modifier.width(150.dp),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "RecommendationRow — Light")
@Composable
private fun RecommendationRowLightPreview() {
    EBookStoreTheme {
        RecommendationRow(
            title       = "You May Also Like",
            books       = MockData.books.take(4),
            onBookClick = {},
        )
    }
}

@Preview(name = "RecommendationRow — Dark")
@Composable
private fun RecommendationRowDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        RecommendationRow(
            title        = "Trending Now",
            books        = MockData.books,
            onBookClick  = {},
            onSeeAllClick = {},
        )
    }
}
