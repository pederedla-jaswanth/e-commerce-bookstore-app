package com.example.ebookstore.di

import com.example.ebookstore.data.mock.MockRecommendationRepository
import com.example.ebookstore.data.preferences.ThemePreferenceRepository
import com.example.ebookstore.data.preferences.ThemePreferenceRepositoryImpl
import com.example.ebookstore.data.repository.RoomGiftPointsRepository
import com.example.ebookstore.domain.model.GiftPointsConfig
import com.example.ebookstore.domain.repository.GiftPointsRepository
import com.example.ebookstore.domain.repository.RecommendationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides app-scoped bindings.
 *
 * - [ThemePreferenceRepository] → DataStore-backed implementation
 * - [GiftPointsConfig]          → app-wide singleton rule set (no hardcoded values in screens)
 * - [GiftPointsRepository]      → Room-backed implementation
 * - [RecommendationRepository]  → Mock implementation (replace with API impl later)
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindThemePreferenceRepository(
        impl: ThemePreferenceRepositoryImpl,
    ): ThemePreferenceRepository

    @Binds
    @Singleton
    abstract fun bindGiftPointsRepository(
        impl: RoomGiftPointsRepository,
    ): GiftPointsRepository

    @Binds
    @Singleton
    abstract fun bindRecommendationRepository(
        impl: MockRecommendationRepository,
    ): RecommendationRepository

    companion object {
        /**
         * Provides the app-wide [GiftPointsConfig] singleton.
         *
         * All business rules (points per rupee, redeem ratio, welcome bonus,
         * minimum redeem threshold) are defined here — no magic numbers anywhere else.
         */
        @Provides
        @Singleton
        fun provideGiftPointsConfig(): GiftPointsConfig = GiftPointsConfig(
            pointsPerRupee      = 0.1,     // 1 point per ₹10 spent
            redeemRatio         = 0.10,    // 1 point = ₹0.10
            welcomeBonus        = 100,
            minimumRedeemPoints = 500,
        )
    }
}
