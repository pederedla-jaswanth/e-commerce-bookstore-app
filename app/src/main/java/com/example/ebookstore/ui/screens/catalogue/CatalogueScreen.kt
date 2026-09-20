package com.example.ebookstore.ui.screens.catalogue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ebookstore.data.mock.MockData
import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.ui.cart.CartViewModel
import com.example.ebookstore.ui.components.BookCard
import com.example.ebookstore.ui.components.CardLayoutMode
import com.example.ebookstore.ui.components.CategoryChipRow
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme
import com.example.ebookstore.ui.theme.ThemeMode

// ─────────────────────────────────────────────────────────────────────────────
// Entry point (Hilt-wired)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Catalogue screen — top-level tab destination (Phase 7).
 *
 * Layout (top → bottom):
 * 1. Search bar with clear button
 * 2. Category chip row (D4) — horizontal scrollable
 * 3. Sort / filter action bar (result count left, sort dropdown + filter icon right)
 * 4. [LazyColumn] of [BookCard] in HORIZONTAL mode
 *
 * States handled: loading spinner, empty results, error message.
 *
 * Filter sheet: bottom sheet with Format chips, On-Sale toggle, Price range,
 * and a "Clear all" action. Opened via the filter icon button.
 *
 * @param onNavigateToBook  Called with bookId when a card is tapped.
 * @param onNavigateToCart  Called when cart icon (future top bar) is tapped.
 * @param catalogueViewModel  Hilt-injected; owns all filter + sort state.
 * @param cartViewModel       Shared NavGraph-level ViewModel.
 * @param modifier           Applied to the root Surface.
 */
@Composable
fun CatalogueScreen(
    cartViewModel: CartViewModel,
    modifier: Modifier = Modifier,
    onNavigateToBook: (String) -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    catalogueViewModel: CatalogueViewModel = hiltViewModel(),
) {
    val uiState   by catalogueViewModel.uiState.collectAsStateWithLifecycle()
    val cartState by cartViewModel.uiState.collectAsStateWithLifecycle()

    CatalogueScreenContent(
        uiState             = uiState,
        cartItemCount       = cartState.itemCount,
        onSearchQueryChange = catalogueViewModel::onSearchQueryChange,
        onSearchSubmit      = catalogueViewModel::onSearchSubmit,
        onCategorySelect    = catalogueViewModel::onCategorySelected,
        onSortSelected      = catalogueViewModel::onSortSelected,
        onFormatSelected    = catalogueViewModel::onFormatSelected,
        onSaleOnlyToggled   = catalogueViewModel::onSaleOnlyToggled,
        onClearFilters      = catalogueViewModel::onClearFilters,
        onFilterSheetToggle = catalogueViewModel::onFilterSheetToggle,
        onFilterSheetDismiss = catalogueViewModel::onFilterSheetDismiss,
        onNavigateToBook    = onNavigateToBook,
        onAddToCart         = cartViewModel::addToCart,
        modifier            = modifier,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Stateless content layer (preview-friendly)
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogueScreenContent(
    uiState: CatalogueUiState,
    cartItemCount: Int,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onCategorySelect: (String) -> Unit,
    onSortSelected: (SortOption) -> Unit,
    onFormatSelected: (String?) -> Unit,
    onSaleOnlyToggled: () -> Unit,
    onClearFilters: () -> Unit,
    onFilterSheetToggle: () -> Unit,
    onFilterSheetDismiss: () -> Unit,
    onNavigateToBook: (String) -> Unit,
    onAddToCart: (Book) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Surface(
        modifier = modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── 1. Search bar ─────────────────────────────────────────────────
            CatalogueSearchBar(
                query          = uiState.searchQuery,
                onQueryChange  = onSearchQueryChange,
                onSearchSubmit = onSearchSubmit,
                modifier       = Modifier.padding(
                    horizontal = EBookStoreSpacing.Medium,
                    vertical   = EBookStoreSpacing.Small,
                ),
            )

            // ── 2. Category chips ─────────────────────────────────────────────
            CategoryChipRow(
                categories       = uiState.categories,
                selectedCategory = uiState.selectedCategory,
                onCategorySelect = onCategorySelect,
                modifier         = Modifier.padding(bottom = EBookStoreSpacing.XSmall),
            )

            // ── 3. Sort / filter action bar ───────────────────────────────────
            SortFilterBar(
                resultCount         = uiState.resultCount,
                selectedSort        = uiState.filters.sortBy,
                hasActiveFilters    = uiState.filters != FilterState().copy(sortBy = uiState.filters.sortBy)
                                   || uiState.filters.onSaleOnly,
                onSortSelected      = onSortSelected,
                onFilterSheetToggle = onFilterSheetToggle,
                onClearFilters      = onClearFilters,
                modifier            = Modifier.padding(
                    horizontal = EBookStoreSpacing.Medium,
                    vertical   = EBookStoreSpacing.XSmall,
                ),
            )

            // ── 4. Book list / loading / empty / error ────────────────────────
            Box(
                modifier         = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            color    = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(top = EBookStoreSpacing.XLarge)
                                .size(48.dp),
                        )
                    }

                    uiState.error != null -> {
                        ErrorState(
                            message       = uiState.error,
                            onClearFilters = onClearFilters,
                        )
                    }

                    uiState.displayedBooks.isEmpty() -> {
                        EmptyState(onClearFilters = onClearFilters)
                    }

                    else -> {
                        BookList(
                            books          = uiState.displayedBooks,
                            onCardClick    = onNavigateToBook,
                            onAddToCart    = onAddToCart,
                        )
                    }
                }
            }
        }
    }

    // ── Filter bottom sheet ───────────────────────────────────────────────────
    if (uiState.isFilterSheetOpen) {
        ModalBottomSheet(
            onDismissRequest  = onFilterSheetDismiss,
            sheetState        = sheetState,
            containerColor    = MaterialTheme.colorScheme.surface,
        ) {
            FilterSheetContent(
                filters           = uiState.filters,
                onFormatSelected  = onFormatSelected,
                onSaleOnlyToggled = onSaleOnlyToggled,
                onClearFilters    = {
                    onClearFilters()
                    onFilterSheetDismiss()
                },
                onDone            = onFilterSheetDismiss,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-composables
// ─────────────────────────────────────────────────────────────────────────────

/** Full-width search field with leading search icon and trailing clear button. */
@Composable
private fun CatalogueSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboard = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value             = query,
        onValueChange     = onQueryChange,
        placeholder       = { Text("Search books, authors…") },
        leadingIcon       = {
            Icon(
                imageVector        = Icons.Filled.Search,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector        = Icons.Filled.Close,
                        contentDescription = "Clear search",
                        tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        singleLine      = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            keyboard?.hide()
            onSearchSubmit()
        }),
        shape  = MaterialTheme.shapes.extraLarge,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            cursorColor          = MaterialTheme.colorScheme.primary,
        ),
        modifier = modifier.fillMaxWidth(),
    )
}

/** Result count on the left, sort dropdown + filter icon on the right. */
@Composable
private fun SortFilterBar(
    resultCount: Int,
    selectedSort: SortOption,
    hasActiveFilters: Boolean,
    onSortSelected: (SortOption) -> Unit,
    onFilterSheetToggle: () -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier              = modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Result count label
        Text(
            text  = "$resultCount result${if (resultCount != 1) "s" else ""}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Sort dropdown — Box with wrapContentSize anchors the menu to the button
            Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                TextButton(
                    onClick = { sortMenuExpanded = !sortMenuExpanded },
                ) {
                    Text(
                        text  = "Sort: ${selectedSort.label}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Icon(
                        imageVector        = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.primary,
                    )
                }
                DropdownMenu(
                    expanded         = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false },
                    offset           = DpOffset(x = EBookStoreSpacing.Small, y = EBookStoreSpacing.XXSmall),
                ) {
                    SortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text  = option.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (option == selectedSort)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (option == selectedSort) FontWeight.SemiBold else null,
                                )
                            },
                            onClick = {
                                onSortSelected(option)
                                sortMenuExpanded = false
                            },
                        )
                    }
                }
            }

            // Filter icon — tinted orange when filters are active
            IconButton(onClick = onFilterSheetToggle) {
                Icon(
                    imageVector        = Icons.Filled.FilterList,
                    contentDescription = "Filters${if (hasActiveFilters) " (active)" else ""}",
                    tint               = if (hasActiveFilters)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** LazyColumn list of books in HORIZONTAL card mode. */
@Composable
private fun BookList(
    books: List<Book>,
    onCardClick: (String) -> Unit,
    onAddToCart: (Book) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier        = modifier.fillMaxSize(),
        contentPadding  = PaddingValues(
            horizontal = EBookStoreSpacing.Medium,
            vertical   = EBookStoreSpacing.Small,
        ),
        verticalArrangement = Arrangement.spacedBy(EBookStoreSpacing.Small),
    ) {
        items(items = books, key = { it.id }) { book ->
            BookCard(
                book            = book,
                onCardClick     = onCardClick,
                onWishlistClick = { onAddToCart(book) },
                layoutMode      = CardLayoutMode.HORIZONTAL,
                modifier        = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** Centered empty state shown when no books match the current filters. */
@Composable
private fun EmptyState(
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier.padding(EBookStoreSpacing.XLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EBookStoreSpacing.Medium),
    ) {
        Icon(
            imageVector        = Icons.Outlined.SentimentDissatisfied,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier           = Modifier.size(64.dp),
        )
        Text(
            text      = "No books found",
            style     = MaterialTheme.typography.titleMedium,
            color     = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text      = "Try adjusting your search or filters.",
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        TextButton(onClick = onClearFilters) {
            Text(
                text  = "Clear all filters",
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

/** Centered error state. */
@Composable
private fun ErrorState(
    message: String,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier.padding(EBookStoreSpacing.XLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EBookStoreSpacing.Medium),
    ) {
        Text(
            text      = "Something went wrong",
            style     = MaterialTheme.typography.titleMedium,
            color     = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        Text(
            text      = message,
            style     = MaterialTheme.typography.bodySmall,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        TextButton(onClick = onClearFilters) {
            Text(
                text  = "Retry",
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Filter bottom sheet content
// ─────────────────────────────────────────────────────────────────────────────

private val BOOK_FORMATS = listOf("Paperback", "eBook", "Hard Cover")

/**
 * Content rendered inside the filter [ModalBottomSheet].
 *
 * Sections:
 * - Format: row of [FilterChip]s (Paperback / eBook / Hard Cover)
 * - On Sale Only: a labeled [Switch]
 * - Footer: "Clear all" + "Done" buttons
 */
@Composable
private fun FilterSheetContent(
    filters: FilterState,
    onFormatSelected: (String?) -> Unit,
    onSaleOnlyToggled: () -> Unit,
    onClearFilters: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = EBookStoreSpacing.Medium)
            .padding(bottom = EBookStoreSpacing.XLarge),
        verticalArrangement = Arrangement.spacedBy(EBookStoreSpacing.Medium),
    ) {

        // Sheet handle label
        Text(
            text  = "Filters",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )

        // ── Format ────────────────────────────────────────────────────────────
        Text(
            text  = "Format",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(EBookStoreSpacing.XSmall)) {
            BOOK_FORMATS.forEach { fmt ->
                val isSelected = filters.format == fmt
                FilterChip(
                    selected = isSelected,
                    onClick  = { onFormatSelected(if (isSelected) null else fmt) },
                    label    = {
                        Text(
                            text  = fmt,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor     = MaterialTheme.colorScheme.onPrimary,
                        containerColor         = MaterialTheme.colorScheme.surface,
                        labelColor             = MaterialTheme.colorScheme.secondary,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled             = true,
                        selected            = isSelected,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        borderColor         = MaterialTheme.colorScheme.secondary,
                    ),
                )
            }
        }

        // ── On Sale Only ──────────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text  = "On sale only",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Switch(
                checked         = filters.onSaleOnly,
                onCheckedChange = { onSaleOnlyToggled() },
                colors          = SwitchDefaults.colors(
                    checkedThumbColor  = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor  = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            )
        }

        Spacer(Modifier.height(EBookStoreSpacing.Small))

        // ── Footer buttons ────────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(EBookStoreSpacing.Small),
        ) {
            OutlinedButton(
                onClick  = onClearFilters,
                modifier = Modifier.weight(1f),
                colors   = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text("Clear all")
            }
            androidx.compose.material3.Button(
                onClick  = onDone,
                modifier = Modifier.weight(1f),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor   = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text("Done")
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews — stateless layer only (no Hilt)
// ─────────────────────────────────────────────────────────────────────────────

private val previewCatalogueUiState = CatalogueUiState(
    allBooks         = MockData.books,
    displayedBooks   = MockData.books,
    categories       = MockData.categories,
    selectedCategory = "All",
    searchQuery      = "",
    filters          = FilterState(),
    isLoading        = false,
)

@Preview(name = "Catalogue — Light", showBackground = true)
@Composable
private fun CatalogueScreenLightPreview() {
    EBookStoreTheme {
        CatalogueScreenContent(
            uiState             = previewCatalogueUiState,
            cartItemCount       = 0,
            onSearchQueryChange = {},
            onSearchSubmit      = {},
            onCategorySelect    = {},
            onSortSelected      = {},
            onFormatSelected    = {},
            onSaleOnlyToggled   = {},
            onClearFilters      = {},
            onFilterSheetToggle = {},
            onFilterSheetDismiss = {},
            onNavigateToBook    = {},
            onAddToCart         = {},
        )
    }
}

@Preview(name = "Catalogue — Dark", showBackground = true)
@Composable
private fun CatalogueScreenDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        CatalogueScreenContent(
            uiState             = previewCatalogueUiState,
            cartItemCount       = 2,
            onSearchQueryChange = {},
            onSearchSubmit      = {},
            onCategorySelect    = {},
            onSortSelected      = {},
            onFormatSelected    = {},
            onSaleOnlyToggled   = {},
            onClearFilters      = {},
            onFilterSheetToggle = {},
            onFilterSheetDismiss = {},
            onNavigateToBook    = {},
            onAddToCart         = {},
        )
    }
}

@Preview(name = "Catalogue — Empty State", showBackground = true)
@Composable
private fun CatalogueEmptyPreview() {
    EBookStoreTheme {
        CatalogueScreenContent(
            uiState             = previewCatalogueUiState.copy(displayedBooks = emptyList()),
            cartItemCount       = 0,
            onSearchQueryChange = {},
            onSearchSubmit      = {},
            onCategorySelect    = {},
            onSortSelected      = {},
            onFormatSelected    = {},
            onSaleOnlyToggled   = {},
            onClearFilters      = {},
            onFilterSheetToggle = {},
            onFilterSheetDismiss = {},
            onNavigateToBook    = {},
            onAddToCart         = {},
        )
    }
}

@Preview(name = "Catalogue — Filter Sheet", showBackground = true)
@Composable
private fun CatalogueFilterSheetPreview() {
    EBookStoreTheme {
        FilterSheetContent(
            filters           = FilterState(format = "Paperback", onSaleOnly = true),
            onFormatSelected  = {},
            onSaleOnlyToggled = {},
            onClearFilters    = {},
            onDone            = {},
        )
    }
}
