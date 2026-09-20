package com.example.ebookstore.data.mock

import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.domain.repository.RecommendationRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock [RecommendationRepository] backed by [MockData].
 *
 * All recommendation logic is category-driven and rating-sorted.
 * No hardcoded book IDs — the algorithms operate on the [Book.categories]
 * and [Book.rating] / [Book.ratingCount] fields.
 *
 * Replace with an API-backed implementation when a backend is available;
 * no other files need changing.
 */
@Singleton
class MockRecommendationRepository @Inject constructor() : RecommendationRepository {

    private val allBooks: List<Book> get() = MockData.books

    override fun relatedBooks(book: Book, limit: Int): List<Book> =
        allBooks
            .filter { other ->
                other.id != book.id &&
                    other.categories.any { it in book.categories }
            }
            .sortedByDescending { it.rating }
            .take(limit)

    override fun categoryRecommendations(recentlyViewed: List<Book>, limit: Int): List<Book> {
        if (recentlyViewed.isEmpty()) return trendingBooks(limit)

        // Collect all categories from recently-viewed books (deduplicated, order-preserved)
        val interestCategories = recentlyViewed
            .flatMap { it.categories }
            .distinct()

        val excludeIds = recentlyViewed.map { it.id }.toSet()

        return allBooks
            .filter { book ->
                book.id !in excludeIds &&
                    book.categories.any { it in interestCategories }
            }
            .sortedByDescending { it.rating * it.ratingCount }   // popularity score
            .take(limit)
    }

    override fun trendingBooks(limit: Int): List<Book> =
        allBooks
            .sortedByDescending { it.rating * it.ratingCount }
            .take(limit)
}
