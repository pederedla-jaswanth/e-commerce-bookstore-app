package com.example.ebookstore.ui.screens.catalogue

import androidx.lifecycle.ViewModel
import com.example.ebookstore.data.mock.MockData
import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.domain.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
// Sort options
// ─────────────────────────────────────────────────────────────────────────────

enum class SortOption(val label: String) {
    RELEVANCE("Relevance"),
    PRICE_ASC("Price: Low to High"),
    PRICE_DESC("Price: High to Low"),
    RATING("Highest Rated"),
    NEWEST("Newest"),
}

// ─────────────────────────────────────────────────────────────────────────────
// Filter state
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Immutable snapshot of all active filter values.
 * The ViewModel derives [CatalogueUiState.displayedBooks] by applying these.
 */
data class FilterState(
    val format: String?    = null,   // "Paperback" | "eBook" | "Hard Cover" | null = all
    val minPrice: Int?     = null,
    val maxPrice: Int?     = null,
    val onSaleOnly: Boolean = false,
    val sortBy: SortOption  = SortOption.RELEVANCE,
)

// ─────────────────────────────────────────────────────────────────────────────
// UI State
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Immutable snapshot of everything [CatalogueScreen] needs to render.
 *
 * [displayedBooks] is the filtered + sorted subset of [allBooks].
 * [resultCount] is a convenience derived value for the results label.
 */
data class CatalogueUiState(
    val allBooks: List<Book>          = emptyList(),
    val displayedBooks: List<Book>    = emptyList(),
    val categories: List<Category>    = emptyList(),
    val selectedCategory: String      = "All",
    val searchQuery: String           = "",
    val filters: FilterState          = FilterState(),
    val isFilterSheetOpen: Boolean    = false,
    val isLoading: Boolean            = true,
    val error: String?                = null,
) {
    val resultCount: Int get() = displayedBooks.size
}

// ─────────────────────────────────────────────────────────────────────────────
// ViewModel
// ─────────────────────────────────────────────────────────────────────────────

/**
 * ViewModel for [CatalogueScreen].
 *
 * All filtering and sorting is done in-memory against [MockData.books].
 * A later phase will replace [loadData] with a coroutine repository call.
 *
 * Filtering pipeline (applied in this order):
 * 1. Category filter
 * 2. Search query (title or author, case-insensitive)
 * 3. Format filter
 * 4. Price range filter
 * 5. Sale-only filter
 * 6. Sort
 */
@HiltViewModel
class CatalogueViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogueUiState())
    val uiState: StateFlow<CatalogueUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    // ── Loaders ───────────────────────────────────────────────────────────────

    private fun loadData() {
        val books = MockData.books
        _uiState.update { state ->
            state.copy(
                allBooks       = books,
                categories     = MockData.categories,
                displayedBooks = applyFilters(books, state),
                isLoading      = false,
            )
        }
    }

    // ── Public event handlers ─────────────────────────────────────────────────

    /** Called on every keystroke in the search field. */
    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            val updated = state.copy(searchQuery = query)
            updated.copy(displayedBooks = applyFilters(state.allBooks, updated))
        }
    }

    /** Called when the user submits the search (IME action). */
    fun onSearchSubmit() {
        // No additional action needed — live filtering already applied.
    }

    /** Called when a category chip is tapped. */
    fun onCategorySelected(categoryName: String) {
        _uiState.update { state ->
            val updated = state.copy(selectedCategory = categoryName)
            updated.copy(displayedBooks = applyFilters(state.allBooks, updated))
        }
    }

    /** Called when a new sort option is chosen. */
    fun onSortSelected(sortOption: SortOption) {
        _uiState.update { state ->
            val updated = state.copy(filters = state.filters.copy(sortBy = sortOption))
            updated.copy(displayedBooks = applyFilters(state.allBooks, updated))
        }
    }

    /** Called when a format filter chip is toggled. Passing the same value clears it. */
    fun onFormatSelected(format: String?) {
        _uiState.update { state ->
            val updated = state.copy(filters = state.filters.copy(format = format))
            updated.copy(displayedBooks = applyFilters(state.allBooks, updated))
        }
    }

    /** Called when the price range inputs change. */
    fun onPriceRangeChanged(min: Int?, max: Int?) {
        _uiState.update { state ->
            val updated = state.copy(
                filters = state.filters.copy(minPrice = min, maxPrice = max)
            )
            updated.copy(displayedBooks = applyFilters(state.allBooks, updated))
        }
    }

    /** Toggles the "on sale only" filter. */
    fun onSaleOnlyToggled() {
        _uiState.update { state ->
            val updated = state.copy(
                filters = state.filters.copy(onSaleOnly = !state.filters.onSaleOnly)
            )
            updated.copy(displayedBooks = applyFilters(state.allBooks, updated))
        }
    }

    /** Resets all filters and search, restoring the full catalogue. */
    fun onClearFilters() {
        _uiState.update { state ->
            val cleared = state.copy(
                searchQuery      = "",
                selectedCategory = "All",
                filters          = FilterState(),
            )
            cleared.copy(displayedBooks = applyFilters(state.allBooks, cleared))
        }
    }

    /** Opens/closes the filter bottom sheet. */
    fun onFilterSheetToggle() {
        _uiState.update { it.copy(isFilterSheetOpen = !it.isFilterSheetOpen) }
    }

    fun onFilterSheetDismiss() {
        _uiState.update { it.copy(isFilterSheetOpen = false) }
    }

    // ── Filter pipeline ───────────────────────────────────────────────────────

    private fun applyFilters(books: List<Book>, state: CatalogueUiState): List<Book> {
        var result = books

        // 1. Category
        if (state.selectedCategory != "All") {
            result = result.filter { book ->
                book.categories.any { it.equals(state.selectedCategory, ignoreCase = true) }
            }
        }

        // 2. Search query (title or author)
        val query = state.searchQuery.trim()
        if (query.isNotEmpty()) {
            result = result.filter { book ->
                book.title.contains(query, ignoreCase = true) ||
                    book.author.contains(query, ignoreCase = true)
            }
        }

        // 3. Format
        state.filters.format?.let { fmt ->
            result = result.filter { it.format.equals(fmt, ignoreCase = true) }
        }

        // 4. Price range
        state.filters.minPrice?.let { min ->
            result = result.filter { it.price >= min }
        }
        state.filters.maxPrice?.let { max ->
            result = result.filter { it.price <= max }
        }

        // 5. Sale only
        if (state.filters.onSaleOnly) {
            result = result.filter { it.originalPrice != null }
        }

        // 6. Sort
        result = when (state.filters.sortBy) {
            SortOption.RELEVANCE  -> result
            SortOption.PRICE_ASC  -> result.sortedBy { it.price }
            SortOption.PRICE_DESC -> result.sortedByDescending { it.price }
            SortOption.RATING     -> result.sortedByDescending { it.rating }
            SortOption.NEWEST     -> result.reversed()
        }

        return result
    }
}
