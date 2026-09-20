package com.example.ebookstore.domain.model

/**
 * Represents a single book in the catalogue.
 * Used as the UI model for BookCard, HomeScreen sections, and CatalogueScreen.
 */
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String,          // Remote URL; Coil loads it asynchronously
    val price: Double,
    val originalPrice: Double?,    // Non-null → show sale badge + strikethrough
    val rating: Float,             // 0.0 – 5.0
    val ratingCount: Int,
    val format: String,            // "Paperback", "eBook", "Hardcover"
    val categories: List<String>,
    val deliveryDate: String,      // e.g. "Mon, 21 Jul"
    val isInWishlist: Boolean = false,
    // Detail-screen fields (populated lazily; absent in list views)
    val description: String = "",
    val pageCount: Int = 0,
    val publisher: String = "",
    val publishedDate: String = "",
    val isbn: String = "",
    val authorId: String = "",     // Links to Author model
)

/**
 * Promotional banner shown in the hero pager on the Home screen.
 */
data class Banner(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val actionRoute: String,       // Navigation route to push when tapped
)

/** Represents a selectable book category / genre. */
data class Category(
    val id: String,
    val name: String,
)

/** Author bio card shown on the Book Detail screen. */
data class Author(
    val id: String,
    val name: String,
    val bio: String,
    val photoUrl: String,
)

/** A single user review shown on the Book Detail screen. */
data class Review(
    val id: String,
    val reviewerName: String,
    val rating: Float,     // 0.0 – 5.0
    val body: String,
    val date: String,      // e.g. "15 Jul 2025"
)
