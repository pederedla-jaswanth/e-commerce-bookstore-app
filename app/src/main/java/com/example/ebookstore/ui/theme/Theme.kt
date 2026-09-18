package com.example.ebookstore.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════════════════════════════
// Custom CompositionLocal: Rating Star Color
// ═══════════════════════════════════════════════════════════════════════════════
// Amber is not part of the M3 color system. Expose it via CompositionLocal so
// StarRating composables never hardcode a color value.
// Usage: val starColor = LocalRatingStarColor.current
val LocalRatingStarColor = staticCompositionLocalOf<Color> {
    error("No LocalRatingStarColor provided — wrap with EBookStoreTheme")
}

// ═══════════════════════════════════════════════════════════════════════════════
// Material 3 Light Color Scheme
// ═══════════════════════════════════════════════════════════════════════════════
private val LightColorScheme = lightColorScheme(
    primary                = BrandOrange,
    onPrimary              = NeutralWhite,
    primaryContainer       = BrandOrangeLight,
    onPrimaryContainer     = OnPrimaryDark,

    secondary              = BrandGreen,
    onSecondary            = NeutralWhite,
    secondaryContainer     = BrandGreenLight,
    onSecondaryContainer   = NeutralWhite,

    tertiary               = BrandGreen,
    onTertiary             = NeutralWhite,
    tertiaryContainer      = BrandGreenLight,
    onTertiaryContainer    = NeutralWhite,

    error                  = BrandRed,
    onError                = NeutralWhite,
    errorContainer         = BrandRedLight,
    onErrorContainer       = OnPrimaryDark,

    background             = NeutralOffWhite,
    onBackground           = NeutralTextLight,

    surface                = NeutralWhite,
    onSurface              = NeutralTextLight,

    surfaceVariant         = NeutralLightGrey,
    onSurfaceVariant       = NeutralMidGrey,

    outline                = NeutralMidGrey,
)

// ═══════════════════════════════════════════════════════════════════════════════
// Material 3 Dark Color Scheme
// ═══════════════════════════════════════════════════════════════════════════════
// Primary and secondary are lightened for sufficient contrast (WCAG AA)
// on dark backgrounds. BrandOrange (#F57C00) on #121212 = ~3.8:1 (insufficient),
// BrandOrangeLight (#FFAD42) on #121212 = ~5.2:1 (passes).
private val DarkColorScheme = darkColorScheme(
    primary                = BrandOrangeLight,
    onPrimary              = OnPrimaryDark,
    primaryContainer       = BrandOrangeDark,
    onPrimaryContainer     = BrandOrangeLight,

    secondary              = BrandGreenLight,
    onSecondary            = BrandGreenDark,
    secondaryContainer     = BrandGreen,
    onSecondaryContainer   = NeutralTextDark,

    tertiary               = BrandGreenLight,
    onTertiary             = BrandGreenDark,
    tertiaryContainer      = BrandGreen,
    onTertiaryContainer    = NeutralTextDark,

    error                  = BrandRedLight,
    onError                = OnErrorDark,
    errorContainer         = BrandRed,
    onErrorContainer       = BrandRedLight,

    background             = NeutralDeepDark,
    onBackground           = NeutralTextDark,

    surface                = NeutralDarkGrey,
    onSurface              = NeutralTextDark,

    surfaceVariant         = NeutralCardDark,
    onSurfaceVariant       = NeutralMidGrey,

    outline                = NeutralMidGrey,
)

// ═══════════════════════════════════════════════════════════════════════════════
// Material 3 Shape System
// ═══════════════════════════════════════════════════════════════════════════════
private val EBookStoreShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // Input fields, small chips
    small      = RoundedCornerShape(8.dp),   // Book cards, filter dropdowns
    medium     = RoundedCornerShape(12.dp),  // Bottom sheets, dialogs
    large      = RoundedCornerShape(16.dp),  // Large cards, modals
    extraLarge = RoundedCornerShape(50.dp),  // Genre chips, category chips (pill)
)

// ═══════════════════════════════════════════════════════════════════════════════
// EBookStoreTheme — entry point composable
// ═══════════════════════════════════════════════════════════════════════════════
/**
 * The single theme composable for the entire EBookStore application.
 *
 * Dynamic colors are permanently disabled to preserve brand identity.
 *
 * @param themeMode One of [ThemeMode.SYSTEM], [ThemeMode.LIGHT], [ThemeMode.DARK].
 *   Defaults to [ThemeMode.SYSTEM] so existing call sites compile without changes.
 *   When the ThemeViewModel is wired in MainActivity, it will pass the user's
 *   saved preference from DataStore.
 * @param content The composable content to render inside the theme.
 */
@Composable
fun EBookStoreTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT  -> false
        ThemeMode.DARK   -> true
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalRatingStarColor provides BrandAmber,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = Typography,
            shapes      = EBookStoreShapes,
            content     = content,
        )
    }
}
