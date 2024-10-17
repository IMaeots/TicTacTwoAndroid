package com.inmaeo.tictactwo.views

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.inmaeo.tictactwo.R
import com.inmaeo.tictactwo.TicTacTwoApp
import com.inmaeo.tictactwo.databinding.FragmentGameBinding
import com.inmaeo.tictactwo.domain.GamePiece
import com.inmaeo.tictactwo.domain.getTextByPiece
import com.inmaeo.tictactwo.viewmodels.GameViewModel
import com.inmaeo.tictactwo.viewmodels.GameViewModelFactory

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
        viewModel = ViewModelProvider(this, GameViewModelFactory(gameConfigRepository)).get(GameViewModel::class.java)

        viewModel.gameState.observe(viewLifecycleOwner) { newBoard ->
            updateGrid(newBoard)
        }

        setupGridLayout()
        binding.resetButton.setOnClickListener { viewModel.resetGame() }
        binding.gameStatusTextView.text = getString(R.string.game_status_players_turn, GamePiece.Player1)
    }

    private fun setupGridLayout() {
        gridLayout = GridLayout(requireContext()).apply {
            rowCount = viewModel.gridSize
            columnCount = viewModel.gridSize
            layoutParams = GridLayout.LayoutParams().apply {
                width = GridLayout.LayoutParams.MATCH_PARENT
                height = GridLayout.LayoutParams.MATCH_PARENT
            }
        }

        binding.gridLayoutContainer.addView(gridLayout)
        setupGrid()
    }

    private fun setupGrid() {
        for (i in 0 until viewModel.gridSize) {
            for (j in 0 until viewModel.gridSize) {
                val button = createGridButton(i, j)
                gridLayout.addView(button)
            }
        }
    }

    private fun createGridButton(row: Int, col: Int): Button {
        return Button(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 0
                rowSpec = GridLayout.spec(row, 1f)
                columnSpec = GridLayout.spec(col, 1f)
            }
            background = resources.getDrawable(R.drawable.button_background, null)
            textSize = 24f // Set text size
            setOnClickListener {
                binding.gameStatusTextView.text = viewModel.onCellClick(row, col)
            }
            // Optional: Add padding for better visuals
            setPadding(16, 16, 16, 16)
        }
    }

    private fun updateGrid(newBoard: Array<Array<GamePiece>>) {
        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i) as Button
            val row = i / viewModel.gridSize
            val col = i % viewModel.gridSize
            button.text = newBoard[row][col].getTextByPiece()
            // Update button color based on game state
            button.setBackgroundColor(
                when {
                    newBoard[row][col] == GamePiece.Empty -> Color.LTGRAY // Empty grid cells
                    newBoard[row][col] == GamePiece.Player1 -> Color.BLUE // Player 1's color
                    newBoard[row][col] == GamePiece.Player2 -> Color.GREEN // Player 2's color
                    else -> Color.LTGRAY // Default for any unexpected state
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
