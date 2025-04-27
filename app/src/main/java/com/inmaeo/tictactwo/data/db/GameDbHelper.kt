package com.inmaeo.tictactwo.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GameDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COLUMN_GAME_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_GAME_NAME TEXT UNIQUE NOT NULL,
                $COLUMN_GAME_STATE TEXT NOT NULL
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "game.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "SavedGame"
        const val COLUMN_GAME_ID = "game_id"
        const val COLUMN_GAME_NAME = "game_name"
        const val COLUMN_GAME_STATE = "game_state"

        @Volatile private var instance: GameDbHelper? = null

        fun getInstance(context: Context): GameDbHelper =
            instance ?: synchronized(this) {
                instance ?: GameDbHelper(context.applicationContext).also { instance = it }
            }
    }
}
