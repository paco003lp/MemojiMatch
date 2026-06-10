package com.example.memojimatch.data

import android.content.Context

class GameResultRepository(context: Context) {

    private val dao = AppDatabase.getDatabase(context).gameResultDao()

    suspend fun insertResult(result: GameResult) = dao.insertResult(result)

    suspend fun getTopTen(): List<GameResult> = dao.getTopTen()

    suspend fun getLastFive(userId: Long): List<GameResult> = dao.getLastFive(userId)

    suspend fun getBestScore(userId: Long): Int = dao.getBestScore(userId)

    suspend fun getBestScoreByDifficulty(userId: Long, difficulty: String): Int =
        dao.getBestScoreByDifficulty(userId, difficulty)
}
