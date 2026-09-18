package com.example.ebookstore.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ebookstore.data.preferences.ThemePreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Activity-scoped ViewModel that owns the user's theme preference.
 *
 * Exposes [themeMode] as a [StateFlow] so [MainActivity] can collect it
 * and pass the current value to [EBookStoreTheme]. Because this ViewModel
 * is scoped to the Activity, it survives Compose recomposition but is
 * recreated with a new Activity instance — which is the correct behaviour
 * for a theme that must be applied before the first frame is drawn.
 *
 * [setThemeMode] writes the new selection to DataStore via
 * [ThemePreferenceRepository]. The Flow emits the updated value, which
 * propagates through [themeMode] back to the UI automatically.
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val repository: ThemePreferenceRepository,
) : ViewModel() {

    /**
     * The currently active theme mode.
     * Initial value is [ThemeMode.SYSTEM]; the DataStore-persisted value
     * replaces it as soon as the first emission arrives.
     */
    val themeMode: StateFlow<ThemeMode> = repository.themeMode
        .stateIn(
            scope          = viewModelScope,
            started        = SharingStarted.WhileSubscribed(5_000),
            initialValue   = ThemeMode.SYSTEM,
        )

    /** Persists [mode] to DataStore and triggers a theme recomposition. */
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            repository.setThemeMode(mode)
        }
    }
}
