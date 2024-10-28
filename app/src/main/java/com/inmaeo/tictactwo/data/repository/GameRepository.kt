package com.inmaeo.tictactwo.data.repository

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.google.gson.Gson
import com.inmaeo.tictactwo.data.db.GameDbHelper
import com.inmaeo.tictactwo.domain.GameConfiguration
import com.inmaeo.tictactwo.domain.GameState

class GameRepository(
    private val dbHelper: GameDbHelper
) {
    
    private val gson = Gson()

    fun getGameConfiguration(): GameConfiguration {
        return GameConfiguration(
            boardSize = 5,
            gridSize = 3,
            winCondition = 3,
            numberOfMarkers = 5,
            name = "TicTacTwo"
        )
    }

    fun saveGameState(gameName: String, gameState: GameState) {
        val db = dbHelper.writableDatabase
        val json = gson.toJson(gameState)
        val values = ContentValues().apply {
            put(GameDbHelper.COLUMN_GAME_NAME, gameName)
            put(GameDbHelper.COLUMN_GAME_STATE, json)
        }
        db.insertWithOnConflict(GameDbHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun loadGameState(gameName: String): GameState? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            GameDbHelper.TABLE_NAME,
            arrayOf(GameDbHelper.COLUMN_GAME_STATE),
            "${GameDbHelper.COLUMN_GAME_NAME} = ?",
            arrayOf(gameName),
            null, null, null
        )

        return cursor.use { c ->
            if (c.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(GameDbHelper.COLUMN_GAME_STATE)
                if (columnIndex != -1) {
                    val gameStateJson = cursor.getString(columnIndex)
                    gson.fromJson(gameStateJson, GameState::class.java)
                } else {
                    Log.e("DatabaseError", "${GameDbHelper.COLUMN_GAME_STATE} does not exist in the query result")
                    null
                }
            } else {
                null
            }
        }.also { db.close() }
    }

    fun getSavedGameNames(): List<String> {
        val db = dbHelper.readableDatabase
        val gameNames = mutableListOf<String>()
        val cursor = db.query(
            GameDbHelper.TABLE_NAME,
            arrayOf(GameDbHelper.COLUMN_GAME_NAME),
            null, null, null, null, null
        )

        cursor.use { c ->
            val columnIndex = c.getColumnIndex(GameDbHelper.COLUMN_GAME_NAME)
            if (columnIndex != -1) {
                while (c.moveToNext()) {
                    val gameName = c.getString(columnIndex)
                    gameNames.add(gameName)
                }
            } else {
                Log.e("DatabaseError", "Column ${GameDbHelper.COLUMN_GAME_NAME} does not exist in the query result")
            }
        }

        db.close()
        return gameNames
    }

    fun clearSavedGames() {
        val db = dbHelper.writableDatabase
        db.execSQL("DELETE FROM ${GameDbHelper.TABLE_NAME}")
        db.close()
    }
}
