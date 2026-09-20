package com.example.ebookstore.ui.screens.recommendations

import androidx.lifecycle.ViewModel
import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.domain.repository.RecommendationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
// UI State
// ─────────────────────────────────────────────────────────────────────────────

/**
 * All recommendation lists a screen may need.
 *
 * [relatedBooks]          — books sharing categories with the currently-viewed book.
 * [categorySuggestions]   — books derived from the user's browsing history categories.
 * [trending]              — highest-rated / most-reviewed books globally.
 */
data class RecommendationsUiState(
    val relatedBooks: List<Book>        = emptyList(),
    val categorySuggestions: List<Book> = emptyList(),
    val trending: List<Book>            = emptyList(),
)

// ─────────────────────────────────────────────────────────────────────────────
// ViewModel
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Shared ViewModel for all recommendation surfaces.
 *
 * NavGraph-scoped so the same recently-viewed history is used by both
 * [BookDetailScreen] (related books) and [HomeScreen] (category suggestions).
 *
 * All logic is delegated to [RecommendationRepository] — no business rules
 * live in this ViewModel.
 */
@HiltViewModel
class RecommendationsViewModel @Inject constructor(
    private val repository: RecommendationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecommendationsUiState())
    val uiState: StateFlow<RecommendationsUiState> = _uiState.asStateFlow()

    /** Books the user has navigated to this session — drives category suggestions. */
    private val recentlyViewed = mutableListOf<Book>()

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Call when the user opens a book detail page.
     * Refreshes [relatedBooks] for that specific book and keeps [recentlyViewed] up-to-date.
     */
    fun onBookViewed(book: Book) {
        if (recentlyViewed.none { it.id == book.id }) {
            recentlyViewed.add(0, book)        // newest-first
            if (recentlyViewed.size > 10) recentlyViewed.removeLast()
        }
        refreshForBook(book)
    }

    /**
     * Refreshes all recommendation lists for the current context book.
     * Call from [BookDetailScreen] whenever the displayed book changes.
     */
    fun refreshForBook(book: Book) {
        _uiState.update {
            it.copy(
                relatedBooks        = repository.relatedBooks(book),
                categorySuggestions = repository.categoryRecommendations(recentlyViewed),
                trending            = repository.trendingBooks(),
            )
        }
    }

    /**
     * Refreshes only [categorySuggestions] and [trending] — used by [HomeScreen]
     * where there is no specific "current book".
     */
    fun refreshGlobal() {
        _uiState.update {
            it.copy(
                categorySuggestions = repository.categoryRecommendations(recentlyViewed),
                trending            = repository.trendingBooks(),
            )
        }
    }
}
