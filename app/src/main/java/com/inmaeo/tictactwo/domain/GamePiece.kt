package com.inmaeo.tictactwo.domain

enum class GamePiece {
    Empty, Player1, Player2
}

fun GamePiece.getTextByPiece(): String = when(this) {
    GamePiece.Empty -> ""
    GamePiece.Player1 -> "X"
    GamePiece.Player2 -> "O"
}
