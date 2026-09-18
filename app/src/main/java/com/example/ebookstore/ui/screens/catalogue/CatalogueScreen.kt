package com.example.ebookstore.ui.screens.catalogue

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
 * Catalogue screen — top-level tab destination.
 *
 * Phase 5: Scaffold placeholder.
 * Phase 7 will implement: search bar, category chip row, filter bar,
 * book list with horizontal/vertical card modes.
 *
 * @param modifier Applied to the root container.
 */
@Composable
fun CatalogueScreen(modifier: Modifier = Modifier) {
    Box(
        modifier          = modifier.fillMaxSize(),
        contentAlignment  = Alignment.Center,
    ) {
        Text(
            text      = "Catalogue\n\nPhase 7 will build the\nfull catalogue screen here.",
            style     = MaterialTheme.typography.titleMedium,
            color     = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(EBookStoreSpacing.Medium),
        )
    }
}

@Preview(name = "Catalogue Screen — Light")
@Composable
private fun CatalogueScreenLightPreview() {
    EBookStoreTheme {
        CatalogueScreen()
    }
}

@Preview(name = "Catalogue Screen — Dark")
@Composable
private fun CatalogueScreenDarkPreview() {
    EBookStoreTheme(themeMode = com.example.ebookstore.ui.theme.ThemeMode.DARK) {
        CatalogueScreen()
    }
}
