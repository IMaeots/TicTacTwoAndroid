package com.inmaeo.tictactwo.domain

class GameOutcomeChecker(gameState: GameState) {
    private val config = gameState.gameConfiguration
    private val gameBoard = gameState.gameBoard
    private val gridX = gameState.gridMainCorner.x
    private val gridY = gameState.gridMainCorner.y

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

        return (gridX..xLimit).any { x ->
            (gridY..yLimit).any { y ->
                isWinningLine(x, xLimit, y, yLimit, player, direction)
            }
        }
    }

    private fun isWinningLine(startX: Int, xLimit: Int, startY: Int, yLimit: Int, player: GamePiece, direction: GameOutcomeCheckDirection): Boolean {
        if (!hasSpaceForWinCondition(startX, xLimit, startY, yLimit, direction)) return false

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
        } catch (e: IndexOutOfBoundsException) {
            return false
        }

        return true
    }

    private fun hasSpaceForWinCondition(startX: Int, xLimit: Int, startY: Int, yLimit: Int, direction: GameOutcomeCheckDirection): Boolean =
        when (direction) {
            GameOutcomeCheckDirection.Vertical -> startY + config.winCondition - 1 <= yLimit
            GameOutcomeCheckDirection.Horizontal -> startX + config.winCondition - 1 <= xLimit
            GameOutcomeCheckDirection.DiagonalTopLeftToBottomRight ->
                startX + config.winCondition - 1 <= xLimit && startY + config.winCondition - 1 <= yLimit
            GameOutcomeCheckDirection.DiagonalBottomLeftToTopRight ->
                startX + config.winCondition - 1 <= xLimit && startY - config.winCondition + 1 >= gridY
        }
}

enum class GameOutcome {
    None, Player1Won, Player2Won, Draw
}

enum class GameOutcomeCheckDirection {
    Vertical, Horizontal, DiagonalTopLeftToBottomRight, DiagonalBottomLeftToTopRight
}
