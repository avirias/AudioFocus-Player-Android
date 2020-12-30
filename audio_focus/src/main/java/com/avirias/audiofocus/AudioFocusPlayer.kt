package com.avirias.audiofocus

import android.content.Context
import android.media.*
import android.net.Uri
import android.os.Handler
import android.os.Looper

class AudioFocusPlayer(private val context: Context) {

    private val audioManager = context.getSystemService(AudioManager::class.java)
    private val mediaPlayer: MediaPlayer = MediaPlayer()

    private var datasource: Uri? = null

    val isPlaying: Boolean
        get() = mediaPlayer.isPlaying

    val duration: Int
        get() = mediaPlayer.duration

    val currentPosition: Int
        get() = mediaPlayer.currentPosition

    var playbackParams: PlaybackParams
        get() = mediaPlayer.playbackParams
        set(value) {
            mediaPlayer.playbackParams = value
        }

    val trackInfo: Array<out MediaPlayer.TrackInfo>
        get() = mediaPlayer.trackInfo

    var preferredDevice: AudioDeviceInfo
        get() = mediaPlayer.preferredDevice
        set(value) {
            mediaPlayer.preferredDevice = value
        }
    var screenOnWhilePlaying: Boolean = false
        set(value) {
            mediaPlayer.setScreenOnWhilePlaying(value)
        }

    fun onComplete(callback: (AudioFocusPlayer) -> Unit) {
        mediaPlayer.setOnCompletionListener { callback.invoke(this) }
    }

    fun onError(callback: (AudioFocusPlayer,Int,Int) -> Boolean) {
        mediaPlayer.setOnErrorListener { _, what, extra -> callback(this,what,extra) }
    }

    fun onSeekComplete(callback: (AudioFocusPlayer) -> Unit) {
        mediaPlayer.setOnSeekCompleteListener { callback(this) }
    }



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
            setUsage(AudioAttributes.USAGE_MEDIA)
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

