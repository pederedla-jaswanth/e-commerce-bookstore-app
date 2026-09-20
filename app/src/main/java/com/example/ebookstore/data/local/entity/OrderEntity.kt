package com.example.ebookstore.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for a placed order header.
 *
 * Line items are stored separately in [OrderItemEntity] linked by [orderId].
 */
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val orderId: String,
    val placedAt: Long,             // epoch millis
    val subtotal: Double,
    val shipping: Double,
    val grandTotal: Double,
    val status: String,             // OrderStatus.name
    val paymentMethod: String,
    val shippingAddress: String,
    val estimatedDelivery: String,
)

/**
 * Room entity for a single line item within an order.
 *
 * Each row belongs to one [OrderEntity] via [orderId].
 * The composite primary key [orderId] + [bookId] ensures uniqueness per order.
 */
@Entity(
    tableName    = "order_items",
    primaryKeys  = ["orderId", "bookId"],
    foreignKeys  = [
        ForeignKey(
            entity        = OrderEntity::class,
            parentColumns = ["orderId"],
            childColumns  = ["orderId"],
            onDelete      = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("orderId")],
)
data class OrderItemEntity(
    val orderId: String,
    val bookId: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val format: String,
    val priceAtPurchase: Double,
    val quantity: Int,
)
