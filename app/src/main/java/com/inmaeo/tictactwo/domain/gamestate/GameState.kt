package com.inmaeo.tictactwo.domain.gamestate

import com.inmaeo.tictactwo.domain.GameConfiguration
import com.inmaeo.tictactwo.domain.GamePiece
import com.inmaeo.tictactwo.domain.LocationCoordinates

data class GameState(
    val gameConfiguration: GameConfiguration,
    val gameBoard: List<List<GamePiece>>,
    val gameOutcome: GameOutcome = GameOutcome.None,
    val nextMoveBy: GamePiece = GamePiece.Player1,
    val gridMainCorner: LocationCoordinates = LocationCoordinates(
        x = (gameConfiguration.boardSize - gameConfiguration.gridSize) / 2,
        y = (gameConfiguration.boardSize - gameConfiguration.gridSize) / 2
    ),
    val selectedMarker: LocationCoordinates? = null,
    val player1MarkersPlaced: Int = 0,
    val player2MarkersPlaced: Int = 0,
    val moveCount: Int = 0,
    val error: GameStateError? = null
)

sealed interface GameStateError {
    data object UnknownError : GameStateError
    data object InvalidMove : GameStateError
}
