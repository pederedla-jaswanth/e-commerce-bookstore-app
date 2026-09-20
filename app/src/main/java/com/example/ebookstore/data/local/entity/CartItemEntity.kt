package com.example.ebookstore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for a cart line-item.
 *
 * [bookId] is the primary key — each book can appear at most once in the cart,
 * with [quantity] tracking how many copies the user wants.
 *
 * Book metadata (title, cover, price) is denormalised here so the cart screen
 * can render without joining to a separate books table.
 */
@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey
    val bookId: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val price: Double,
    val originalPrice: Double?,  // null = no sale
    val format: String,
    val quantity: Int,
)
