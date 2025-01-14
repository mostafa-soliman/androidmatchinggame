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
    private val connectedWords = mutableMapOf<String, String>()

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
        val shuffledWords = shuffledPairs.map { it.word }.shuffled()
        val shuffledMeanings = shuffledPairs.map { it.meaning }.shuffled()
        _gameState.value = GameState(
            words = shuffledWords,
            meanings = shuffledMeanings
        )
        connectedWords.clear()
    }

    fun canAddConnection(word: String, meaning: String): Boolean {
        return !connectedWords.containsKey(word) && !connectedWords.containsValue(meaning)
    }

    fun isValidConnection(word: String, meaning: String): Boolean {
        return wordMeaningPairs.any {
            it.word == word && it.meaning == meaning
        }
    }
    fun addConnection(word: String, meaning: String, connection: Connection) {
        if (isValidConnection(word, meaning) && canAddConnection(word, meaning)) {
            connectedWords[word] = meaning
            _gameState.value = _gameState.value.copy(
                connections = _gameState.value.connections + connection
            )
        }
    }
    // دالة التحقق من صحة الإجابات
    fun checkAnswers(): Boolean {
        return connectedWords.all { (word, meaning) ->
            wordMeaningPairs.any { it.word == word && it.meaning == meaning }
        } && connectedWords.size == wordMeaningPairs.size
    }
}
