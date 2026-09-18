package com.example.ebookstore.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
 * Profile screen — top-level tab destination.
 *
 * Phase 5: Scaffold placeholder.
 * A later phase will implement: user avatar, personal details,
 * gift points balance, and the ThemeToggle (System / Light / Dark)
 * that writes to DataStore via ThemeViewModel.
 *
 * @param modifier Applied to the root container.
 */
@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier
            .fillMaxSize()
            .padding(EBookStoreSpacing.Medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text      = "Profile",
            style     = MaterialTheme.typography.titleLarge,
            color     = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(EBookStoreSpacing.Medium))
        Text(
            text      = "Theme toggle, user details, and gift points\nwill appear here in a future phase.",
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(name = "Profile Screen — Light")
@Composable
private fun ProfileScreenLightPreview() {
    EBookStoreTheme {
        ProfileScreen()
    }
}

@Preview(name = "Profile Screen — Dark")
@Composable
private fun ProfileScreenDarkPreview() {
    EBookStoreTheme(themeMode = com.example.ebookstore.ui.theme.ThemeMode.DARK) {
        ProfileScreen()
    }
}
