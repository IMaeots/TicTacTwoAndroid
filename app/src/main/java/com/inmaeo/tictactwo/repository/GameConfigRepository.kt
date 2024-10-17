package com.inmaeo.tictactwo.repository

import com.inmaeo.tictactwo.domain.GameConfiguration

class GameConfigRepository {

    fun getGameConfiguration(): GameConfiguration {
        return GameConfiguration(
            boardSize = 5,
            gridSize = 5,
            winCondition = 3,
            numberOfMarkers = 3,
            name = "TicTacToe"
        )
    }
}
