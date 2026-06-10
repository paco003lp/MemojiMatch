package com.example.memojimatch.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.memojimatch.data.GameResult
import com.example.memojimatch.data.GameResultRepository
import com.example.memojimatch.data.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CardState(val emoji: String, val matched: Boolean = false, val faceUp: Boolean = false)

data class GameState(
    val cards: List<CardState> = emptyList(),
    val score: Int = 0,
    val matchedPairs: Int = 0,
    val errorCount: Int = 0,
    val timeRemainingMs: Long = 0L,
    val timerStarted: Boolean = false,
    val hasMatchedAny: Boolean = false,
    val firstIndex: Int? = null,
    val isBusy: Boolean = false,
    val finished: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val gameResultRepository = GameResultRepository(application)
    private val profileRepository = ProfileRepository(application)

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private var currentUserId: Long = -1L

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    val isInitialized get() = _gameState.value.cards.isNotEmpty()

    init {
        viewModelScope.launch {
            currentUserId = profileRepository.getCurrentUserId()
            _username.value = profileRepository.getUsername()
        }
    }

    fun initGame(cards: List<CardState>, timeMs: Long) {
        if (isInitialized && !_gameState.value.finished) return
        _gameState.value = GameState(cards = cards, timeRemainingMs = timeMs)
    }

    fun updateState(update: GameState.() -> GameState) {
        _gameState.value = _gameState.value.update()
    }

    fun saveResult(score: Int, difficulty: String, timeRemaining: Int, isWon: Boolean) {
        viewModelScope.launch {
            val userId = if (currentUserId != -1L) currentUserId else profileRepository.getCurrentUserId()
            val username = _username.value.ifBlank { profileRepository.getUsername() }
            gameResultRepository.insertResult(
                GameResult(
                    userId = userId,
                    username = username,
                    score = score,
                    difficulty = difficulty,
                    timeRemaining = timeRemaining,
                    isWon = isWon,
                    playedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
