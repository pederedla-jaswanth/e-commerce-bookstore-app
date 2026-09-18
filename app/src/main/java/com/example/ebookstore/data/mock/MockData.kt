package com.example.ebookstore.data.mock

import com.example.ebookstore.domain.model.Banner
import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.domain.model.Category

/**
 * Compile-time mock data for Compose Previews and HomeViewModel before a real API is wired.
 *
 * All cover URLs point to picsum.photos which returns deterministic placeholder images.
 * Replace with real API calls in a later phase — no other files need changing.
 */
object MockData {

    // ── Categories ────────────────────────────────────────────────────────────
    val categories = listOf(
        Category("all",       "All"),
        Category("fiction",   "Fiction"),
        Category("non-fiction","Non-Fiction"),
        Category("self-help", "Self Help"),
        Category("science",   "Science"),
        Category("history",   "History"),
        Category("biography", "Biography"),
        Category("children",  "Children"),
        Category("fantasy",   "Fantasy"),
        Category("thriller",  "Thriller"),
        Category("romance",   "Romance"),
        Category("cooking",   "Cooking"),
    )

    // ── Banners (Hero pager) ──────────────────────────────────────────────────
    val banners = listOf(
        Banner(
            id          = "b1",
            title       = "Top Reads to Help You",
            subtitle    = "Make • Manage • Multiply Your Money",
            imageUrl    = "https://picsum.photos/seed/banner1/800/300",
            actionRoute = "catalogue?category=non-fiction",
        ),
        Banner(
            id          = "b2",
            title       = "New in Fiction",
            subtitle    = "Discover the best new novels of the season",
            imageUrl    = "https://picsum.photos/seed/banner2/800/300",
            actionRoute = "catalogue?category=fiction",
        ),
        Banner(
            id          = "b3",
            title       = "Best Sellers This Month",
            subtitle    = "Books everyone is talking about",
            imageUrl    = "https://picsum.photos/seed/banner3/800/300",
            actionRoute = "catalogue",
        ),
    )

    // ── Books ─────────────────────────────────────────────────────────────────
    val books = listOf(
        Book(
            id            = "1",
            title         = "The Joy of Minimalism",
            author        = "Daniel Reed",
            coverUrl      = "https://picsum.photos/seed/book1/120/180",
            price         = 149.0,
            originalPrice = 299.0,
            rating        = 4.5f,
            ratingCount   = 312,
            format        = "Paperback",
            categories    = listOf("Non-Fiction", "Self Help"),
            deliveryDate  = "Mon, 21 Jul",
        ),
        Book(
            id            = "2",
            title         = "The Art of Focus",
            author        = "Arjun Patel",
            coverUrl      = "https://picsum.photos/seed/book2/120/180",
            price         = 399.0,
            originalPrice = null,
            rating        = 4.8f,
            ratingCount   = 521,
            format        = "Paperback",
            categories    = listOf("Non-Fiction", "Self Help"),
            deliveryDate  = "Mon, 21 Jul",
        ),
        Book(
            id            = "3",
            title         = "The Midnight Hour",
            author        = "James Adams",
            coverUrl      = "https://picsum.photos/seed/book3/120/180",
            price         = 299.0,
            originalPrice = 499.0,
            rating        = 4.2f,
            ratingCount   = 198,
            format        = "Paperback",
            categories    = listOf("Fiction", "Thriller"),
            deliveryDate  = "Tue, 22 Jul",
        ),
        Book(
            id            = "4",
            title         = "Beneath the Stars",
            author        = "Jessica Martin",
            coverUrl      = "https://picsum.photos/seed/book4/120/180",
            price         = 499.0,
            originalPrice = null,
            rating        = 4.6f,
            ratingCount   = 403,
            format        = "Hard Cover",
            categories    = listOf("Fiction", "Romance"),
            deliveryDate  = "Mon, 21 Jul",
        ),
        Book(
            id            = "5",
            title         = "Girls That Invest",
            author        = "Simran Kaur",
            coverUrl      = "https://picsum.photos/seed/book5/120/180",
            price         = 259.0,
            originalPrice = 350.0,
            rating        = 4.9f,
            ratingCount   = 876,
            format        = "Paperback",
            categories    = listOf("Non-Fiction", "Finance"),
            deliveryDate  = "Mon, 21 Jul",
        ),
        Book(
            id            = "6",
            title         = "The Final Frontier",
            author        = "Laura Mitchell",
            coverUrl      = "https://picsum.photos/seed/book6/120/180",
            price         = 359.0,
            originalPrice = null,
            rating        = 4.3f,
            ratingCount   = 167,
            format        = "Paperback",
            categories    = listOf("Fiction", "Science Fiction"),
            deliveryDate  = "Wed, 23 Jul",
        ),
        Book(
            id            = "7",
            title         = "The Path to Success",
            author        = "James Wright",
            coverUrl      = "https://picsum.photos/seed/book7/120/180",
            price         = 359.0,
            originalPrice = 499.0,
            rating        = 4.7f,
            ratingCount   = 634,
            format        = "Paperback",
            categories    = listOf("Non-Fiction", "Self Help"),
            deliveryDate  = "Mon, 21 Jul",
        ),
        Book(
            id            = "8",
            title         = "The Art of Learning",
            author        = "Raj Patel",
            coverUrl      = "https://picsum.photos/seed/book8/120/180",
            price         = 259.0,
            originalPrice = null,
            rating        = 4.4f,
            ratingCount   = 289,
            format        = "Paperback",
            categories    = listOf("Non-Fiction", "Self Help"),
            deliveryDate  = "Tue, 22 Jul",
        ),
    )

    // ── Convenience slices used by HomeScreen sections ─────────────────────────
    val newReleases      get() = books.take(4)
    val recommendations  get() = books.drop(2).take(4)
    val bestsellers      get() = books.filter { it.ratingCount > 300 }.take(4)
}
