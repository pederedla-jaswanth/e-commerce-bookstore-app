package com.example.ebookstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme

/**
 * Red "Sale" pill badge overlaid on the top-left corner of a book cover.
 *
 * Colors are entirely token-based:
 * - Background: [MaterialTheme.colorScheme.error]  (#D32F2F light / #FF6659 dark)
 * - Text:       [MaterialTheme.colorScheme.onError]
 *
 * @param label   Badge text — defaults to "Sale".
 * @param modifier Applied to the badge container.
 */
@Composable
fun SaleBadge(
    modifier: Modifier = Modifier,
    label: String = "Sale",
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.error,
                shape = MaterialTheme.shapes.extraLarge,
            )
            .padding(
                horizontal = EBookStoreSpacing.Small,
                vertical   = EBookStoreSpacing.XXSmall,
            ),
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onError,
        )
    }
}

@Preview(name = "Sale Badge — Light")
@Composable
private fun SaleBadgeLightPreview() {
    EBookStoreTheme { SaleBadge() }
}

@Preview(name = "Sale Badge — Dark")
@Composable
private fun SaleBadgeDarkPreview() {
    EBookStoreTheme(themeMode = com.example.ebookstore.ui.theme.ThemeMode.DARK) {
        SaleBadge()
    }
}
