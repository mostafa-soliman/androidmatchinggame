package com.example.androidmatchinggame.data

data class GameState(
    val words: List<String> = emptyList(),
    val meanings: List<String> = emptyList(),
    val connections: List<Connection> = emptyList()
)

data class Connection(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float
)