package com.inmaeo.tictactwo.domain

import com.inmaeo.tictactwo.viewmodels.MoveGridDirection

class GameLogic {

    fun checkForGameEnd(gameState: GameState): GameOutcome = GameOutcomeChecker(gameState).checkGameOutcome()

    fun hasGameFinished(gameState: GameState) = gameState.gameOutcome != GameOutcome.None

    fun canPlaceMarker(gameState: GameState): Boolean {
        return when (gameState.nextMoveBy) {
            GamePiece.Player1 -> gameState.player1MarkersPlaced < gameState.gameConfiguration.numberOfMarkers
            GamePiece.Player2 -> gameState.player2MarkersPlaced < gameState.gameConfiguration.numberOfMarkers
            else -> false
        }
    }

    fun placeMarker(gameState: GameState, x: Int, y: Int): GameState? {
        val newBoard = gameState.gameBoard.map { it.toMutableList() }
        if (x < 0 || y < 0 || x >= gameState.gameConfiguration.boardSize || y >= gameState.gameConfiguration.boardSize ||
            newBoard[x][y] != GamePiece.Empty) {
            return null
        }
        newBoard[x][y] = gameState.nextMoveBy

        return gameState.copy(
            gameBoard = newBoard.toList(),
            player1MarkersPlaced = if (gameState.nextMoveBy == GamePiece.Player1) gameState.player1MarkersPlaced + 1 else gameState.player1MarkersPlaced,
            player2MarkersPlaced = if (gameState.nextMoveBy == GamePiece.Player2) gameState.player2MarkersPlaced + 1 else gameState.player2MarkersPlaced,
            nextMoveBy = if (gameState.nextMoveBy == GamePiece.Player1) GamePiece.Player2 else GamePiece.Player1,
            moveCount = gameState.moveCount + 1
        )
    }

    fun canMoveThatMarker(gameState: GameState, currentX: Int, currentY: Int): Boolean {
        return gameState.gameBoard[currentX][currentY] == gameState.nextMoveBy
    }

    fun selectMarker(gameState: GameState, currentX: Int, currentY: Int) {
        gameState.selectedMarker = LocationCoordinates(currentX, currentY)
    }

    fun moveMarker(gameState: GameState, oldX: Int, oldY: Int, newX: Int, newY: Int): GameState? {
        if (oldX < 0 || oldY < 0 || newX < 0 || newY < 0 ||
            oldX >= gameState.gameConfiguration.boardSize || oldY >= gameState.gameConfiguration.boardSize ||
            newX >= gameState.gameConfiguration.boardSize || newY >= gameState.gameConfiguration.boardSize ||
            gameState.gameBoard[oldX][oldY] != gameState.nextMoveBy || gameState.gameBoard[newX][newY] != GamePiece.Empty) {
            return null
        }

        val newBoard = gameState.gameBoard.map { it.toMutableList() }
        newBoard[oldX][oldY] = GamePiece.Empty
        newBoard[newX][newY] = gameState.nextMoveBy

        return gameState.copy(
            gameBoard = newBoard,
            nextMoveBy = if (gameState.nextMoveBy == GamePiece.Player1) GamePiece.Player2 else GamePiece.Player1,
            moveCount = gameState.moveCount + 1,
            selectedMarker = null
        )
    }

    fun canMoveGrid(gameState: GameState): Boolean {
        return gameState.gridMainCorner.x >= 0 && gameState.gridMainCorner.y >= 0 &&
                gameState.gridMainCorner.x + gameState.gameConfiguration.gridSize <= gameState.gameConfiguration.boardSize &&
                gameState.gridMainCorner.y + gameState.gameConfiguration.gridSize <= gameState.gameConfiguration.boardSize
    }

    fun moveGrid(gameState: GameState, direction: MoveGridDirection): GameState? {
        val (gridX, gridY) = gameState.run {
            when (direction) {
                MoveGridDirection.LEFT -> gridMainCorner.x - 1 to gridMainCorner.y
                MoveGridDirection.RIGHT -> gridMainCorner.x + 1 to gridMainCorner.y
                MoveGridDirection.UP -> gridMainCorner.x to gridMainCorner.y - 1
                MoveGridDirection.DOWN -> gridMainCorner.x to gridMainCorner.y + 1
            }
        }

        val gridSize = gameState.gameConfiguration.gridSize
        val boardSize = gameState.gameConfiguration.boardSize
        if (gridX !in 0..(boardSize - gridSize) || gridY !in 0..(boardSize - gridSize)) return null

        return gameState.copy(gridMainCorner = LocationCoordinates(gridX, gridY))
    }

    fun isButtonPartOfGrid(gameState: GameState, row: Int, col: Int): Boolean {
        val gridX = gameState.gridMainCorner.x
        val gridY = gameState.gridMainCorner.y
        val gridSize = gameState.gameConfiguration.gridSize

        return row in gridX until (gridX + gridSize) && col in gridY until (gridY + gridSize)
    }

    fun isGamePieceSelected(gameState: GameState, row: Int, col: Int): Boolean {
        return gameState.selectedMarker?.x == row && gameState.selectedMarker?.y == col
    }

    fun resetGame(gameState: GameState): GameState {
        val boardSize = gameState.gameConfiguration.boardSize
        val newBoard = MutableList(boardSize) { MutableList(boardSize) { GamePiece.Empty } }

        return gameState.copy(
            gameBoard = newBoard,
            gameOutcome = GameOutcome.None,
            nextMoveBy = GamePiece.Player1,
            gridMainCorner = LocationCoordinates(
                x = (boardSize - gameState.gameConfiguration.gridSize) / 2,
                y = (boardSize - gameState.gameConfiguration.gridSize) / 2
            ),
            player1MarkersPlaced = 0,
            player2MarkersPlaced = 0,
            moveCount = 0
        )
    }
}
