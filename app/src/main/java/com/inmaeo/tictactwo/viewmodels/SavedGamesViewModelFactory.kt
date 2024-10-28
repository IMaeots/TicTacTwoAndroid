package com.inmaeo.tictactwo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.inmaeo.tictactwo.data.repository.GameRepository

@Suppress("UNCHECKED_CAST")
class SavedGamesViewModelFactory(
    private val gameRepository: GameRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SavedGamesViewModel::class.java) -> {
                SavedGamesViewModel(gameRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
