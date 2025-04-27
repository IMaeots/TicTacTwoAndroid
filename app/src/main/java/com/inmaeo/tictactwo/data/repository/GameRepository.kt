package com.inmaeo.tictactwo.data.repository

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.google.gson.Gson
import com.inmaeo.tictactwo.data.db.GameDbHelper
import com.inmaeo.tictactwo.domain.GameConfiguration
import com.inmaeo.tictactwo.domain.gamestate.GameState

class GameRepository(
    private val dbHelper: GameDbHelper
) {
    
    private val gson = Gson()

    fun getGameConfiguration(): GameConfiguration {
        return GameConfiguration(
            boardSize = 5,
            gridSize = 3,
            winCondition = 3,
            numberOfMarkers = 6,
            name = "TicTacTwo"
        )
    }

    fun saveGameState(gameName: String, gameState: GameState): SaveGameResult {
        val cleanGameState = gameState.copy(error = null)
        
        if (checkIfGameNameExists(gameName)) return SaveGameResult.Failure("Game with the same name already exists.")

        val db = dbHelper.writableDatabase
        return try {
            val jsonGameState = gson.toJson(cleanGameState)
            val values = ContentValues().apply {
                put(GameDbHelper.COLUMN_GAME_NAME, gameName)
                put(GameDbHelper.COLUMN_GAME_STATE, jsonGameState)
            }

            val result = db.insertWithOnConflict(GameDbHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_FAIL)

            if (result != -1L) SaveGameResult.Success else SaveGameResult.Failure("Failed to save game state.")
        } catch (e: Exception) {
            Log.e("GameRepository", "Error saving game: ${e.message}")
            SaveGameResult.Failure("Error occurred while saving")
        } finally {
            db.close()
        }
    }

    private fun checkIfGameNameExists(gameName: String): Boolean {
        val db = dbHelper.readableDatabase
        return try {
            db.query(
                GameDbHelper.TABLE_NAME,
                arrayOf(GameDbHelper.COLUMN_GAME_NAME),
                "${GameDbHelper.COLUMN_GAME_NAME} = ?",
                arrayOf(gameName),
                null, null, null, "1"
            ).use { cursor ->
                cursor.count > 0
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "Error checking game name: ${e.message}")
            false
        } finally {
            db.close()
        }
    }

    fun loadGameState(gameName: String): GameState? {
        val db = dbHelper.readableDatabase
        try {
            val cursor = db.query(
                GameDbHelper.TABLE_NAME,
                arrayOf(GameDbHelper.COLUMN_GAME_STATE),
                "${GameDbHelper.COLUMN_GAME_NAME} = ?",
                arrayOf(gameName),
                null, null, null
            )

            return cursor.use { c ->
                if (c.moveToFirst()) {
                    val columnIndex = c.getColumnIndex(GameDbHelper.COLUMN_GAME_STATE)
                    if (columnIndex != -1) {
                        try {
                            val gameStateJson = c.getString(columnIndex)
                            gson.fromJson(gameStateJson, GameState::class.java)
                        } catch (e: Exception) {
                            Log.e("GameRepository", "Error parsing game state: ${e.message}")
                            null
                        }
                    } else {
                        Log.e("GameRepository", "Column does not exist in query result")
                        null
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "Error loading game: ${e.message}")
            return null
        } finally {
            db.close()
        }
    }

    fun getSavedGameNames(): List<String> {
        val db = dbHelper.readableDatabase
        val gameNames = mutableListOf<String>()
        
        try {
            db.query(
                GameDbHelper.TABLE_NAME,
                arrayOf(GameDbHelper.COLUMN_GAME_NAME),
                null, null, null, null, null
            ).use { cursor ->
                val columnIndex = cursor.getColumnIndex(GameDbHelper.COLUMN_GAME_NAME)
                if (columnIndex != -1) {
                    while (cursor.moveToNext()) {
                        gameNames.add(cursor.getString(columnIndex))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "Error getting saved games: ${e.message}")
        } finally {
            db.close()
        }
        
        return gameNames
    }

    fun clearSavedGames() {
        val db = dbHelper.writableDatabase
        try {
            db.execSQL("DELETE FROM ${GameDbHelper.TABLE_NAME}")
        } catch (e: Exception) {
            Log.e("GameRepository", "Error clearing saved games: ${e.message}")
        } finally {
            db.close()
        }
    }
}

sealed interface SaveGameResult {
    data object Success : SaveGameResult
    data class Failure(val message: String) : SaveGameResult
}
