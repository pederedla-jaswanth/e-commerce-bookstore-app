package com.example.ebookstore.domain.model

import java.time.Instant

/**
 * Status of an order.
 *
 * Transitions (mock only):
 *   PROCESSING → CONFIRMED → SHIPPED → DELIVERED
 *   Any non-DELIVERED, non-CANCELLED order placed within 48 h → CANCELLED
 */
enum class OrderStatus(val label: String) {
    PROCESSING("Processing"),
    CONFIRMED("Confirmed"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled"),
}

/** A single line item within an order (book + quantity at time of purchase). */
data class OrderItem(
    val bookId: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val format: String,
    val priceAtPurchase: Double,
    val quantity: Int,
) {
    val lineTotal: Double get() = priceAtPurchase * quantity
}

/**
 * A completed or in-progress order.
 *
 * [placedAt] is an epoch-millis timestamp so the 48-hour cancellation
 * window can be evaluated at runtime.
 */
data class Order(
    val id: String,
    val placedAt: Long,           // epoch millis
    val items: List<OrderItem>,
    val subtotal: Double,
    val shipping: Double,
    val grandTotal: Double,
    val status: OrderStatus,
    val paymentMethod: String,    // e.g. "UPI", "Credit / Debit Card"
    val shippingAddress: String,  // formatted single string for display
    val estimatedDelivery: String, // e.g. "Mon, 28 Jul"
) {
    /**
     * Returns true when the order may still be cancelled:
     * - Status is not already DELIVERED or CANCELLED.
     * - Placed within the last 48 hours.
     */
    val isCancellable: Boolean
        get() = status != OrderStatus.DELIVERED
             && status != OrderStatus.CANCELLED
             && (Instant.now().toEpochMilli() - placedAt) < 48L * 60 * 60 * 1_000
}
