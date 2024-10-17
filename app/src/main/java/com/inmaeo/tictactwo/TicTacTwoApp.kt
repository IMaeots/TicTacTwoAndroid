package com.inmaeo.tictactwo

import android.app.Application
import com.inmaeo.tictactwo.repository.GameConfigRepository

class TicTacTwoApp : Application() {

    val gameConfigRepository: GameConfigRepository by lazy {
        GameConfigRepository()
    }
}
