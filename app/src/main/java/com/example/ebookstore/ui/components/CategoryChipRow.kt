package com.example.ebookstore.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.example.ebookstore.data.mock.MockData
import com.example.ebookstore.domain.model.Category
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme

/**
 * Horizontally scrollable single-line row of category filter chips.
 *
 * Selected chip: filled, primary orange ([MaterialTheme.colorScheme.primary]).
 * Unselected chip: outlined, secondary green border.
 *
 * Fully accessible — each chip has a [contentDescription] and [Role.RadioButton] semantics.
 *
 * @param categories       Full list of available categories.
 * @param selectedCategory Name of the currently selected category.
 * @param onCategorySelect Called with the category name when a chip is tapped.
 * @param modifier         Applied to the outer [Row].
 */
@Composable
fun CategoryChipRow(
    categories: List<Category>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = EBookStoreSpacing.Medium),
        horizontalArrangement = Arrangement.spacedBy(EBookStoreSpacing.XSmall),
    ) {
        categories.forEach { category ->
            val isSelected = category.name == selectedCategory
            FilterChip(
                selected = isSelected,
                onClick  = { onCategorySelect(category.name) },
                label    = {
                    Text(
                        text  = category.name,
                        style = MaterialTheme.typography.labelMedium,
                    )
                },
                modifier = Modifier.semantics {
                    contentDescription = if (isSelected)
                        "${category.name}, selected"
                    else
                        category.name
                    role = Role.RadioButton
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor     = MaterialTheme.colorScheme.primary,
                    selectedLabelColor         = MaterialTheme.colorScheme.onPrimary,
                    containerColor             = MaterialTheme.colorScheme.surface,
                    labelColor                 = MaterialTheme.colorScheme.secondary,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled          = true,
                    selected         = isSelected,
                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                    borderColor      = MaterialTheme.colorScheme.secondary,
                ),
            )
        }
    }
}

@Preview(name = "Category Chip Row — Light")
@Composable
private fun CategoryChipRowLightPreview() {
    EBookStoreTheme {
        CategoryChipRow(
            categories       = MockData.categories,
            selectedCategory = "All",
            onCategorySelect = {},
        )
    }
}

@Preview(name = "Category Chip Row — Dark, Self Help selected")
@Composable
private fun CategoryChipRowDarkPreview() {
    EBookStoreTheme(themeMode = com.example.ebookstore.ui.theme.ThemeMode.DARK) {
        CategoryChipRow(
            categories       = MockData.categories,
            selectedCategory = "Self Help",
            onCategorySelect = {},
        )
    }
}
