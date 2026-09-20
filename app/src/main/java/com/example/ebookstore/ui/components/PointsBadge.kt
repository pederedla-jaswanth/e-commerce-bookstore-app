package com.example.ebookstore.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme
import com.example.ebookstore.ui.theme.ThemeMode

/**
 * Compact chip that displays the user's current gift-points balance.
 *
 * Renders as a filled pill using [MaterialTheme.colorScheme.tertiaryContainer]
 * so it is visually distinct from action buttons but always on-brand.
 *
 * @param points      Current points balance to display.
 * @param modifier    Applied to the outer [Surface].
 * @param showLabel   When true, appends " pts" after the number.
 */
@Composable
fun PointsBadge(
    points: Int,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
) {
    Surface(
        modifier  = modifier,
        shape     = MaterialTheme.shapes.extraLarge,
        color     = MaterialTheme.colorScheme.tertiaryContainer,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier            = Modifier.padding(
                horizontal = EBookStoreSpacing.Small,
                vertical   = EBookStoreSpacing.XXSmall,
            ),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EBookStoreSpacing.XXSmall),
        ) {
            Icon(
                imageVector        = Icons.Filled.Star,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.tertiary,
                modifier           = Modifier.size(14.dp),
            )
            Text(
                text  = if (showLabel) "$points pts" else "$points",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "PointsBadge — Light")
@Composable
private fun PointsBadgeLightPreview() {
    EBookStoreTheme {
        PointsBadge(points = 350)
    }
}

@Preview(name = "PointsBadge — Dark")
@Composable
private fun PointsBadgeDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        PointsBadge(points = 1_250)
    }
}
