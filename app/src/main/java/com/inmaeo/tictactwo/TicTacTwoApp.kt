package com.inmaeo.tictactwo

import android.app.Application
import android.content.Context
import com.inmaeo.tictactwo.data.db.GameDbHelper
import com.inmaeo.tictactwo.data.repository.GameRepository
import com.inmaeo.tictactwo.service.BackgroundMusicManager

class TicTacTwoApp : Application() {

    private val dbHelper: GameDbHelper by lazy { GameDbHelper(this) }
    val gameRepository: GameRepository by lazy { GameRepository(dbHelper) }

    companion object {
        const val SETTINGS = "settings"
        const val MUSIC = "background_music"
        const val STAT1 = "player1_wins"
        const val STAT2 = "player2_wins"
        const val STAT3 = "draws"
    }
}
