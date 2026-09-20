package com.example.ebookstore.domain.model

/**
 * Configurable rules for the gift-points programme.
 *
 * All business rules live here — no hardcoded magic numbers in ViewModels or UI.
 * Swap defaults or inject via DI to change point behaviour without touching screens.
 *
 * @param pointsPerRupee       Points awarded per ₹1 spent (default: 1 point / ₹10).
 * @param redeemRatio          Rupee value of 1 point when redeeming (default: ₹0.10).
 * @param welcomeBonus         Points given on first registration (default: 100).
 * @param minimumRedeemPoints  Minimum balance needed to redeem (default: 500).
 */
data class GiftPointsConfig(
    val pointsPerRupee: Double = 0.1,          // 1 pt per ₹10
    val redeemRatio: Double = 0.10,            // 1 pt = ₹0.10
    val welcomeBonus: Int = 100,
    val minimumRedeemPoints: Int = 500,
) {
    /** Calculates points earned for a given order [grandTotal]. */
    fun pointsForOrder(grandTotal: Double): Int =
        (grandTotal * pointsPerRupee).toInt()

    /** Calculates the rupee value of [points]. */
    fun rupeeValueOf(points: Int): Double = points * redeemRatio
}

/**
 * A single entry in the points ledger.
 *
 * @param id          Unique row identifier.
 * @param delta       Points change — positive = earned, negative = redeemed/expired.
 * @param description Human-readable reason, e.g. "Order ORD-00000001" or "Welcome bonus".
 * @param timestamp   Epoch-millis when the entry was recorded.
 */
data class PointsEntry(
    val id: Long = 0,
    val delta: Int,
    val description: String,
    val timestamp: Long,
)
