package com.inmaeo.tictactwo.domain

class GameLogic(gameConfiguration: GameConfiguration) {
    val gameState: GameState

    val boardDimX: Int
        get() = gameState.gameBoard.size
    val boardDimY: Int
        get() = gameState.gameBoard[0].size
    val gridSize: Int
        get() = gameState.gameConfiguration.gridSize
    val nextMoveBy: GamePiece
        get() = gameState.nextMoveBy
    val gridX: Int
        get() = gameState.gridX
    val gridY: Int
        get() = gameState.gridY

    init {
        val gameBoard = Array(gameConfiguration.boardSize) {
            Array(gameConfiguration.boardSize) { GamePiece.Empty }
        }
        gameState = GameState(gameBoard, gameConfiguration)
    }

    fun getGameConfigName(): String {
        return gameState.gameConfiguration.name
    }

    fun getGameBoard(): Array<Array<GamePiece>> {
        return gameState.gameBoard.map { it.clone() }.toTypedArray()
    }

    fun checkForGameEnd(): GameOutcome {
        val gameOutcomeChecker = GameOutcomeChecker(gameState)
        return gameOutcomeChecker.checkGameOutcome()
    }

    fun canPlaceMarker(): Boolean {
        return when (gameState.nextMoveBy) {
            GamePiece.Player1 -> gameState.player1MarkersPlaced < gameState.gameConfiguration.numberOfMarkers
            GamePiece.Player2 -> gameState.player2MarkersPlaced < gameState.gameConfiguration.numberOfMarkers
            else -> false
        }
    }

    fun placeMarker(x: Int, y: Int): Boolean {
        if (gameState.gameBoard[x][y] != GamePiece.Empty) {
            return false
        }

        gameState.gameBoard[x][y] = gameState.nextMoveBy

        when (gameState.nextMoveBy) {
            GamePiece.Player1 -> gameState.player1MarkersPlaced++
            GamePiece.Player2 -> gameState.player2MarkersPlaced++
            GamePiece.Empty -> throw IllegalArgumentException("Marker placement was tryied to be done by Empty GamePiece...")
        }

        moveMade()
        return true
    }

    fun canMoveThatMarker(currentX: Int, currentY: Int): Boolean {
        return gameState.gameBoard[currentX][currentY] == gameState.nextMoveBy
    }

    fun moveMarker(oldX: Int, oldY: Int, newX: Int, newY: Int): Boolean {
        if (gameState.gameBoard[oldX][oldY] == gameState.nextMoveBy && gameState.gameBoard[newX][newY] == GamePiece.Empty) {
            gameState.gameBoard[oldX][oldY] = GamePiece.Empty
            gameState.gameBoard[newX][newY] = gameState.nextMoveBy
            moveMade()
            return true
        }
        return false
    }

    fun canMoveGrid(): Boolean {
        return gameState.gridX >= 0 && gameState.gridY >= 0
    }

    fun moveGrid(newGridX: Int, newGridY: Int): Boolean {
        val gridSize = gameState.gameConfiguration.gridSize
        val boardSize = gameState.gameConfiguration.boardSize

        if (newGridX < 0 || newGridX + gridSize > boardSize || newGridY < 0 || newGridY + gridSize > boardSize) {
            return false
        }

        gameState.gridX = newGridX
        gameState.gridY = newGridY

        moveMade()
        return true
    }

    private fun moveMade() {
        gameState.nextMoveBy = if (gameState.nextMoveBy == GamePiece.Player1) GamePiece.Player2 else GamePiece.Player1
        gameState.moveCount++
    }

    fun resetGame() {
        val boardSize = gameState.gameConfiguration.boardSize
        gameState.gameBoard = Array(boardSize) { Array(boardSize) { GamePiece.Empty } }
        gameState.nextMoveBy = GamePiece.Player1
        gameState.player1MarkersPlaced = 0
        gameState.player2MarkersPlaced = 0
    }
}
