package com.example.ebookstore.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.domain.repository.CartItemData
import com.example.ebookstore.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
// UI model (kept as-is so all existing screens compile without changes)
// ─────────────────────────────────────────────────────────────────────────────

/** One line-item in the cart (UI model). */
data class CartItem(
    val book: Book,
    val quantity: Int = 1,
) {
    val lineTotal: Double get() = book.price * quantity
}

data class CartUiState(
    val items: List<CartItem> = emptyList(),
) {
    val itemCount: Int  get() = items.sumOf { it.quantity }
    val subtotal: Double get() = items.sumOf { it.lineTotal }
    val shipping: Double get() = if (subtotal >= 500.0) 0.0 else 49.0
    val grandTotal: Double get() = subtotal + shipping
    val isEmpty: Boolean get() = items.isEmpty()
}

// ─────────────────────────────────────────────────────────────────────────────
// ViewModel
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Shared ViewModel for cart state.
 *
 * Now backed by [CartRepository] (Room/SQLite) — cart contents survive app
 * restarts. The [uiState] Flow is derived directly from the Room Flow so the
 * UI reacts to any database change automatically.
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
) : ViewModel() {

    val uiState: StateFlow<CartUiState> = cartRepository.observeItems()
        .map { items -> CartUiState(items = items.map { it.toCartItem() }) }
        .stateIn(
            scope         = viewModelScope,
            started       = SharingStarted.WhileSubscribed(5_000),
            initialValue  = CartUiState(),
        )

    // ── Write operations ──────────────────────────────────────────────────────

    fun addToCart(book: Book) {
        viewModelScope.launch { cartRepository.addToCart(book) }
    }

    fun increaseQuantity(bookId: String) {
        viewModelScope.launch {
            val current = uiState.value.items.find { it.book.id == bookId }?.quantity ?: return@launch
            cartRepository.setQuantity(bookId, current + 1)
        }
    }

    fun decreaseQuantity(bookId: String) {
        viewModelScope.launch {
            val current = uiState.value.items.find { it.book.id == bookId }?.quantity ?: return@launch
            cartRepository.setQuantity(bookId, current - 1)
        }
    }

    fun removeItem(bookId: String) {
        viewModelScope.launch { cartRepository.removeItem(bookId) }
    }

    fun clearCart() {
        viewModelScope.launch { cartRepository.clearCart() }
    }

    // ── Read helpers ──────────────────────────────────────────────────────────

    fun isInCart(bookId: String): Boolean =
        uiState.value.items.any { it.book.id == bookId }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private fun CartItemData.toCartItem() = CartItem(
        book = Book(
            id            = bookId,
            title         = title,
            author        = author,
            coverUrl      = coverUrl,
            price         = price,
            originalPrice = originalPrice,
            rating        = 0f,
            ratingCount   = 0,
            format        = format,
            categories    = emptyList(),
            deliveryDate  = "",
        ),
        quantity = quantity,
    )
}
