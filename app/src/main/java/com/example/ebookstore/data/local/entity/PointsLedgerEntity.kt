package com.example.ebookstore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for a single gift-points ledger entry.
 *
 * [delta] is positive for earned points and negative for redeemed/expired points.
 * The running balance is derived by summing all [delta] values for a user — no
 * separate "balance" column is stored, keeping the data normalised and auditable.
 *
 * Currently the app has a single-user model, so [userId] defaults to 1 and is
 * included for forward-compatibility when multi-user support is added.
 */
@Entity(tableName = "points_ledger")
data class PointsLedgerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long = 1L,          // placeholder for future multi-user support
    val delta: Int,                 // positive = earned, negative = redeemed
    val description: String,
    val timestamp: Long,            // epoch millis
)
