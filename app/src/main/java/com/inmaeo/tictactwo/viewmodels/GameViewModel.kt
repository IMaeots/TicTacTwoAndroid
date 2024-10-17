package com.inmaeo.tictactwo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inmaeo.tictactwo.domain.GameConfiguration
import com.inmaeo.tictactwo.domain.GameLogic
import com.inmaeo.tictactwo.domain.GamePiece
import com.inmaeo.tictactwo.repository.GameConfigRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val gameConfigRepository: GameConfigRepository
) : ViewModel() {
    private val _gameState = MutableSharedFlow<GameViewState>()
    val gameState = _gameState.asSharedFlow()

    var lastGameState: GameViewState? = null

    private lateinit var gameLogic: GameLogic
    private val gameConfig = GameConfiguration(
        boardSize = 5,
        gridSize = 3,
        winCondition = 3,
        numberOfMarkers = 3,
        name = "Tic Tac Two"
    )

    init {
        resetGame()
    }

    fun onCellClick(row: Int, col: Int): String {
        if (gameLogic.canPlaceMarker()) {
            val placed = gameLogic.placeMarker(row, col)
            return if (placed) {
                // Check for win or draw and return the appropriate message
                updateGameState()
                return "All good"
            } else {
                return "not good"
            }
        }
        return "very bad"
    }


    private fun updateGameState() {
        lastGameState = GameViewState(
            gameConfiguration = gameLogic.gameState.gameConfiguration,
            gameBoard = gameLogic.getGameBoard(),
            nextMoveBy = gameLogic.nextMoveBy,
            gridX = gameLogic.gridX,
            gridY = gameLogic.gridY,
            player1MarkersPlaced = gameLogic.gameState.player1MarkersPlaced,
            player2MarkersPlaced = gameLogic.gameState.player2MarkersPlaced,
            moveCount = gameLogic.gameState.moveCount
        )

        viewModelScope.launch {
            _gameState.emit(
                lastGameState!!
            )
        }
    }

    fun resetGame() {
        gameLogic = GameLogic(gameConfig)

        viewModelScope.launch {
            _gameState.emit(
                GameViewState(
                    gameConfiguration = gameConfig,
                    gameBoard = gameLogic.getGameBoard(),
                    gridX = gameLogic.gridX,
                    gridY = gameLogic.gridY,
                    player1MarkersPlaced = 0,
                    player2MarkersPlaced = 0,
                    moveCount = 0
                )
            )
        }
    }
}


data class GameViewState(
    val gameConfiguration: GameConfiguration,
    val nextMoveBy: GamePiece = GamePiece.Player1,
    val gameBoard: Array<Array<GamePiece>>,
    val gridX: Int,
    val gridY: Int,
    val player1MarkersPlaced: Int = 0,
    val player2MarkersPlaced: Int = 0,
    val moveCount: Int = 0
)
