package com.inmaeo.tictactwo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.inmaeo.tictactwo.domain.GameConfiguration
import com.inmaeo.tictactwo.domain.GameLogic
import com.inmaeo.tictactwo.domain.GameOutcome
import com.inmaeo.tictactwo.domain.GamePiece
import com.inmaeo.tictactwo.repository.GameConfigRepository

class GameViewModel(
    private val gameConfigRepository: GameConfigRepository
) : ViewModel() {
    private val gameLogic: GameLogic
    var gridSize: Int

    private val _gameState = MutableLiveData<Array<Array<GamePiece>>>()
    val gameState: LiveData<Array<Array<GamePiece>>> = _gameState

    init {
        val gameConfig = GameConfiguration(
            boardSize = 5,
            gridSize = 3,
            winCondition = 3,
            numberOfMarkers = 3,
            name = "Tic Tac Two"
        )

        gameLogic = GameLogic(gameConfigRepository.getGameConfiguration())
        gridSize = gameLogic.gridSize
        _gameState.value = gameLogic.getGameBoard()
    }

    fun onCellClick(row: Int, col: Int): String {
        if (gameLogic.placeMarker(row, col)) {
            _gameState.value = gameLogic.getGameBoard()

            val outcome = gameLogic.checkForGameEnd()
            return when (outcome) {
                    GameOutcome.None -> "Next Turn: ${gameLogic.nextMoveBy}"
                    GameOutcome.Player1Won -> "Player 1 Wins!"
                    GameOutcome.Player2Won -> "Player 2 Wins!"
                    GameOutcome.Draw -> "It's a Draw!"
                }

        }

        return "Invalid Move"
    }

    fun resetGame() {
        gameLogic.resetGame()
        _gameState.value = gameLogic.getGameBoard()
    }
}
