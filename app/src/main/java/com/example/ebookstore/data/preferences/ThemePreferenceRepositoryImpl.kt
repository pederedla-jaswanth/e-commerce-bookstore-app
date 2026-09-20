package com.example.ebookstore.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ebookstore.ui.theme.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// DataStore instance scoped to the application context.
// The "theme_preferences" file is created once and reused for the app's lifetime.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "theme_preferences"
)

/**
 * DataStore-backed implementation of [ThemePreferenceRepository].
 *
 * Stores [ThemeMode] as the enum's name String (e.g. "SYSTEM", "LIGHT", "DARK").
 * Falls back to [ThemeMode.SYSTEM] if the stored value is missing or unrecognised.
 *
 * Injected as a [Singleton] by Hilt — there is always exactly one instance per
 * application process.
 */
@Singleton
class ThemePreferenceRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ThemePreferenceRepository {

    private companion object {
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }

    override val themeMode: Flow<ThemeMode> = context.dataStore.data
        .map { preferences ->
            val stored = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
            ThemeMode.entries.find { it.name == stored } ?: ThemeMode.SYSTEM
        }

    override suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }
}
