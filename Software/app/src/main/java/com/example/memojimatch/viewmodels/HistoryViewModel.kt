package com.example.memojimatch.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.memojimatch.data.GameResult
import com.example.memojimatch.data.GameResultRepository
import com.example.memojimatch.data.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameResultRepository(application)
    private val profileRepository = ProfileRepository(application)

    private val _lastFive = MutableStateFlow<List<GameResult>>(emptyList())
    val lastFive: StateFlow<List<GameResult>> = _lastFive.asStateFlow()

    suspend fun loadLastFive() {
        val userId = profileRepository.getCurrentUserId()
        _lastFive.value = repository.getLastFive(userId)
    }
}
