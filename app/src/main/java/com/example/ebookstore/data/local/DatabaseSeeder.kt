package com.example.ebookstore.data.local

import com.example.ebookstore.data.local.dao.OrderDao
import com.example.ebookstore.data.local.entity.OrderEntity
import com.example.ebookstore.data.local.entity.OrderItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeds 5 realistic demo orders on first install so the Orders screen is
 * populated without requiring a real checkout flow.
 *
 * [seedIfEmpty] is a no-op when orders already exist, so it is safe to call
 * on every app launch from [AppInitializer].
 *
 * Orders cover every [OrderStatus] and both cancellable / non-cancellable
 * windows so every UI state is reachable in the demo.
 */
@Singleton
class DatabaseSeeder @Inject constructor(
    private val orderDao: OrderDao,
) {

    fun seedIfEmpty() {
        CoroutineScope(Dispatchers.IO).launch {
            if (orderDao.count() > 0) return@launch   // already seeded

            val now = Instant.now().toEpochMilli()
            val h   = ChronoUnit.HOURS.duration.toMillis()
            val d   = ChronoUnit.DAYS.duration.toMillis()

            // ── Order 1: PROCESSING, 2 h ago → cancellable ─────────────────
            orderDao.insertOrder(
                OrderEntity(
                    orderId           = "ORD-00000001",
                    placedAt          = now - 2 * h,
                    subtotal          = 548.0,
                    shipping          = 0.0,
                    grandTotal        = 548.0,
                    status            = "PROCESSING",
                    paymentMethod     = "UPI",
                    shippingAddress   = "123 MG Road, Bengaluru, Karnataka 560001",
                    estimatedDelivery = "Wed, 23 Jul",
                )
            )
            orderDao.insertOrderItems(
                listOf(
                    OrderItemEntity("ORD-00000001", "1",
                        "The Joy of Minimalism", "Daniel Reed",
                        "https://picsum.photos/seed/book1/120/180", "Paperback", 149.0, 1),
                    OrderItemEntity("ORD-00000001", "2",
                        "The Art of Focus", "Arjun Patel",
                        "https://picsum.photos/seed/book2/120/180", "Paperback", 399.0, 1),
                )
            )

            // ── Order 2: CONFIRMED, 30 h ago → cancellable ─────────────────
            orderDao.insertOrder(
                OrderEntity(
                    orderId           = "ORD-00000002",
                    placedAt          = now - 30 * h,
                    subtotal          = 259.0,
                    shipping          = 49.0,
                    grandTotal        = 308.0,
                    status            = "CONFIRMED",
                    paymentMethod     = "Credit / Debit Card",
                    shippingAddress   = "456 Linking Road, Mumbai, Maharashtra 400050",
                    estimatedDelivery = "Fri, 25 Jul",
                )
            )
            orderDao.insertOrderItems(
                listOf(
                    OrderItemEntity("ORD-00000002", "5",
                        "Girls That Invest", "Simran Kaur",
                        "https://picsum.photos/seed/book5/120/180", "Paperback", 259.0, 1),
                )
            )

            // ── Order 3: SHIPPED, 3 days ago → NOT cancellable ─────────────
            orderDao.insertOrder(
                OrderEntity(
                    orderId           = "ORD-00000003",
                    placedAt          = now - 3 * d,
                    subtotal          = 598.0,
                    shipping          = 0.0,
                    grandTotal        = 598.0,
                    status            = "SHIPPED",
                    paymentMethod     = "Net Banking",
                    shippingAddress   = "789 Jubilee Hills, Hyderabad, Telangana 500033",
                    estimatedDelivery = "Mon, 21 Jul",
                )
            )
            orderDao.insertOrderItems(
                listOf(
                    OrderItemEntity("ORD-00000003", "3",
                        "The Midnight Hour", "James Adams",
                        "https://picsum.photos/seed/book3/120/180", "Paperback", 299.0, 2),
                )
            )

            // ── Order 4: DELIVERED, 10 days ago ────────────────────────────
            orderDao.insertOrder(
                OrderEntity(
                    orderId           = "ORD-00000004",
                    placedAt          = now - 10 * d,
                    subtotal          = 618.0,
                    shipping          = 0.0,
                    grandTotal        = 618.0,
                    status            = "DELIVERED",
                    paymentMethod     = "Cash on Delivery",
                    shippingAddress   = "22 Anna Salai, Chennai, Tamil Nadu 600002",
                    estimatedDelivery = "12 Jul",
                )
            )
            orderDao.insertOrderItems(
                listOf(
                    OrderItemEntity("ORD-00000004", "7",
                        "The Path to Success", "James Wright",
                        "https://picsum.photos/seed/book7/120/180", "Paperback", 359.0, 1),
                    OrderItemEntity("ORD-00000004", "8",
                        "The Art of Learning", "Raj Patel",
                        "https://picsum.photos/seed/book8/120/180", "Paperback", 259.0, 1),
                )
            )

            // ── Order 5: CANCELLED, 5 days ago ─────────────────────────────
            orderDao.insertOrder(
                OrderEntity(
                    orderId           = "ORD-00000005",
                    placedAt          = now - 5 * d,
                    subtotal          = 499.0,
                    shipping          = 49.0,
                    grandTotal        = 548.0,
                    status            = "CANCELLED",
                    paymentMethod     = "UPI",
                    shippingAddress   = "10 Park Street, Kolkata, West Bengal 700016",
                    estimatedDelivery = "N/A",
                )
            )
            orderDao.insertOrderItems(
                listOf(
                    OrderItemEntity("ORD-00000005", "4",
                        "Beneath the Stars", "Jessica Martin",
                        "https://picsum.photos/seed/book4/120/180", "Hard Cover", 499.0, 1),
                )
            )
        }
    }
}
