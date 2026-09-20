package com.example.ebookstore.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ebookstore.ui.theme.EBookStoreTheme
import com.example.ebookstore.ui.theme.LocalRatingStarColor

/**
 * Read-only 5-star rating row.
 *
 * Star color uses [LocalRatingStarColor] (amber `#FFB300`) provided by
 * [EBookStoreTheme] — never hardcoded.
 *
 * Renders filled stars for whole values, a half-star for .5, and outlined
 * stars for the remainder.
 *
 * @param rating  Value between 0.0 and 5.0.
 * @param starSize  Dp size of each star icon. Defaults to 16.dp.
 * @param modifier  Applied to the outer [Row].
 */
@Composable
fun StarRating(
    rating: Float,
    modifier: Modifier = Modifier,
    starSize: Dp = 16.dp,
) {
    val starColor = LocalRatingStarColor.current
    val totalStars = 5
    val fullStars = rating.toInt()
    val hasHalf = (rating - fullStars) >= 0.5f

    Row(
        modifier = modifier.semantics {
            contentDescription = "Rating: $rating out of 5"
        },
    ) {
        repeat(totalStars) { index ->
            val icon = when {
                index < fullStars              -> Icons.Filled.Star
                index == fullStars && hasHalf  -> Icons.AutoMirrored.Filled.StarHalf
                else                           -> Icons.Outlined.StarOutline
            }
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = starColor,
                modifier           = Modifier.size(starSize),
            )
        }
    }
}

@Preview(name = "Star Rating 4.5 — Light")
@Composable
private fun StarRatingPreview() {
    EBookStoreTheme { StarRating(rating = 4.5f) }
}

@Preview(name = "Star Rating 3.0 — Dark")
@Composable
private fun StarRatingDarkPreview() {
    EBookStoreTheme(themeMode = com.example.ebookstore.ui.theme.ThemeMode.DARK) {
        StarRating(rating = 3.0f)
    }
}
