package com.example.ebookstore.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ebookstore.ui.components.PointsBadge
import com.example.ebookstore.ui.screens.auth.MockUser
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme
import com.example.ebookstore.ui.theme.ThemeMode

// ─────────────────────────────────────────────────────────────────────────────
// ProfileScreen — stateless entry point (state lives in NavGraph-scoped VMs)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Profile screen — top-level tab destination.
 *
 * State is passed down from [EBookStoreNavGraph]; this composable is stateless
 * so it can be safely used in Compose Previews.
 *
 * @param currentUser          The currently logged-in user, or null if not authenticated.
 * @param themeMode            The active [ThemeMode] (passed from Activity-scoped ThemeViewModel).
 * @param onThemeModeChange    Callback to change the theme preference.
 * @param onNavigateToWishlist Navigate to the Wishlist screen.
 * @param onNavigateToLogin    Navigate to the Login screen (when not logged in).
 * @param onLogout             Trigger logout via [AuthViewModel].
 */
@Composable
fun ProfileScreen(
    currentUser: MockUser?,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onNavigateToWishlist: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    pointsBalance: Int = 0,
) {
    if (currentUser == null) {
        ProfileGuestContent(
            onNavigateToLogin = onNavigateToLogin,
            modifier          = modifier,
        )
    } else {
        ProfileLoggedInContent(
            user              = currentUser,
            themeMode         = themeMode,
            onThemeModeChange = onThemeModeChange,
            onNavigateToWishlist = onNavigateToWishlist,
            onLogout          = onLogout,
            pointsBalance     = pointsBalance,
            modifier          = modifier,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Guest state
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileGuestContent(
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier
            .fillMaxSize()
            .padding(EBookStoreSpacing.Medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector        = Icons.Default.Person,
            contentDescription = null,
            modifier           = Modifier.size(72.dp),
            tint               = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
        )
        Spacer(Modifier.height(EBookStoreSpacing.Medium))
        Text(
            text      = "You're not signed in",
            style     = MaterialTheme.typography.titleMedium,
            color     = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(EBookStoreSpacing.Small))
        Text(
            text      = "Sign in to see your profile, wishlist, and order history.",
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(EBookStoreSpacing.Large))
        Button(
            onClick  = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth(0.7f),
        ) {
            Text("Sign In")
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Logged-in state
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileLoggedInContent(
    user: MockUser,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onNavigateToWishlist: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    pointsBalance: Int = 0,
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(EBookStoreSpacing.Medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(Modifier.height(EBookStoreSpacing.Large))

        // ── Avatar ────────────────────────────────────────────────────────
        Box(
            modifier            = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment    = Alignment.Center,
        ) {
            Text(
                text  = user.avatarInitials.take(2),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        Spacer(Modifier.height(EBookStoreSpacing.Medium))

        // ── Name & email ──────────────────────────────────────────────────
        Text(
            text  = user.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(EBookStoreSpacing.XSmall))
        Text(
            text  = user.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        )

        Spacer(Modifier.height(EBookStoreSpacing.Large))

        // ── Gift points card ───────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape    = MaterialTheme.shapes.medium,
            colors   = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            ),
        ) {
            Row(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(EBookStoreSpacing.Medium),
                verticalAlignment   = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text  = "Gift Points",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    Spacer(Modifier.height(EBookStoreSpacing.XXSmall))
                    Text(
                        text  = "Earn 1 pt per ₹10 spent",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(EBookStoreSpacing.XSmall),
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Star,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.tertiary,
                        modifier           = Modifier.size(20.dp),
                    )
                    Text(
                        text       = "$pointsBalance pts",
                        style      = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color      = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
            }
        }

        Spacer(Modifier.height(EBookStoreSpacing.XLarge))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        Spacer(Modifier.height(EBookStoreSpacing.Large))

        // ── Wishlist shortcut ─────────────────────────────────────────────
        ProfileActionRow(
            icon    = Icons.Default.FavoriteBorder,
            label   = "My Wishlist",
            onClick = onNavigateToWishlist,
        )

        Spacer(Modifier.height(EBookStoreSpacing.Large))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        Spacer(Modifier.height(EBookStoreSpacing.Large))

        // ── Theme toggle ──────────────────────────────────────────────────
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text  = "Appearance",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(EBookStoreSpacing.Small))

            val themeModes = ThemeMode.entries.toList()
            val themeLabels = listOf("System", "Light", "Dark")

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                themeModes.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = themeMode == mode,
                        onClick  = { onThemeModeChange(mode) },
                        shape    = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = themeModes.size,
                        ),
                        label = { Text(themeLabels[index]) },
                    )
                }
            }
        }

        Spacer(Modifier.height(EBookStoreSpacing.XLarge))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        Spacer(Modifier.height(EBookStoreSpacing.Large))

        // ── Logout button ─────────────────────────────────────────────────
        OutlinedButton(
            onClick  = { showLogoutDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors   = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error,
            ),
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier           = Modifier.size(18.dp),
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Sign Out")
        }

        Spacer(Modifier.height(EBookStoreSpacing.Large))
    }

    // ── Logout confirmation dialog ─────────────────────────────────────────
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title            = { Text("Sign Out") },
            text             = { Text("Are you sure you want to sign out?") },
            confirmButton    = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Sign Out", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton    = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared row component
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick  = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier            = Modifier.fillMaxWidth(),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Profile — Logged In — Light")
@Composable
private fun ProfileLoggedInLightPreview() {
    EBookStoreTheme {
        ProfileScreen(
            currentUser          = MockUser("Demo User", "demo@example.com"),
            themeMode            = ThemeMode.SYSTEM,
            onThemeModeChange    = {},
            onNavigateToWishlist = {},
            onNavigateToLogin    = {},
            onLogout             = {},
        )
    }
}

@Preview(name = "Profile — Logged In — Dark")
@Composable
private fun ProfileLoggedInDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        ProfileScreen(
            currentUser          = MockUser("Jaswanth P", "jaswanth@example.com"),
            themeMode            = ThemeMode.DARK,
            onThemeModeChange    = {},
            onNavigateToWishlist = {},
            onNavigateToLogin    = {},
            onLogout             = {},
        )
    }
}

@Preview(name = "Profile — Guest — Light")
@Composable
private fun ProfileGuestLightPreview() {
    EBookStoreTheme {
        ProfileScreen(
            currentUser          = null,
            themeMode            = ThemeMode.SYSTEM,
            onThemeModeChange    = {},
            onNavigateToWishlist = {},
            onNavigateToLogin    = {},
            onLogout             = {},
        )
    }
}

@Preview(name = "Profile — Guest — Dark")
@Composable
private fun ProfileGuestDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        ProfileScreen(
            currentUser          = null,
            themeMode            = ThemeMode.DARK,
            onThemeModeChange    = {},
            onNavigateToWishlist = {},
            onNavigateToLogin    = {},
            onLogout             = {},
        )
    }
}
