package com.example.ebookstore.ui.screens.bookdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.ebookstore.data.mock.MockData
import com.example.ebookstore.domain.model.Author
import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.domain.model.Review
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
 * Immutable snapshot of everything [BookDetailScreen] needs to render.
 */
data class BookDetailUiState(
    val book: Book?              = null,
    val author: Author?          = null,
    val reviews: List<Review>    = emptyList(),
    val relatedBooks: List<Book> = emptyList(),
    val isInWishlist: Boolean    = false,
    val isInCart: Boolean        = false,
    val isLoading: Boolean       = true,
    val error: String?           = null,
)

// ─────────────────────────────────────────────────────────────────────────────
// ViewModel
// ─────────────────────────────────────────────────────────────────────────────

/**
 * ViewModel for [BookDetailScreen].
 *
 * Receives [bookId] from [SavedStateHandle] (populated by the nav argument).
 * Loads mock data synchronously — a later phase will replace this with a
 * coroutine-based repository call.
 */
@HiltViewModel
class BookDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val bookId: String = checkNotNull(savedStateHandle["bookId"])

    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        val book = MockData.bookById(bookId)
        if (book == null) {
            _uiState.update { it.copy(isLoading = false, error = "Book not found.") }
            return
        }
        _uiState.update {
            it.copy(
                book         = book,
                author       = MockData.authorById(book.authorId),
                reviews      = MockData.reviewsForBook(bookId),
                relatedBooks = MockData.relatedBooks(book),
                isLoading    = false,
            )
        }
    }

    /** Toggles wishlist state in-place (no navigation). */
    fun onWishlistToggle() {
        _uiState.update { it.copy(isInWishlist = !it.isInWishlist) }
    }

    /** Marks the book as in-cart. Called after [CartViewModel.addToCart]. */
    fun onAddedToCart() {
        _uiState.update { it.copy(isInCart = true) }
    }
}
