package com.example.memojimatch.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_results")
data class GameResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val username: String,
    val score: Int,
    val difficulty: String,
    val timeRemaining: Int,
    val isWon: Boolean,
    val playedAt: Long
)
