package com.avirias.audiofocus

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper

class AudioFocusPlayer(private val context: Context) {

    private val audioManager = context.getSystemService(AudioManager::class.java)
    private val mediaPlayer: MediaPlayer = MediaPlayer()

    private var datasource: Uri? = null

    val isPlaying: Boolean
        get() = mediaPlayer.isPlaying


    fun setDataSource(datasource: Uri) {
        this.datasource = datasource
        mediaPlayer.setDataSource(context, this.datasource!!)
        mediaPlayer.prepare()
    }

    var audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .build()


    private val listener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN ->
                if (playbackDelayed || resumeOnFocusGain) {
                    synchronized(focusLock) {
                        playbackDelayed = false
                        resumeOnFocusGain = false
                    }
                    mediaPlayer.start()
                }
            AudioManager.AUDIOFOCUS_LOSS -> {
                synchronized(focusLock) {
                    resumeOnFocusGain = false
                    playbackDelayed = false
                }
                mediaPlayer.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                synchronized(focusLock) {
                    // only resume if playback is being interrupted
                    resumeOnFocusGain = mediaPlayer.isPlaying
                    playbackDelayed = false
                }
                mediaPlayer.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // ... pausing or ducking depends on your app
            }
        }
    }
    private val handler = Handler(Looper.getMainLooper())

    private val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
        setAudioAttributes(AudioAttributes.Builder().run {
            setUsage(AudioAttributes.USAGE_GAME)
            setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            build()
        })
        setAcceptsDelayedFocusGain(true)
        setOnAudioFocusChangeListener(listener, handler)
        build()
    }
    private val focusLock = Any()
    private var playbackDelayed = false

    private var resumeOnFocusGain = true

    fun play() {
        if (datasource != null) {
            val requestAudioFocus = audioManager.requestAudioFocus(focusRequest)
            synchronized(focusLock) {
                when (requestAudioFocus) {
                    AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                        mediaPlayer.start()
                    }
                }
            }
        } else {
            throw Throwable("Datasource is null")
        }

    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun destroy() {
        mediaPlayer.release()
    }

    fun reset() {
        mediaPlayer.reset()
        datasource = null

    }

    fun stop() {
        mediaPlayer.stop()
    }

    fun seekTo(mSec: Int) {
        mediaPlayer.seekTo(mSec)
    }

    fun setVolume(leftVolume: Float, rightVolume: Float) {
        mediaPlayer.setVolume(leftVolume, rightVolume)
    }

}
