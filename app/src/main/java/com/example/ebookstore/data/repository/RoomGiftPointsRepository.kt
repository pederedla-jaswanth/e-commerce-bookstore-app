package com.example.ebookstore.data.repository

import com.example.ebookstore.data.local.dao.GiftPointsDao
import com.example.ebookstore.data.local.entity.PointsLedgerEntity
import com.example.ebookstore.domain.model.GiftPointsConfig
import com.example.ebookstore.domain.model.PointsEntry
import com.example.ebookstore.domain.repository.GiftPointsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room-backed [GiftPointsRepository].
 *
 * Points are stored as an append-only ledger — every earned or redeemed
 * event is a row with a signed [delta].  The live balance is a SQL SUM so
 * the UI reacts to every database change automatically.
 */
@Singleton
class RoomGiftPointsRepository @Inject constructor(
    private val dao: GiftPointsDao,
) : GiftPointsRepository {

    override fun observeBalance(): Flow<Int> = dao.observeBalance()

    override fun observeHistory(): Flow<List<PointsEntry>> =
        dao.observeHistory().map { rows ->
            rows.map { it.toDomain() }
        }

    override suspend fun awardOrderPoints(
        orderId: String,
        grandTotal: Double,
        config: GiftPointsConfig,
    ) {
        val pts = config.pointsForOrder(grandTotal)
        if (pts <= 0) return
        dao.insertEntry(
            PointsLedgerEntity(
                delta       = pts,
                description = "Order $orderId",
                timestamp   = Instant.now().toEpochMilli(),
            )
        )
    }

    override suspend fun awardWelcomeBonus(config: GiftPointsConfig) {
        if (config.welcomeBonus <= 0) return
        // Only award once — guard by checking for an existing welcome-bonus row.
        if (dao.welcomeBonusCount() > 0) return
        dao.insertEntry(
            PointsLedgerEntity(
                delta       = config.welcomeBonus,
                description = "Welcome bonus",
                timestamp   = Instant.now().toEpochMilli(),
            )
        )
    }

    override suspend fun getBalance(): Int = dao.getBalance()

    // ── Mapper ────────────────────────────────────────────────────────────────

    private fun PointsLedgerEntity.toDomain() = PointsEntry(
        id          = id,
        delta       = delta,
        description = description,
        timestamp   = timestamp,
    )
}
