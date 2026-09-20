package com.example.ebookstore.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a registered user.
 *
 * [email] has a unique index so duplicate registrations are rejected at the
 * DB level even if the repository layer misses the check.
 *
 * Passwords are stored as a salted SHA-256 hex digest — never plain-text.
 * The [salt] is a random 16-byte hex string generated at registration time.
 */
@Entity(
    tableName = "users",
    indices   = [Index(value = ["email"], unique = true)],
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,          // normalised lowercase
    val passwordHash: String,   // SHA-256(salt + password) as hex
    val salt: String,           // 16-byte random hex
    val createdAt: Long,        // epoch millis
)
