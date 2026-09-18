package com.example.ebookstore.ui.screens.orders

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
 * Orders screen — top-level tab destination.
 *
 * Phase 5: Scaffold placeholder.
 * A later phase will implement: order history list, order status chips,
 * cancellation (within 48 hours), and "Buy Again" action.
 *
 * @param modifier Applied to the root container.
 */
@Composable
fun OrdersScreen(modifier: Modifier = Modifier) {
    Box(
        modifier          = modifier.fillMaxSize(),
        contentAlignment  = Alignment.Center,
    ) {
        Text(
            text      = "Orders\n\nNo orders yet.\nA future phase will\nbuild this screen.",
            style     = MaterialTheme.typography.titleMedium,
            color     = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(EBookStoreSpacing.Medium),
        )
    }
}

@Preview(name = "Orders Screen — Light")
@Composable
private fun OrdersScreenLightPreview() {
    EBookStoreTheme {
        OrdersScreen()
    }
}

@Preview(name = "Orders Screen — Dark")
@Composable
private fun OrdersScreenDarkPreview() {
    EBookStoreTheme(themeMode = com.example.ebookstore.ui.theme.ThemeMode.DARK) {
        OrdersScreen()
    }
}
