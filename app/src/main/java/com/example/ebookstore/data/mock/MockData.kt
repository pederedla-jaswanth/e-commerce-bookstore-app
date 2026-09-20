package com.example.ebookstore.data.mock

import com.example.ebookstore.domain.model.Author
import com.example.ebookstore.domain.model.Banner
import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.domain.model.Category
import com.example.ebookstore.domain.model.Review

/**
 * Compile-time mock data for Compose Previews and ViewModels before a real API is wired.
 *
 * All cover/photo URLs point to picsum.photos which returns deterministic placeholder images.
 * Replace with real API calls in a later phase — no other files need changing.
 */
object MockData {

    // ── Categories ────────────────────────────────────────────────────────────
    val categories = listOf(
        Category("all",        "All"),
        Category("fiction",    "Fiction"),
        Category("non-fiction","Non-Fiction"),
        Category("self-help",  "Self Help"),
        Category("science",    "Science"),
        Category("history",    "History"),
        Category("biography",  "Biography"),
        Category("children",   "Children"),
        Category("fantasy",    "Fantasy"),
        Category("thriller",   "Thriller"),
        Category("romance",    "Romance"),
        Category("cooking",    "Cooking"),
    )

    // ── Authors ───────────────────────────────────────────────────────────────
    val authors = listOf(
        Author(
            id       = "a1",
            name     = "Daniel Reed",
            bio      = "Daniel Reed is a bestselling author and minimalism advocate. His practical approach to decluttering has helped over a million readers simplify their lives and rediscover what matters most.",
            photoUrl = "https://picsum.photos/seed/author1/200/200",
        ),
        Author(
            id       = "a2",
            name     = "Arjun Patel",
            bio      = "Arjun Patel is a productivity coach, speaker, and author. Drawing on neuroscience and mindfulness, he helps high-performers master deep focus and avoid distraction.",
            photoUrl = "https://picsum.photos/seed/author2/200/200",
        ),
        Author(
            id       = "a3",
            name     = "James Adams",
            bio      = "James Adams is a thriller novelist with a background in forensic psychology. His dark, character-driven stories have won multiple literary awards across Europe.",
            photoUrl = "https://picsum.photos/seed/author3/200/200",
        ),
        Author(
            id       = "a4",
            name     = "Jessica Martin",
            bio      = "Jessica Martin writes sweeping romance novels set against dramatic backdrops. Her rich prose and complex characters have earned her a passionate global readership.",
            photoUrl = "https://picsum.photos/seed/author4/200/200",
        ),
        Author(
            id       = "a5",
            name     = "Simran Kaur",
            bio      = "Simran Kaur is a personal finance educator and podcast host. She is on a mission to make investing approachable for young women everywhere.",
            photoUrl = "https://picsum.photos/seed/author5/200/200",
        ),
        Author(
            id       = "a6",
            name     = "Laura Mitchell",
            bio      = "Laura Mitchell is a science journalist and award-winning author. She translates complex astrophysics into stories that ignite curiosity in readers of all ages.",
            photoUrl = "https://picsum.photos/seed/author6/200/200",
        ),
        Author(
            id       = "a7",
            name     = "James Wright",
            bio      = "James Wright is an executive coach who has worked with Fortune 500 CEOs. His philosophy blends stoic wisdom with modern leadership research.",
            photoUrl = "https://picsum.photos/seed/author7/200/200",
        ),
        Author(
            id       = "a8",
            name     = "Raj Patel",
            bio      = "Raj Patel is an educator and learning researcher. He has spent two decades studying how people acquire skills and shares evidence-based techniques for mastering anything.",
            photoUrl = "https://picsum.photos/seed/author8/200/200",
        ),
    )

    /** Quick lookup: author by id. */
    fun authorById(authorId: String): Author? = authors.find { it.id == authorId }

    // ── Reviews ───────────────────────────────────────────────────────────────
    private val reviewsMap: Map<String, List<Review>> = mapOf(
        "1" to listOf(
            Review("r1a", "Priya M.",    4.5f, "Life-changing read. Simple advice that actually sticks.", "10 Jul 2025"),
            Review("r1b", "Vikram S.",   5.0f, "Beautifully written, very practical. Gifted it to three friends.", "05 Jul 2025"),
            Review("r1c", "Ananya R.",   4.0f, "Great concepts but some chapters felt repetitive.", "28 Jun 2025"),
        ),
        "2" to listOf(
            Review("r2a", "Meera T.",    5.0f, "This book genuinely changed how I work. Highly recommend.", "12 Jul 2025"),
            Review("r2b", "Rohan D.",    4.5f, "Dense with useful frameworks. Not a quick read but worth it.", "08 Jul 2025"),
            Review("r2c", "Kavya B.",    4.0f, "Good book, but some examples felt dated.", "01 Jul 2025"),
        ),
        "3" to listOf(
            Review("r3a", "Sanjay K.",   4.0f, "Gripping from the first page. Couldn't put it down.", "15 Jul 2025"),
            Review("r3b", "Nisha P.",    4.5f, "Twists I never saw coming. Brilliant thriller.", "07 Jul 2025"),
        ),
        "4" to listOf(
            Review("r4a", "Lakshmi R.", 5.0f, "Breathtakingly beautiful. Cried at the ending.", "14 Jul 2025"),
            Review("r4b", "Arun M.",    4.5f, "The characters feel so real. Wonderful writing.", "09 Jul 2025"),
            Review("r4c", "Deepa S.",   4.0f, "A bit slow to start, but absolutely worth it.", "03 Jul 2025"),
        ),
        "5" to listOf(
            Review("r5a", "Pooja V.",    5.0f, "Finally a finance book written for people like me!", "13 Jul 2025"),
            Review("r5b", "Shreya N.",   5.0f, "Simran Kaur makes investing feel fun and doable.", "06 Jul 2025"),
            Review("r5c", "Tanya G.",    4.5f, "Wished I had found this book three years ago.", "29 Jun 2025"),
        ),
        "6" to listOf(
            Review("r6a", "Arjun T.",    4.5f, "Makes astrophysics feel personal. Stunning.", "11 Jul 2025"),
            Review("r6b", "Divya K.",    4.0f, "Dense in places, but the writing is extraordinary.", "04 Jul 2025"),
        ),
        "7" to listOf(
            Review("r7a", "Kiran M.",    5.0f, "The best leadership book I have ever read. Period.", "16 Jul 2025"),
            Review("r7b", "Rahul J.",    4.5f, "Practical, inspiring, and grounded in research.", "08 Jul 2025"),
            Review("r7c", "Sunita P.",   4.0f, "Some advice is common sense, but delivered well.", "02 Jul 2025"),
        ),
        "8" to listOf(
            Review("r8a", "Aditya B.",   4.5f, "The chapter on deliberate practice alone is worth it.", "10 Jul 2025"),
            Review("r8b", "Neha R.",     4.0f, "Solid book, more academic than I expected.", "03 Jul 2025"),
        ),
    )

    fun reviewsForBook(bookId: String): List<Review> = reviewsMap[bookId] ?: emptyList()

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
            authorId      = "a1",
            coverUrl      = "https://picsum.photos/seed/book1/120/180",
            price         = 149.0,
            originalPrice = 299.0,
            rating        = 4.5f,
            ratingCount   = 312,
            format        = "Paperback",
            categories    = listOf("Non-Fiction", "Self Help"),
            deliveryDate  = "Mon, 21 Jul",
            description   = "In a world overflowing with stuff, Daniel Reed offers a clear, compassionate guide to living with less. This book walks you through decluttering your home, simplifying your digital life, and finding calm in minimalism — without sacrificing the things you love. Whether you're overwhelmed by clutter or just curious about a simpler life, this book is your starting point.",
            pageCount     = 256,
            publisher     = "Clarity Press",
            publishedDate = "Jan 2024",
            isbn          = "978-1-23456-789-0",
        ),
        Book(
            id            = "2",
            title         = "The Art of Focus",
            author        = "Arjun Patel",
            authorId      = "a2",
            coverUrl      = "https://picsum.photos/seed/book2/120/180",
            price         = 399.0,
            originalPrice = null,
            rating        = 4.8f,
            ratingCount   = 521,
            format        = "Paperback",
            categories    = listOf("Non-Fiction", "Self Help"),
            deliveryDate  = "Mon, 21 Jul",
            description   = "Distraction is the enemy of greatness. Arjun Patel draws on cutting-edge neuroscience and his decade of coaching elite performers to reveal the habits and environments that make deep focus possible. Packed with actionable techniques, this book will help you do your best work — consistently.",
            pageCount     = 304,
            publisher     = "MindShift Books",
            publishedDate = "Mar 2024",
            isbn          = "978-1-23456-790-6",
        ),
        Book(
            id            = "3",
            title         = "The Midnight Hour",
            author        = "James Adams",
            authorId      = "a3",
            coverUrl      = "https://picsum.photos/seed/book3/120/180",
            price         = 299.0,
            originalPrice = 499.0,
            rating        = 4.2f,
            ratingCount   = 198,
            format        = "Paperback",
            categories    = listOf("Fiction", "Thriller"),
            deliveryDate  = "Tue, 22 Jul",
            description   = "When Detective Sara Cole is called to the scene of a seemingly impossible crime, she finds herself pulled into a labyrinth of secrets that go back decades. James Adams's razor-sharp thriller keeps you guessing until the very last page.",
            pageCount     = 368,
            publisher     = "Dark Lane Publishing",
            publishedDate = "Nov 2023",
            isbn          = "978-1-23456-791-3",
        ),
        Book(
            id            = "4",
            title         = "Beneath the Stars",
            author        = "Jessica Martin",
            authorId      = "a4",
            coverUrl      = "https://picsum.photos/seed/book4/120/180",
            price         = 499.0,
            originalPrice = null,
            rating        = 4.6f,
            ratingCount   = 403,
            format        = "Hard Cover",
            categories    = listOf("Fiction", "Romance"),
            deliveryDate  = "Mon, 21 Jul",
            description   = "Two souls, separated by circumstance and bound by an unspoken love. Set against the backdrop of coastal Scotland, Beneath the Stars is a sweeping romance about second chances, family secrets, and the courage it takes to follow your heart.",
            pageCount     = 432,
            publisher     = "Hearthside Fiction",
            publishedDate = "Feb 2024",
            isbn          = "978-1-23456-792-0",
        ),
        Book(
            id            = "5",
            title         = "Girls That Invest",
            author        = "Simran Kaur",
            authorId      = "a5",
            coverUrl      = "https://picsum.photos/seed/book5/120/180",
            price         = 259.0,
            originalPrice = 350.0,
            rating        = 4.9f,
            ratingCount   = 876,
            format        = "Paperback",
            categories    = listOf("Non-Fiction", "Finance"),
            deliveryDate  = "Mon, 21 Jul",
            description   = "Simran Kaur — host of the #1 investing podcast for women — breaks down everything you need to know about growing your wealth. From stock market basics to building a diversified portfolio, this friendly, jargon-free guide proves that investing isn't just for Wall Street.",
            pageCount     = 288,
            publisher     = "Wealth for All",
            publishedDate = "Apr 2024",
            isbn          = "978-1-23456-793-7",
        ),
        Book(
            id            = "6",
            title         = "The Final Frontier",
            author        = "Laura Mitchell",
            authorId      = "a6",
            coverUrl      = "https://picsum.photos/seed/book6/120/180",
            price         = 359.0,
            originalPrice = null,
            rating        = 4.3f,
            ratingCount   = 167,
            format        = "Paperback",
            categories    = listOf("Fiction", "Science Fiction"),
            deliveryDate  = "Wed, 23 Jul",
            description   = "In the year 2187, humanity's last exploration vessel sets course for the edge of the known galaxy. What they discover challenges everything we believe about existence. Laura Mitchell's debut sci-fi epic is a breathtaking meditation on human curiosity, sacrifice, and our place in the cosmos.",
            pageCount     = 512,
            publisher     = "Horizon SciFi",
            publishedDate = "Sep 2023",
            isbn          = "978-1-23456-794-4",
        ),
        Book(
            id            = "7",
            title         = "The Path to Success",
            author        = "James Wright",
            authorId      = "a7",
            coverUrl      = "https://picsum.photos/seed/book7/120/180",
            price         = 359.0,
            originalPrice = 499.0,
            rating        = 4.7f,
            ratingCount   = 634,
            format        = "Paperback",
            categories    = listOf("Non-Fiction", "Self Help"),
            deliveryDate  = "Mon, 21 Jul",
            description   = "What separates extraordinary leaders from the rest? James Wright spent 20 years studying high-performers across industries to find out. The result is a battle-tested framework for decision-making, resilience, and leading teams through uncertainty.",
            pageCount     = 320,
            publisher     = "Executive Edge",
            publishedDate = "Jan 2024",
            isbn          = "978-1-23456-795-1",
        ),
        Book(
            id            = "8",
            title         = "The Art of Learning",
            author        = "Raj Patel",
            authorId      = "a8",
            coverUrl      = "https://picsum.photos/seed/book8/120/180",
            price         = 259.0,
            originalPrice = null,
            rating        = 4.4f,
            ratingCount   = 289,
            format        = "Paperback",
            categories    = listOf("Non-Fiction", "Self Help"),
            deliveryDate  = "Tue, 22 Jul",
            description   = "What do chess prodigies, Olympic athletes, and master musicians have in common? They understand how to learn. Raj Patel's research-backed guide reveals the mental models and deliberate practice techniques that accelerate skill acquisition in any domain.",
            pageCount     = 272,
            publisher     = "Think Deep Press",
            publishedDate = "May 2024",
            isbn          = "978-1-23456-796-8",
        ),
    )

    /** Quick lookup: book by id. */
    fun bookById(bookId: String): Book? = books.find { it.id == bookId }

    /** Related books: same category as [book], excluding itself, up to 4. */
    fun relatedBooks(book: Book): List<Book> =
        books.filter { other ->
            other.id != book.id &&
                other.categories.any { it in book.categories }
        }.take(4)

    // ── Convenience slices used by HomeScreen sections ────────────────────────
    val newReleases      get() = books.take(4)
    val recommendations  get() = books.drop(2).take(4)
    val bestsellers      get() = books.filter { it.ratingCount > 300 }.take(4)
}
