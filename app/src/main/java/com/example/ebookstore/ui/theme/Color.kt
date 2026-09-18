package com.example.ebookstore.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════════════════
// EBookStore Brand Palette
// ═══════════════════════════════════════════════════════════════════════════════
// All color tokens in this project derive from the constants below.
// UI components must never use raw Color(0xFF...) values — always reference
// a named constant here, or a MaterialTheme.colorScheme token.
// ═══════════════════════════════════════════════════════════════════════════════

// ── Primary: Orange ───────────────────────────────────────────────────────────
val BrandOrange      = Color(0xFFF57C00) // Primary action, CTA buttons
val BrandOrangeLight = Color(0xFFFFAD42) // Primary (dark theme), primaryContainer (light)
val BrandOrangeDark  = Color(0xFFBB4D00) // primaryContainer (dark theme)
val OnPrimaryDark    = Color(0xFF1A0A00) // onPrimary (dark theme), onPrimaryContainer (light)

// ── Secondary: Green ──────────────────────────────────────────────────────────
val BrandGreen       = Color(0xFF1B5E20) // Secondary brand, category chips, nav strip
val BrandGreenLight  = Color(0xFF4C8C4A) // Secondary (dark theme), secondaryContainer (light)
val BrandGreenDark   = Color(0xFF003300) // onSecondary (dark), secondaryContainer backdrop

// ── Custom: Amber (Star Ratings) ─────────────────────────────────────────────
// Not part of the M3 system — accessed via LocalRatingStarColor.current
val BrandAmber       = Color(0xFFFFB300)

// ── Error / Sale Badges: Red ──────────────────────────────────────────────────
val BrandRed         = Color(0xFFD32F2F) // error (light), errorContainer (dark)
val BrandRedLight    = Color(0xFFFF6659) // errorContainer (light), error (dark)
val OnErrorDark      = Color(0xFF1A0000) // onError (dark theme)

// ── Neutral: Light Theme ──────────────────────────────────────────────────────
val NeutralWhite      = Color(0xFFFFFFFF) // surface (light)
val NeutralOffWhite   = Color(0xFFFFF8F0) // background (light, warm brand tint)
val NeutralLightGrey  = Color(0xFFF5F0EB) // surfaceVariant (light)

// ── Neutral: Dark Theme ───────────────────────────────────────────────────────
val NeutralDarkGrey   = Color(0xFF1C1C1E) // surface (dark)
val NeutralDeepDark   = Color(0xFF121212) // background (dark)
val NeutralCardDark   = Color(0xFF1E1E1E) // surfaceVariant (dark)

// ── Neutral: Text ─────────────────────────────────────────────────────────────
val NeutralTextLight  = Color(0xFF212121) // onBackground, onSurface (light)
val NeutralTextDark   = Color(0xFFE0E0E0) // onBackground, onSurface (dark)
val NeutralMidGrey    = Color(0xFF9E9E9E) // onSurfaceVariant (muted text, both themes)
