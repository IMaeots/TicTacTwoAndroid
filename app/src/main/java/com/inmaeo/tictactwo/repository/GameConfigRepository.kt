package com.inmaeo.tictactwo.repository

import com.inmaeo.tictactwo.domain.GameConfiguration

class GameConfigRepository {

    fun getGameConfiguration(): GameConfiguration {
        return GameConfiguration(
            boardSize = 5,
            gridSize = 3,
            winCondition = 3,
            numberOfMarkers = 5,
            name = "TicTacTwo"
        )
    }
}
