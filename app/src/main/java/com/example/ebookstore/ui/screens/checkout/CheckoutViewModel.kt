package com.example.ebookstore.ui.screens.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ebookstore.domain.model.CountryCatalogue
import com.example.ebookstore.domain.model.CountryData
import com.example.ebookstore.domain.model.Order
import com.example.ebookstore.domain.model.OrderItem
import com.example.ebookstore.domain.model.OrderStatus
import com.example.ebookstore.domain.repository.OrderRepository
import com.example.ebookstore.ui.cart.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
// Address form data
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Validated, filled shipping address.
 *
 * [country] drives currency display and field validation rules — switching
 * country resets phone/postal errors so the user can re-enter them.
 */
data class ShippingAddress(
    val fullName: String = "",
    val phone: String = "",
    val pinCode: String = "",
    val addressLine1: String = "",
    val addressLine2: String = "",
    val city: String = "",
    val state: String = "",
    val country: CountryData = CountryCatalogue.default,
)

/** Per-field validation errors for the shipping form. */
data class AddressErrors(
    val fullName: String? = null,
    val phone: String? = null,
    val pinCode: String? = null,
    val addressLine1: String? = null,
    val city: String? = null,
    val state: String? = null,
) {
    val hasErrors: Boolean
        get() = listOf(fullName, phone, pinCode, addressLine1, city, state).any { it != null }
}

// ─────────────────────────────────────────────────────────────────────────────
// Payment method
// ─────────────────────────────────────────────────────────────────────────────

enum class PaymentMethod(val label: String) {
    CREDIT_CARD("Credit / Debit Card"),
    UPI("UPI"),
    NET_BANKING("Net Banking"),
    CASH_ON_DELIVERY("Cash on Delivery"),
}

// ─────────────────────────────────────────────────────────────────────────────
// Checkout step
// ─────────────────────────────────────────────────────────────────────────────

enum class CheckoutStep {
    ADDRESS,   // Step 1 — shipping address form
    PAYMENT,   // Step 2 — payment method selection + mock processing
}

// ─────────────────────────────────────────────────────────────────────────────
// Order result
// ─────────────────────────────────────────────────────────────────────────────

sealed class OrderResult {
    object Idle : OrderResult()
    object Processing : OrderResult()
    data class Success(val orderId: String) : OrderResult()
    data class Failure(val reason: String) : OrderResult()
}

// ─────────────────────────────────────────────────────────────────────────────
// UI State
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Complete UI state for the Checkout + Payment flow.
 *
 * [currencySymbol] is derived from [address.country] so every screen that
 * displays a price observes the selected country's currency automatically.
 */
data class CheckoutUiState(
    val step: CheckoutStep = CheckoutStep.ADDRESS,
    val address: ShippingAddress = ShippingAddress(),
    val addressErrors: AddressErrors = AddressErrors(),
    val selectedPayment: PaymentMethod = PaymentMethod.CREDIT_CARD,
    val orderResult: OrderResult = OrderResult.Idle,
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val shipping: Double = 0.0,
) {
    val grandTotal: Double get() = subtotal + shipping
    val isProcessing: Boolean get() = orderResult == OrderResult.Processing

    /** Currency symbol from the selected country — use this to format all prices. */
    val currencySymbol: String get() = address.country.currencySymbol
}

// ─────────────────────────────────────────────────────────────────────────────
// ViewModel
// ─────────────────────────────────────────────────────────────────────────────

/**
 * ViewModel for the two-step Checkout → Payment flow.
 *
 * NavGraph-scoped (created in [EBookStoreNavGraph]) so both screens share state.
 * Cart snapshot is seeded via [seedFromCart] before navigation to Checkout begins.
 *
 * Validation rules (phone digits, postal regex, field labels) are all driven by
 * [ShippingAddress.country] — no India-specific hard-coding.
 */
@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    // ── Cart snapshot ─────────────────────────────────────────────────────────

    fun seedFromCart(items: List<CartItem>, subtotal: Double, shipping: Double) {
        _uiState.update {
            it.copy(
                items       = items,
                subtotal    = subtotal,
                shipping    = shipping,
                step        = CheckoutStep.ADDRESS,
                orderResult = OrderResult.Idle,
            )
        }
    }

    // ── Address form ──────────────────────────────────────────────────────────

    fun onFullNameChange(v: String)     = _uiState.update { it.copy(address = it.address.copy(fullName = v),     addressErrors = it.addressErrors.copy(fullName = null)) }
    fun onPhoneChange(v: String)        = _uiState.update { it.copy(address = it.address.copy(phone = v),        addressErrors = it.addressErrors.copy(phone = null)) }
    fun onPinCodeChange(v: String)      = _uiState.update { it.copy(address = it.address.copy(pinCode = v),      addressErrors = it.addressErrors.copy(pinCode = null)) }
    fun onAddressLine1Change(v: String) = _uiState.update { it.copy(address = it.address.copy(addressLine1 = v), addressErrors = it.addressErrors.copy(addressLine1 = null)) }
    fun onAddressLine2Change(v: String) = _uiState.update { it.copy(address = it.address.copy(addressLine2 = v)) }
    fun onCityChange(v: String)         = _uiState.update { it.copy(address = it.address.copy(city = v),         addressErrors = it.addressErrors.copy(city = null)) }
    fun onStateChange(v: String)        = _uiState.update { it.copy(address = it.address.copy(state = v),        addressErrors = it.addressErrors.copy(state = null)) }

    /**
     * Called when the user selects a city suggestion from the autocomplete dropdown.
     * Simultaneously fills the city name, state, and a representative postal code.
     */
    fun onCityAutoFill(cityName: String, state: String, postalCode: String) {
        _uiState.update {
            it.copy(
                address = it.address.copy(
                    city    = cityName,
                    state   = state,
                    pinCode = postalCode,
                ),
                addressErrors = it.addressErrors.copy(city = null, state = null, pinCode = null),
            )
        }
    }

    /**
     * Switching country clears city/state/postal and pre-fills a sample postal code
     * so the user sees an example of the expected format immediately.
     */
    fun onCountryChange(country: CountryData) {
        val samplePostal = CountryCatalogue.samplePostalByCountry[country.code] ?: ""
        _uiState.update {
            it.copy(
                address = it.address.copy(
                    country = country,
                    city    = "",
                    state   = "",
                    pinCode = samplePostal,
                ),
                addressErrors = it.addressErrors.copy(phone = null, pinCode = null, city = null, state = null),
            )
        }
    }

    /**
     * Validates the form using the rules of the selected country.
     * Returns true if valid and advances to [CheckoutStep.PAYMENT].
     */
    fun submitAddress(): Boolean {
        val a       = _uiState.value.address
        val country = a.country
        val errors = AddressErrors(
            fullName     = if (a.fullName.isBlank()) "Full name is required" else null,
            phone        = when {
                a.phone.isBlank()              -> "Phone number is required"
                !country.isValidPhone(a.phone) ->
                    if (country.phoneDigits > 0)
                        "Enter a valid ${country.phoneDigits}-digit number"
                    else
                        "Enter a valid phone number (6–15 digits)"
                else                           -> null
            },
            pinCode      = when {
                a.pinCode.isBlank()              -> "${country.postalLabel} is required"
                !country.isValidPostal(a.pinCode) -> "Enter a valid ${country.postalLabel}"
                else                              -> null
            },
            addressLine1 = if (a.addressLine1.isBlank()) "Address is required" else null,
            city         = if (a.city.isBlank()) "City is required" else null,
            state        = if (a.state.isBlank()) "State / Region is required" else null,
        )
        _uiState.update { it.copy(addressErrors = errors) }
        if (errors.hasErrors) return false
        _uiState.update { it.copy(step = CheckoutStep.PAYMENT) }
        return true
    }

    fun backToAddress() {
        _uiState.update { it.copy(step = CheckoutStep.ADDRESS) }
    }

    // ── Payment ───────────────────────────────────────────────────────────────

    fun onPaymentMethodSelected(method: PaymentMethod) {
        _uiState.update { it.copy(selectedPayment = method) }
    }

    fun placeOrder() {
        if (_uiState.value.isProcessing) return
        _uiState.update { it.copy(orderResult = OrderResult.Processing) }
        viewModelScope.launch {
            delay(1_500L)

            val snapshot     = _uiState.value
            val addr         = snapshot.address
            val orderId      = "ORD-${System.currentTimeMillis().toString().takeLast(8)}"
            val now          = Instant.now().toEpochMilli()
            val deliveryDate = LocalDate.now().plusDays(5)
                .format(DateTimeFormatter.ofPattern("EEE, d MMM", Locale.ENGLISH))

            val addressLine = buildString {
                append(addr.fullName)
                append(", ")
                append(addr.addressLine1)
                if (addr.addressLine2.isNotBlank()) { append(", "); append(addr.addressLine2) }
                append(", ")
                append(addr.city)
                append(", ")
                append(addr.state)
                append(" ")
                append(addr.pinCode)
                append(", ")
                append(addr.country.name)
            }

            val order = Order(
                id                = orderId,
                placedAt          = now,
                items             = snapshot.items.map { cartItem ->
                    OrderItem(
                        bookId          = cartItem.book.id,
                        title           = cartItem.book.title,
                        author          = cartItem.book.author,
                        coverUrl        = cartItem.book.coverUrl,
                        format          = cartItem.book.format,
                        priceAtPurchase = cartItem.book.price,
                        quantity        = cartItem.quantity,
                    )
                },
                subtotal          = snapshot.subtotal,
                shipping          = snapshot.shipping,
                grandTotal        = snapshot.grandTotal,
                status            = OrderStatus.PROCESSING,
                paymentMethod     = snapshot.selectedPayment.label,
                shippingAddress   = addressLine,
                estimatedDelivery = deliveryDate,
            )
            orderRepository.saveOrder(order)

            _uiState.update { it.copy(orderResult = OrderResult.Success(orderId)) }
        }
    }

    fun retryPayment() {
        _uiState.update { it.copy(orderResult = OrderResult.Idle) }
    }

    fun reset() {
        _uiState.update { CheckoutUiState() }
    }
}
