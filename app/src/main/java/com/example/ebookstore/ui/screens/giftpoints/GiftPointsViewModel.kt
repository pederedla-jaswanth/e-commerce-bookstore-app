package com.example.ebookstore.ui.screens.giftpoints

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ebookstore.domain.model.GiftPointsConfig
import com.example.ebookstore.domain.model.PointsEntry
import com.example.ebookstore.domain.repository.GiftPointsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
// UI State
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Everything the UI needs to render the gift-points widget or full history page.
 *
 * @param balance   Current points balance (live from Room).
 * @param history   Recent ledger entries, newest-first, up to 20.
 * @param config    Rule set — used to display rupee value of points in the UI.
 */
data class GiftPointsUiState(
    val balance: Int = 0,
    val history: List<PointsEntry> = emptyList(),
    val config: GiftPointsConfig = GiftPointsConfig(),
)

// ─────────────────────────────────────────────────────────────────────────────
// ViewModel
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Shared ViewModel for the gift-points feature.
 *
 * NavGraph-scoped so the balance shown in [ProfileScreen], [OrderConfirmationScreen],
 * and any future points-history screen all observe the same live state.
 *
 * [config] is injected so business rules are never hardcoded in screens or VMs.
 *
 * ### lastAwardedPoints
 * A separate [StateFlow] tracks the points earned in the most recent [awardOrderPoints]
 * call.  Using a dedicated StateFlow (rather than a plain `var`) ensures:
 * - The write from the IO coroutine is visible to the Main thread without races.
 * - The confirmation screen Composable recomposes automatically when the value changes.
 */
@HiltViewModel
class GiftPointsViewModel @Inject constructor(
    private val repository: GiftPointsRepository,
    val config: GiftPointsConfig,
) : ViewModel() {

    val uiState: StateFlow<GiftPointsUiState> =
        combine(
            repository.observeBalance(),
            repository.observeHistory(),
        ) { balance, history ->
            GiftPointsUiState(
                balance = balance,
                history = history,
                config  = config,
            )
        }.stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = GiftPointsUiState(config = config),
        )

    // Separate StateFlow so the confirmation screen recomposes when points are awarded.
    private val _lastAwardedPoints = MutableStateFlow(0)
    val lastAwardedPointsFlow: StateFlow<Int> = _lastAwardedPoints.asStateFlow()

    /** Snapshot accessor — safe to read from composition (StateFlow value is @Volatile). */
    val lastAwardedPoints: Int get() = _lastAwardedPoints.value

    // ── Actions ───────────────────────────────────────────────────────────────

    /**
     * Persists the points earned for a completed order and updates
     * [lastAwardedPoints] so the confirmation screen can show "You earned N pts".
     */
    fun awardOrderPoints(orderId: String, grandTotal: Double) {
        viewModelScope.launch {
            repository.awardOrderPoints(orderId, grandTotal, config)
            _lastAwardedPoints.update { config.pointsForOrder(grandTotal) }
        }
    }

    /**
     * Awards a one-time welcome bonus on registration.
     * Safe to call multiple times — the repository ignores duplicates.
     */
    fun awardWelcomeBonus() {
        viewModelScope.launch {
            repository.awardWelcomeBonus(config)
        }
    }

    /** Call after the confirmation screen has consumed the value. */
    fun clearLastAwardedPoints() {
        _lastAwardedPoints.update { 0 }
    }
}
