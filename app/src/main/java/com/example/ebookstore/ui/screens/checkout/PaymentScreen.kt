package com.example.ebookstore.ui.screens.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ebookstore.data.mock.MockData
import com.example.ebookstore.ui.cart.CartItem
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme
import com.example.ebookstore.ui.theme.ThemeMode

// ─────────────────────────────────────────────────────────────────────────────
// Stateful entry point
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Payment screen — Step 2: Payment method selection + mock order placement.
 *
 * Observes [CheckoutViewModel.uiState] for [OrderResult.Success] / [OrderResult.Failure]
 * and calls the appropriate navigation callback so the NavGraph can react.
 *
 * @param checkoutViewModel NavGraph-scoped; never call hiltViewModel() here.
 * @param onNavigateBack    Go back to the address step.
 * @param onOrderSuccess    Called with orderId when the mock order succeeds.
 */
@Composable
fun PaymentScreen(
    checkoutViewModel: CheckoutViewModel,
    onNavigateBack: () -> Unit,
    onOrderSuccess: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by checkoutViewModel.uiState.collectAsStateWithLifecycle()

    // React to order result changes.
    LaunchedEffect(uiState.orderResult) {
        when (val result = uiState.orderResult) {
            is OrderResult.Success -> onOrderSuccess(result.orderId)
            else                   -> Unit
        }
    }

    PaymentScreenContent(
        selectedPayment  = uiState.selectedPayment,
        items            = uiState.items,
        subtotal         = uiState.subtotal,
        shipping         = uiState.shipping,
        grandTotal       = uiState.grandTotal,
        currencySymbol   = uiState.currencySymbol,
        isProcessing     = uiState.isProcessing,
        onPaymentSelected = checkoutViewModel::onPaymentMethodSelected,
        onPlaceOrder     = checkoutViewModel::placeOrder,
        onNavigateBack   = {
            checkoutViewModel.backToAddress()
            onNavigateBack()
        },
        modifier         = modifier,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Stateless content composable
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreenContent(
    selectedPayment: PaymentMethod,
    items: List<CartItem>,
    subtotal: Double,
    shipping: Double,
    grandTotal: Double,
    isProcessing: Boolean,
    onPaymentSelected: (PaymentMethod) -> Unit,
    onPlaceOrder: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    currencySymbol: String = "₹",
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top app bar ────────────────────────────────────────────────
            TopAppBar(
                title = {
                    Text(
                        text  = "Payment",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, enabled = !isProcessing) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // ── Scrollable body ────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(EBookStoreSpacing.Medium),
            ) {

                StepIndicator(currentStep = 2, totalSteps = 2, label = "Payment Method")

                Spacer(Modifier.height(EBookStoreSpacing.Large))

                SectionLabel("Select Payment Method")
                Spacer(Modifier.height(EBookStoreSpacing.Small))

                PaymentMethod.entries.forEach { method ->
                    PaymentMethodRow(
                        method     = method,
                        icon       = iconForMethod(method),
                        isSelected = selectedPayment == method,
                        enabled    = !isProcessing,
                        onClick    = { onPaymentSelected(method) },
                    )
                    Spacer(Modifier.height(EBookStoreSpacing.XSmall))
                }

                Spacer(Modifier.height(EBookStoreSpacing.Large))

                OrderSummaryCard(
                    items          = items,
                    subtotal       = subtotal,
                    shipping       = shipping,
                    grandTotal     = grandTotal,
                    currencySymbol = currencySymbol,
                )

                Spacer(Modifier.height(EBookStoreSpacing.Large))

                // Mock disclaimer
                Text(
                    text  = "🔒  This is a demo app. No real payment is processed.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )

                Spacer(Modifier.height(EBookStoreSpacing.Large))
            }

            // ── Sticky bottom CTA ──────────────────────────────────────────
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Column(modifier = Modifier.padding(EBookStoreSpacing.Medium)) {
                Button(
                    onClick  = onPlaceOrder,
                    modifier = Modifier.fillMaxWidth(),
                    enabled  = !isProcessing,
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(20.dp),
                            color       = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text("Place Order — $currencySymbol${"%.0f".format(grandTotal)}")
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Payment method row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PaymentMethodRow(
    method: PaymentMethod,
    icon: ImageVector,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (isSelected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

    Card(
        onClick  = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled  = enabled,
        border   = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor,
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            else
                MaterialTheme.colorScheme.surface,
        ),
        shape = MaterialTheme.shapes.small,
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = EBookStoreSpacing.Medium, vertical = EBookStoreSpacing.Small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = if (isSelected) MaterialTheme.colorScheme.primary
                                     else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(24.dp),
            )
            Spacer(Modifier.size(EBookStoreSpacing.Medium))
            Text(
                text     = method.label,
                style    = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                ),
                color    = if (isSelected) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            RadioButton(
                selected = isSelected,
                onClick  = null,        // card click handles selection
                enabled  = enabled,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun iconForMethod(method: PaymentMethod): ImageVector = when (method) {
    PaymentMethod.CREDIT_CARD      -> Icons.Default.CreditCard
    PaymentMethod.UPI              -> Icons.Default.Smartphone
    PaymentMethod.NET_BANKING      -> Icons.Default.AccountBalance
    PaymentMethod.CASH_ON_DELIVERY -> Icons.Default.LocalShipping
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

private val previewItems = MockData.books.take(2).map { CartItem(it, 1) }

@Preview(name = "Payment Screen — Light")
@Composable
private fun PaymentScreenLightPreview() {
    EBookStoreTheme {
        PaymentScreenContent(
            selectedPayment  = PaymentMethod.UPI,
            items            = previewItems,
            subtotal         = 548.0,
            shipping         = 0.0,
            grandTotal       = 548.0,
            isProcessing     = false,
            onPaymentSelected = {},
            onPlaceOrder     = {},
            onNavigateBack   = {},
        )
    }
}

@Preview(name = "Payment Screen — Dark — Processing")
@Composable
private fun PaymentScreenDarkProcessingPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        PaymentScreenContent(
            selectedPayment  = PaymentMethod.CREDIT_CARD,
            items            = previewItems,
            subtotal         = 548.0,
            shipping         = 0.0,
            grandTotal       = 548.0,
            isProcessing     = true,
            onPaymentSelected = {},
            onPlaceOrder     = {},
            onNavigateBack   = {},
        )
    }
}
