package com.example.ebookstore.data.mock

import com.example.ebookstore.domain.model.Order
import com.example.ebookstore.domain.model.OrderItem
import com.example.ebookstore.domain.model.OrderStatus
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory mock repository for order history.
 *
 * Pre-seeds 5 realistic orders at different statuses and ages so every
 * UI state (processing, shipped, delivered, cancelled, cancellable) is
 * reachable in the demo.
 *
 * Order placed within 48 h (seeded relative to [Instant.now]) are cancellable.
 * Replace with a Room-backed implementation in a future phase — no other
 * files need changing.
 */
@Singleton
class MockOrderRepository @Inject constructor() {

    private val _orders: MutableList<Order> = mutableListOf<Order>().also { list ->
        val now = Instant.now().toEpochMilli()

        list += Order(
            id               = "ORD-00000001",
            placedAt         = now - ChronoUnit.HOURS.duration.toMillis() * 2,  // 2 h ago → cancellable
            items            = listOf(
                OrderItem("1", "The Joy of Minimalism", "Daniel Reed",
                    "https://picsum.photos/seed/book1/120/180", "Paperback", 149.0, 1),
                OrderItem("2", "The Art of Focus", "Arjun Patel",
                    "https://picsum.photos/seed/book2/120/180", "Paperback", 399.0, 1),
            ),
            subtotal         = 548.0,
            shipping         = 0.0,
            grandTotal       = 548.0,
            status           = OrderStatus.PROCESSING,
            paymentMethod    = "UPI",
            shippingAddress  = "123 MG Road, Bengaluru, Karnataka 560001",
            estimatedDelivery = "Wed, 23 Jul",
        )

        list += Order(
            id               = "ORD-00000002",
            placedAt         = now - ChronoUnit.HOURS.duration.toMillis() * 30,  // 30 h ago → cancellable
            items            = listOf(
                OrderItem("5", "Girls That Invest", "Simran Kaur",
                    "https://picsum.photos/seed/book5/120/180", "Paperback", 259.0, 1),
            ),
            subtotal         = 259.0,
            shipping         = 49.0,
            grandTotal       = 308.0,
            status           = OrderStatus.CONFIRMED,
            paymentMethod    = "Credit / Debit Card",
            shippingAddress  = "456 Linking Road, Mumbai, Maharashtra 400050",
            estimatedDelivery = "Fri, 25 Jul",
        )

        list += Order(
            id               = "ORD-00000003",
            placedAt         = now - ChronoUnit.DAYS.duration.toMillis() * 3,   // 3 days → NOT cancellable
            items            = listOf(
                OrderItem("3", "The Midnight Hour", "James Adams",
                    "https://picsum.photos/seed/book3/120/180", "Paperback", 299.0, 2),
            ),
            subtotal         = 598.0,
            shipping         = 0.0,
            grandTotal       = 598.0,
            status           = OrderStatus.SHIPPED,
            paymentMethod    = "Net Banking",
            shippingAddress  = "789 Jubilee Hills, Hyderabad, Telangana 500033",
            estimatedDelivery = "Mon, 21 Jul",
        )

        list += Order(
            id               = "ORD-00000004",
            placedAt         = now - ChronoUnit.DAYS.duration.toMillis() * 10,
            items            = listOf(
                OrderItem("7", "The Path to Success", "James Wright",
                    "https://picsum.photos/seed/book7/120/180", "Paperback", 359.0, 1),
                OrderItem("8", "The Art of Learning", "Raj Patel",
                    "https://picsum.photos/seed/book8/120/180", "Paperback", 259.0, 1),
            ),
            subtotal         = 618.0,
            shipping         = 0.0,
            grandTotal       = 618.0,
            status           = OrderStatus.DELIVERED,
            paymentMethod    = "Cash on Delivery",
            shippingAddress  = "22 Anna Salai, Chennai, Tamil Nadu 600002",
            estimatedDelivery = "12 Jul",
        )

        list += Order(
            id               = "ORD-00000005",
            placedAt         = now - ChronoUnit.DAYS.duration.toMillis() * 5,
            items            = listOf(
                OrderItem("4", "Beneath the Stars", "Jessica Martin",
                    "https://picsum.photos/seed/book4/120/180", "Hard Cover", 499.0, 1),
            ),
            subtotal         = 499.0,
            shipping         = 49.0,
            grandTotal       = 548.0,
            status           = OrderStatus.CANCELLED,
            paymentMethod    = "UPI",
            shippingAddress  = "10 Park Street, Kolkata, West Bengal 700016",
            estimatedDelivery = "N/A",
        )
    }

    /** Returns all orders sorted newest-first. */
    fun getOrders(): List<Order> = _orders.sortedByDescending { it.placedAt }

    /** Returns the order with [id], or null if not found. */
    fun getOrderById(id: String): Order? = _orders.find { it.id == id }

    /**
     * Cancels the order with [id] if it is still cancellable.
     * Returns true on success, false if the order was not found or not cancellable.
     */
    fun cancelOrder(id: String): Boolean {
        val idx = _orders.indexOfFirst { it.id == id }
        if (idx < 0) return false
        val order = _orders[idx]
        if (!order.isCancellable) return false
        _orders[idx] = order.copy(status = OrderStatus.CANCELLED)
        return true
    }
}
