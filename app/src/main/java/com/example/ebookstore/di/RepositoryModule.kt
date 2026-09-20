package com.example.ebookstore.di

import com.example.ebookstore.data.repository.RoomCartRepository
import com.example.ebookstore.data.repository.RoomOrderRepository
import com.example.ebookstore.data.repository.RoomUserRepository
import com.example.ebookstore.data.repository.RoomWishlistRepository
import com.example.ebookstore.domain.repository.CartRepository
import com.example.ebookstore.domain.repository.OrderRepository
import com.example.ebookstore.domain.repository.UserRepository
import com.example.ebookstore.domain.repository.WishlistRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds each domain repository interface to its
 * Room-backed implementation.
 *
 * Keeping this separate from [DatabaseModule] (which provides DAOs)
 * and [AppModule] (which provides DataStore) makes each module's
 * responsibility clear.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: RoomUserRepository): UserRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: RoomCartRepository): CartRepository

    @Binds
    @Singleton
    abstract fun bindWishlistRepository(impl: RoomWishlistRepository): WishlistRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: RoomOrderRepository): OrderRepository
}
