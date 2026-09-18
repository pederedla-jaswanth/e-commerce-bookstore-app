package com.example.ebookstore.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ═══════════════════════════════════════════════════════════════════════════════
// EBookStore Typography Scale
// ═══════════════════════════════════════════════════════════════════════════════
// All roles derived from visual inspection of the three reference screens.
// System font (Roboto) is used. To switch to a custom font family, replace
// FontFamily.Default below with a FontFamily loaded via Google Fonts or assets,
// and apply it consistently to all styles.
// ═══════════════════════════════════════════════════════════════════════════════
val Typography = Typography(
    // Hero banner headline — e.g. "MAKE MANAGE MULTIPLY" on Home screen
    displayLarge = TextStyle(
        fontFamily   = FontFamily.Default,
        fontWeight   = FontWeight.ExtraBold,
        fontSize     = 40.sp,
        lineHeight   = 48.sp,
        letterSpacing = 0.sp,
    ),

    // Not used in current screens — provided for completeness
    displayMedium = TextStyle(
        fontFamily   = FontFamily.Default,
        fontWeight   = FontWeight.Bold,
        fontSize     = 32.sp,
        lineHeight   = 40.sp,
        letterSpacing = 0.sp,
    ),

    // Section headings — e.g. "New Releases", "Recommended for You"
    titleLarge = TextStyle(
        fontFamily   = FontFamily.Default,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 22.sp,
        lineHeight   = 28.sp,
        letterSpacing = 0.sp,
    ),

    // Book title in card, app bar wordmark
    titleMedium = TextStyle(
        fontFamily   = FontFamily.Default,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 16.sp,
        lineHeight   = 24.sp,
        letterSpacing = 0.15.sp,
    ),

    // Price display — bold to stand out from surrounding text
    titleSmall = TextStyle(
        fontFamily   = FontFamily.Default,
        fontWeight   = FontWeight.Bold,
        fontSize     = 14.sp,
        lineHeight   = 20.sp,
        letterSpacing = 0.1.sp,
    ),

    // Author name, review body text — regular weight, comfortable reading
    bodyLarge = TextStyle(
        fontFamily   = FontFamily.Default,
        fontWeight   = FontWeight.Normal,
        fontSize     = 16.sp,
        lineHeight   = 24.sp,
        letterSpacing = 0.5.sp,
    ),

    bodyMedium = TextStyle(
        fontFamily   = FontFamily.Default,
        fontWeight   = FontWeight.Normal,
        fontSize     = 14.sp,
        lineHeight   = 20.sp,
        letterSpacing = 0.25.sp,
    ),

    // Delivery estimate, metadata row (Language · Rating · Copies sold)
    bodySmall = TextStyle(
        fontFamily   = FontFamily.Default,
        fontWeight   = FontWeight.Normal,
        fontSize     = 12.sp,
        lineHeight   = 16.sp,
        letterSpacing = 0.4.sp,
    ),

    // Button labels — "Add to Cart", "Add to Wishlist", "Submit"
    labelLarge = TextStyle(
        fontFamily   = FontFamily.Default,
        fontWeight   = FontWeight.Medium,
        fontSize     = 14.sp,
        lineHeight   = 20.sp,
        letterSpacing = 0.1.sp,
    ),

    // Breadcrumb links — "Home / Non-Fiction / Self Help"
    labelMedium = TextStyle(
        fontFamily   = FontFamily.Default,
        fontWeight   = FontWeight.Normal,
        fontSize     = 13.sp,
        lineHeight   = 16.sp,
        letterSpacing = 0.5.sp,
    ),

    // Genre/format chip labels, badge text
    labelSmall = TextStyle(
        fontFamily   = FontFamily.Default,
        fontWeight   = FontWeight.Medium,
        fontSize     = 11.sp,
        lineHeight   = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)
