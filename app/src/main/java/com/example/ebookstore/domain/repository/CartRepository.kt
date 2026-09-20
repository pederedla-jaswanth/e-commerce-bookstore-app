package com.example.ebookstore.domain.repository

import com.example.ebookstore.domain.model.Book
import kotlinx.coroutines.flow.Flow

/** Contract for cart persistence. */
interface CartRepository {
    /** Live stream of cart items. */
    fun observeItems(): Flow<List<CartItemData>>

    /** Add one copy of [book], or increment quantity if already present. */
    suspend fun addToCart(book: Book)

    /** Set quantity for [bookId]. If quantity ≤ 0 the item is removed. */
    suspend fun setQuantity(bookId: String, quantity: Int)

    /** Remove one item entirely. */
    suspend fun removeItem(bookId: String)

    /** Clear the entire cart. */
    suspend fun clearCart()
}

/** Minimal projection used by ViewModels to avoid coupling to Room entities. */
data class CartItemData(
    val bookId: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val price: Double,
    val originalPrice: Double?,
    val format: String,
    val quantity: Int,
) {
    val lineTotal: Double get() = price * quantity
}
