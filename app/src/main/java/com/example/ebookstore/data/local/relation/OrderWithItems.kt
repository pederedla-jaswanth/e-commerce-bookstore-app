package com.example.ebookstore.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.ebookstore.data.local.entity.OrderEntity
import com.example.ebookstore.data.local.entity.OrderItemEntity

/**
 * Room one-to-many relation: one [OrderEntity] → many [OrderItemEntity].
 * Used by [OrderDao] to load the full order in a single query.
 */
data class OrderWithItems(
    @Embedded
    val order: OrderEntity,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
    )
    val items: List<OrderItemEntity>,
)
