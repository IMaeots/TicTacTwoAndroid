package com.inmaeo.tictactwo.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.inmaeo.tictactwo.TicTacTwoApp
import com.inmaeo.tictactwo.databinding.FragmentSavedGamesBinding
import com.inmaeo.tictactwo.viewmodels.SavedGamesViewModel
import com.inmaeo.tictactwo.viewmodels.SavedGamesViewModelFactory
import com.inmaeo.tictactwo.views.components.SavedGamesAdapter

class SavedGamesFragment : Fragment() {

    private lateinit var binding: FragmentSavedGamesBinding

    private val viewModel: SavedGamesViewModel by viewModels {
        SavedGamesViewModelFactory((requireActivity().application as TicTacTwoApp).gameRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FragmentSavedGamesBinding.inflate(layoutInflater, container, false).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            viewModel.gameNames.observe(viewLifecycleOwner) { gameNames ->
                if (gameNames.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    noSavedGamesTextView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    noSavedGamesTextView.visibility = View.GONE

                    val adapter = SavedGamesAdapter(gameNames) { gameName ->
                        navigateToGameFragment(gameName)
                    }
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.adapter = adapter
                }
            }

            clearGamesButton.setOnClickListener {
                viewModel.clearSavedGames()
            }
        }
    }

    private fun navigateToGameFragment(gameName: String) {
        val action = SavedGamesFragmentDirections.actionSavedGamesFragmentToGameFragment(gameName)
        findNavController().navigate(action)
    }
}
