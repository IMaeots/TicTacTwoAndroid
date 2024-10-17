package com.inmaeo.tictactwo.domain

class GameOutcomeChecker(private val gameState: GameState) {
    private val config = gameState.gameConfiguration
    private val gameBoard = gameState.gameBoard
    private val gridX = gameState.gridX
    private val gridY = gameState.gridY

    fun checkGameOutcome(): GameOutcome {
        val player1Wins = checkForPlayerWin(GamePiece.Player1)
        val player2Wins = checkForPlayerWin(GamePiece.Player2)

        return when {
            player1Wins && player2Wins -> GameOutcome.Draw
            player1Wins -> GameOutcome.Player1Won
            player2Wins -> GameOutcome.Player2Won
            else -> GameOutcome.None
        }
    }

    private fun checkForPlayerWin(player: GamePiece): Boolean {
        return checkLines(player, GameOutcomeCheckDirection.Vertical)
                || checkLines(player, GameOutcomeCheckDirection.Horizontal)
                || checkLines(player, GameOutcomeCheckDirection.DiagonalTopLeftToBottomRight)
                || checkLines(player, GameOutcomeCheckDirection.DiagonalBottomLeftToTopRight)
    }

    private fun checkLines(player: GamePiece, direction: GameOutcomeCheckDirection): Boolean {
        val xLimit = gridX + config.gridSize - 1
        val yLimit = gridY + config.gridSize - 1

        for (x in gridX..xLimit) {
            for (y in gridY..yLimit) {
                if (isWinningLine(x, y, player, direction)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isWinningLine(startX: Int, startY: Int, player: GamePiece, direction: GameOutcomeCheckDirection): Boolean {
        try {
            for (i in 0 until config.winCondition) {
                val currentPiece = when (direction) {
                    GameOutcomeCheckDirection.Vertical -> gameBoard[startX][startY + i]
                    GameOutcomeCheckDirection.Horizontal -> gameBoard[startX + i][startY]
                    GameOutcomeCheckDirection.DiagonalTopLeftToBottomRight -> gameBoard[startX + i][startY + i]
                    GameOutcomeCheckDirection.DiagonalBottomLeftToTopRight -> gameBoard[startX + i][startY - i]
                }

                if (currentPiece != player) return false
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            return false
        }

        return true
    }
}

enum class GameOutcome {
    None, Player1Won, Player2Won, Draw
}

enum class GameOutcomeCheckDirection {
    Vertical, Horizontal, DiagonalTopLeftToBottomRight, DiagonalBottomLeftToTopRight
}
