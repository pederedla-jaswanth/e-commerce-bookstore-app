package com.example.ebookstore.ui.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.domain.model.Order
import com.example.ebookstore.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
// UI State
// ─────────────────────────────────────────────────────────────────────────────

sealed class OrdersUiState {
    object Loading : OrdersUiState()
    object Empty   : OrdersUiState()
    data class Success(val orders: List<Order>) : OrdersUiState()
    data class Error(val message: String)       : OrdersUiState()
}

/**
 * Transient one-shot event emitted after a cancel or buy-again action.
 * Consumed by the UI then set back to [None].
 */
sealed class OrdersEvent {
    object None : OrdersEvent()
    data class CancelSuccess(val orderId: String) : OrdersEvent()
    data class CancelFailed(val reason: String)   : OrdersEvent()
    /** Books from the cancelled/delivered order, ready to add back to cart. */
    data class BuyAgainReady(val books: List<Book>) : OrdersEvent()
}

// ─────────────────────────────────────────────────────────────────────────────
// ViewModel
// ─────────────────────────────────────────────────────────────────────────────

/**
 * ViewModel for the Orders History screen and Order Detail screen.
 *
 * NavGraph-scoped so both screens share the same instance and the detail
 * screen can mutate the list (e.g. cancel) without reloading.
 *
 * Backed by [OrderRepository] (Room) — [uiState] is a live Flow that
 * re-emits automatically whenever any order row changes.
 */
@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
) : ViewModel() {

    /** Live UI state derived from the Room Flow. */
    val uiState: StateFlow<OrdersUiState> = orderRepository.observeOrders()
        .map { orders ->
            if (orders.isEmpty()) OrdersUiState.Empty
            else OrdersUiState.Success(orders)
        }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = OrdersUiState.Loading,
        )

    private val _event = MutableStateFlow<OrdersEvent>(OrdersEvent.None)
    val event: StateFlow<OrdersEvent> = _event.asStateFlow()

    // ── Detail ────────────────────────────────────────────────────────────────

    /**
     * Returns a single order by ID from the current in-memory snapshot.
     * For reactive detail observation use [orderRepository.observeOrders] filtered by id.
     */
    fun getOrderById(orderId: String): Order? =
        (uiState.value as? OrdersUiState.Success)?.orders?.find { it.id == orderId }

    // ── Cancel ────────────────────────────────────────────────────────────────

    /**
     * Attempts to cancel the order identified by [orderId].
     *
     * Emits [OrdersEvent.CancelSuccess] on success — the Room Flow
     * automatically re-emits the updated list so no manual reload is needed.
     * Emits [OrdersEvent.CancelFailed] when outside the 48-hour window.
     */
    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            val ok = orderRepository.cancelOrder(orderId)
            if (ok) {
                _event.update { OrdersEvent.CancelSuccess(orderId) }
            } else {
                _event.update {
                    OrdersEvent.CancelFailed("This order can no longer be cancelled.")
                }
            }
        }
    }

    // ── Buy Again ─────────────────────────────────────────────────────────────

    /**
     * Resolves the books in [orderId] and emits [OrdersEvent.BuyAgainReady].
     *
     * The NavGraph (or screen) must consume the event and call
     * [CartViewModel.addToCart] for each returned book.
     */
    fun buyAgain(orderId: String) {
        val order = getOrderById(orderId) ?: return
        val books = order.items.map { item ->
            // Reconstruct a minimal Book from the OrderItem snapshot.
            Book(
                id            = item.bookId,
                title         = item.title,
                author        = item.author,
                coverUrl      = item.coverUrl,
                price         = item.priceAtPurchase,
                originalPrice = null,
                rating        = 0f,
                ratingCount   = 0,
                format        = item.format,
                categories    = emptyList(),
                deliveryDate  = "",
            )
        }
        _event.update { OrdersEvent.BuyAgainReady(books) }
    }

    /** Consumes the current event — call after the UI has acted on it. */
    fun clearEvent() {
        _event.update { OrdersEvent.None }
    }
}
