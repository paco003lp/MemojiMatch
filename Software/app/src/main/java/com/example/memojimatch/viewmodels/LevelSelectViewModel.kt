package com.example.memojimatch.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.memojimatch.data.GameResultRepository
import com.example.memojimatch.data.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
        _uiState.value = LevelSelectUiState(
                username = profileRepository.getUsername().ifBlank { "Igrač" },
                avatarEmoji = profileRepository.getAvatarEmoji(),
                bestScore = gameResultRepository.getBestScore(),
                bestEasy = gameResultRepository.getBestScoreByDifficulty("EASY"),
                bestMedium = gameResultRepository.getBestScoreByDifficulty("MEDIUM"),
                bestHard = gameResultRepository.getBestScoreByDifficulty("HARD")
            )
    }
}
