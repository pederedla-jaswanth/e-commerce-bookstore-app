package com.example.ebookstore.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.ui.cart.CartViewModel
import com.example.ebookstore.ui.components.BookCard
import com.example.ebookstore.ui.components.CardLayoutMode
import com.example.ebookstore.ui.components.CategoryChipRow
import com.example.ebookstore.ui.components.EBookStoreTopAppBar
import com.example.ebookstore.ui.components.HeroBanner
import com.example.ebookstore.ui.components.RecommendationRow
import com.example.ebookstore.ui.components.SectionHeader
import com.example.ebookstore.ui.screens.recommendations.RecommendationsViewModel
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme
import com.example.ebookstore.ui.theme.ThemeMode
import kotlinx.coroutines.launch

/**
 * Home screen — fully assembled Phase 6 implementation.
 *
 * Delegates to [HomeScreenContent] after collecting state from both ViewModels.
 * ViewModels are injected by Hilt; the stateless overload is used for previews.
 *
 * @param onNavigateToCart      Called when "Open Cart" snackbar action is tapped.
 * @param onNavigateToBook      Called with bookId when a book card is tapped.
 * @param onNavigateToCatalogue Called when "See all" or search-submit is triggered.
 * @param homeViewModel         Hilt-injected; owns category/search/section state.
 * @param cartViewModel         Shared NavGraph-level ViewModel; owns cart item count.
 * @param modifier              Applied to the root Surface.
 */
@Composable
fun HomeScreen(
    cartViewModel: CartViewModel,
    recommendationsViewModel: RecommendationsViewModel,
    modifier: Modifier = Modifier,
    onNavigateToCart: () -> Unit = {},
    onNavigateToBook: (String) -> Unit = {},
    onNavigateToCatalogue: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState          by homeViewModel.uiState.collectAsStateWithLifecycle()
    val cartState        by cartViewModel.uiState.collectAsStateWithLifecycle()
    val recommendState   by recommendationsViewModel.uiState.collectAsStateWithLifecycle()

    // Refresh global recommendations when the home screen is entered
    androidx.compose.runtime.LaunchedEffect(Unit) {
        recommendationsViewModel.refreshGlobal()
    }

    HomeScreenContent(
        uiState               = uiState,
        cartItemCount         = cartState.itemCount,
        trendingBooks         = recommendState.trending,
        onSearchQueryChange   = homeViewModel::onSearchQueryChange,
        onSearchSubmit        = {
            homeViewModel.onSearchSubmit()
            onNavigateToCatalogue()
        },
        onCategorySelect      = homeViewModel::onCategorySelected,
        onNavigateToCart      = onNavigateToCart,
        onNavigateToBook      = onNavigateToBook,
        onNavigateToCatalogue = onNavigateToCatalogue,
        onNavigateToProfile   = onNavigateToProfile,
        onAddToCart           = cartViewModel::addToCart,
        modifier              = modifier,
    )
}

/**
 * Stateless rendering layer for the Home screen. Receives all data and
 * callbacks as plain parameters — no ViewModel references — so it can be
 * rendered in Compose Previews without Hilt.
 */
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    cartItemCount: Int,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onCategorySelect: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToBook: (String) -> Unit,
    onNavigateToCatalogue: () -> Unit,
    onAddToCart: (Book) -> Unit,
    modifier: Modifier = Modifier,
    trendingBooks: List<Book> = emptyList(),
    onNavigateToProfile: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope    = rememberCoroutineScope()

    // ── Helper: show "Added to cart" snackbar (D5) ────────────────────────────
    fun showAddedToCartSnackbar(book: Book) {
        coroutineScope.launch {
            onAddToCart(book)
            val result = snackbarHostState.showSnackbar(
                message     = "\"${book.title}\" added to cart",
                actionLabel = "Open Cart",
                duration    = SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) {
                onNavigateToCart()
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.background,
    ) {
        // SnackbarHost sits at root so it floats above the LazyColumn content
        androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {

                // ── 1. Top App Bar + inline search ────────────────────────────
                item(key = "top_bar") {
                    EBookStoreTopAppBar(
                        searchQuery         = uiState.searchQuery,
                        onSearchQueryChange = onSearchQueryChange,
                        onSearchSubmit      = onSearchSubmit,
                        cartItemCount       = cartItemCount,
                        onCartClick         = onNavigateToCart,
                        onAccountClick      = onNavigateToProfile,
                    )
                }

                // ── 2. Category chip row (D4) ─────────────────────────────────
                item(key = "category_chips") {
                    CategoryChipRow(
                        categories       = uiState.categories,
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelect = onCategorySelect,
                        modifier         = Modifier.padding(vertical = EBookStoreSpacing.XSmall),
                    )
                }

                // ── 3. Hero banner ────────────────────────────────────────────
                item(key = "hero_banner") {
                    HeroBanner(
                        banners       = uiState.banners,
                        onBannerClick = { /* catalogue navigation in Phase 7 */ },
                        modifier      = Modifier.padding(
                            horizontal = EBookStoreSpacing.Medium,
                            vertical   = EBookStoreSpacing.Small,
                        ),
                    )
                }

                // ── 4. New Releases ───────────────────────────────────────────
                item(key = "new_releases_header") {
                    Spacer(Modifier.height(EBookStoreSpacing.Small))
                    SectionHeader(
                        title         = "New Releases",
                        onSeeAllClick = onNavigateToCatalogue,
                    )
                }
                item(key = "new_releases_row") {
                    BookCardRow(
                        books       = uiState.newReleases,
                        onCardClick = onNavigateToBook,
                        onAddToCart = ::showAddedToCartSnackbar,
                    )
                }

                // ── 5. Recommendations ────────────────────────────────────────
                item(key = "recommendations_header") {
                    Spacer(Modifier.height(EBookStoreSpacing.Small))
                    SectionHeader(
                        title         = "Recommended For You",
                        onSeeAllClick = onNavigateToCatalogue,
                    )
                }
                item(key = "recommendations_row") {
                    BookCardRow(
                        books       = uiState.recommendations,
                        onCardClick = onNavigateToBook,
                        onAddToCart = ::showAddedToCartSnackbar,
                    )
                }

                // ── 6. Bestsellers ────────────────────────────────────────────
                item(key = "bestsellers_header") {
                    Spacer(Modifier.height(EBookStoreSpacing.Small))
                    SectionHeader(
                        title         = "Bestsellers",
                        onSeeAllClick = onNavigateToCatalogue,
                    )
                }
                item(key = "bestsellers_row") {
                    BookCardRow(
                        books       = uiState.bestsellers,
                        onCardClick = onNavigateToBook,
                        onAddToCart = ::showAddedToCartSnackbar,
                    )
                }

                // ── 7. Trending Now ───────────────────────────────────────────
                if (trendingBooks.isNotEmpty()) {
                    item(key = "trending_row") {
                        Spacer(Modifier.height(EBookStoreSpacing.Small))
                        RecommendationRow(
                            title         = "Trending Now",
                            books         = trendingBooks,
                            onBookClick   = onNavigateToBook,
                            onSeeAllClick = onNavigateToCatalogue,
                        )
                    }
                }

                // ── Loading / error states ────────────────────────────────────
                if (uiState.isLoading) {
                    item(key = "loading") {
                        Text(
                            text      = "Loading…",
                            style     = MaterialTheme.typography.bodyMedium,
                            color     = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            modifier  = Modifier
                                .fillMaxWidth()
                                .padding(EBookStoreSpacing.Large),
                        )
                    }
                }
                uiState.error?.let { msg ->
                    item(key = "error") {
                        Text(
                            text      = msg,
                            style     = MaterialTheme.typography.bodyMedium,
                            color     = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier  = Modifier
                                .fillMaxWidth()
                                .padding(EBookStoreSpacing.Large),
                        )
                    }
                }

                // Bottom breathing room
                item(key = "bottom_spacer") {
                    Spacer(Modifier.height(EBookStoreSpacing.Large))
                }
            }

            // Snackbar overlay — floats above the list
            SnackbarHost(
                hostState = snackbarHostState,
                modifier  = Modifier
                    .align(androidx.compose.ui.Alignment.BottomCenter)
                    .padding(bottom = EBookStoreSpacing.Medium),
                snackbar  = { data ->
                    Snackbar(
                        snackbarData   = data,
                        containerColor = MaterialTheme.colorScheme.inverseSurface,
                        contentColor   = MaterialTheme.colorScheme.inverseOnSurface,
                        actionColor    = MaterialTheme.colorScheme.primary,
                    )
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Private helpers
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Horizontal lazy row of [BookCard] items in [CardLayoutMode.VERTICAL] mode.
 *
 * Card width is responsive: ~40% of screen width, clamped to 140–200 dp,
 * so cards look good on phones AND tablets without hardcoded sizes.
 */
@Composable
private fun BookCardRow(
    books: List<Book>,
    onCardClick: (String) -> Unit,
    onAddToCart: (Book) -> Unit,
    modifier: Modifier = Modifier,
) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val cardWidthDp   = (screenWidthDp * 0.38f).coerceIn(140f, 200f)

    LazyRow(
        modifier            = modifier.fillMaxWidth(),
        contentPadding      = PaddingValues(horizontal = EBookStoreSpacing.Medium),
        horizontalArrangement = Arrangement.spacedBy(EBookStoreSpacing.Small),
    ) {
        items(items = books, key = { it.id }) { book ->
            BookCard(
                book            = book,
                onCardClick     = onCardClick,
                onWishlistClick = { onAddToCart(book) },
                layoutMode      = CardLayoutMode.VERTICAL,
                modifier        = Modifier.width(cardWidthDp.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews — use HomeScreenContent (stateless) so Hilt is not required
// ─────────────────────────────────────────────────────────────────────────────

private val previewUiState = HomeUiState(
    banners         = com.example.ebookstore.data.mock.MockData.banners,
    categories      = com.example.ebookstore.data.mock.MockData.categories,
    selectedCategory = "All",
    newReleases     = com.example.ebookstore.data.mock.MockData.newReleases,
    recommendations = com.example.ebookstore.data.mock.MockData.recommendations,
    bestsellers     = com.example.ebookstore.data.mock.MockData.bestsellers,
    isLoading       = false,
)

@Preview(name = "Home Screen — Light", showBackground = true)
@Composable
private fun HomeScreenLightPreview() {
    EBookStoreTheme {
        HomeScreenContent(
            uiState             = previewUiState,
            cartItemCount       = 2,
            onSearchQueryChange = {},
            onSearchSubmit      = {},
            onCategorySelect    = {},
            onNavigateToCart    = {},
            onNavigateToBook    = {},
            onNavigateToCatalogue = {},
            onAddToCart         = {},
        )
    }
}

@Preview(name = "Home Screen — Dark", showBackground = true)
@Composable
private fun HomeScreenDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        HomeScreenContent(
            uiState             = previewUiState,
            cartItemCount       = 2,
            onSearchQueryChange = {},
            onSearchSubmit      = {},
            onCategorySelect    = {},
            onNavigateToCart    = {},
            onNavigateToBook    = {},
            onNavigateToCatalogue = {},
            onAddToCart         = {},
        )
    }
}
