package com.example.ebookstore.data.repository

import com.example.ebookstore.data.local.dao.UserDao
import com.example.ebookstore.data.local.entity.UserEntity
import com.example.ebookstore.domain.repository.LoginResult
import com.example.ebookstore.domain.repository.RegisterResult
import com.example.ebookstore.domain.repository.UserRepository
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room-backed [UserRepository].
 *
 * Passwords are never stored in plain-text. A random 16-byte salt is generated
 * at registration time and stored alongside the SHA-256 digest of (salt + password).
 * This is sufficient for an offline/local demo; replace with bcrypt or Argon2
 * when moving to a real authentication backend.
 */
@Singleton
class RoomUserRepository @Inject constructor(
    private val userDao: UserDao,
) : UserRepository {

    override suspend fun register(
        name: String,
        email: String,
        password: String,
    ): RegisterResult {
        val normalised = email.trim().lowercase()
        if (userDao.countByEmail(normalised) > 0) {
            return RegisterResult.EmailAlreadyExists
        }
        val salt = generateSalt()
        val hash = hashPassword(salt, password)
        userDao.insertUser(
            UserEntity(
                name         = name.trim(),
                email        = normalised,
                passwordHash = hash,
                salt         = salt,
                createdAt    = Instant.now().toEpochMilli(),
            )
        )
        return RegisterResult.Success(name = name.trim(), email = normalised)
    }

    override suspend fun login(email: String, password: String): LoginResult {
        val normalised = email.trim().lowercase()
        val entity = userDao.getUserByEmail(normalised)
            ?: return LoginResult.InvalidCredentials
        val expectedHash = hashPassword(entity.salt, password)
        if (expectedHash != entity.passwordHash) return LoginResult.InvalidCredentials
        return LoginResult.Success(name = entity.name, email = entity.email)
    }

    // ── Crypto helpers ────────────────────────────────────────────────────────

    private fun generateSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.toHex()
    }

    private fun hashPassword(salt: String, password: String): String {
        val input = (salt + password).toByteArray(Charsets.UTF_8)
        return MessageDigest.getInstance("SHA-256").digest(input).toHex()
    }

    private fun ByteArray.toHex(): String =
        joinToString("") { "%02x".format(it) }
}
