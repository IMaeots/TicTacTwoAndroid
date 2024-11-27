package com.inmaeo.tictactwo.views

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.inmaeo.tictactwo.R
import com.inmaeo.tictactwo.TicTacTwoApp.Companion.MUSIC
import com.inmaeo.tictactwo.TicTacTwoApp.Companion.SETTINGS
import com.inmaeo.tictactwo.databinding.FragmentSettingsBinding
import com.inmaeo.tictactwo.service.BackgroundMusicManager

class SettingsFragment : Fragment()  {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var isMusicEnabled = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
        isMusicEnabled = sharedPreferences.getBoolean(MUSIC, true)

        with(binding) {
            switchMusic.isChecked = isMusicEnabled
            switchMusic.setOnCheckedChangeListener { _, isChecked ->
                isMusicEnabled = isChecked
            }

            saveButton.setOnClickListener {
                applySettings(sharedPreferences)
            }
        }
    }

    private fun applySettings(sharedPreferences: SharedPreferences) {
        if (isMusicEnabled) {
            BackgroundMusicManager.startMusic(requireContext())
        } else {
            BackgroundMusicManager.stopMusic()
        }

        with(sharedPreferences.edit()) {
            putBoolean(MUSIC, isMusicEnabled)
            apply()
        }

        Toast.makeText(requireContext(), getString(R.string.settings_popup_saved), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
