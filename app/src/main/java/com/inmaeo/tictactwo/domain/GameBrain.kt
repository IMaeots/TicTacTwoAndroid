package com.inmaeo.tictactwo.domain

import com.inmaeo.tictactwo.domain.gamestate.GameOutcome
import com.inmaeo.tictactwo.domain.gamestate.GameState
import com.inmaeo.tictactwo.domain.gamestate.checkGameOutcome
import com.inmaeo.tictactwo.viewmodels.MoveGridDirection

class GameBrain {

    fun checkForGameEnd(gameState: GameState) = gameState.checkGameOutcome()

    fun canPlaceMarker(gameState: GameState): Boolean {
        val maxMarkers = gameState.gameConfiguration.numberOfMarkers
        val hasMarkersLeft = when (gameState.nextMoveBy) {
            GamePiece.Player1 -> gameState.player1MarkersPlaced < maxMarkers
            GamePiece.Player2 -> gameState.player2MarkersPlaced < maxMarkers
            else -> false
        }
        
        return hasMarkersLeft
    }

    fun placeMarker(gameState: GameState, x: Int, y: Int): GameState? {
        if (!isButtonPartOfGrid(gameState, x, y)) return null
        
        val newBoard = gameState.gameBoard.map { it.toMutableList() }
        if (!isValidCoordinate(x, y, gameState.gameConfiguration.boardSize) || newBoard[x][y] != GamePiece.Empty) {
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
        if (gameState.moveCount < gameState.gameConfiguration.numberOfTotalMovesForSpecials) return false
        if (!isButtonPartOfGrid(gameState, currentX, currentY)) return false
        
        return gameState.gameBoard[currentX][currentY] == gameState.nextMoveBy
    }

    fun moveMarker(gameState: GameState, oldX: Int, oldY: Int, newX: Int, newY: Int): GameState? {
        if (!isButtonPartOfGrid(gameState, oldX, oldY) || !isButtonPartOfGrid(gameState, newX, newY)) return null
        
        if (!isValidCoordinate(oldX, oldY, gameState.gameConfiguration.boardSize) ||
            !isValidCoordinate(newX, newY, gameState.gameConfiguration.boardSize) ||
            gameState.gameBoard[oldX][oldY] != gameState.nextMoveBy || gameState.gameBoard[newX][newY] != GamePiece.Empty
        ) {
            return null
        }

        val updatedState = updateBoard(gameState, oldX, oldY, newX, newY)
        return updatedState?.copy(
            nextMoveBy = if (gameState.nextMoveBy == GamePiece.Player1) GamePiece.Player2 else GamePiece.Player1,
            moveCount = gameState.moveCount + 1,
            selectedMarker = null
        )
    }

    fun canMoveGrid(gameState: GameState): Boolean {
        if (gameState.moveCount < gameState.gameConfiguration.numberOfTotalMovesForSpecials) return false
        
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

        return gameState.copy(
            gridMainCorner = LocationCoordinates(gridX, gridY),
            nextMoveBy = if (gameState.nextMoveBy == GamePiece.Player1) GamePiece.Player2 else GamePiece.Player1,
            selectedMarker = null,
            moveCount = gameState.moveCount + 1
        )
    }

    fun isButtonPartOfGrid(gameState: GameState, currentX: Int, currentY: Int): Boolean {
        val gridX = gameState.gridMainCorner.x
        val gridY = gameState.gridMainCorner.y
        val gridSize = gameState.gameConfiguration.gridSize

        return currentX in gridX until (gridX + gridSize) && currentY in gridY until (gridY + gridSize)
    }

    fun isGamePieceSelected(gameState: GameState, currentX: Int, currentY: Int): Boolean {
        return gameState.selectedMarker?.x == currentX && gameState.selectedMarker?.y == currentY
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

    private fun isValidCoordinate(x: Int, y: Int, boardSize: Int): Boolean {
        return x in 0 until boardSize && y in 0 until boardSize
    }

    private fun updateBoard(gameState: GameState, oldX: Int, oldY: Int, newX: Int, newY: Int): GameState? {
        if (!isValidCoordinate(newX, newY, gameState.gameConfiguration.boardSize)) return null
        val newBoard = gameState.gameBoard.map { it.toMutableList() }
        if (oldX >= 0 && oldY >= 0) {
            newBoard[oldX][oldY] = GamePiece.Empty
        }
        newBoard[newX][newY] = gameState.nextMoveBy

        return gameState.copy(gameBoard = newBoard)
    }
}
