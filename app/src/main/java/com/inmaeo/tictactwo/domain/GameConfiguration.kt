package com.inmaeo.tictactwo.domain

data class GameConfiguration(
    val boardSize: Int,
    val gridSize: Int,
    val winCondition: Int,
    val numberOfMarkers: Int,
    val numberOfTotalMovesForSpecials: Int,
    val name: String
)
