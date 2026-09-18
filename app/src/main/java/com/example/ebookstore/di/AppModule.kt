package com.example.ebookstore.di

import com.example.ebookstore.data.preferences.ThemePreferenceRepository
import com.example.ebookstore.data.preferences.ThemePreferenceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides app-scoped bindings.
 *
 * Binds [ThemePreferenceRepositoryImpl] as the [ThemePreferenceRepository]
 * interface so any class that injects [ThemePreferenceRepository] receives
 * the DataStore-backed implementation automatically.
 *
 * Additional bindings (NetworkModule, DatabaseModule, etc.) will be added
 * in separate module files as each feature is implemented.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindThemePreferenceRepository(
        impl: ThemePreferenceRepositoryImpl,
    ): ThemePreferenceRepository
}
