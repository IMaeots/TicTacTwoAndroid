package com.inmaeo.tictactwo.domain

import android.content.Context
import android.media.MediaPlayer
import com.inmaeo.tictactwo.R

object BackgroundMusicManager {
    private var mediaPlayer: MediaPlayer? = null

    fun startMusic(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.background_music).apply {
                isLooping = true
                start()
            }
        }
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
