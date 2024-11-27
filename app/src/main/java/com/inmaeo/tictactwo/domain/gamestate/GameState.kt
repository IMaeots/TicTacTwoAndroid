package com.inmaeo.tictactwo.domain.gamestate

import com.inmaeo.tictactwo.domain.GameConfiguration
import com.inmaeo.tictactwo.domain.GamePiece
import com.inmaeo.tictactwo.domain.LocationCoordinates

data class GameState(
    val gameConfiguration: GameConfiguration,
    val gameBoard: List<List<GamePiece>>,
    val gameOutcome: GameOutcome = GameOutcome.None,
    var nextMoveBy: GamePiece = GamePiece.Player1,
    var gridMainCorner: LocationCoordinates = LocationCoordinates(
        x = (gameConfiguration.boardSize - gameConfiguration.gridSize) / 2,
        y = (gameConfiguration.boardSize - gameConfiguration.gridSize) / 2
    ),
    var selectedMarker: LocationCoordinates? = null,
    var player1MarkersPlaced: Int = 0,
    var player2MarkersPlaced: Int = 0,
    var moveCount: Int = 0,
    var error: GameStateError? = null
)

sealed interface GameStateError {
    data object UnknownError : GameStateError
    data object InvalidMove : GameStateError
}
