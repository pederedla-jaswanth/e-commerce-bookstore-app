package com.example.ebookstore.domain.repository

/**
 * Contract for user account operations.
 * The Room-backed implementation lives in data/repository.
 */
interface UserRepository {
    /** Register a new account. Returns [RegisterResult]. */
    suspend fun register(name: String, email: String, password: String): RegisterResult

    /** Attempt login. Returns [LoginResult]. */
    suspend fun login(email: String, password: String): LoginResult
}

sealed class RegisterResult {
    data class Success(val name: String, val email: String) : RegisterResult()
    object EmailAlreadyExists : RegisterResult()
}

sealed class LoginResult {
    data class Success(val name: String, val email: String) : LoginResult()
    object InvalidCredentials : LoginResult()
}
