package com.example.ebookstore.domain.model

/**
 * Represents a single book in the catalogue.
 * Used as the UI model for BookCard, HomeScreen sections, and CatalogueScreen.
 */
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String,        // Remote URL; Coil loads it asynchronously
    val price: Double,
    val originalPrice: Double?,  // Non-null → show sale badge + strikethrough
    val rating: Float,           // 0.0 – 5.0
    val ratingCount: Int,
    val format: String,          // "Paperback", "eBook", "Hardcover"
    val categories: List<String>,
    val deliveryDate: String,    // e.g. "Mon, 21 Jul"
    val isInWishlist: Boolean = false,
)

/**
 * Promotional banner shown in the hero pager on the Home screen.
 */
data class Banner(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val actionRoute: String,     // Navigation route to push when tapped
)

/** Represents a selectable book category / genre. */
data class Category(
    val id: String,
    val name: String,
)
