package com.avirias.audiofocusplayer

import android.media.RingtoneManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.avirias.audiofocus.AudioFocusPlayer

class MainActivity : AppCompatActivity() {

    lateinit var playButton: Button
    lateinit var pauseButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playButton = findViewById(R.id.btn)
        pauseButton = findViewById(R.id.button3)

        val ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
            this,
            RingtoneManager.TYPE_RINGTONE
        )

        //
        val audioFocusPlayer = AudioFocusPlayer(this)

        //
        audioFocusPlayer.setDataSource(ringtoneUri)

        playButton.setOnClickListener {
            audioFocusPlayer.play()

        }

        pauseButton.setOnClickListener {
            audioFocusPlayer.pause()
        }
    }
}