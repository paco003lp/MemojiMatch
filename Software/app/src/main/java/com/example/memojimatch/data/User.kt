package com.example.memojimatch.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class User(
    @PrimaryKey val id: Int = 1,
    val username: String,
    val avatarIndex: Int
)
