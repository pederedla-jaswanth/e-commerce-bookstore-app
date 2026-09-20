package com.example.ebookstore.data.repository

import com.example.ebookstore.data.local.dao.WishlistDao
import com.example.ebookstore.data.local.entity.WishlistItemEntity
import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room-backed [WishlistRepository].
 *
 * Categories are stored as a comma-separated string (e.g. "Fiction,Thriller")
 * and split back to a List on read. Sufficient for the current data shape;
 * replace with a proper normalised table or JSON column if categories grow.
 */
@Singleton
class RoomWishlistRepository @Inject constructor(
    private val wishlistDao: WishlistDao,
) : WishlistRepository {

    override fun observeItems(): Flow<List<Book>> =
        wishlistDao.observeAll().map { entities -> entities.map { it.toBook() } }

    override suspend fun add(book: Book) {
        wishlistDao.upsert(book.toEntity())
    }

    override suspend fun remove(bookId: String) {
        wishlistDao.deleteById(bookId)
    }

    override suspend fun isInWishlist(bookId: String): Boolean =
        wishlistDao.countById(bookId) > 0

    // ── Mappers ───────────────────────────────────────────────────────────────

    private fun WishlistItemEntity.toBook() = Book(
        id            = bookId,
        title         = title,
        author        = author,
        coverUrl      = coverUrl,
        price         = price,
        originalPrice = originalPrice,
        rating        = rating,
        ratingCount   = ratingCount,
        format        = format,
        categories    = if (categories.isBlank()) emptyList()
                        else categories.split(","),
        deliveryDate  = "",
        isInWishlist  = true,
    )

    private fun Book.toEntity() = WishlistItemEntity(
        bookId        = id,
        title         = title,
        author        = author,
        coverUrl      = coverUrl,
        price         = price,
        originalPrice = originalPrice,
        rating        = rating,
        ratingCount   = ratingCount,
        format        = format,
        categories    = categories.joinToString(","),
        addedAt       = Instant.now().toEpochMilli(),
    )
}
