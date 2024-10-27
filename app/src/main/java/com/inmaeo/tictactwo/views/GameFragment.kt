package com.inmaeo.tictactwo.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.inmaeo.tictactwo.TicTacTwoApp
import com.inmaeo.tictactwo.databinding.FragmentGameBinding
import com.inmaeo.tictactwo.domain.GameOutcome
import com.inmaeo.tictactwo.domain.GamePiece
import com.inmaeo.tictactwo.domain.GameState
import com.inmaeo.tictactwo.viewmodels.GameViewModel
import com.inmaeo.tictactwo.viewmodels.GameViewModelFactory
import com.inmaeo.tictactwo.views.components.CustomButton
import kotlinx.coroutines.launch

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: GameViewModel
    private lateinit var gridLayout: GridLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gameConfigRepository = (requireActivity().application as TicTacTwoApp).gameConfigRepository
        viewModel = ViewModelProvider(this, GameViewModelFactory(gameConfigRepository))[GameViewModel::class.java]

        setUpLayout()
        subscribeToGameState()
    }

    private fun setUpLayout() {
        gridLayout = binding.gridLayoutContainer
        binding.resetButton.setOnClickListener { viewModel.resetGame() }
    }

    private fun subscribeToGameState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.gameState.collect { gameState ->
                    handleGameState(gameState)
                }
            }
        }
    }

    private fun handleGameState(gameState: GameState) {
        drawBoard(gameState)
        with(binding) {
            gameStatusTextView.text = when(gameState.gameOutcome) {
                GameOutcome.None -> "It is ${gameState.nextMoveBy}'s turn"
                GameOutcome.Player1Won -> "Player 1 won!"
                GameOutcome.Player2Won -> "Player 2 won!"
                GameOutcome.Draw -> "Game ended in a DRAW! Both won!"
            }
            resetButton.visibility = if (gameState.gameOutcome == GameOutcome.None) View.GONE else View.VISIBLE
        }
    }

    private fun drawBoard(gameState: GameState) {
        if (gridLayout.childCount == 0) {
            initializeEmptyBoard(gameState)
        }

        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i) as CustomButton
            val row = i / gameState.gameConfiguration.boardSize
            val col = i % gameState.gameConfiguration.boardSize

            button.piece = gameState.gameBoard[row][col]
            button.isGridButton = viewModel.isButtonPartOfGrid(gameState, row, col)
            button.isButtonSelected = viewModel.isGamePieceSelected(gameState, row, col)
        }
    }

    private fun initializeEmptyBoard(gameState: GameState) {
        gridLayout.removeAllViews()
        val boardSize = gameState.gameConfiguration.boardSize
        gridLayout.rowCount = boardSize
        gridLayout.columnCount = boardSize

        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                val button = createInitialButton(row, col, gameState)
                gridLayout.addView(button)
            }
        }
    }

    private fun createInitialButton(row: Int, col: Int, gameState: GameState): CustomButton {
        return CustomButton(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 0
                rowSpec = GridLayout.spec(row, 1f)
                columnSpec = GridLayout.spec(col, 1f)
            }
            piece = GamePiece.Empty // TODO: Modify in-case of starting from saved game.
            isGridButton = viewModel.isButtonPartOfGrid(gameState, row, col)
            isButtonSelected = viewModel.isGamePieceSelected(gameState, row, col)
            setOnClickListener {
                viewModel.handleGameButtonClick(row, col)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
