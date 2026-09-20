package com.example.ebookstore.ui.screens.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ebookstore.data.mock.MockData
import com.example.ebookstore.domain.model.CountryCatalogue
import com.example.ebookstore.domain.model.CountryData
import com.example.ebookstore.ui.cart.CartItem
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme
import com.example.ebookstore.ui.theme.ThemeMode

// ─────────────────────────────────────────────────────────────────────────────
// Stateful entry point
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CheckoutScreen(
    checkoutViewModel: CheckoutViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPayment: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by checkoutViewModel.uiState.collectAsStateWithLifecycle()

    CheckoutScreenContent(
        address              = uiState.address,
        addressErrors        = uiState.addressErrors,
        items                = uiState.items,
        subtotal             = uiState.subtotal,
        shipping             = uiState.shipping,
        grandTotal           = uiState.grandTotal,
        currencySymbol       = uiState.currencySymbol,
        onFullNameChange     = checkoutViewModel::onFullNameChange,
        onPhoneChange        = checkoutViewModel::onPhoneChange,
        onPinCodeChange      = checkoutViewModel::onPinCodeChange,
        onAddressLine1Change = checkoutViewModel::onAddressLine1Change,
        onAddressLine2Change = checkoutViewModel::onAddressLine2Change,
        onCityChange         = checkoutViewModel::onCityChange,
        onCityAutoFill       = checkoutViewModel::onCityAutoFill,
        onStateChange        = checkoutViewModel::onStateChange,
        onCountryChange      = checkoutViewModel::onCountryChange,
        onContinue           = {
            if (checkoutViewModel.submitAddress()) onNavigateToPayment()
        },
        onNavigateBack       = onNavigateBack,
        modifier             = modifier,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Stateless content composable
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreenContent(
    address: ShippingAddress,
    addressErrors: AddressErrors,
    items: List<CartItem>,
    subtotal: Double,
    shipping: Double,
    grandTotal: Double,
    currencySymbol: String,
    onFullNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPinCodeChange: (String) -> Unit,
    onAddressLine1Change: (String) -> Unit,
    onAddressLine2Change: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onCityAutoFill: (cityName: String, state: String, postalCode: String) -> Unit,
    onStateChange: (String) -> Unit,
    onCountryChange: (CountryData) -> Unit,
    onContinue: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top app bar ────────────────────────────────────────────────
            TopAppBar(
                title = { Text("Checkout", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                    .imePadding()
                    .padding(EBookStoreSpacing.Medium),
            ) {

                StepIndicator(currentStep = 1, totalSteps = 2, label = "Shipping Address")
                Spacer(Modifier.height(EBookStoreSpacing.Large))

                // ── Delivery details ───────────────────────────────────────
                SectionLabel("Delivery Details")
                Spacer(Modifier.height(EBookStoreSpacing.Small))

                // Full name
                OutlinedTextField(
                    value          = address.fullName,
                    onValueChange  = onFullNameChange,
                    modifier       = Modifier.fillMaxWidth(),
                    label          = { Text("Full Name") },
                    isError        = addressErrors.fullName != null,
                    supportingText = addressErrors.fullName?.let { msg -> { Text(msg) } },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction      = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    singleLine = true,
                )

                Spacer(Modifier.height(EBookStoreSpacing.Small))

                // Phone
                OutlinedTextField(
                    value          = address.phone,
                    onValueChange  = onPhoneChange,
                    modifier       = Modifier.fillMaxWidth(),
                    label          = { Text("Phone Number") },
                    isError        = addressErrors.phone != null,
                    supportingText = addressErrors.phone?.let { msg -> { Text(msg) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction    = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    singleLine = true,
                )

                Spacer(Modifier.height(EBookStoreSpacing.Small))

                // Address line 1
                OutlinedTextField(
                    value          = address.addressLine1,
                    onValueChange  = onAddressLine1Change,
                    modifier       = Modifier.fillMaxWidth(),
                    label          = { Text("Address Line 1") },
                    isError        = addressErrors.addressLine1 != null,
                    supportingText = addressErrors.addressLine1?.let { msg -> { Text(msg) } },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction      = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    singleLine = true,
                )

                Spacer(Modifier.height(EBookStoreSpacing.Small))

                // Address line 2
                OutlinedTextField(
                    value          = address.addressLine2,
                    onValueChange  = onAddressLine2Change,
                    modifier       = Modifier.fillMaxWidth(),
                    label          = { Text("Address Line 2 (optional)") },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction      = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    singleLine = true,
                )

                Spacer(Modifier.height(EBookStoreSpacing.Small))

                // ── Country dropdown ───────────────────────────────────────
                CountryDropdown(
                    selected       = address.country,
                    onCountrySelect = onCountryChange,
                    modifier       = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(EBookStoreSpacing.Small))

                // ── City autocomplete + State/Region row ───────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(EBookStoreSpacing.Small),
                ) {
                    CityAutocompleteField(
                        value         = address.city,
                        onValueChange = onCityChange,
                        onCityAutoFill = onCityAutoFill,
                        isError       = addressErrors.city != null,
                        errorMessage  = addressErrors.city,
                        modifier      = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value          = address.state,
                        onValueChange  = onStateChange,
                        modifier       = Modifier.weight(1f),
                        label          = { Text("State / Region") },
                        isError        = addressErrors.state != null,
                        supportingText = addressErrors.state?.let { msg -> { Text(msg) } },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction      = ImeAction.Next,
                        ),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine = true,
                    )
                }

                Spacer(Modifier.height(EBookStoreSpacing.Small))

                // ── Postal code (label from country) ──────────────────────
                OutlinedTextField(
                    value          = address.pinCode,
                    onValueChange  = onPinCodeChange,
                    modifier       = Modifier.fillMaxWidth(),
                    label          = { Text(address.country.postalLabel) },
                    isError        = addressErrors.pinCode != null,
                    supportingText = addressErrors.pinCode?.let { msg -> { Text(msg) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction    = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    singleLine = true,
                )

                Spacer(Modifier.height(EBookStoreSpacing.Large))

                // ── Order summary ──────────────────────────────────────────
                OrderSummaryCard(
                    items          = items,
                    subtotal       = subtotal,
                    shipping       = shipping,
                    grandTotal     = grandTotal,
                    currencySymbol = currencySymbol,
                )

                Spacer(Modifier.height(EBookStoreSpacing.Large))
            }

            // ── Sticky bottom CTA ──────────────────────────────────────────
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Column(modifier = Modifier.padding(EBookStoreSpacing.Medium)) {
                Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                    Text("Continue to Payment")
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Country dropdown
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountryDropdown(
    selected: CountryData,
    onCountrySelect: (CountryData) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded        = expanded,
        onExpandedChange = { expanded = it },
        modifier        = modifier,
    ) {
        OutlinedTextField(
            value         = "${selected.name}  (${selected.currencyCode})",
            onValueChange = {},
            readOnly      = true,
            label         = { Text("Country") },
            trailingIcon  = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier      = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            singleLine    = true,
        )
        ExposedDropdownMenu(
            expanded        = expanded,
            onDismissRequest = { expanded = false },
        ) {
            CountryCatalogue.all.forEach { country ->
                DropdownMenuItem(
                    text    = {
                        Text("${country.name}  (${country.currencyCode} ${country.currencySymbol})")
                    },
                    onClick = {
                        onCountrySelect(country)
                        expanded = false
                    },
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// City autocomplete field
// ─────────────────────────────────────────────────────────────────────────────

/**
 * An [OutlinedTextField] that shows a filtered dropdown of city suggestions as
 * the user types.
 *
 * Selecting a suggestion:
 * - fills the bare city name into the City field
 * - calls [onCityAutoFill] with the matching state and sample postal code so
 *   State/Region and PIN/ZIP are filled automatically
 * - closes the dropdown and does NOT reopen it for that selection (justSelected guard)
 */
@Composable
fun CityAutocompleteField(
    value: String,
    onValueChange: (String) -> Unit,
    onCityAutoFill: (cityName: String, state: String, postalCode: String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    // We own this state completely — no ExposedDropdownMenuBox interference.
    var expanded by remember { mutableStateOf(false) }

    // Recompute suggestions only from what the user typed.
    val suggestions = remember(value) {
        if (value.isBlank()) emptyList()
        else CountryCatalogue.cityInfos
            .filter { it.display.contains(value.trim(), ignoreCase = true) }
            .take(6)
    }

    // Use a plain Box so the DropdownMenu anchors to the text field width,
    // and we have 100% control over open/close without ExposedDropdownMenuBox
    // fighting us.
    Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
        OutlinedTextField(
            value          = value,
            onValueChange  = { newValue ->
                onValueChange(newValue)
                // Open on new non-blank keystroke; close when field is cleared.
                expanded = newValue.isNotBlank() && CountryCatalogue.cityInfos
                    .any { it.display.contains(newValue.trim(), ignoreCase = true) }
            },
            label          = { Text("City") },
            isError        = isError,
            supportingText = errorMessage?.let { msg -> { Text(msg) } },
            trailingIcon   = if (suggestions.isNotEmpty() && expanded) {
                { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) }
            } else null,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction      = ImeAction.Next,
            ),
            singleLine = true,
            modifier   = Modifier.fillMaxWidth(),
        )

        // Plain DropdownMenu — no focus/expand hooks, purely driven by our flag.
        DropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
            offset           = DpOffset(x = EBookStoreSpacing.XXSmall, y = EBookStoreSpacing.XXSmall),
        ) {
            if (suggestions.isEmpty()) {
                expanded = false
            }
            suggestions.forEach { info ->
                DropdownMenuItem(
                    text    = { Text(info.display, style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        expanded = false                               // close first
                        onCityAutoFill(info.cityName, info.state, info.postalCode)
                    },
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared sub-composables (also used by PaymentScreen)
// ─────────────────────────────────────────────────────────────────────────────

/** Numbered step progress indicator. */
@Composable
internal fun StepIndicator(
    currentStep: Int,
    totalSteps: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text  = "Step $currentStep of $totalSteps",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

/** Section label with brand primary color. */
@Composable
internal fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text     = text,
        style    = MaterialTheme.typography.labelLarge,
        color    = MaterialTheme.colorScheme.primary,
        modifier = modifier,
    )
}

/**
 * Order summary card — shows line items, subtotal, shipping, and grand total.
 *
 * [currencySymbol] is passed from [CheckoutUiState.currencySymbol] so prices are
 * always displayed in the currency the user selected.
 */
@Composable
internal fun OrderSummaryCard(
    items: List<CartItem>,
    subtotal: Double,
    shipping: Double,
    grandTotal: Double,
    modifier: Modifier = Modifier,
    currencySymbol: String = "₹",
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape    = MaterialTheme.shapes.medium,
    ) {
        Column(modifier = Modifier.padding(EBookStoreSpacing.Medium)) {
            Text(
                text  = "Order Summary",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(EBookStoreSpacing.Small))

            items.forEach { item ->
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Text(
                        text     = "${item.book.title} × ${item.quantity}",
                        style    = MaterialTheme.typography.bodySmall,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text  = "$currencySymbol${"%.0f".format(item.lineTotal)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(EBookStoreSpacing.XSmall))
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = EBookStoreSpacing.XSmall),
                color    = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            )

            SummaryRow("Subtotal", "$currencySymbol${"%.0f".format(subtotal)}")
            SummaryRow(
                label = "Shipping",
                value = if (shipping == 0.0) "FREE" else "$currencySymbol${"%.0f".format(shipping)}",
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = EBookStoreSpacing.XSmall),
                color    = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            )

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text  = "Total",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text  = "$currencySymbol${"%.0f".format(grandTotal)}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text  = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

private val previewItems = MockData.books.take(2).map { CartItem(it, 1) }

@Preview(name = "Checkout — Light")
@Composable
private fun CheckoutScreenLightPreview() {
    EBookStoreTheme {
        CheckoutScreenContent(
            address              = ShippingAddress(),
            addressErrors        = AddressErrors(),
            items                = previewItems,
            subtotal             = 548.0,
            shipping             = 0.0,
            grandTotal           = 548.0,
            currencySymbol       = "₹",
            onFullNameChange     = {},
            onPhoneChange        = {},
            onPinCodeChange      = {},
            onAddressLine1Change = {},
            onAddressLine2Change = {},
            onCityChange         = {},
            onCityAutoFill       = { _, _, _ -> },
            onStateChange        = {},
            onCountryChange      = {},
            onContinue           = {},
            onNavigateBack       = {},
        )
    }
}

@Preview(name = "Checkout — Dark (US)")
@Composable
private fun CheckoutScreenDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        CheckoutScreenContent(
            address = ShippingAddress(
                fullName     = "Jane Smith",
                phone        = "5551234567",
                pinCode      = "10001",
                addressLine1 = "350 Fifth Ave",
                city         = "New York",
                state        = "NY",
                country      = CountryCatalogue.byCode("US"),
            ),
            addressErrors        = AddressErrors(),
            items                = previewItems,
            subtotal             = 54.80,
            shipping             = 0.0,
            grandTotal           = 54.80,
            currencySymbol       = "$",
            onFullNameChange     = {},
            onPhoneChange        = {},
            onPinCodeChange      = {},
            onAddressLine1Change = {},
            onAddressLine2Change = {},
            onCityChange         = {},
            onCityAutoFill       = { _, _, _ -> },
            onStateChange        = {},
            onCountryChange      = {},
            onContinue           = {},
            onNavigateBack       = {},
        )
    }
}
