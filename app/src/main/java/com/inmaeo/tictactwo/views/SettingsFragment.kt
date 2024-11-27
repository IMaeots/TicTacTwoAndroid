package com.inmaeo.tictactwo.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.inmaeo.tictactwo.TicTacTwoApp.Companion.MUSIC
import com.inmaeo.tictactwo.TicTacTwoApp.Companion.SETTINGS
import com.inmaeo.tictactwo.databinding.FragmentSettingsBinding
import com.inmaeo.tictactwo.service.BackgroundMusicManager

class SettingsFragment : Fragment()  {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentSettingsBinding.inflate(layoutInflater, container, false).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
        val isMusicEnabled = sharedPreferences.getBoolean(MUSIC, true)

        with(binding) {
            switchMusic.isChecked = isMusicEnabled
            switchMusic.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    BackgroundMusicManager.startMusic(requireContext())
                } else {
                    BackgroundMusicManager.stopMusic()
                }

                with(sharedPreferences.edit()) {
                    putBoolean(MUSIC, isChecked)
                    apply()
                }
            }
        }
    }
}
