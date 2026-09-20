package com.example.ebookstore.di

import android.content.Context
import androidx.room.Room
import com.example.ebookstore.data.local.AppDatabase
import com.example.ebookstore.data.local.dao.CartDao
import com.example.ebookstore.data.local.dao.GiftPointsDao
import com.example.ebookstore.data.local.dao.OrderDao
import com.example.ebookstore.data.local.dao.UserDao
import com.example.ebookstore.data.local.dao.WishlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides the [AppDatabase] singleton and its DAOs.
 *
 * The database file is named "ebookstore.db". In the current development
 * phase, schema migrations are handled by [fallbackToDestructiveMigration]
 * — add explicit [androidx.room.migration.Migration] objects before
 * shipping to production to avoid data loss on updates.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "ebookstore.db",
    )
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    @Provides
    @Singleton
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun provideCartDao(db: AppDatabase): CartDao = db.cartDao()

    @Provides
    @Singleton
    fun provideWishlistDao(db: AppDatabase): WishlistDao = db.wishlistDao()

    @Provides
    @Singleton
    fun provideOrderDao(db: AppDatabase): OrderDao = db.orderDao()

    @Provides
    @Singleton
    fun provideGiftPointsDao(db: AppDatabase): GiftPointsDao = db.giftPointsDao()
}
