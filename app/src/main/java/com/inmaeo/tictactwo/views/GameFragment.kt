package com.inmaeo.tictactwo.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
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
import com.inmaeo.tictactwo.TicTacTwoApp.Companion.SETTINGS
import com.inmaeo.tictactwo.TicTacTwoApp.Companion.STAT1
import com.inmaeo.tictactwo.TicTacTwoApp.Companion.STAT2
import com.inmaeo.tictactwo.TicTacTwoApp.Companion.STAT3
import com.inmaeo.tictactwo.data.repository.SaveGameResult
import com.inmaeo.tictactwo.databinding.FragmentGameBinding
import com.inmaeo.tictactwo.domain.GameOutcome
import com.inmaeo.tictactwo.domain.GameState
import com.inmaeo.tictactwo.viewmodels.GameBoardAction
import com.inmaeo.tictactwo.viewmodels.GameViewModel
import com.inmaeo.tictactwo.viewmodels.GameViewModelFactory
import com.inmaeo.tictactwo.viewmodels.MoveGridDirection
import com.inmaeo.tictactwo.views.components.TicTacTwoButton
import kotlinx.coroutines.launch
import kotlin.math.abs

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding
    private lateinit var viewModel: GameViewModel
    private lateinit var gridLayout: GridLayout

    private val args: GameFragmentArgs by navArgs()
    private val sharedPreferences by lazy { requireContext().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE) }
    private val gestureDetector by lazy { GestureDetector(context, SwipeGestureListener()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentGameBinding.inflate(inflater, container, false).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val gameRepository = (requireActivity().application as TicTacTwoApp).gameRepository
        viewModel = ViewModelProvider(this, GameViewModelFactory(gameRepository))[GameViewModel::class.java]

        gridLayout = binding.gridLayoutContainer
        binding.gridLayoutContainer.setGestureDetector(gestureDetector)
        viewModel.initializeGame(args.gameName)
        subscribeToGameState()
    }

    private fun subscribeToGameState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.gameState.collect(::handleGameState)
            }
        }
    }

    private fun handleGameState(gameState: GameState) {
        drawBoard(gameState)
        binding.apply {
            gameStatusTextView.text = getString(
                when (gameState.gameOutcome) {
                    GameOutcome.None -> R.string.game_status_turn
                    GameOutcome.Player1Won -> {
                        Toast.makeText(requireContext(), getString(R.string.game_status_player1_won), Toast.LENGTH_SHORT).show()
                        R.string.game_status_player1_won
                    }
                    GameOutcome.Player2Won -> {
                        Toast.makeText(requireContext(), getString(R.string.game_status_player2_won), Toast.LENGTH_SHORT).show()
                        R.string.game_status_player2_won
                    }
                    GameOutcome.Draw -> {
                        Toast.makeText(requireContext(), getString(R.string.game_status_draw), Toast.LENGTH_SHORT).show()
                        R.string.game_status_draw
                    }
                }, gameState.nextMoveBy
            )

            if (gameState.gameOutcome != GameOutcome.None) updateStatistics(gameState.gameOutcome)

            actionButton.apply {
                text = getString(if (gameState.gameOutcome == GameOutcome.None) R.string.game_action_save else R.string.game_action_reset)
                setOnClickListener {
                    if (gameState.gameOutcome == GameOutcome.None) promptForGameName(gameState)
                    else viewModel.resetGame()
                }
            }
        }
    }

    private fun updateStatistics(outcome: GameOutcome) {
        val statKey = when (outcome) {
            GameOutcome.Player1Won -> STAT1
            GameOutcome.Player2Won -> STAT2
            GameOutcome.Draw -> STAT3
            else -> return
        }
        sharedPreferences.edit().putInt(statKey, sharedPreferences.getInt(statKey, 0) + 1).apply()
    }

    private fun drawBoard(gameState: GameState) =
        binding.gridLayoutContainer.apply {
            val boardSize = gameState.gameConfiguration.boardSize
            if (childCount == 0) initializeEmptyBoard(boardSize)

            for (i in 0 until childCount) {
                val row = i / boardSize
                val col = i % boardSize
                (getChildAt(i) as TicTacTwoButton).apply {
                    piece = viewModel.getGamePiece(gameState, row, col)
                    isButtonSelected = viewModel.isGamePieceSelected(gameState, row, col)
                    isGridButton = viewModel.isButtonPartOfGrid(gameState, row, col)
                }
            }
        }

    private fun initializeEmptyBoard(size: Int) =
        binding.gridLayoutContainer.apply {
            removeAllViews()
            rowCount = size
            columnCount = size
            repeat(size * size) { index ->
                val row = index / size
                val col = index % size
                addView(TicTacTwoButton(requireContext()).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        rowSpec = GridLayout.spec(row, 1f)
                        columnSpec = GridLayout.spec(col, 1f)
                    }
                    setOnClickListener { viewModel.handleGameButtonClick(row, col) }
                })
            }
        }

    @SuppressLint("InflateParams")
    private fun promptForGameName(gameState: GameState) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_save_game, null)
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.game_action_save))
            setView(dialogView)
            setPositiveButton(getString(R.string.general_action_save)) { dialog, _ ->
                val gameName = dialogView.findViewById<EditText>(R.id.gameNameInput).text.toString()
                if (gameName.isValidName()) {
                    val result = viewModel.saveGame(gameName, gameState)
                    val messageRes = if (result is SaveGameResult.Success) R.string.game_popup_saved
                        else R.string.game_popup_save_failed
                    Toast.makeText(requireContext(), getString(messageRes, gameName), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.game_popup_invalid_name), Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            setNegativeButton(getString(R.string.general_action_cancel)) { dialog, _ -> dialog.cancel() }
        }.show()
    }

    private fun String.isValidName() = isNotBlank() && matches(Regex("^[a-zA-Z0-9]+$"))

    inner class SwipeGestureListener : GestureDetector.SimpleOnGestureListener() {
        private val swipeThreshold = 150
        private val swipeVelocityThreshold = 300

        override fun onDown(e: MotionEvent): Boolean = true

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val diffX = e2.x - (e1?.x ?: 0f)
            val diffY = e2.y - (e1?.y ?: 0f)
            return when {
                abs(diffX) > abs(diffY) && abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold -> {
                    viewModel.onGameBoardAction(GameBoardAction.MoveGrid(if (diffX > 0) MoveGridDirection.RIGHT else MoveGridDirection.LEFT))
                    true
                }
                abs(diffY) > swipeThreshold && abs(velocityY) > swipeVelocityThreshold -> {
                    viewModel.onGameBoardAction(GameBoardAction.MoveGrid(if (diffY > 0) MoveGridDirection.DOWN else MoveGridDirection.UP))
                    true
                }
                else -> false
            }
        }
    }
}
