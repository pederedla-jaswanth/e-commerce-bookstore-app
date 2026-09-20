package com.example.ebookstore.domain.repository

import com.example.ebookstore.domain.model.Book
import kotlinx.coroutines.flow.Flow

/** Contract for wishlist persistence. */
interface WishlistRepository {
    /** Live stream of wishlisted books. */
    fun observeItems(): Flow<List<Book>>

    /** Add [book] to the wishlist (no-op if already present). */
    suspend fun add(book: Book)

    /** Remove [bookId] from the wishlist. */
    suspend fun remove(bookId: String)

    /** Returns true if [bookId] is currently in the wishlist. */
    suspend fun isInWishlist(bookId: String): Boolean
}
