package com.example.ebookstore.domain.repository

import com.example.ebookstore.domain.model.Order
import kotlinx.coroutines.flow.Flow

/** Contract for order persistence. */
interface OrderRepository {
    /** Live stream of all orders, newest-first. */
    fun observeOrders(): Flow<List<Order>>

    /** Fetch a single order by ID, or null. */
    suspend fun getOrderById(orderId: String): Order?

    /** Persist a new order (header + items). */
    suspend fun saveOrder(order: Order)

    /**
     * Cancel the order if it is still within the 48-hour window.
     * Returns true on success, false if not cancellable.
     */
    suspend fun cancelOrder(orderId: String): Boolean
}
