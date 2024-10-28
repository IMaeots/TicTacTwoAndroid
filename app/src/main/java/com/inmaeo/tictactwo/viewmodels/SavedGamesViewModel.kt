package com.inmaeo.tictactwo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inmaeo.tictactwo.data.repository.GameRepository

class SavedGamesViewModel(private val gameRepository: GameRepository) : ViewModel() {
    private val _gameNames = MutableLiveData<List<String>>()
    val gameNames: LiveData<List<String>> get() = _gameNames

    init {
        loadSavedGames()
    }

    private fun loadSavedGames() {
        _gameNames.value = gameRepository.getSavedGameNames()
    }

    fun clearSavedGames() {
        gameRepository.clearSavedGames()
        loadSavedGames()
    }
}
