package com.inmaeo.tictactwo

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.inmaeo.tictactwo.data.db.GameDbHelper
import com.inmaeo.tictactwo.data.repository.GameRepository
import com.inmaeo.tictactwo.databinding.ActivityMainBinding
import com.inmaeo.tictactwo.TicTacTwoApp.Companion.MUSIC
import com.inmaeo.tictactwo.TicTacTwoApp.Companion.SETTINGS
import com.inmaeo.tictactwo.service.BackgroundMusicManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        with(binding) {
            setContentView(root)

            val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
            navController = navHostFragment.navController
            topBar.settingsButton.setOnClickListener { navController.navigate(R.id.action_to_settingsFragment) }
            topBar.onboardingButton.setOnClickListener { navController.navigate(R.id.action_to_onboardingFragment) }
            topBar.statsButton.setOnClickListener { navController.navigate(R.id.action_to_statsFragment) }
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
        val isMusicEnabled = sharedPreferences.getBoolean(MUSIC, true)
        
        if (isMusicEnabled) {
            BackgroundMusicManager.startMusic(this)
        }
    }

    override fun onPause() {
        super.onPause()
        BackgroundMusicManager.stopMusic()
    }
}
