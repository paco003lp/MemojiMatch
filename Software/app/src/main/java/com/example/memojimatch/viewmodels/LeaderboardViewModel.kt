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

class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {

    private val gameResultRepository = GameResultRepository(application)
    private val profileRepository = ProfileRepository(application)

    private val _topResults = MutableStateFlow<List<GameResult>>(emptyList())
    val topResults: StateFlow<List<GameResult>> = _topResults.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _avatarEmoji = MutableStateFlow("")
    val avatarEmoji: StateFlow<String> = _avatarEmoji.asStateFlow()

    suspend fun load() {
        _username.value = profileRepository.getUsername()
        _avatarEmoji.value = profileRepository.getAvatarEmoji()
        _topResults.value = gameResultRepository.getTopTen()
    }
}
