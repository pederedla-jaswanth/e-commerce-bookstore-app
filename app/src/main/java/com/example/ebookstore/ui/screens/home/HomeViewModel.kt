package com.example.ebookstore.ui.screens.home

import androidx.lifecycle.ViewModel
import com.example.ebookstore.data.mock.MockData
import com.example.ebookstore.domain.model.Banner
import com.example.ebookstore.domain.model.Book
import com.example.ebookstore.domain.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
// UI State
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Immutable snapshot of everything the Home screen needs to render.
 * The ViewModel emits a new instance whenever any field changes.
 */
data class HomeUiState(
    val banners: List<Banner>           = emptyList(),
    val categories: List<Category>      = emptyList(),
    val selectedCategory: String        = "All",
    val newReleases: List<Book>         = emptyList(),
    val recommendations: List<Book>     = emptyList(),
    val bestsellers: List<Book>         = emptyList(),
    val searchQuery: String             = "",
    val cartItemCount: Int              = 0,
    val isLoading: Boolean              = true,
    val error: String?                  = null,
)

// ─────────────────────────────────────────────────────────────────────────────
// ViewModel
// ─────────────────────────────────────────────────────────────────────────────

/**
 * ViewModel for [HomeScreen].
 *
 * Currently loads mock data synchronously. A later phase will replace
 * [loadHomeData] with a coroutine-based repository call.
 */
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        _uiState.update {
            it.copy(
                banners         = MockData.banners,
                categories      = MockData.categories,
                newReleases     = MockData.newReleases,
                recommendations = MockData.recommendations,
                bestsellers     = MockData.bestsellers,
                isLoading       = false,
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelected(categoryName: String) {
        _uiState.update { it.copy(selectedCategory = categoryName) }
    }

    fun onSearchSubmit() {
        // Navigation to CatalogueScreen with query is handled in HomeScreen
        // via the navController callback passed from NavGraph.
    }
}
