package com.example.ebookstore.ui.cart

import androidx.lifecycle.ViewModel
import com.example.ebookstore.domain.model.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
// Data model
// ─────────────────────────────────────────────────────────────────────────────

/** One line-item in the cart. */
data class CartItem(
    val book: Book,
    val quantity: Int = 1,
)

// ─────────────────────────────────────────────────────────────────────────────
// UI State
// ─────────────────────────────────────────────────────────────────────────────

data class CartUiState(
    val items: List<CartItem> = emptyList(),
) {
    /** Total number of individual books in the cart (sum of quantities). */
    val itemCount: Int get() = items.sumOf { it.quantity }
}

// ─────────────────────────────────────────────────────────────────────────────
// ViewModel
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Shared ViewModel for cart state.
 *
 * Scoped to the NavGraph (created in [EBookStoreNavGraph]) so every screen
 * reads the same cart — the bottom-bar badge and CartScreen both observe this.
 *
 * Phase 9 will replace in-memory storage with a Room-backed repository.
 */
@HiltViewModel
class CartViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    /** Adds one copy of [book] to the cart (increments quantity if already present). */
    fun addToCart(book: Book) {
        _uiState.update { state ->
            val existing = state.items.indexOfFirst { it.book.id == book.id }
            if (existing >= 0) {
                val updated = state.items.toMutableList().also {
                    it[existing] = it[existing].copy(quantity = it[existing].quantity + 1)
                }
                state.copy(items = updated)
            } else {
                state.copy(items = state.items + CartItem(book))
            }
        }
    }

    /** Removes a single copy of [bookId]; removes the line entirely if quantity reaches 0. */
    fun removeFromCart(bookId: String) {
        _uiState.update { state ->
            val updated = state.items.mapNotNull { item ->
                if (item.book.id == bookId) {
                    if (item.quantity > 1) item.copy(quantity = item.quantity - 1) else null
                } else {
                    item
                }
            }
            state.copy(items = updated)
        }
    }

    /** Returns true if [bookId] is already in the cart. */
    fun isInCart(bookId: String): Boolean =
        _uiState.value.items.any { it.book.id == bookId }
}
