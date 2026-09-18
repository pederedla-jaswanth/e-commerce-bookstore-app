package com.example.ebookstore.ui.theme

import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════════════════════════════
// EBookStore Spacing Tokens
// ═══════════════════════════════════════════════════════════════════════════════
// Use these constants instead of hardcoded dp values in all composables.
// Derived from layout analysis of the three reference screenshots.
// ═══════════════════════════════════════════════════════════════════════════════
object EBookStoreSpacing {
    val XXSmall  = 2.dp   // Chip internal vertical padding
    val XSmall   = 4.dp   // Between genre chips, small gaps
    val Small    = 8.dp   // Card internal padding, badge margins
    val Medium   = 16.dp  // Section horizontal padding, card grid gap
    val Large    = 24.dp  // Section vertical spacing
    val XLarge   = 32.dp  // Hero banner vertical padding
    val XXLarge  = 48.dp  // Screen top/bottom safe area padding

    // Fixed aspect ratio for all book cover images (width:height = 2:3)
    const val BookCoverAspectRatio = 2f / 3f
}
