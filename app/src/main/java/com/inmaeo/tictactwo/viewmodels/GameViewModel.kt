package com.inmaeo.tictactwo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inmaeo.tictactwo.data.repository.GameRepository
import com.inmaeo.tictactwo.domain.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GameViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _gameState = MutableSharedFlow<GameState>(replay = 1)
    val gameState = _gameState.asSharedFlow()

    private val gameLogic = GameLogic()
    private val gameConfig = gameRepository.getGameConfiguration()
    private var hasGameBeenInitialized = false

    fun initializeGame(gameName: String?) {
        if (hasGameBeenInitialized) return

        if (gameName != null) {
            loadGame(gameName)
        } else {
            startNewBasicGame()
        }
        hasGameBeenInitialized = true
    }

    fun resetGame()  {
        viewModelScope.launch {
            val initialGameState = gameLogic.resetGame(gameState.first())
            _gameState.emit(initialGameState)
        }
    }

    fun saveGame(gameName: String, gameState: GameState) {
        gameRepository.saveGameState(gameName, gameState)
    }

    fun getGamePiece(gameState: GameState, row: Int, col: Int): GamePiece =
        gameState.gameBoard[col][row]

    fun isButtonPartOfGrid(gameState: GameState, row: Int, col: Int): Boolean =
        gameLogic.isButtonPartOfGrid(gameState, currentX = col, currentY = row)

    fun isGamePieceSelected(gameState: GameState, row: Int, col: Int): Boolean =
        gameLogic.isGamePieceSelected(gameState, currentX = col, currentY = row)

    fun handleGameButtonClick(row: Int, col: Int) = viewModelScope.launch {
        val currentState = _gameState.first()

        val selectedMarker = currentState.selectedMarker
        if (getGamePiece(currentState, row, col) != GamePiece.Empty) {
            selectMarker(currentState, xPosition = col, yPosition = row)
        } else if (selectedMarker != null) {
            onGameBoardAction(
                action = GameBoardAction.MoveMarker(
                    oldX = selectedMarker.x, oldY = selectedMarker.y, newX = col, newY = row
                )
            )
        } else {
            onGameBoardAction(action = GameBoardAction.PlaceMarker(x = col, y = row))
        }
    }

    fun onGameBoardAction(action: GameBoardAction) = viewModelScope.launch {
        val currentState = _gameState.first()

        if (currentState.gameOutcome != GameOutcome.None) return@launch

        var updatedState: GameState? = null
        when (action) {
            is GameBoardAction.PlaceMarker -> {
                if (gameLogic.canPlaceMarker(currentState)) {
                    updatedState = gameLogic.placeMarker(currentState, action.x, action.y)
                } else {
                    _gameState.emit(currentState.copy(error = GameStateError.UnknownError))
                    return@launch
                }
            }
            is GameBoardAction.MoveMarker -> {
                if (gameLogic.canMoveThatMarker(currentState, action.oldX, action.oldY)) {
                    updatedState = gameLogic.moveMarker(
                        currentState,
                        action.oldX,
                        action.oldY,
                        action.newX,
                        action.newY
                    )
                } else {
                    _gameState.emit(currentState.copy(error = GameStateError.UnknownError))
                }
            }
            is GameBoardAction.MoveGrid -> {
                if (gameLogic.canMoveGrid(currentState)) {
                    updatedState = gameLogic.moveGrid(currentState, action.direction)
                } else {
                    _gameState.emit(currentState.copy(error = GameStateError.UnknownError))
                }
            }
        }
        if (updatedState != null) {
            val gameOutcome = gameLogic.checkForGameEnd(updatedState)
            _gameState.emit(updatedState.copy(gameOutcome = gameOutcome))
        } else {
            _gameState.emit(currentState.copy(error = GameStateError.InvalidMove))
        }
    }

    private fun selectMarker(currentState: GameState, xPosition: Int, yPosition: Int) = viewModelScope.launch {
        if (!gameLogic.canMoveThatMarker(currentState, xPosition, yPosition)) {
            _gameState.emit(currentState.copy(error = GameStateError.UnknownError))
        } else {
            _gameState.emit(currentState.copy(
                selectedMarker = LocationCoordinates(xPosition, yPosition)
            ))
        }
    }

    private fun loadGame(gameName: String) = viewModelScope.launch {
        val savedGame = gameRepository.loadGameState(gameName)
        if (savedGame != null) {
            _gameState.emit(savedGame)
        } else {
            startNewBasicGame()
        }
    }

    private fun startNewBasicGame() = viewModelScope.launch {
        val initialState = GameState(
            gameConfiguration = gameConfig,
            gameBoard = MutableList(gameConfig.boardSize) { MutableList(gameConfig.boardSize) { GamePiece.Empty } }
        )
        _gameState.emit(initialState)
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
