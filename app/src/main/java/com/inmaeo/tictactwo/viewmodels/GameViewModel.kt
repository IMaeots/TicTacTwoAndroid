package com.inmaeo.tictactwo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inmaeo.tictactwo.domain.GameLogic
import com.inmaeo.tictactwo.domain.GamePiece
import com.inmaeo.tictactwo.domain.GameState
import com.inmaeo.tictactwo.domain.GameStateError
import com.inmaeo.tictactwo.repository.GameConfigRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GameViewModel(
    gameConfigRepository: GameConfigRepository
) : ViewModel() {

    private val _gameState = MutableSharedFlow<GameState>(replay = 1)
    val gameState = _gameState.asSharedFlow()

    private val gameLogic = GameLogic()
    private val gameConfig = gameConfigRepository.getGameConfiguration()

    fun isButtonPartOfGrid(gameState: GameState, row: Int, col: Int): Boolean = gameLogic.isButtonPartOfGrid(gameState, row, col)

    fun isGamePieceSelected(gameState: GameState, row: Int, col: Int): Boolean = gameLogic.isGamePieceSelected(gameState, row, col)

    init {
        viewModelScope.launch {
            val initialState = GameState(
                gameConfiguration = gameConfig,
                gameBoard = MutableList(gameConfig.boardSize) { MutableList(gameConfig.boardSize) { GamePiece.Empty } }
            )
            _gameState.emit(initialState)
        }
    }

    fun resetGame()  {
        viewModelScope.launch {
            val initialGameState = gameLogic.resetGame(gameState.first())
            _gameState.emit(initialGameState)
        }
    }

    fun handleGameButtonClick(row: Int, col: Int) {
        viewModelScope.launch {
            val currentState = _gameState.first()
            val selectedMarker = currentState.selectedMarker

            if (currentState.gameBoard[row][col] != GamePiece.Empty) {
                selectMarker(row, col)
            } else if (selectedMarker != null) {
                onGameBoardAction(
                    action = GameBoardAction.MoveMarker(
                        oldX = selectedMarker.x, oldY = selectedMarker.y, newX = row, newY = col
                    )
                )
            } else {
                onGameBoardAction(action = GameBoardAction.PlaceMarker(row, col))
            }
        }
    }

    private fun onGameBoardAction(action: GameBoardAction) {
        viewModelScope.launch {
            val currentState = _gameState.first()
            if (gameLogic.hasGameFinished(currentState)) return@launch

            val updatedState: GameState?
            when (action) {
                is GameBoardAction.PlaceMarker -> {
                    if (gameLogic.canPlaceMarker(currentState)) {
                        updatedState = gameLogic.placeMarker(currentState, action.x, action.y)
                        if (updatedState != null) {
                            val gameOutcome = gameLogic.checkForGameEnd(updatedState)
                            _gameState.emit(updatedState.copy(gameOutcome = gameOutcome))
                        } else {
                            _gameState.emit(currentState.copy(error = GameStateError.UnknownError))
                        }
                    } else {
                        _gameState.emit(currentState.copy(error = GameStateError.UnknownError))
                    }
                }
                is GameBoardAction.MoveMarker -> {
                    if (gameLogic.canMoveThatMarker(currentState, action.oldX, action.oldY)) {
                        updatedState = gameLogic.moveMarker(currentState, action.oldX, action.oldY, action.newX, action.newY)
                        if (updatedState != null) {
                            val gameOutcome = gameLogic.checkForGameEnd(updatedState)
                            _gameState.emit(updatedState.copy(gameOutcome = gameOutcome))
                        } else {
                            _gameState.emit(currentState.copy(error = GameStateError.UnknownError))
                        }
                    } else {
                        _gameState.emit(currentState.copy(error = GameStateError.UnknownError))
                    }
                }
                is GameBoardAction.MoveGrid -> {
                    if (gameLogic.canMoveGrid(currentState)) {
                        updatedState = gameLogic.moveGrid(currentState, action.direction)
                        if (updatedState != null) {
                            val gameOutcome = gameLogic.checkForGameEnd(updatedState)
                            _gameState.emit(updatedState.copy(gameOutcome = gameOutcome))
                        } else {
                            _gameState.emit(currentState.copy(error = GameStateError.UnknownError))
                        }
                    } else {
                        _gameState.emit(currentState.copy(error = GameStateError.UnknownError))
                    }
                }
            }
        }
    }

    private fun selectMarker(row: Int, col: Int) {
        viewModelScope.launch {
            val currentState = _gameState.first()
            if (!gameLogic.canMoveThatMarker(currentState, row, col)) {
                _gameState.emit(currentState.copy(error = GameStateError.UnknownError))
            } else {
                gameLogic.selectMarker(currentState, row, col)
            }
        }
    }
}

sealed interface GameBoardAction {
    data class PlaceMarker(val x: Int, val y: Int): GameBoardAction
    data class MoveMarker(val oldX: Int, val oldY: Int, val newX: Int, val newY: Int): GameBoardAction
    data class MoveGrid(val direction: MoveGridDirection): GameBoardAction
}

enum class MoveGridDirection {
    LEFT, UP ,RIGHT, DOWN
}
