package com.example.ebookstore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ebookstore.data.local.entity.UserEntity

@Dao
interface UserDao {

    /** Insert a new user. Returns the new row id, or -1 on conflict (duplicate email). */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: UserEntity): Long

    /** Fetch a user by normalised (lowercase) email address. */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    /** Returns true if an account with [email] already exists. */
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun countByEmail(email: String): Int
}
