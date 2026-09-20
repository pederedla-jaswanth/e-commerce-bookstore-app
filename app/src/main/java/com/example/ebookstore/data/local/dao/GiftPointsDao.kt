package com.example.ebookstore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ebookstore.data.local.entity.PointsLedgerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GiftPointsDao {

    /** Live stream of the current points balance for [userId]. */
    @Query("SELECT COALESCE(SUM(delta), 0) FROM points_ledger WHERE userId = :userId")
    fun observeBalance(userId: Long = 1L): Flow<Int>

    /** Insert a new ledger entry (earned or redeemed). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: PointsLedgerEntity)

    /** Recent history, newest-first, limited to [limit] rows. */
    @Query(
        "SELECT * FROM points_ledger WHERE userId = :userId " +
        "ORDER BY timestamp DESC LIMIT :limit"
    )
    fun observeHistory(userId: Long = 1L, limit: Int = 20): Flow<List<PointsLedgerEntity>>

    /** One-shot balance read (used by seeder / unit tests). */
    @Query("SELECT COALESCE(SUM(delta), 0) FROM points_ledger WHERE userId = :userId")
    suspend fun getBalance(userId: Long = 1L): Int

    /** Returns true if a welcome-bonus row already exists for [userId]. */
    @Query(
        "SELECT COUNT(*) FROM points_ledger " +
        "WHERE userId = :userId AND description = 'Welcome bonus' LIMIT 1"
    )
    suspend fun welcomeBonusCount(userId: Long = 1L): Int
}
