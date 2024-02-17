package io.github.rsookram.mediaplayer

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import androidx.media3.common.C
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import io.github.rsookram.mediaplayer.view.PlayerView

class PlayerActivity : Activity() {

    private val player by lazy {
        ExoPlayer.Builder(this)
            .setTrackSelector(
                DefaultTrackSelector(this).apply {
                    // For some reason the video track needs to be disabled to
                    // disable subs
                    parameters = buildUponParameters()
                        .setRendererDisabled(C.TRACK_TYPE_VIDEO, true)
                        .build()
                }
            )
            .setUsePlatformDiagnostics(false)
            .build()
    }

    private lateinit var view: PlayerView

    private var playbackSpeed = 1.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent.data
        if (uri == null) {
            finish()
            return
        }

        val title = uri.lastPathSegment ?: uri.toString()

        val container = findViewById<ViewGroup>(android.R.id.content)
        view = PlayerView(container, player, title)

        enableImmersiveMode(window)

        setPlaybackSpeed(view, loadSpeed())

        view.onEvent = { event ->
            when (event) {
                Event.DecreaseSpeed -> decreaseSpeed(view)
                Event.IncreaseSpeed -> increaseSpeed(view)
                Event.Rewind -> rewind()
                Event.FastForward -> fastForward()
                Event.TogglePlayPause -> togglePlayPause()
            }
        }

        player.addListener(object : Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                view.setIsPlaying(playWhenReady)
            }
        })

        val mediaSource = createMediaSource(this, uri)

        player.setMediaSource(mediaSource)
        player.prepare()
        player.playWhenReady = true
    }

    private fun decreaseSpeed(view: PlayerView) {
        setPlaybackSpeed(view, playbackSpeed - 0.25F)
    }

    private fun increaseSpeed(view: PlayerView) {
        setPlaybackSpeed(view, playbackSpeed + 0.25F)
    }

    private fun setPlaybackSpeed(view: PlayerView, speed: Float) {
        playbackSpeed = speed
        player.playbackParameters = PlaybackParameters(playbackSpeed)

        view.setPlaybackSpeed(playbackSpeed)
        saveSpeed(speed)
    }

    private fun loadSpeed(): Float = prefs().getFloat("speed", 1.0f)

    private fun saveSpeed(speed: Float) {
        prefs().edit().putFloat("speed", speed).apply()
    }

    private fun prefs(): SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

    private fun rewind() {
        if (!player.isCurrentMediaItemSeekable) return

        player.seekTo((player.currentPosition - 10_000).coerceAtLeast(0))
    }

    private fun fastForward() {
        if (!player.isCurrentMediaItemSeekable) return

        val durationMs = player.duration
        val desired = player.currentPosition + 10_000
        player.seekTo(
            if (durationMs != C.TIME_UNSET) minOf(durationMs, desired) else desired
        )
    }

    private fun togglePlayPause() {
        player.playWhenReady = !player.playWhenReady
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean =
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                rewind()
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                fastForward()
                true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                decreaseSpeed(view)
                true
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                increaseSpeed(view)
                true
            }
            KeyEvent.KEYCODE_SPACE -> {
                togglePlayPause()
                true
            }
            KeyEvent.KEYCODE_MENU -> {
                view.toggleControls()
                true
            }
            else -> super.onKeyUp(keyCode, event)
        }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            enableImmersiveMode(window)
        }
    }
}
