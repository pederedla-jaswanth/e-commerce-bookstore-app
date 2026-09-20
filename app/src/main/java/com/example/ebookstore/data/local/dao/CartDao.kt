package com.example.ebookstore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ebookstore.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    /** Observe all cart items as a live Flow — emits on every change. */
    @Query("SELECT * FROM cart_items")
    fun observeAll(): Flow<List<CartItemEntity>>

    /** Insert a new item or replace if [bookId] already exists. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CartItemEntity)

    /** Update quantity and price (used by increase/decrease). */
    @Update
    suspend fun update(item: CartItemEntity)

    /** Fetch a single item by bookId, or null if absent. */
    @Query("SELECT * FROM cart_items WHERE bookId = :bookId LIMIT 1")
    suspend fun getById(bookId: String): CartItemEntity?

    /** Remove one line item. */
    @Query("DELETE FROM cart_items WHERE bookId = :bookId")
    suspend fun deleteById(bookId: String)

    /** Remove all items (clear cart). */
    @Query("DELETE FROM cart_items")
    suspend fun deleteAll()
}
