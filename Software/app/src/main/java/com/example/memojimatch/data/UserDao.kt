package com.example.memojimatch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM user_profile WHERE id = :id")
    suspend fun getUserById(id: Long): User?

    @Query("SELECT * FROM user_profile WHERE username = :username COLLATE NOCASE LIMIT 1")
    suspend fun getUserByUsername(username: String): User?
}
