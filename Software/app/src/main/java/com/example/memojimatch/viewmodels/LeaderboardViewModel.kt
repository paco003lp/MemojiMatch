package com.example.memojimatch.viewmodels

import androidx.lifecycle.ViewModel
import com.example.memojimatch.data.GameResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LeaderboardViewModel : ViewModel() {

    // TODO: zamijeniti s Room upitom getTopTen() kada baza bude gotova
    private val _topResults = MutableStateFlow<List<GameResult>>(emptyList())
    val topResults: StateFlow<List<GameResult>> = _topResults.asStateFlow()
}
