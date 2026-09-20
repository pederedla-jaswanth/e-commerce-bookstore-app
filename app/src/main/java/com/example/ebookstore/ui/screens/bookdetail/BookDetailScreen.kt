package com.example.ebookstore.ui.screens.bookdetail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.ebookstore.data.mock.MockData
import com.example.ebookstore.domain.model.Author
import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.domain.model.Review
import com.example.ebookstore.ui.cart.CartViewModel
import com.example.ebookstore.ui.components.BookCard
import com.example.ebookstore.ui.components.CardLayoutMode
import com.example.ebookstore.ui.components.RecommendationRow
import com.example.ebookstore.ui.components.SaleBadge
import com.example.ebookstore.ui.components.SectionHeader
import com.example.ebookstore.ui.components.StarRating
import com.example.ebookstore.ui.screens.recommendations.RecommendationsViewModel
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme
import com.example.ebookstore.ui.theme.ThemeMode
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// Entry point (Hilt-wired)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Book Detail screen — full-screen detail destination (Phase 8).
 *
 * Layout (scrollable [LazyColumn]):
 * 1. [TopAppBar] with back arrow and title
 * 2. Hero section — cover image + title, author, price, rating, format chips,
 *    "Add to Cart" / "Add to Wishlist" buttons
 * 3. Book description (expandable)
 * 4. Book metadata row (pages · format · publisher · published)
 * 5. About the Author card
 * 6. Customer reviews (up to 3 shown)
 * 7. Related Reads — [LazyRow] of vertical [BookCard]s
 *
 * Add-to-Cart follows D5:
 * - Calls [CartViewModel.addToCart], shows [Snackbar] with "Open Cart" action.
 * - Button changes to "In Cart ✓" (disabled) once added.
 *
 * @param onNavigateBack    Called when the back arrow is tapped.
 * @param onNavigateToCart  Called when "Open Cart" snackbar action is tapped.
 * @param onNavigateToBook  Called with bookId when a related book card is tapped.
 * @param detailViewModel   Hilt-injected; owns book/author/reviews/related state.
 * @param cartViewModel     Shared NavGraph-level ViewModel.
 * @param modifier          Applied to root Surface.
 */
@Composable
fun BookDetailScreen(
    cartViewModel: CartViewModel,
    recommendationsViewModel: RecommendationsViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToBook: (String) -> Unit = {},
    isLoggedIn: Boolean = false,
    onLoginRequired: () -> Unit = {},
    detailViewModel: BookDetailViewModel = hiltViewModel(),
) {
    val uiState          by detailViewModel.uiState.collectAsStateWithLifecycle()
    val cartState        by cartViewModel.uiState.collectAsStateWithLifecycle()
    val recommendState   by recommendationsViewModel.uiState.collectAsStateWithLifecycle()

    // Notify recommendations engine when book is loaded
    val book = uiState.book
    androidx.compose.runtime.LaunchedEffect(book?.id) {
        if (book != null) recommendationsViewModel.onBookViewed(book)
    }

    BookDetailScreenContent(
        uiState               = uiState,
        cartItemCount         = cartState.itemCount,
        recommendedBooks      = recommendState.categorySuggestions,
        onNavigateBack        = onNavigateBack,
        onNavigateToCart      = onNavigateToCart,
        onNavigateToBook      = onNavigateToBook,
        onWishlistToggle      = {
            if (isLoggedIn) detailViewModel.onWishlistToggle()
            else onLoginRequired()
        },
        onAddToCart           = { book ->
            cartViewModel.addToCart(book)
            detailViewModel.onAddedToCart()
        },
        modifier              = modifier,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Stateless content layer (preview-friendly)
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreenContent(
    uiState: BookDetailUiState,
    cartItemCount: Int,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToBook: (String) -> Unit,
    onWishlistToggle: () -> Unit,
    onAddToCart: (Book) -> Unit,
    modifier: Modifier = Modifier,
    recommendedBooks: List<Book> = emptyList(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope    = rememberCoroutineScope()

    fun showCartSnackbar(book: Book) {
        coroutineScope.launch {
            val result = snackbarHostState.showSnackbar(
                message     = "\"${book.title}\" added to cart",
                actionLabel = "Open Cart",
                duration    = SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) onNavigateToCart()
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.background,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Top App Bar ───────────────────────────────────────────────
                TopAppBar(
                    title = {
                        Text(
                            text     = uiState.book?.title ?: "Book Detail",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style    = MaterialTheme.typography.titleMedium,
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
                        containerColor    = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )

                // ── Body ──────────────────────────────────────────────────────
                when {
                    uiState.isLoading -> LoadingState()
                    uiState.error != null -> ErrorState(
                        message       = uiState.error,
                        onNavigateBack = onNavigateBack,
                    )
                    uiState.book != null -> DetailBody(
                        uiState          = uiState,
                        book             = uiState.book,
                        recommendedBooks = recommendedBooks,
                        onNavigateToBook = onNavigateToBook,
                        onWishlistToggle = onWishlistToggle,
                        onAddToCart      = { book ->
                            onAddToCart(book)
                            showCartSnackbar(book)
                        },
                    )
                }
            }

            // Snackbar overlay
            SnackbarHost(
                hostState = snackbarHostState,
                modifier  = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = EBookStoreSpacing.Medium),
                snackbar  = { data ->
                    Snackbar(
                        snackbarData   = data,
                        containerColor = MaterialTheme.colorScheme.inverseSurface,
                        contentColor   = MaterialTheme.colorScheme.inverseOnSurface,
                        actionColor    = MaterialTheme.colorScheme.primary,
                    )
                },
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main detail body (scrollable)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DetailBody(
    uiState: BookDetailUiState,
    book: Book,
    recommendedBooks: List<Book>,
    onNavigateToBook: (String) -> Unit,
    onWishlistToggle: () -> Unit,
    onAddToCart: (Book) -> Unit,
) {
    LazyColumn(
        modifier       = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = EBookStoreSpacing.XLarge),
    ) {

        // ── 1. Hero section ───────────────────────────────────────────────────
        item(key = "hero") {
            HeroSection(
                book             = book,
                isInCart         = uiState.isInCart,
                isInWishlist     = uiState.isInWishlist,
                onAddToCart      = onAddToCart,
                onWishlistToggle = onWishlistToggle,
            )
        }

        // ── 2. Description ────────────────────────────────────────────────────
        if (book.description.isNotBlank()) {
            item(key = "desc_header") {
                SectionDivider()
                SectionHeader(
                    title = "Description",
                    modifier = Modifier.padding(top = EBookStoreSpacing.Small),
                )
            }
            item(key = "desc_body") {
                ExpandableDescription(
                    text     = book.description,
                    modifier = Modifier.padding(horizontal = EBookStoreSpacing.Medium),
                )
            }
        }

        // ── 3. Book metadata ──────────────────────────────────────────────────
        item(key = "metadata") {
            SectionDivider()
            BookMetadataRow(book = book)
        }

        // ── 4. About the Author ───────────────────────────────────────────────
        uiState.author?.let { author ->
            item(key = "author_header") {
                SectionDivider()
                SectionHeader(title = "About the Author")
            }
            item(key = "author_card") {
                AboutAuthorCard(
                    author   = author,
                    modifier = Modifier.padding(horizontal = EBookStoreSpacing.Medium),
                )
            }
        }

        // ── 5. Reviews ────────────────────────────────────────────────────────
        if (uiState.reviews.isNotEmpty()) {
            item(key = "reviews_header") {
                SectionDivider()
                SectionHeader(title = "Customer Reviews")
            }
            items(items = uiState.reviews.take(3), key = { it.id }) { review ->
                ReviewCard(
                    review   = review,
                    modifier = Modifier.padding(
                        horizontal = EBookStoreSpacing.Medium,
                        vertical   = EBookStoreSpacing.XSmall,
                    ),
                )
            }
        }

        // ── 6. Related Reads ──────────────────────────────────────────────────
        if (uiState.relatedBooks.isNotEmpty()) {
            item(key = "related_header") {
                SectionDivider()
                SectionHeader(title = "Related Reads")
            }
            item(key = "related_row") {
                LazyRow(
                    contentPadding        = PaddingValues(horizontal = EBookStoreSpacing.Medium),
                    horizontalArrangement = Arrangement.spacedBy(EBookStoreSpacing.Small),
                ) {
                    items(items = uiState.relatedBooks, key = { it.id }) { relBook ->
                        BookCard(
                            book        = relBook,
                            onCardClick = onNavigateToBook,
                            layoutMode  = CardLayoutMode.VERTICAL,
                            modifier    = Modifier.width(150.dp),
                        )
                    }
                }
                Spacer(Modifier.height(EBookStoreSpacing.Small))
            }
        }

        // ── 7. Recommended for You (category-based) ───────────────────────────
        if (recommendedBooks.isNotEmpty()) {
            item(key = "rec_header") {
                SectionDivider()
            }
            item(key = "rec_row") {
                RecommendationRow(
                    title       = "Recommended for You",
                    books       = recommendedBooks,
                    onBookClick = onNavigateToBook,
                    modifier    = Modifier.padding(bottom = EBookStoreSpacing.Small),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Hero section
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HeroSection(
    book: Book,
    isInCart: Boolean,
    isInWishlist: Boolean,
    onAddToCart: (Book) -> Unit,
    onWishlistToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(EBookStoreSpacing.Medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Cover image
        val context = LocalContext.current
        Box(contentAlignment = Alignment.TopStart) {
            AsyncImage(
                model              = ImageRequest.Builder(context)
                    .data(book.coverUrl)
                    .crossfade(300)
                    .build(),
                contentDescription = book.title,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .width(160.dp)
                    .aspectRatio(EBookStoreSpacing.BookCoverAspectRatio)
                    .clip(MaterialTheme.shapes.small),
            )
            if (book.originalPrice != null) {
                SaleBadge(
                    modifier = Modifier.padding(EBookStoreSpacing.XSmall),
                )
            }
        }

        Spacer(Modifier.height(EBookStoreSpacing.Medium))

        // Title
        Text(
            text      = book.title,
            style     = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color     = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(EBookStoreSpacing.XSmall))

        // Author
        Text(
            text  = "by ${book.author}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(Modifier.height(EBookStoreSpacing.Small))

        // Rating row
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EBookStoreSpacing.XSmall),
        ) {
            StarRating(rating = book.rating, starSize = 18.dp)
            Text(
                text  = "${book.rating}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text  = "(${book.ratingCount} reviews)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(EBookStoreSpacing.Small))

        // Price row
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EBookStoreSpacing.Small),
        ) {
            Text(
                text       = "₹${book.price.toInt()}",
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.primary,
            )
            if (book.originalPrice != null) {
                Text(
                    text           = "₹${book.originalPrice.toInt()}",
                    style          = MaterialTheme.typography.bodyLarge,
                    color          = MaterialTheme.colorScheme.onSurfaceVariant,
                    textDecoration = TextDecoration.LineThrough,
                )
            }
        }

        Spacer(Modifier.height(EBookStoreSpacing.XSmall))

        // Format chip
        Surface(
            color  = MaterialTheme.colorScheme.surfaceVariant,
            shape  = MaterialTheme.shapes.extraLarge,
        ) {
            Text(
                text     = book.format,
                style    = MaterialTheme.typography.labelMedium,
                color    = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(
                    horizontal = EBookStoreSpacing.Medium,
                    vertical   = EBookStoreSpacing.XXSmall,
                ),
            )
        }

        Spacer(Modifier.height(EBookStoreSpacing.Medium))

        // Delivery info
        Text(
            text  = "Delivery by ${book.deliveryDate}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(EBookStoreSpacing.Medium))

        // Add to Cart button (D5)
        Button(
            onClick  = { if (!isInCart) onAddToCart(book) },
            enabled  = !isInCart,
            modifier = Modifier.fillMaxWidth(),
            colors   = ButtonDefaults.buttonColors(
                containerColor         = MaterialTheme.colorScheme.primary,
                contentColor           = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor   = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        ) {
            Icon(
                imageVector        = Icons.Filled.ShoppingCart,
                contentDescription = null,
                modifier           = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(EBookStoreSpacing.XSmall))
            Text(
                text  = if (isInCart) "In Cart ✓" else "Add to Cart",
                style = MaterialTheme.typography.labelLarge,
            )
        }

        Spacer(Modifier.height(EBookStoreSpacing.Small))

        // Add to Wishlist button
        OutlinedButton(
            onClick  = onWishlistToggle,
            modifier = Modifier.fillMaxWidth(),
            colors   = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.secondary,
            ),
            border   = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary,
            ),
        ) {
            Icon(
                imageVector        = if (isInWishlist) Icons.Filled.Favorite
                                     else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                modifier           = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(EBookStoreSpacing.XSmall))
            Text(
                text  = if (isInWishlist) "Saved to Wishlist" else "Add to Wishlist",
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Expandable description
// ─────────────────────────────────────────────────────────────────────────────

private const val DESCRIPTION_COLLAPSED_LINES = 3

@Composable
private fun ExpandableDescription(
    text: String,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text     = text,
            style    = MaterialTheme.typography.bodyMedium,
            color    = MaterialTheme.colorScheme.onBackground,
            maxLines = if (expanded) Int.MAX_VALUE else DESCRIPTION_COLLAPSED_LINES,
            overflow = if (expanded) TextOverflow.Visible else TextOverflow.Ellipsis,
        )
        TextButton(
            onClick = { expanded = !expanded },
            contentPadding = PaddingValues(0.dp),
        ) {
            Text(
                text  = if (expanded) "Show less" else "Read more",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Book metadata row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BookMetadataRow(
    book: Book,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier              = modifier
            .fillMaxWidth()
            .padding(horizontal = EBookStoreSpacing.Medium, vertical = EBookStoreSpacing.Small),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        if (book.pageCount > 0) MetadataItem(label = "Pages",     value = "${book.pageCount}")
        MetadataItem(label = "Format",    value = book.format)
        if (book.publisher.isNotBlank()) MetadataItem(label = "Publisher", value = book.publisher)
        if (book.publishedDate.isNotBlank()) MetadataItem(label = "Published", value = book.publishedDate)
    }
}

@Composable
private fun MetadataItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EBookStoreSpacing.XXSmall),
    ) {
        Text(
            text  = value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// About the Author card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AboutAuthorCard(
    author: Author,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = MaterialTheme.shapes.medium,
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier.padding(EBookStoreSpacing.Medium),
            verticalAlignment = Alignment.Top,
        ) {
            // Circular author photo
            val authorContext = LocalContext.current
            AsyncImage(
                model              = ImageRequest.Builder(authorContext)
                    .data(author.photoUrl)
                    .crossfade(300)
                    .build(),
                contentDescription = author.name,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline),
            )
            Spacer(Modifier.width(EBookStoreSpacing.Medium))
            Column {
                Text(
                    text       = author.name,
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(EBookStoreSpacing.XXSmall))
                Text(
                    text  = author.bio,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Review card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ReviewCard(
    review: Review,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = MaterialTheme.shapes.small,
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(EBookStoreSpacing.Medium)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text(
                    text       = review.reviewerName,
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text  = review.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(EBookStoreSpacing.XXSmall))
            StarRating(rating = review.rating, starSize = 14.dp)
            Spacer(Modifier.height(EBookStoreSpacing.XSmall))
            Text(
                text  = review.body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared utilities
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionDivider() {
    HorizontalDivider(
        modifier  = Modifier.padding(vertical = EBookStoreSpacing.Small),
        color     = MaterialTheme.colorScheme.outlineVariant,
        thickness = 0.5.dp,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Loading & Error states
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LoadingState() {
    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color    = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp),
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onNavigateBack: () -> Unit,
) {
    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(EBookStoreSpacing.Large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text  = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(EBookStoreSpacing.Medium))
        TextButton(onClick = onNavigateBack) {
            Text(
                text  = "Go back",
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews — stateless layer only (no Hilt)
// ─────────────────────────────────────────────────────────────────────────────

private val previewBook    = MockData.books.first()
private val previewAuthor  = MockData.authorById("a1")
private val previewReviews = MockData.reviewsForBook("1")
private val previewRelated = MockData.relatedBooks(previewBook)

private val previewDetailState = BookDetailUiState(
    book         = previewBook,
    author       = previewAuthor,
    reviews      = previewReviews,
    relatedBooks = previewRelated,
    isInCart     = false,
    isInWishlist = false,
    isLoading    = false,
)

@Preview(name = "Book Detail — Light", showBackground = true)
@Composable
private fun BookDetailLightPreview() {
    EBookStoreTheme {
        BookDetailScreenContent(
            uiState          = previewDetailState,
            cartItemCount    = 0,
            onNavigateBack   = {},
            onNavigateToCart = {},
            onNavigateToBook = {},
            onWishlistToggle = {},
            onAddToCart      = {},
        )
    }
}

@Preview(name = "Book Detail — Dark", showBackground = true)
@Composable
private fun BookDetailDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        BookDetailScreenContent(
            uiState          = previewDetailState,
            cartItemCount    = 1,
            onNavigateBack   = {},
            onNavigateToCart = {},
            onNavigateToBook = {},
            onWishlistToggle = {},
            onAddToCart      = {},
        )
    }
}

@Preview(name = "Book Detail — In Cart + Wishlist", showBackground = true)
@Composable
private fun BookDetailInCartPreview() {
    EBookStoreTheme {
        BookDetailScreenContent(
            uiState          = previewDetailState.copy(isInCart = true, isInWishlist = true),
            cartItemCount    = 1,
            onNavigateBack   = {},
            onNavigateToCart = {},
            onNavigateToBook = {},
            onWishlistToggle = {},
            onAddToCart      = {},
        )
    }
}

@Preview(name = "Book Detail — Loading", showBackground = true)
@Composable
private fun BookDetailLoadingPreview() {
    EBookStoreTheme {
        BookDetailScreenContent(
            uiState          = BookDetailUiState(isLoading = true),
            cartItemCount    = 0,
            onNavigateBack   = {},
            onNavigateToCart = {},
            onNavigateToBook = {},
            onWishlistToggle = {},
            onAddToCart      = {},
        )
    }
}

@Preview(name = "Book Detail — Error", showBackground = true)
@Composable
private fun BookDetailErrorPreview() {
    EBookStoreTheme {
        BookDetailScreenContent(
            uiState          = BookDetailUiState(isLoading = false, error = "Book not found."),
            cartItemCount    = 0,
            onNavigateBack   = {},
            onNavigateToCart = {},
            onNavigateToBook = {},
            onWishlistToggle = {},
            onAddToCart      = {},
        )
    }
}
