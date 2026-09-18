package com.example.ebookstore.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme

/**
 * App-wide top app bar for the Home screen.
 *
 * Layout (left → right):
 * - Brand wordmark / title text  (primary orange)
 * - Spacer (fills width)
 * - Account icon
 * - Cart icon with badge
 *
 * Below the bar a [SearchBar] is rendered inline — this keeps the search
 * field within the top area rather than overlapping screen content.
 *
 * Colors: container = [MaterialTheme.colorScheme.surface],
 *         title     = [MaterialTheme.colorScheme.primary] (orange brand).
 *
 * @param searchQuery     Current value of the search field.
 * @param cartItemCount   Cart badge count; hides the badge when 0.
 * @param onSearchQueryChange Called on every keystroke in the search field.
 * @param onSearchSubmit  Called when the user submits (IME action or search icon).
 * @param onCartClick     Called when the cart icon is tapped.
 * @param onAccountClick  Called when the account icon is tapped.
 * @param modifier        Applied to the outer column wrapping the bar + search field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EBookStoreTopAppBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    cartItemCount: Int = 0,
    onCartClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
) {
    val keyboard = LocalSoftwareKeyboardController.current

    androidx.compose.foundation.layout.Column(modifier = modifier) {
        TopAppBar(
            title = {
                Text(
                    text  = "EBookStore",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            },
            actions = {
                // Account icon
                IconButton(onClick = onAccountClick) {
                    Icon(
                        imageVector        = Icons.Filled.AccountCircle,
                        contentDescription = "My account",
                        tint               = MaterialTheme.colorScheme.onSurface,
                    )
                }
                // Cart icon with badge
                IconButton(onClick = onCartClick) {
                    BadgedBox(
                        badge = {
                            if (cartItemCount > 0) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor   = MaterialTheme.colorScheme.onError,
                                ) {
                                    Text(
                                        text  = if (cartItemCount > 99) "99+" else "$cartItemCount",
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector        = Icons.Filled.ShoppingCart,
                            contentDescription = "Cart ($cartItemCount items)",
                            tint               = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor    = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
        )

        // Search field directly below the top bar
        OutlinedTextField(
            value             = searchQuery,
            onValueChange     = onSearchQueryChange,
            placeholder       = { Text("Search for books, authors…") },
            leadingIcon       = {
                Icon(
                    imageVector        = Icons.Filled.Search,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            trailingIcon      = {
                IconButton(onClick = {
                    keyboard?.hide()
                    onSearchSubmit()
                }) {
                    Icon(
                        imageVector        = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint               = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            singleLine        = true,
            keyboardOptions   = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions   = KeyboardActions(onSearch = {
                keyboard?.hide()
                onSearchSubmit()
            }),
            shape             = MaterialTheme.shapes.extraLarge,
            colors            = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor    = MaterialTheme.colorScheme.primary,
                cursorColor          = MaterialTheme.colorScheme.primary,
            ),
            modifier          = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = EBookStoreSpacing.Medium,
                    vertical   = EBookStoreSpacing.Small,
                ),
        )
    }
}

@Preview(name = "Top App Bar — Light, no badge")
@Composable
private fun TopAppBarLightPreview() {
    EBookStoreTheme {
        EBookStoreTopAppBar(
            searchQuery         = "",
            onSearchQueryChange = {},
            onSearchSubmit      = {},
        )
    }
}

@Preview(name = "Top App Bar — Dark, cart badge 3")
@Composable
private fun TopAppBarDarkPreview() {
    EBookStoreTheme(themeMode = com.example.ebookstore.ui.theme.ThemeMode.DARK) {
        EBookStoreTopAppBar(
            searchQuery         = "Minimalism",
            onSearchQueryChange = {},
            onSearchSubmit      = {},
            cartItemCount       = 3,
        )
    }
}
