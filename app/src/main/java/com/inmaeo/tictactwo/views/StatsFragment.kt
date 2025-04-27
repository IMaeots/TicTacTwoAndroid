package com.inmaeo.tictactwo.views

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.inmaeo.tictactwo.R
import com.inmaeo.tictactwo.TicTacTwoApp.Companion.SETTINGS
import com.inmaeo.tictactwo.TicTacTwoApp.Companion.STAT1
import com.inmaeo.tictactwo.TicTacTwoApp.Companion.STAT2
import com.inmaeo.tictactwo.TicTacTwoApp.Companion.STAT3
import com.inmaeo.tictactwo.databinding.FragmentStatsBinding

class StatsFragment : Fragment() {

    private lateinit var binding: FragmentStatsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FragmentStatsBinding.inflate(layoutInflater, container, false).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
        updateStats()

        binding.resetStatsButton.setOnClickListener {
            sharedPreferences.edit().apply {
                putInt(STAT1, 0)
                putInt(STAT2, 0)
                putInt(STAT3, 0)
                apply()
            }
            updateStats()
        }
    }

    private fun updateStats() {
        val wins = sharedPreferences.getInt(STAT1, 0)
        val losses = sharedPreferences.getInt(STAT2, 0)
        val draws = sharedPreferences.getInt(STAT3, 0)

        binding.winStat.text = getString(R.string.stats_player1_wins, wins)
        binding.lossStat.text = getString(R.string.stats_player2_wins, losses)
        binding.drawsStat.text = getString(R.string.stats_draws, draws)
    }
}
