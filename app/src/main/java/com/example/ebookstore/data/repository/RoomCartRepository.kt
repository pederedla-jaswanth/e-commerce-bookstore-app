package com.example.ebookstore.data.repository

import com.example.ebookstore.data.local.dao.CartDao
import com.example.ebookstore.data.local.entity.CartItemEntity
import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.domain.repository.CartItemData
import com.example.ebookstore.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room-backed [CartRepository].
 *
 * [observeItems] returns a Flow that Room re-emits automatically whenever
 * the cart_items table changes — no manual refresh needed.
 */
@Singleton
class RoomCartRepository @Inject constructor(
    private val cartDao: CartDao,
) : CartRepository {

    override fun observeItems(): Flow<List<CartItemData>> =
        cartDao.observeAll().map { entities -> entities.map { it.toData() } }

    override suspend fun addToCart(book: Book) {
        val existing = cartDao.getById(book.id)
        if (existing != null) {
            cartDao.update(existing.copy(quantity = existing.quantity + 1))
        } else {
            cartDao.upsert(book.toEntity(quantity = 1))
        }
    }

    override suspend fun setQuantity(bookId: String, quantity: Int) {
        if (quantity <= 0) {
            cartDao.deleteById(bookId)
            return
        }
        val existing = cartDao.getById(bookId) ?: return
        cartDao.update(existing.copy(quantity = quantity))
    }

    override suspend fun removeItem(bookId: String) {
        cartDao.deleteById(bookId)
    }

    override suspend fun clearCart() {
        cartDao.deleteAll()
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private fun CartItemEntity.toData() = CartItemData(
        bookId        = bookId,
        title         = title,
        author        = author,
        coverUrl      = coverUrl,
        price         = price,
        originalPrice = originalPrice,
        format        = format,
        quantity      = quantity,
    )

    private fun Book.toEntity(quantity: Int) = CartItemEntity(
        bookId        = id,
        title         = title,
        author        = author,
        coverUrl      = coverUrl,
        price         = price,
        originalPrice = originalPrice,
        format        = format,
        quantity      = quantity,
    )
}
