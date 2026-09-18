package com.example.ebookstore.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme

/**
 * Section header with an optional "See all" action.
 *
 * - Title uses [MaterialTheme.typography.titleLarge] + [MaterialTheme.colorScheme.onBackground].
 * - "See all" link uses [MaterialTheme.colorScheme.primary] (orange).
 *
 * @param title         Section heading text.
 * @param onSeeAllClick If non-null, renders an orange "See all" button on the right.
 * @param modifier      Applied to the outer Row.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    onSeeAllClick: (() -> Unit)? = null,
) {
    Row(
        modifier            = modifier
            .fillMaxWidth()
            .padding(
                horizontal = EBookStoreSpacing.Medium,
                vertical   = EBookStoreSpacing.XSmall,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(
            text  = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        if (onSeeAllClick != null) {
            TextButton(onClick = onSeeAllClick) {
                Text(
                    text  = "See all",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Preview(name = "Section Header — with See All")
@Composable
private fun SectionHeaderPreview() {
    EBookStoreTheme {
        SectionHeader(title = "New Releases", onSeeAllClick = {})
    }
}

@Preview(name = "Section Header — no See All")
@Composable
private fun SectionHeaderNoActionPreview() {
    EBookStoreTheme {
        SectionHeader(title = "Bestsellers")
    }
}
