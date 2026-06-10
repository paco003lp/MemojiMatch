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

    private var testUserId: Long = 0L

    @Before
    fun setup() = runTest {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        gameResultDao = db.gameResultDao()
        userDao = db.userDao()

        testUserId = userDao.insertUser(User(username = "TestUser", avatarIndex = 0))
    }

    @After
    fun teardown() {
        db.close()
    }

    private fun makeResult(
        username: String = "TestUser",
        score: Int = 100,
        difficulty: String = "EASY",
        userId: Long = testUserId
    ) = GameResult(
        userId = userId,
        username = username,
        score = score,
        difficulty = difficulty,
        timeRemaining = 10,
        isWon = true,
        playedAt = System.currentTimeMillis()
    )

    // ── Emoji testovi ─────────────────────────────────────────────────────────

    @Test
    fun spremi_i_dohvati_korisnika_s_emoji_avatarom() = runTest {
        val id = userDao.insertUser(User(username = "Ana 🐼", avatarIndex = 0))
        val result = userDao.getUserById(id)
        assertEquals("Ana 🐼", result?.username)
        assertEquals(0, result?.avatarIndex)
    }

    @Test
    fun spremi_rezultat_s_emoji_u_usernameu() = runTest {
        val emojiUserId = userDao.insertUser(User(username = "Igrač 🦊", avatarIndex = 1))
        gameResultDao.insertResult(makeResult(username = "Igrač 🦊", userId = emojiUserId))

        val top = gameResultDao.getTopTen()
        assertEquals("Igrač 🦊", top.first().username)
    }

    @Test
    fun spremi_rezultat_s_posebnim_znakovima_u_usernameu() = runTest {
        gameResultDao.insertResult(makeResult(username = "Ana-Marić_123!@#"))

        val last = gameResultDao.getLastFive(testUserId)
        assertEquals("Ana-Marić_123!@#", last.first().username)
    }

    // ── SQL injection testovi ─────────────────────────────────────────────────

    @Test
    fun sql_injection_u_usernameu_ne_brise_tablicu() = runTest {
        val maliciousUsername = "'; DROP TABLE game_results; --"
        gameResultDao.insertResult(makeResult(username = maliciousUsername))

        val results = gameResultDao.getTopTen()
        assertEquals(1, results.size)
        assertEquals(maliciousUsername, results.first().username)
    }

    @Test
    fun sql_injection_u_difficultyu_ne_vraca_krive_rezultate() = runTest {
        gameResultDao.insertResult(makeResult(score = 800, difficulty = "EASY"))

        val injectionAttempt = "EASY' OR '1'='1"
        val result = gameResultDao.getBestScoreByDifficulty(testUserId, injectionAttempt)
        assertEquals(0, result)
    }

    // ── Sortiranje i limit testovi ────────────────────────────────────────────

    @Test
    fun top_ten_je_sortiran_po_scoreu_padajuce() = runTest {
        listOf(500, 1200, 300, 900, 100).forEach { score ->
            gameResultDao.insertResult(makeResult(score = score))
        }

        val top = gameResultDao.getTopTen()
        assertEquals(1200, top.first().score)
        assertTrue(top[0].score >= top[1].score)
    }

    @Test
    fun last_five_vraca_maksimalno_5_zapisa() = runTest {
        repeat(8) { i ->
            gameResultDao.insertResult(makeResult(score = i * 100))
        }

        val last = gameResultDao.getLastFive(testUserId)
        assertEquals(5, last.size)
    }

    // ── Multi-user testovi ────────────────────────────────────────────────────

    @Test
    fun history_filtrira_po_korisniku() = runTest {
        val user2Id = userDao.insertUser(User(username = "Drugi", avatarIndex = 1))

        repeat(3) { gameResultDao.insertResult(makeResult(userId = testUserId, score = 100)) }
        repeat(4) { gameResultDao.insertResult(makeResult(userId = user2Id, username = "Drugi", score = 200)) }

        val historyUser1 = gameResultDao.getLastFive(testUserId)
        val historyUser2 = gameResultDao.getLastFive(user2Id)

        assertTrue(historyUser1.all { it.userId == testUserId })
        assertTrue(historyUser2.all { it.userId == user2Id })
    }

    @Test
    fun top_ten_je_globalan_za_sve_korisnike() = runTest {
        val user2Id = userDao.insertUser(User(username = "Drugi", avatarIndex = 1))

        gameResultDao.insertResult(makeResult(userId = testUserId, score = 500))
        gameResultDao.insertResult(makeResult(userId = user2Id, username = "Drugi", score = 800))

        val top = gameResultDao.getTopTen()
        assertEquals(2, top.size)
        assertEquals(800, top.first().score)
    }

    @Test
    fun best_score_filtrira_po_korisniku() = runTest {
        val user2Id = userDao.insertUser(User(username = "Drugi", avatarIndex = 1))

        gameResultDao.insertResult(makeResult(userId = testUserId, score = 300))
        gameResultDao.insertResult(makeResult(userId = user2Id, username = "Drugi", score = 999))

        val best1 = gameResultDao.getBestScore(testUserId)
        val best2 = gameResultDao.getBestScore(user2Id)

        assertEquals(300, best1)
        assertEquals(999, best2)
    }
}
