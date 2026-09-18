package com.example.ebookstore.ui.screens.cart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme

/**
 * Cart screen — top-level tab destination.
 *
 * Phase 5: Scaffold placeholder.
 * A later phase will implement: cart item list, quantity steppers,
 * price summary, remove action, and checkout CTA.
 *
 * @param modifier Applied to the root container.
 */
@Composable
fun CartScreen(modifier: Modifier = Modifier) {
    Box(
        modifier          = modifier.fillMaxSize(),
        contentAlignment  = Alignment.Center,
    ) {
        Text(
            text      = "Cart\n\nYour cart is empty.\nA future phase will\nbuild this screen.",
            style     = MaterialTheme.typography.titleMedium,
            color     = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(EBookStoreSpacing.Medium),
        )
    }
}

@Preview(name = "Cart Screen — Light")
@Composable
private fun CartScreenLightPreview() {
    EBookStoreTheme {
        CartScreen()
    }
}

@Preview(name = "Cart Screen — Dark")
@Composable
private fun CartScreenDarkPreview() {
    EBookStoreTheme(themeMode = com.example.ebookstore.ui.theme.ThemeMode.DARK) {
        CartScreen()
    }
}
