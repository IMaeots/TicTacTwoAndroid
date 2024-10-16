package com.inmaeo.tictactwo.viewmodels

import android.widget.Button
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    val gridSize = 5
    private var currentPlayer: GamePiece = GamePiece.PLAYER1
    private val gameBoard: Array<GamePiece?> = Array(gridSize * gridSize) { GamePiece.EMPTY }

    fun onCellClick(button: Button, row: Int, col: Int): String {
        val index = row * gridSize + col
        if (gameBoard[index] == GamePiece.EMPTY) {
            gameBoard[index] = currentPlayer
            button.text = currentPlayer.getTextByPiece()

            // TODO: Check if game won.

            currentPlayer = if (currentPlayer == GamePiece.PLAYER1) GamePiece.PLAYER2 else GamePiece.PLAYER1
            return "${currentPlayer}'s Turn"
        } else {
            throw IllegalArgumentException("That button is taken.")
        }
    }

    fun resetGame(): String {
        gameBoard.fill(GamePiece.EMPTY)
        currentPlayer = GamePiece.PLAYER1
        return "${currentPlayer}'s Turn"
    }
}

enum class GamePiece {
    EMPTY,
    PLAYER1,
    PLAYER2
}

fun GamePiece.getTextByPiece(): String = when(this) {
    GamePiece.EMPTY -> ""
    GamePiece.PLAYER1 -> "X"
    GamePiece.PLAYER2 -> "O"
}
