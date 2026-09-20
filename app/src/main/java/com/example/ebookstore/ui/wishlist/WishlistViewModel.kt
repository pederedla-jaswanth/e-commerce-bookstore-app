package com.example.ebookstore.ui.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.domain.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
// UI State
// ─────────────────────────────────────────────────────────────────────────────

data class WishlistUiState(
    val items: List<Book> = emptyList(),
) {
    val itemCount: Int   get() = items.size
    val isEmpty: Boolean get() = items.isEmpty()
}

// ─────────────────────────────────────────────────────────────────────────────
// ViewModel
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Shared ViewModel for wishlist state.
 *
 * Now backed by [WishlistRepository] (Room/SQLite) — wishlist survives app
 * restarts. The [uiState] Flow is driven directly by the Room query.
 */
@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val wishlistRepository: WishlistRepository,
) : ViewModel() {

    val uiState: StateFlow<WishlistUiState> = wishlistRepository.observeItems()
        .map { WishlistUiState(items = it) }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = WishlistUiState(),
        )

    fun addToWishlist(book: Book) {
        viewModelScope.launch { wishlistRepository.add(book) }
    }

    fun removeFromWishlist(bookId: String) {
        viewModelScope.launch { wishlistRepository.remove(bookId) }
    }

    fun toggleWishlist(book: Book): Boolean {
        val isIn = isInWishlist(book.id)
        if (isIn) removeFromWishlist(book.id) else addToWishlist(book)
        return !isIn
    }

    fun isInWishlist(bookId: String): Boolean =
        uiState.value.items.any { it.id == bookId }
}
