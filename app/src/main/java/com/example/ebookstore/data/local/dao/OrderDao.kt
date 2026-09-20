package com.example.ebookstore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.ebookstore.data.local.entity.OrderEntity
import com.example.ebookstore.data.local.entity.OrderItemEntity
import com.example.ebookstore.data.local.relation.OrderWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    /** Observe all orders with their items, newest-first. */
    @Transaction
    @Query("SELECT * FROM orders ORDER BY placedAt DESC")
    fun observeAllWithItems(): Flow<List<OrderWithItems>>

    /** Fetch a single order with items by orderId. */
    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    suspend fun getOrderWithItems(orderId: String): OrderWithItems?

    /** Insert the order header. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    /** Insert all line items for an order. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    /** Update just the status field of an existing order. */
    @Query("UPDATE orders SET status = :status WHERE orderId = :orderId")
    suspend fun updateStatus(orderId: String, status: String)

    /** Returns the total number of order rows (used by the seeder). */
    @Query("SELECT COUNT(*) FROM orders")
    suspend fun count(): Int
}
