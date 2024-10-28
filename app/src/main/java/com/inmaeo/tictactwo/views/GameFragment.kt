package com.inmaeo.tictactwo.views

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.inmaeo.tictactwo.R
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

        val args: GameFragmentArgs by navArgs()
        val gameRepository = (requireActivity().application as TicTacTwoApp).gameRepository
        viewModel =
            ViewModelProvider(this, GameViewModelFactory(gameRepository))[GameViewModel::class.java]

        gridLayout = binding.gridLayoutContainer
        subscribeToGameState()
        viewModel.initializeGame(args.gameName)
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

            if (gameState.gameOutcome == GameOutcome.None) {
                actionButton.text = context?.getString(R.string.save_game_button_text)
                actionButton.setOnClickListener {
                    promptForGameName(gameState)
                }
            } else {
                actionButton.text = context?.getString(R.string.reset_button_text)
                actionButton.setOnClickListener { viewModel.resetGame() }
            }
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
            piece = GamePiece.Empty
            isGridButton = viewModel.isButtonPartOfGrid(gameState, row, col)
            isButtonSelected = viewModel.isGamePieceSelected(gameState, row, col)
            setOnClickListener {
                viewModel.handleGameButtonClick(row, col)
            }
        }
    }

    private fun promptForGameName(gameState: GameState) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.save_game_button_text))

        val dialogView = layoutInflater.inflate(R.layout.dialog_save_game, null)
        builder.setView(dialogView)

        val input = dialogView.findViewById<EditText>(R.id.gameNameInput)

        builder.setPositiveButton(getString(R.string.general_save)) { dialog, _ ->
            val gameName = input.text.toString()
            if (isValidGameName(gameName)) {
                viewModel.saveGame(gameName, gameState)
                Toast.makeText(requireContext(), "Your game was saved under the name of: $gameName!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Game name provided does not match validation rules.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.general_cancel)) { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun isValidGameName(name: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9]+$")
        return name.isNotBlank() && regex.matches(name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
