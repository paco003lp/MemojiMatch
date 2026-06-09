package com.example.memojimatch.data

import android.content.Context

class GameResultRepository(context: Context) {

    private val dao = AppDatabase.getDatabase(context).gameResultDao()

    suspend fun insertResult(result: GameResult) = dao.insertResult(result)

    suspend fun getTopTen(): List<GameResult> = dao.getTopTen()

    suspend fun getLastFive(): List<GameResult> = dao.getLastFive()

    suspend fun getBestScore(): Int = dao.getBestScore()

    suspend fun getBestScoreByDifficulty(difficulty: String): Int =
        dao.getBestScoreByDifficulty(difficulty)
}
