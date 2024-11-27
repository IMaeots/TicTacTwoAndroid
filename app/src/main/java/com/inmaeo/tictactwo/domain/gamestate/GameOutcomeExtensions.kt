package com.inmaeo.tictactwo.domain.gamestate

import com.inmaeo.tictactwo.domain.GamePiece

fun GameState.checkGameOutcome(): GameOutcome {
    val player1Wins = checkForPlayerWin(GamePiece.Player1)
    val player2Wins = checkForPlayerWin(GamePiece.Player2)

    return when {
        player1Wins && player2Wins -> GameOutcome.Draw
        player1Wins -> GameOutcome.Player1Won
        player2Wins -> GameOutcome.Player2Won
        else -> GameOutcome.None
    }
}

private fun GameState.checkForPlayerWin(player: GamePiece): Boolean {
    return checkLines(player, GameOutcomeCheckDirection.Vertical)
            || checkLines(player, GameOutcomeCheckDirection.Horizontal)
            || checkLines(player, GameOutcomeCheckDirection.DiagonalTopLeftToBottomRight)
            || checkLines(player, GameOutcomeCheckDirection.DiagonalBottomLeftToTopRight)
}

private fun GameState.checkLines(player: GamePiece, direction: GameOutcomeCheckDirection): Boolean {
    val gridX = gridMainCorner.x
    val gridY = gridMainCorner.y
    val xLimit = gridX + gameConfiguration.gridSize - 1
    val yLimit = gridY + gameConfiguration.gridSize - 1

    return (gridX..xLimit).any { x ->
        (gridY..yLimit).any { y ->
            isWinningLine(x, xLimit, y, yLimit, player, direction)
        }
    }
}

private fun GameState.isWinningLine(startX: Int, xLimit: Int, startY: Int, yLimit: Int, player: GamePiece, direction: GameOutcomeCheckDirection): Boolean {
    if (!hasSpaceForWinCondition(startX, xLimit, startY, yLimit, direction)) return false

    try {
        for (i in 0 until gameConfiguration.winCondition) {
            val currentPiece = when (direction) {
                GameOutcomeCheckDirection.Vertical -> gameBoard[startX][startY + i]
                GameOutcomeCheckDirection.Horizontal -> gameBoard[startX + i][startY]
                GameOutcomeCheckDirection.DiagonalTopLeftToBottomRight -> gameBoard[startX + i][startY + i]
                GameOutcomeCheckDirection.DiagonalBottomLeftToTopRight -> gameBoard[startX + i][startY - i]
            }

            if (currentPiece != player) return false
        }
    } catch (e: IndexOutOfBoundsException) {
        return false
    }

    return true
}

private fun GameState.hasSpaceForWinCondition(startX: Int, xLimit: Int, startY: Int, yLimit: Int, direction: GameOutcomeCheckDirection): Boolean =
    when (direction) {
        GameOutcomeCheckDirection.Vertical -> startY + gameConfiguration.winCondition - 1 <= yLimit
        GameOutcomeCheckDirection.Horizontal -> startX + gameConfiguration.winCondition - 1 <= xLimit
        GameOutcomeCheckDirection.DiagonalTopLeftToBottomRight ->
            startX + gameConfiguration.winCondition - 1 <= xLimit && startY + gameConfiguration.winCondition - 1 <= yLimit
        GameOutcomeCheckDirection.DiagonalBottomLeftToTopRight ->
            startX + gameConfiguration.winCondition - 1 <= xLimit && startY - gameConfiguration.winCondition + 1 >= gridMainCorner.y
    }

enum class GameOutcome {
    None, Player1Won, Player2Won, Draw
}

enum class GameOutcomeCheckDirection {
    Vertical, Horizontal, DiagonalTopLeftToBottomRight, DiagonalBottomLeftToTopRight
}
