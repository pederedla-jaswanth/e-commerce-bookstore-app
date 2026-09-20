package com.example.ebookstore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for a wishlist entry.
 *
 * [bookId] is the primary key — each book appears at most once.
 * Book metadata is denormalised for display without a separate books table.
 */
@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey
    val bookId: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val price: Double,
    val originalPrice: Double?,
    val rating: Float,
    val ratingCount: Int,
    val format: String,
    val categories: String,     // JSON array stored as String; decoded in repository
    val addedAt: Long,          // epoch millis — for ordering
)
