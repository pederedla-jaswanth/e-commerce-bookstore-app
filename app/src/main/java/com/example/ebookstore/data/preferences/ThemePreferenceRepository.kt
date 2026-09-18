package com.example.ebookstore.data.preferences

import com.example.ebookstore.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * Contract for reading and persisting the user's theme preference.
 *
 * The implementation ([ThemePreferenceRepositoryImpl]) stores the value in
 * Jetpack DataStore Preferences and exposes it as a [Flow] so the
 * [ThemeViewModel] can collect it as a StateFlow and apply it reactively
 * to [EBookStoreTheme].
 */
interface ThemePreferenceRepository {
    /** Emits the current [ThemeMode] and any subsequent changes. */
    val themeMode: Flow<ThemeMode>

    /** Persists [mode] to DataStore. Suspends until the write is complete. */
    suspend fun setThemeMode(mode: ThemeMode)
}
