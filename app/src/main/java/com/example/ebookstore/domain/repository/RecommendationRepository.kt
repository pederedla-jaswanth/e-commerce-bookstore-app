package com.example.ebookstore.domain.repository

import com.example.ebookstore.domain.model.Book

/**
 * Contract for book recommendations.
 *
 * Uses mock data in the current phase — replace implementations
 * with API calls when the backend is wired.
 */
interface RecommendationRepository {

    /**
     * Returns books related to [book] (same categories, excluding itself).
     * Up to [limit] results.
     */
    fun relatedBooks(book: Book, limit: Int = 6): List<Book>

    /**
     * Returns category-based recommendations: books sharing at least one
     * category with any book in [recentlyViewed], de-duplicated and excluding
     * exact matches.  Falls back to trending books when the list is empty.
     */
    fun categoryRecommendations(recentlyViewed: List<Book>, limit: Int = 8): List<Book>

    /**
     * Trending / editorial picks: high-rating, high-review-count books.
     */
    fun trendingBooks(limit: Int = 8): List<Book>
}
