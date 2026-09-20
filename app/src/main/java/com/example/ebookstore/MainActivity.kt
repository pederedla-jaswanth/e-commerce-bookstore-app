package com.example.ebookstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ebookstore.ui.navigation.EBookStoreNavGraph
import com.example.ebookstore.ui.theme.EBookStoreTheme
import com.example.ebookstore.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single Activity host for the EBookStore app.
 *
 * Responsibilities:
 * - Observe [ThemeViewModel] and apply the persisted [ThemeMode] to [EBookStoreTheme].
 * - Host the [EBookStoreNavGraph] which owns the Scaffold, BottomBar, and NavHost.
 *
 * Navigation, screen logic, and state management live in their respective
 * composables and ViewModels — MainActivity stays thin.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()

            EBookStoreTheme(themeMode = themeMode) {
                EBookStoreNavGraph(
                    themeMode         = themeMode,
                    onThemeModeChange = themeViewModel::setThemeMode,
                )
            }
        }
    }
}
