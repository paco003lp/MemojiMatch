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

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val gameResultRepository = GameResultRepository(application)
    private val profileRepository = ProfileRepository(application)

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    init {
        viewModelScope.launch {
            _username.value = profileRepository.getUsername()
        }
    }

    fun saveResult(
        score: Int,
        difficulty: String,
        timeRemaining: Int,
        isWon: Boolean
    ) {
        viewModelScope.launch {
            gameResultRepository.insertResult(
                GameResult(
                    username = _username.value.ifBlank { profileRepository.getUsername() },
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
