package com.example.ebookstore.ui.screens.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ebookstore.data.mock.MockData
import com.example.ebookstore.ui.cart.CartItem
import com.example.ebookstore.ui.components.PointsBadge
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme
import com.example.ebookstore.ui.theme.ThemeMode

// ─────────────────────────────────────────────────────────────────────────────
// Order Confirmation Screen (stateless — all state passed from NavGraph)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Order confirmation screen — shows success or failure after mock payment.
 *
 * Stateless: the NavGraph passes [orderId] (non-null = success, null = failure)
 * directly from the nav argument, so this screen is fully preview-safe.
 *
 * On success: shows order ID, order summary card, and a "Continue Shopping" button.
 * On failure: shows error message with "Try Again" and "Continue Shopping" buttons.
 *
 * @param orderId          Non-null order ID on success; null on failure.
 * @param items            Cart snapshot for the order summary (empty on failure path).
 * @param subtotal         Order subtotal.
 * @param shipping         Shipping cost.
 * @param grandTotal       Order grand total.
 * @param onContinueShopping Navigate back to the Home or Catalogue screen.
 * @param onRetryPayment   Navigate back to the Payment screen to retry.
 */
@Composable
fun OrderConfirmationScreen(
    orderId: String?,
    items: List<CartItem>,
    subtotal: Double,
    shipping: Double,
    grandTotal: Double,
    onContinueShopping: () -> Unit,
    onRetryPayment: () -> Unit,
    modifier: Modifier = Modifier,
    pointsEarned: Int = 0,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.background,
    ) {
        if (orderId != null) {
            OrderSuccessContent(
                orderId            = orderId,
                items              = items,
                subtotal           = subtotal,
                shipping           = shipping,
                grandTotal         = grandTotal,
                pointsEarned       = pointsEarned,
                onContinueShopping = onContinueShopping,
            )
        } else {
            OrderFailureContent(
                onRetryPayment     = onRetryPayment,
                onContinueShopping = onContinueShopping,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Success content
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun OrderSuccessContent(
    orderId: String,
    items: List<CartItem>,
    subtotal: Double,
    shipping: Double,
    grandTotal: Double,
    onContinueShopping: () -> Unit,
    modifier: Modifier = Modifier,
    pointsEarned: Int = 0,
) {
    Column(
        modifier            = modifier
            .fillMaxSize()
            .padding(EBookStoreSpacing.Medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        // ── Success icon ───────────────────────────────────────────────────
        Icon(
            imageVector        = Icons.Default.CheckCircle,
            contentDescription = "Order placed",
            tint               = MaterialTheme.colorScheme.secondary,
            modifier           = Modifier.size(80.dp),
        )

        Spacer(Modifier.height(EBookStoreSpacing.Medium))

        Text(
            text      = "Order Placed!",
            style     = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color     = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(EBookStoreSpacing.XSmall))

        Text(
            text      = "Thank you for your purchase.",
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(EBookStoreSpacing.Small))

        // ── Order ID chip ──────────────────────────────────────────────────
        Text(
            text      = "Order ID: $orderId",
            style     = MaterialTheme.typography.labelMedium,
            color     = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )

        // ── Points earned badge ────────────────────────────────────────────
        if (pointsEarned > 0) {
            Spacer(Modifier.height(EBookStoreSpacing.Small))
            Text(
                text      = "You earned",
                style     = MaterialTheme.typography.bodySmall,
                color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(EBookStoreSpacing.XXSmall))
            PointsBadge(points = pointsEarned)
        }

        Spacer(Modifier.height(EBookStoreSpacing.Large))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(Modifier.height(EBookStoreSpacing.Large))

        // ── Order summary ──────────────────────────────────────────────────
        OrderSummaryCard(
            items      = items,
            subtotal   = subtotal,
            shipping   = shipping,
            grandTotal = grandTotal,
        )

        Spacer(Modifier.height(EBookStoreSpacing.XLarge))

        Button(
            onClick  = onContinueShopping,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Continue Shopping")
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Failure content
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun OrderFailureContent(
    onRetryPayment: () -> Unit,
    onContinueShopping: () -> Unit,
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
            imageVector        = Icons.Default.ErrorOutline,
            contentDescription = "Payment failed",
            tint               = MaterialTheme.colorScheme.error,
            modifier           = Modifier.size(80.dp),
        )

        Spacer(Modifier.height(EBookStoreSpacing.Medium))

        Text(
            text      = "Payment Failed",
            style     = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color     = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(EBookStoreSpacing.XSmall))

        Text(
            text      = "Something went wrong while processing your payment.\nPlease try again.",
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(EBookStoreSpacing.XLarge))

        Button(
            onClick  = onRetryPayment,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Try Again")
        }

        Spacer(Modifier.height(EBookStoreSpacing.Small))

        OutlinedButton(
            onClick  = onContinueShopping,
            modifier = Modifier.fillMaxWidth(),
            colors   = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onBackground,
            ),
        ) {
            Text("Continue Shopping")
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

private val previewItems = MockData.books.take(2).map { CartItem(it, 1) }

@Preview(name = "Order Confirmation — Success — Light")
@Composable
private fun OrderSuccessLightPreview() {
    EBookStoreTheme {
        OrderConfirmationScreen(
            orderId            = "ORD-12345678",
            items              = previewItems,
            subtotal           = 548.0,
            shipping           = 0.0,
            grandTotal         = 548.0,
            onContinueShopping = {},
            onRetryPayment     = {},
        )
    }
}

@Preview(name = "Order Confirmation — Success — Dark")
@Composable
private fun OrderSuccessDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        OrderConfirmationScreen(
            orderId            = "ORD-87654321",
            items              = previewItems,
            subtotal           = 548.0,
            shipping           = 0.0,
            grandTotal         = 548.0,
            onContinueShopping = {},
            onRetryPayment     = {},
        )
    }
}

@Preview(name = "Order Confirmation — Failure — Light")
@Composable
private fun OrderFailureLightPreview() {
    EBookStoreTheme {
        OrderConfirmationScreen(
            orderId            = null,
            items              = emptyList(),
            subtotal           = 0.0,
            shipping           = 0.0,
            grandTotal         = 0.0,
            onContinueShopping = {},
            onRetryPayment     = {},
        )
    }
}

@Preview(name = "Order Confirmation — Failure — Dark")
@Composable
private fun OrderFailureDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        OrderConfirmationScreen(
            orderId            = null,
            items              = emptyList(),
            subtotal           = 0.0,
            shipping           = 0.0,
            grandTotal         = 0.0,
            onContinueShopping = {},
            onRetryPayment     = {},
        )
    }
}
