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
    var gameBoard: Array<Array<GamePiece>>,
    val gameConfiguration: GameConfiguration,
    var nextMoveBy: GamePiece = GamePiece.Player1,
    var player1MarkersPlaced: Int = 0,
    var player2MarkersPlaced: Int = 0,
    var gridX: Int = 0,
    var gridY: Int = 0,
    var moveCount: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameState

        if (!gameBoard.contentDeepEquals(other.gameBoard)) return false
        if (gameConfiguration != other.gameConfiguration) return false
        if (nextMoveBy != other.nextMoveBy) return false
        if (player1MarkersPlaced != other.player1MarkersPlaced) return false
        if (player2MarkersPlaced != other.player2MarkersPlaced) return false
        if (gridX != other.gridX) return false
        if (gridY != other.gridY) return false
        if (moveCount != other.moveCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = gameBoard.contentDeepHashCode()
        result = 31 * result + gameConfiguration.hashCode()
        result = 31 * result + nextMoveBy.hashCode()
        result = 31 * result + player1MarkersPlaced
        result = 31 * result + player2MarkersPlaced
        result = 31 * result + gridX
        result = 31 * result + gridY
        result = 31 * result + moveCount
        return result
    }
}
