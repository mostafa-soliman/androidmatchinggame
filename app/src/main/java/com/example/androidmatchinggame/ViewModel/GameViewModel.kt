package com.example.androidmatchinggame.ViewModel

import androidx.lifecycle.ViewModel
import com.example.androidmatchinggame.data.Connection
import com.example.androidmatchinggame.data.GameState
import com.example.androidmatchinggame.data.WordMeaningPair
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val wordMeaningPairs = listOf(
        WordMeaningPair("الصلاة", "العبادة اليومية"),
        WordMeaningPair("الزكاة", "التصدق"),
        WordMeaningPair("الصيام", "الامتناع عن الأكل والشرب"),
        WordMeaningPair("الحج", "زيارة الكعبة"),
        WordMeaningPair("القرآن", "كتاب الإسلام المقدس")
    )

    init {
        initializeGame()
    }

    private fun initializeGame() {
        val shuffledPairs = wordMeaningPairs.shuffled()
        _gameState.value = GameState(
            words = shuffledPairs.map { it.word },
            meanings = shuffledPairs.map { it.meaning }
        )
    }

    fun addConnection(connection: Connection) {
        _gameState.value = _gameState.value.copy(
            connections = _gameState.value.connections + connection
        )
    }
}
