package com.example.memojimatch.viewmodels

import androidx.lifecycle.ViewModel
import com.example.memojimatch.data.GameResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HistoryViewModel : ViewModel() {

    // TODO: zamijeniti s Room upitom getLastFive() kada baza bude gotova
    private val _lastFive = MutableStateFlow<List<GameResult>>(emptyList())
    val lastFive: StateFlow<List<GameResult>> = _lastFive.asStateFlow()
}
