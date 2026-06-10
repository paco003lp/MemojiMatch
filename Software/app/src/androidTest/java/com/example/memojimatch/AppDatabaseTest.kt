package com.example.memojimatch

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.memojimatch.data.AppDatabase
import com.example.memojimatch.data.GameResult
import com.example.memojimatch.data.GameResultDao
import com.example.memojimatch.data.User
import com.example.memojimatch.data.UserDao
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var gameResultDao: GameResultDao
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        gameResultDao = db.gameResultDao()
        userDao = db.userDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    // ── Emoji testovi ─────────────────────────────────────────────────────────

    @Test
    fun spremi_i_dohvati_korisnika_s_emoji_avatarom() = runTest {
        val user = User(id = 1, username = "Ana 🐼", avatarIndex = 0)
        userDao.insertUser(user)

        val result = userDao.getUser()
        assertEquals("Ana 🐼", result?.username)
        assertEquals(0, result?.avatarIndex)
    }

    @Test
    fun spremi_rezultat_s_emoji_u_usernameu() = runTest {
        val result = GameResult(
            username = "Igrač 🦊",
            score = 1240,
            difficulty = "MEDIUM",
            timeRemaining = 38,
            isWon = true,
            playedAt = System.currentTimeMillis()
        )
        gameResultDao.insertResult(result)

        val top = gameResultDao.getTopTen()
        assertEquals(1, top.size)
        assertEquals("Igrač 🦊", top.first().username)
    }

    @Test
    fun spremi_rezultat_s_posebnim_znakovima_u_usernameu() = runTest {
        val result = GameResult(
            username = "Ana-Marić_123!@#",
            score = 500,
            difficulty = "EASY",
            timeRemaining = 20,
            isWon = false,
            playedAt = System.currentTimeMillis()
        )
        gameResultDao.insertResult(result)

        val last = gameResultDao.getLastFive()
        assertEquals("Ana-Marić_123!@#", last.first().username)
    }

    // ── SQL injection testovi ─────────────────────────────────────────────────

    @Test
    fun sql_injection_u_usernameu_ne_brise_tablicu() = runTest {
        val maliciousUsername = "'; DROP TABLE game_results; --"
        val result = GameResult(
            username = maliciousUsername,
            score = 999,
            difficulty = "HARD",
            timeRemaining = 0,
            isWon = false,
            playedAt = System.currentTimeMillis()
        )
        gameResultDao.insertResult(result)

        // Tablica mora i dalje postojati i sadržavati zapis
        val results = gameResultDao.getTopTen()
        assertEquals(1, results.size)
        assertEquals(maliciousUsername, results.first().username)
    }

    @Test
    fun sql_injection_u_difficultyu_ne_vraca_krive_rezultate() = runTest {
        gameResultDao.insertResult(
            GameResult(
                username = "Ana",
                score = 800,
                difficulty = "EASY",
                timeRemaining = 10,
                isWon = true,
                playedAt = System.currentTimeMillis()
            )
        )

        // Pokušaj injection-a kroz difficulty parametar
        val injectionAttempt = "EASY' OR '1'='1"
        val results = gameResultDao.getBestScoreByDifficulty(injectionAttempt)

        // Mora vratiti 0 jer ne postoji partija s tim difficulty stringom
        assertEquals(0, results)
    }

    // ── Sortiranje i limit testovi ────────────────────────────────────────────

    @Test
    fun top_ten_je_sortiran_po_scoreu_padajuce() = runTest {
        val scores = listOf(500, 1200, 300, 900, 100)
        scores.forEach { score ->
            gameResultDao.insertResult(
                GameResult(
                    username = "Ana",
                    score = score,
                    difficulty = "EASY",
                    timeRemaining = 10,
                    isWon = true,
                    playedAt = System.currentTimeMillis()
                )
            )
        }

        val top = gameResultDao.getTopTen()
        assertEquals(1200, top.first().score)
        assertTrue(top[0].score >= top[1].score)
    }

    @Test
    fun last_five_vraca_maksimalno_5_zapisa() = runTest {
        repeat(8) { i ->
            gameResultDao.insertResult(
                GameResult(
                    username = "Ana",
                    score = i * 100,
                    difficulty = "EASY",
                    timeRemaining = 10,
                    isWon = true,
                    playedAt = System.currentTimeMillis() + i
                )
            )
        }

        val last = gameResultDao.getLastFive()
        assertEquals(5, last.size)
    }
}
