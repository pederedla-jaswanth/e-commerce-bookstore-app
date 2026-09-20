package com.example.ebookstore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ebookstore.data.local.entity.WishlistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {

    /** Observe all wishlist items ordered newest-first. */
    @Query("SELECT * FROM wishlist_items ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<WishlistItemEntity>>

    /** Insert or replace (re-adding a removed item). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: WishlistItemEntity)

    /** Remove one item by bookId. */
    @Query("DELETE FROM wishlist_items WHERE bookId = :bookId")
    suspend fun deleteById(bookId: String)

    /** Returns 1 if the book is in the wishlist, 0 otherwise. */
    @Query("SELECT COUNT(*) FROM wishlist_items WHERE bookId = :bookId")
    suspend fun countById(bookId: String): Int
}
