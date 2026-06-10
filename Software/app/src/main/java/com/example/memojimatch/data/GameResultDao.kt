package com.example.memojimatch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: GameResult)

    @Query("SELECT * FROM game_results ORDER BY score DESC LIMIT 10")
    suspend fun getTopTen(): List<GameResult>

    @Query("SELECT * FROM game_results WHERE userId = :userId ORDER BY playedAt DESC LIMIT 5")
    suspend fun getLastFive(userId: Long): List<GameResult>

    @Query("SELECT COALESCE(MAX(score), 0) FROM game_results WHERE userId = :userId")
    suspend fun getBestScore(userId: Long): Int

    @Query("SELECT COALESCE(MAX(score), 0) FROM game_results WHERE userId = :userId AND difficulty = :difficulty")
    suspend fun getBestScoreByDifficulty(userId: Long, difficulty: String): Int
}
