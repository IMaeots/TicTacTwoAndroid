package com.inmaeo.tictactwo

import android.app.Application
import com.inmaeo.tictactwo.data.db.GameDbHelper
import com.inmaeo.tictactwo.data.repository.GameRepository

class TicTacTwoApp : Application() {

    private val dbHelper: GameDbHelper by lazy {
        GameDbHelper(this)
    }

    val gameRepository: GameRepository by lazy {
        GameRepository(dbHelper)
    }
}
