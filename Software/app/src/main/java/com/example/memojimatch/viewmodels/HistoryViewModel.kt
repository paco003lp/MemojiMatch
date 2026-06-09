package com.example.memojimatch.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.memojimatch.data.GameResult
import com.example.memojimatch.data.GameResultRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameResultRepository(application)

    private val _lastFive = MutableStateFlow<List<GameResult>>(emptyList())
    val lastFive: StateFlow<List<GameResult>> = _lastFive.asStateFlow()

    suspend fun loadLastFive() {
        _lastFive.value = repository.getLastFive()
    }
}
