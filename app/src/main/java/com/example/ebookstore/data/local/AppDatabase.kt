package com.example.ebookstore.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ebookstore.data.local.dao.CartDao
import com.example.ebookstore.data.local.dao.GiftPointsDao
import com.example.ebookstore.data.local.dao.OrderDao
import com.example.ebookstore.data.local.dao.UserDao
import com.example.ebookstore.data.local.dao.WishlistDao
import com.example.ebookstore.data.local.entity.CartItemEntity
import com.example.ebookstore.data.local.entity.OrderEntity
import com.example.ebookstore.data.local.entity.OrderItemEntity
import com.example.ebookstore.data.local.entity.PointsLedgerEntity
import com.example.ebookstore.data.local.entity.UserEntity
import com.example.ebookstore.data.local.entity.WishlistItemEntity

/**
 * Room database for the EBookStore app.
 *
 * Contains:
 * - [UserEntity]          — registered user accounts
 * - [CartItemEntity]      — persistent cart line-items
 * - [WishlistItemEntity]  — persistent wishlist entries
 * - [OrderEntity]         — order headers
 * - [OrderItemEntity]     — order line items (FK → OrderEntity)
 * - [PointsLedgerEntity]  — gift-points ledger (earned / redeemed entries)
 *
 * Version history:
 *   1 → initial schema (users, cart, wishlist, orders)
 *   2 → add points_ledger table
 *
 * Migrations are destructive in the dev phase. Add proper Migration objects
 * before releasing to production to avoid data loss on updates.
 */
@Database(
    entities = [
        UserEntity::class,
        CartItemEntity::class,
        WishlistItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        PointsLedgerEntity::class,
    ],
    version         = 2,
    exportSchema    = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
    abstract fun giftPointsDao(): GiftPointsDao
}
