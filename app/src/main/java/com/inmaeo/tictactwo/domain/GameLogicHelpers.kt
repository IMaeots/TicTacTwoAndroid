package com.inmaeo.tictactwo.domain

enum class GamePiece {
    Empty, Player1, Player2
}

fun GamePiece.getTextByPiece(): String = when(this) {
    GamePiece.Empty -> ""
    GamePiece.Player1 -> "X"
    GamePiece.Player2 -> "O"
}

data class GameConfiguration(
    val boardSize: Int,
    val gridSize: Int,
    val winCondition: Int,
    val numberOfMarkers: Int,
    val name: String
)

data class GameState(
    val gameConfiguration: GameConfiguration,
    val gameBoard: List<List<GamePiece>>,
    val gameOutcome: GameOutcome = GameOutcome.None,
    var nextMoveBy: GamePiece = GamePiece.Player1,
    var gridX: Int = (gameConfiguration.boardSize - gameConfiguration.gridSize) / 2,
    var gridY: Int= (gameConfiguration.boardSize - gameConfiguration.gridSize) / 2,
    var player1MarkersPlaced: Int = 0,
    var player2MarkersPlaced: Int = 0,
    var moveCount: Int = 0,
    var error: GameStateError? = null
)

sealed interface GameStateError {
    data object UnknownError : GameStateError
}
