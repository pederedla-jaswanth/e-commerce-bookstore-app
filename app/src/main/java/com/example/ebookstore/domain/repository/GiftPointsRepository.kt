package com.example.ebookstore.domain.repository

import com.example.ebookstore.domain.model.GiftPointsConfig
import com.example.ebookstore.domain.model.PointsEntry
import kotlinx.coroutines.flow.Flow

/**
 * Contract for the gift-points programme.
 *
 * All point calculations use the rules in [GiftPointsConfig] — no business
 * logic is embedded here.
 */
interface GiftPointsRepository {

    /** Live stream of the current points balance. */
    fun observeBalance(): Flow<Int>

    /** Live stream of recent points history (newest-first, up to 20 entries). */
    fun observeHistory(): Flow<List<PointsEntry>>

    /**
     * Awards points for a completed order.
     *
     * @param orderId      Used in the human-readable ledger description.
     * @param grandTotal   Order total in rupees; points are derived from [config].
     * @param config       Point rules; callers should pass the app-wide singleton.
     */
    suspend fun awardOrderPoints(orderId: String, grandTotal: Double, config: GiftPointsConfig)

    /**
     * Awards a one-time welcome bonus on new user registration.
     * No-op if a welcome entry already exists.
     */
    suspend fun awardWelcomeBonus(config: GiftPointsConfig)

    /** One-shot balance read (used for the confirmation screen display). */
    suspend fun getBalance(): Int
}
