package com.inmaeo.tictactwo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inmaeo.tictactwo.data.repository.GameRepository
import com.inmaeo.tictactwo.data.repository.SaveGameResult
import com.inmaeo.tictactwo.domain.*
import com.inmaeo.tictactwo.domain.gamestate.GameOutcome
import com.inmaeo.tictactwo.domain.gamestate.GameState
import com.inmaeo.tictactwo.domain.gamestate.GameStateError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GameViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _gameState = MutableSharedFlow<GameState>(replay = 1)
    val gameState = _gameState.asSharedFlow()

    private val gameBrain = GameBrain()
    private val gameConfig = gameRepository.getGameConfiguration()
    private var hasGameBeenInitialized = false

    fun initializeGame(gameName: String?) {
        if (hasGameBeenInitialized) return
        hasGameBeenInitialized = true

        if (gameName != null) loadGame(gameName) else startNewBasicGame()
    }

    fun resetGame()  {
        viewModelScope.launch {
            val initialGameState = gameBrain.resetGame(gameState.first())
            _gameState.emit(initialGameState)
        }
    }

    fun saveGame(gameName: String, gameState: GameState): SaveGameResult =
        gameRepository.saveGameState(gameName, gameState)

    fun getGamePiece(gameState: GameState, row: Int, col: Int): GamePiece =
        gameState.gameBoard[col][row]

    fun isButtonPartOfGrid(gameState: GameState, row: Int, col: Int): Boolean =
        gameBrain.isButtonPartOfGrid(gameState, currentX = col, currentY = row)

    fun isGamePieceSelected(gameState: GameState, row: Int, col: Int): Boolean =
        gameBrain.isGamePieceSelected(gameState, currentX = col, currentY = row)

    fun handleGameButtonClick(row: Int, col: Int) = viewModelScope.launch {
        val currentState = _gameState.first()

        val selectedMarker = currentState.selectedMarker
        val currentPiece = getGamePiece(currentState, row, col)

        when {
            currentPiece != GamePiece.Empty -> selectMarker(currentState, xPosition = col, yPosition = row)
            selectedMarker != null -> onGameBoardAction(GameBoardAction.MoveMarker(selectedMarker.x, selectedMarker.y, col, row))
            else -> onGameBoardAction(GameBoardAction.PlaceMarker(col, row))
        }
    }

    fun onGameBoardAction(action: GameBoardAction) = viewModelScope.launch {
        val currentState = _gameState.first()
        if (currentState.gameOutcome != GameOutcome.None) return@launch

        val updatedState: GameState? = when (action) {
            is GameBoardAction.PlaceMarker -> if (gameBrain.canPlaceMarker(currentState)) {
                    gameBrain.placeMarker(currentState, action.x, action.y)
                } else null
            is GameBoardAction.MoveMarker -> if (gameBrain.canMoveThatMarker(currentState, action.oldX, action.oldY)) {
                    gameBrain.moveMarker(currentState, action.oldX, action.oldY, action.newX, action.newY)
                } else null
            is GameBoardAction.MoveGrid -> if (gameBrain.canMoveGrid(currentState)) {
                    gameBrain.moveGrid(currentState, action.direction)
                } else null
        }

        if (updatedState != null) {
            emitUpdatedState(updatedState.copy(gameOutcome = gameBrain.checkForGameEnd(updatedState)))
        } else {
            emitErrorState(currentState, GameStateError.InvalidMove)
        }
    }

    private fun selectMarker(currentState: GameState, xPosition: Int, yPosition: Int) = viewModelScope.launch {
        if (gameBrain.canMoveThatMarker(currentState, xPosition, yPosition)) {
            emitUpdatedState(currentState.copy(selectedMarker = LocationCoordinates(xPosition, yPosition)))
        } else {
            emitErrorState(currentState)
        }
    }

    private fun emitUpdatedState(updatedState: GameState) = viewModelScope.launch {
        val gameOutcome = gameBrain.checkForGameEnd(updatedState)
        _gameState.emit(updatedState.copy(gameOutcome = gameOutcome))
    }

    private fun emitErrorState(currentState: GameState, error: GameStateError = GameStateError.UnknownError) =
        viewModelScope.launch {
            _gameState.emit(currentState.copy(error = error))
        }

    private fun loadGame(gameName: String) = viewModelScope.launch {
        val savedGame = gameRepository.loadGameState(gameName)
        if (savedGame != null) _gameState.emit(savedGame) else startNewBasicGame()
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
