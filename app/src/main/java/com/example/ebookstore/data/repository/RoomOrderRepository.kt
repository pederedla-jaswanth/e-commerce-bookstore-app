package com.example.ebookstore.data.repository

import com.example.ebookstore.data.local.dao.OrderDao
import com.example.ebookstore.data.local.entity.OrderEntity
import com.example.ebookstore.data.local.entity.OrderItemEntity
import com.example.ebookstore.data.local.relation.OrderWithItems
import com.example.ebookstore.domain.model.Order
import com.example.ebookstore.domain.model.OrderItem
import com.example.ebookstore.domain.model.OrderStatus
import com.example.ebookstore.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room-backed [OrderRepository].
 *
 * [observeOrders] is a live Flow: Room re-emits the full list whenever
 * any order's status changes (e.g. after a cancel), keeping the UI reactive
 * without manual refreshes.
 *
 * The 48-hour cancellation window is evaluated against [Order.isCancellable]
 * which reads [Instant.now()] at call time — no stored state needed.
 */
@Singleton
class RoomOrderRepository @Inject constructor(
    private val orderDao: OrderDao,
) : OrderRepository {

    override fun observeOrders(): Flow<List<Order>> =
        orderDao.observeAllWithItems().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getOrderById(orderId: String): Order? =
        orderDao.getOrderWithItems(orderId)?.toDomain()

    override suspend fun saveOrder(order: Order) {
        orderDao.insertOrder(order.toEntity())
        orderDao.insertOrderItems(order.items.map { it.toEntity(order.id) })
    }

    override suspend fun cancelOrder(orderId: String): Boolean {
        val order = orderDao.getOrderWithItems(orderId)?.toDomain() ?: return false
        if (!order.isCancellable) return false
        orderDao.updateStatus(orderId, OrderStatus.CANCELLED.name)
        return true
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private fun OrderWithItems.toDomain() = Order(
        id                = order.orderId,
        placedAt          = order.placedAt,
        items             = items.map { it.toDomain() },
        subtotal          = order.subtotal,
        shipping          = order.shipping,
        grandTotal        = order.grandTotal,
        status            = OrderStatus.valueOf(order.status),
        paymentMethod     = order.paymentMethod,
        shippingAddress   = order.shippingAddress,
        estimatedDelivery = order.estimatedDelivery,
    )

    private fun OrderItemEntity.toDomain() = OrderItem(
        bookId           = bookId,
        title            = title,
        author           = author,
        coverUrl         = coverUrl,
        format           = format,
        priceAtPurchase  = priceAtPurchase,
        quantity         = quantity,
    )

    private fun Order.toEntity() = OrderEntity(
        orderId           = id,
        placedAt          = placedAt,
        subtotal          = subtotal,
        shipping          = shipping,
        grandTotal        = grandTotal,
        status            = status.name,
        paymentMethod     = paymentMethod,
        shippingAddress   = shippingAddress,
        estimatedDelivery = estimatedDelivery,
    )

    private fun OrderItem.toEntity(orderId: String) = OrderItemEntity(
        orderId          = orderId,
        bookId           = bookId,
        title            = title,
        author           = author,
        coverUrl         = coverUrl,
        format           = format,
        priceAtPurchase  = priceAtPurchase,
        quantity         = quantity,
    )
}
