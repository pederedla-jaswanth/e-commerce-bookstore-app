package com.example.ebookstore.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ebookstore.domain.repository.LoginResult
import com.example.ebookstore.domain.repository.RegisterResult
import com.example.ebookstore.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents a logged-in user (UI model — no password data).
 */
data class MockUser(
    val name: String,
    val email: String,
    val avatarInitials: String = name
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString(""),
)

/**
 * UI state for the authentication flow.
 */
data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val currentUser: MockUser? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

/**
 * ViewModel for Login / Register / Logout flows.
 *
 * Backed by [UserRepository] — credentials are persisted in Room (SQLite) and
 * survive app restarts. Passwords are stored as SHA-256(salt+password) — never
 * plain-text.
 *
 * Scoped at NavGraph level so auth state is preserved across tabs.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ── Public actions ────────────────────────────────────────────────────────

    fun login(email: String, password: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = userRepository.login(email, password)) {
                is LoginResult.Success -> _uiState.update {
                    it.copy(
                        isLoading   = false,
                        isLoggedIn  = true,
                        currentUser = MockUser(result.name, result.email),
                        error       = null,
                    )
                }
                LoginResult.InvalidCredentials -> _uiState.update {
                    it.copy(isLoading = false, error = "Invalid email or password.")
                }
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = userRepository.register(name, email, password)) {
                is RegisterResult.Success -> _uiState.update {
                    it.copy(
                        isLoading   = false,
                        isLoggedIn  = true,
                        currentUser = MockUser(result.name, result.email),
                        error       = null,
                    )
                }
                RegisterResult.EmailAlreadyExists -> _uiState.update {
                    it.copy(isLoading = false, error = "An account with this email already exists.")
                }
            }
        }
    }

    fun logout() {
        _uiState.update { AuthUiState() }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
