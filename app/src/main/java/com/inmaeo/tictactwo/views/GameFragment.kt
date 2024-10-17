package com.inmaeo.tictactwo.views

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.inmaeo.tictactwo.CustomButton
import com.inmaeo.tictactwo.R
import com.inmaeo.tictactwo.TicTacTwoApp
import com.inmaeo.tictactwo.databinding.FragmentGameBinding
import com.inmaeo.tictactwo.domain.GamePiece
import com.inmaeo.tictactwo.viewmodels.GameViewModel
import com.inmaeo.tictactwo.viewmodels.GameViewModelFactory
import com.inmaeo.tictactwo.viewmodels.GameViewState
import kotlinx.coroutines.launch

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: GameViewModel
    private lateinit var gridLayout: GridLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gameConfigRepository = (requireActivity().application as TicTacTwoApp).gameConfigRepository
        viewModel = ViewModelProvider(this, GameViewModelFactory(gameConfigRepository))[GameViewModel::class.java]


        gridLayout = binding.gridLayoutContainer
        setupBoard()
        viewModel.lastGameState?.let { updateGrid(it) }
        subscribeToGameState()

        binding.resetButton.setOnClickListener { viewModel.resetGame() }
        binding.gameStatusTextView.text = getString(R.string.game_status_players_turn, GamePiece.Player1)
    }

    private fun subscribeToGameState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.gameState.collect { gameState ->
                    updateGrid(gameState)
                }
            }
        }
    }

    private fun setupBoard() {
        for (i in 0 until 5) {
            for (j in 0 until 5) {
                val button = createGridButton(i, j)
                gridLayout.addView(button)
            }
        }
    }

    private fun createGridButton(row: Int, col: Int): CustomButton {
        val isGridButton = row in 1..3 && col in 1..3 // TODO: Later do with grid logic

        return CustomButton(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 0
                rowSpec = GridLayout.spec(row, 1f)
                columnSpec = GridLayout.spec(col, 1f)
            }
            textSize = 16f

            borderColor = if (isGridButton) {
                Color.RED
            } else {
                Color.BLACK
            }

            setOnClickListener {
                Log.d("GameFragment", "Button clicked at row: $row, col: $col")
                val message = viewModel.onCellClick(row, col)
                binding.gameStatusTextView.text = message
            }
        }
    }

    private fun updateGrid(gameState: GameViewState) {
        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i) as CustomButton
            val row = i / gameState.gameConfiguration.boardSize
            val col = i % gameState.gameConfiguration.boardSize

            button.piece = gameState.gameBoard[row][col]

            val isGridButton = row in 1..3 && col in 1..3 // TODO: Later do with grid logic
            button.borderColor = if (isGridButton) {
                Color.RED
            } else {
                Color.BLACK
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
