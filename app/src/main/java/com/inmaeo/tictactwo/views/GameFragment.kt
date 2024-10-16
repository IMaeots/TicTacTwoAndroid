package com.inmaeo.tictactwo.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.inmaeo.tictactwo.databinding.FragmentGameBinding
import com.inmaeo.tictactwo.viewmodels.GamePiece
import com.inmaeo.tictactwo.viewmodels.GameViewModel
import com.inmaeo.tictactwo.viewmodels.getTextByPiece

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private lateinit var gridLayout: GridLayout

    private val viewModel: GameViewModel by viewModels()

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
        binding.resetButton.setOnClickListener { resetGame() }
    }

    private fun setupGrid() {
        for (i in 0 until viewModel.gridSize) {
            for (j in 0 until viewModel.gridSize) {
                val button = Button(requireContext()).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        rowSpec = GridLayout.spec(i, 1f)
                        columnSpec = GridLayout.spec(j, 1f)
                    }
                    setOnClickListener {
                        try {
                            binding.gameStatusTextView.text = viewModel.onCellClick(this, i, j)
                        } catch (e: Exception) {
                            // TODO: button taken handling
                        }
                    }
                }
                gridLayout.addView(button)
            }
        }
    }

    private fun resetGame() {
        for (i in 0 until gridLayout.childCount) {
            (gridLayout.getChildAt(i) as Button).text = GamePiece.EMPTY.getTextByPiece()
        }
        binding.gameStatusTextView.text = viewModel.resetGame()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
