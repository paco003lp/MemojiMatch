package com.example.memojimatch.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.memojimatch.data.GameResultRepository
import com.example.memojimatch.data.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LevelSelectUiState(
    val username: String = "",
    val avatarEmoji: String = "",
    val bestScore: Int = 0,
    val bestEasy: Int = 0,
    val bestMedium: Int = 0,
    val bestHard: Int = 0
)

class LevelSelectViewModel(application: Application) : AndroidViewModel(application) {

    private val profileRepository = ProfileRepository(application)
    private val gameResultRepository = GameResultRepository(application)

    private val _uiState = MutableStateFlow(LevelSelectUiState())
    val uiState: StateFlow<LevelSelectUiState> = _uiState.asStateFlow()

    suspend fun load() {
        val userId = profileRepository.getCurrentUserId()
        _uiState.value = LevelSelectUiState(
            username = profileRepository.getUsername().ifBlank { "Igrač" },
            avatarEmoji = profileRepository.getAvatarEmoji(),
            bestScore = gameResultRepository.getBestScore(userId),
            bestEasy = gameResultRepository.getBestScoreByDifficulty(userId, "EASY"),
            bestMedium = gameResultRepository.getBestScoreByDifficulty(userId, "MEDIUM"),
            bestHard = gameResultRepository.getBestScoreByDifficulty(userId, "HARD")
        )
    }
}
